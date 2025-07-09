import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BidService } from 'src/app/services/bid-service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';
import { Bid } from 'src/app/shared/model/bid';
import { Task } from 'src/app/shared/model/task';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule],
  templateUrl: './task-details.component.html',
  styleUrl: './task-details.component.scss'
})
export class TaskDetailsComponent implements OnInit {
  private router = inject(Router);

 task: Task | null = null;
  bids: Bid[] = [];
  loading = true;
  isTaskOwner = false;
  isGrower = false;
  submittingBid = false;
  lowestBidPrice = 0;
  highestBidPrice = 0;

  bidForm: FormGroup;

   constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private bidService: BidService,
    private keycloakService: KeycloakService,
    private fb: FormBuilder,
    private toastr: ToastrService
  ) {
    this.bidForm = this.fb.group({
      price: ['', [Validators.required, Validators.min(0.01)]],
      proposedHarvestDate: ['', Validators.required],
      deliveryMethod: ['delivery', Validators.required],
      deliveryIncluded: [false],
      message: ['', [Validators.required, Validators.minLength(20)]]
    });
  }

 ngOnInit(): void {

  this.route.paramMap.subscribe(params => {
        console.log('Route params:', params);
    const taskId = params.get('id');
    console.log('Task ID:', taskId); 

    if (taskId) {
      this.loadTask(taskId);
      this.isGrower = this.keycloakService.hasRole('GROWER');
    } else {
      this.toastr.error('No task ID provided');
      this.router.navigate(['/tasks']);
    }
  });
}


  loadTask(taskId: string): void {
    this.loading = true;
    this.taskService.getTask(taskId).subscribe({
      next: (task) => {
        console.log('Received task:', task);
        this.task = task;
        this.checkTaskOwnership();
        if (this.shouldLoadBids()) {
          this.loadBids(taskId);
        } else {
          this.loading = false;
        }
      },
      error: (error) => {
        this.handleTaskLoadError(error);
      }
    });
  }

  private checkTaskOwnership(): void {
    const currentUserId = this.keycloakService.getUserId();
    if (currentUserId && this.task) {
      this.isTaskOwner = this.task.userId === currentUserId;
    }
  }

  private shouldLoadBids(): boolean {
    return this.task?.status !== 'COMPLETED' && this.task?.status !== 'CANCELLED';
  }

  private handleTaskLoadError(error: any): void {
    console.error('Error loading task:', error);
    this.toastr.error('Failed to load task details');
    this.loading = false;
    this.router.navigate(['/tasks']);
  }

  loadBids(taskId: string): void {
    this.bidService.getBidsForTask(taskId).subscribe({
      next: (bids) => {
        this.bids = bids;
        this.calculatePriceRange();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading bids:', error);
        this.toastr.warning('Failed to load bids for this task');
        this.loading = false;
      }
    });
  }

  private calculatePriceRange(): void {
    if (this.bids.length > 0) {
      const prices = this.bids.map(b => b.price);
      this.lowestBidPrice = Math.min(...prices);
      this.highestBidPrice = Math.max(...prices);
    }
  }

  submitBid(): void {
    if (this.bidForm.invalid) {
      this.markFormGroupTouched(this.bidForm);
      this.toastr.error('Please fill all required fields correctly');
      return;
    }

    if (!this.keycloakService.getUserId()) {
      this.toastr.warning('Please login to submit a bid');
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.submittingBid = true;
    const bidData = this.createBidData();

    this.bidService.createBid(bidData).subscribe({
      next: (response) => {
        this.handleBidSubmissionSuccess(response);
      },
      error: (error) => {
        this.handleBidSubmissionError(error);
      }
    });
  }

  private createBidData(): any {
    return {
      ...this.bidForm.value,
      taskId: this.task?.id,
      growerId: this.keycloakService.getUserId(),
      growerName: this.keycloakService.getUsername() || 'Anonymous Grower',
      status: 'PENDING',
      createdAt: new Date().toISOString()
    };
  }

  private handleBidSubmissionSuccess(response: any): void {
    this.submittingBid = false;
    this.bids.unshift(response);
    this.bidForm.reset();
    this.calculatePriceRange();
    this.toastr.success('Bid submitted successfully!');
  }

  private handleBidSubmissionError(error: any): void {
    console.error('Error submitting bid:', error);
    this.submittingBid = false;
    this.toastr.error('Failed to submit bid. Please try again.');
  }

  acceptBid(bidId: string): void {
    if (!confirm('Are you sure you want to accept this offer? This will close the task to other bids.')) {
      return;
    }

    this.bidService.updateBidStatus(bidId, 'ACCEPTED').subscribe({
      next: () => {
        this.updateBidStatus(bidId, 'ACCEPTED');
        this.updateTaskStatus('ACTIVE');
      },
      error: (error) => {
        this.handleBidAcceptError(error);
      }
    });
  }

  private updateBidStatus(bidId: string, status: string): void {
    const bid = this.bids.find(b => b.id === bidId);
    if (bid) {
      bid.status = status as any;
    }
  }

  private updateTaskStatus(status: string): void {
    if (!this.task) return;
    
    this.taskService.updateTaskStatus(this.task.id, status).subscribe({
      next: () => {
        if (this.task) {
          this.task.status = status as any;
        }
        this.toastr.success('Bid accepted successfully! The grower has been notified.');
      },
      error: (error) => {
        console.error('Error updating task status:', error);
        this.toastr.error('Failed to update task status');
      }
    });
  }

  private handleBidAcceptError(error: any): void {
    console.error('Error accepting bid:', error);
    this.toastr.error('Failed to accept bid');
  }

  rejectBid(bidId: string): void {
    this.bidService.updateBidStatus(bidId, 'REJECTED').subscribe({
      next: () => {
        this.updateBidStatus(bidId, 'REJECTED');
        this.toastr.info('Bid has been rejected');
      },
      error: (error) => {
        console.error('Error rejecting bid:', error);
        this.toastr.error('Failed to reject bid');
      }
    });
  }

  cancelTask(): void {
    if (!confirm('Are you sure you want to cancel this task? All bids will be rejected.')) {
      return;
    }

    this.taskService.updateTaskStatus(this.task?.id || '', 'CANCELLED').subscribe({
      next: () => {
        if (this.task) {
          this.task.status = 'CANCELLED';
        }
        this.toastr.info('Task has been cancelled');
        this.rejectAllBids();
      },
      error: (error) => {
        console.error('Error cancelling task:', error);
        this.toastr.error('Failed to cancel task');
      }
    });
  }

  private rejectAllBids(): void {
    const pendingBids = this.bids.filter(bid => bid.status === 'PENDING');
    pendingBids.forEach(bid => {
      this.bidService.updateBidStatus(bid.id, 'REJECTED').subscribe({
        error: (error) => {
          console.error(`Error rejecting bid ${bid.id}:`, error);
        }
      });
    });
  }


  completeTask(): void {
    if (!confirm('Mark this task as completed?')) {
      return;
    }

    this.taskService.updateTaskStatus(this.task?.id || '', 'COMPLETED').subscribe({
      next: () => {
        if (this.task) {
          this.task.status = 'COMPLETED';
        }
        this.toastr.success('Task marked as completed');
      },
      error: (error) => {
        console.error('Error completing task:', error);
        this.toastr.error('Failed to complete task');
      }
    });
  }

  contactGrower(): void {
    this.toastr.info('Contact feature will be implemented soon');
  }

  createSimilarTask(): void {
    this.router.navigate(['/tasks/create'], {
      state: {
        similarTask: this.task
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}