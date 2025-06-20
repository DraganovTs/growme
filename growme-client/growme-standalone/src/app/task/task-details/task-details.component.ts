import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { BidService } from 'src/app/services/bid-service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule],
  templateUrl: './task-details.component.html',
  styleUrl: './task-details.component.scss'
})
export class TaskDetailsComponent implements OnInit {

  task: any;
  bids: any[] = [];
  loading = true;
  isTaskOwner = false;
  isGrower = false;
  submittingBid = false;
  
  // For price range visualization
  lowestBidPrice = 0;
  highestBidPrice = 0;

  bidForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
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
    const taskId = this.route.snapshot.paramMap.get('id');
    if (taskId) {
      this.loadTask(taskId);
      this.loadBids(taskId);
      
      // Check user roles
      this.isGrower = this.keycloakService.hasRole('GROWER');
    } else {
      this.toastr.error('No task ID provided');
      this.router.navigate(['/tasks']);
    }
  }

 loadTask(taskId: string): void {
    this.taskService.getTask(taskId).subscribe({
      next: (task) => {
        this.task = task;
        this.loading = false;
        
        // Check if current user is task owner
        const currentUserId = this.keycloakService.getUserId();
        if (currentUserId) {
          this.isTaskOwner = task.userId === currentUserId;
        }

        // If task is cancelled or completed, don't load bids
        if (['cancelled', 'completed'].includes(task.status)) {
          return;
        }
      },
      error: (error) => {
        console.error('Error loading task:', error);
        this.toastr.error('Failed to load task details');
        this.loading = false;
        this.router.navigate(['/tasks']);
      }
    });
  }

  loadBids(taskId: string): void {
    if (this.task?.status === 'cancelled' || this.task?.status === 'completed') {
      return;
    }

    this.bidService.getBidsForTask(taskId).subscribe({
      next: (bids) => {
        this.bids = bids;
        
        // Calculate price range for visualization
        if (bids.length > 0) {
          const prices = bids.map((b: any) => b.price);
          this.lowestBidPrice = Math.min(...prices);
          this.highestBidPrice = Math.max(...prices);
        }
      },
      error: (error) => {
        console.error('Error loading bids:', error);
        this.toastr.warning('Failed to load bids for this task');
      }
    });
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
    
    const bidData = {
      ...this.bidForm.value,
      taskId: this.task.id,
      growerId: this.keycloakService.getUserId(),
      growerName: this.keycloakService.getUsername() || 'Anonymous Grower',
      status: 'pending',
      createdAt: new Date().toISOString()
    };

    this.bidService.createBid(bidData).subscribe({
      next: (response) => {
        this.submittingBid = false;
        this.bids.unshift(response);
        this.bidForm.reset();
        this.toastr.success('Bid submitted successfully!');
      },
      error: (error) => {
        console.error('Error submitting bid:', error);
        this.submittingBid = false;
        this.toastr.error('Failed to submit bid. Please try again.');
      }
    });
  }

  acceptBid(bidId: string): void {
    if (!confirm('Are you sure you want to accept this offer? This will close the task to other bids.')) {
      return;
    }

    this.bidService.updateBidStatus(bidId, 'accepted').subscribe({
      next: () => {
        // Update bid status in local array
        const bid = this.bids.find(b => b.id === bidId);
        if (bid) {
          bid.status = 'accepted';
        }
        
        // Update task status
        this.taskService.updateTaskStatus(this.task.id, 'active').subscribe({
          next: () => {
            this.task.status = 'active';
            this.toastr.success('Bid accepted successfully! The grower has been notified.');
          },
          error: (error) => {
            console.error('Error updating task status:', error);
            this.toastr.error('Failed to update task status');
          }
        });
      },
      error: (error) => {
        console.error('Error accepting bid:', error);
        this.toastr.error('Failed to accept bid');
      }
    });
  }

  rejectBid(bidId: string): void {
    this.bidService.updateBidStatus(bidId, 'rejected').subscribe({
      next: () => {
        // Update bid status in local array
        const bid = this.bids.find(b => b.id === bidId);
        if (bid) {
          bid.status = 'rejected';
        }
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

    this.taskService.updateTaskStatus(this.task.id, 'cancelled').subscribe({
      next: () => {
        this.task.status = 'cancelled';
        this.toastr.info('Task has been cancelled');
        // Optionally reject all bids
        this.rejectAllBids();
      },
      error: (error) => {
        console.error('Error cancelling task:', error);
        this.toastr.error('Failed to cancel task');
      }
    });
  }

  private rejectAllBids(): void {
    const pendingBids = this.bids.filter(bid => bid.status === 'pending');
    pendingBids.forEach(bid => {
      this.bidService.updateBidStatus(bid.id, 'rejected').subscribe({
        error: (error) => {
          console.error(`Error rejecting bid ${bid.id}:`, error);
        }
      });
    });
  }

  private markFormGroupTouched(formGroup: FormGroup) {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  completeTask(): void {
    if (!confirm('Mark this task as completed?')) {
      return;
    }

    this.taskService.updateTaskStatus(this.task.id, 'completed').subscribe({
      next: () => {
        this.task.status = 'completed';
        this.toastr.success('Task marked as completed');
      },
      error: (error) => {
        console.error('Error completing task:', error);
        this.toastr.error('Failed to complete task');
      }
    });
  }

  contactGrower(): void {
    // Implement contact logic
    this.toastr.info('Contact feature will be implemented soon');
  }

  createSimilarTask(): void {
    this.router.navigate(['/tasks/create'], {
      state: {
        similarTask: this.task
      }
    });
  }
}