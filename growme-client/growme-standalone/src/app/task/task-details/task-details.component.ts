import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BidFormComponent } from 'src/app/bids/bid-form/bid-form.component';
import { BidListComponent } from 'src/app/bids/bid-list/bid-list.component';
import { BidService } from 'src/app/services/bid-service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';
import { IBid } from 'src/app/shared/model/bid';
import { ITask } from 'src/app/shared/model/task';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule,RouterModule  , BidFormComponent , BidListComponent],
  templateUrl: './task-details.component.html',
  styleUrl: './task-details.component.scss'
})
export class TaskDetailsComponent implements OnInit {
  private router = inject(Router);

  task: ITask | null = null;
  bids: IBid[] = [];
  loading = true;
  isTaskOwner = false;
  isGrower = false;
  submittingBid = false;
  lowestBidPrice = 0;
  highestBidPrice = 0;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private bidService: BidService,
    private keycloakService: KeycloakService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    console.log('TaskDetails initialized');
  console.log('isTaskOwner:', this.isTaskOwner);
  console.log('isGrower:', this.isGrower);
    this.route.paramMap.subscribe(params => {
      const taskId = params.get('id');
      console.log('Task ID from route:', taskId);
      if (taskId) {
        this.loadTask(taskId);
        this.isGrower = this.keycloakService.hasRole('SELLER');
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
    next: (response) => {
      this.bids = response.dataList; 
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

      if (this.highestBidPrice === 0) {
        this.highestBidPrice = 1;
      }
    }

  }

 handleBidSubmission(bidData: any) {
  this.submittingBid = true;
  
  this.bidService.createBid(bidData).subscribe({
    next: (response) => {
      this.bids.unshift(response);
      this.toastr.success('Bid submitted!');
    },
    error: (error) => {
      console.error('Submission failed:', error);
      this.toastr.error('Failed to submit bid');
    },
    complete: () => {
      this.submittingBid = false; 
    }
  });
}

  acceptBid(bidId: string): void {
    if (!confirm('Are you sure you want to accept this offer?')) return;

    this.bidService.updateBidStatus(bidId, 'ACCEPTED').subscribe({
      next: () => {
        const bid = this.bids.find(b => b.id === bidId);
        if (bid) bid.status = 'ACCEPTED';
        this.updateTaskStatus('ACTIVE');
        this.calculatePriceRange();
        this.toastr.success('Bid accepted successfully!');
      },
      error: (error) => {
        this.toastr.error('Failed to accept bid');
        console.error(error);
      }
    });
  }

  rejectBid(bidId: string): void {
    this.bidService.updateBidStatus(bidId, 'REJECTED').subscribe({
      next: () => {
        const bid = this.bids.find(b => b.id === bidId);
        if (bid) bid.status = 'REJECTED';
        this.toastr.info('Bid rejected');
      },
      error: (error) => {
        this.toastr.error('Failed to reject bid');
        console.error(error);
      }
    });
  }

  private updateTaskStatus(status: string): void {
    if (!this.task) return;

    this.taskService.updateTaskStatus(this.task.id, status).subscribe({
      next: () => {
        if (this.task) this.task.status = status as any;
      },
      error: (error) => {
        console.error('Error updating task status:', error);
        this.toastr.error('Failed to update task status');
      }
    });
  }

  cancelTask(): void {
    if (!confirm('Are you sure you want to cancel this task? All bids will be rejected.')) return;

    this.taskService.updateTaskStatus(this.task?.id || '', 'CANCELLED').subscribe({
      next: () => {
        if (this.task) this.task.status = 'CANCELLED';
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
        error: (error) => console.error(`Error rejecting bid ${bid.id}:`, error)
      });
    });
  }

  completeTask(): void {
    if (!confirm('Mark this task as completed?')) return;

    this.taskService.updateTaskStatus(this.task?.id || '', 'COMPLETED').subscribe({
      next: () => {
        if (this.task) this.task.status = 'COMPLETED';
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
      state: { similarTask: this.task }
    });
  }
}
