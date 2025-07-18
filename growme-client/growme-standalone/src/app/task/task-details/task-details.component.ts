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
import { BidStatus, IBid } from 'src/app/shared/model/bid';
import { IBidParams } from 'src/app/shared/model/bidparams';
import { ITask } from 'src/app/shared/model/task';
import { BidCardComponent } from "src/app/bids/bid-card/bid-card.component";
import { CounterOfferDialogComponent } from 'src/app/bids/counter-offer-dialog/counter-offer-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-task-details',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, BidFormComponent, BidListComponent, BidCardComponent],
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
  myBids: IBid[] = [];
  bidsRequiringAction: IBid[] = [];
  showBidsRequiringAction = false;

  constructor(
    private route: ActivatedRoute,
    private taskService: TaskService,
    private bidService: BidService,
    private keycloakService: KeycloakService,
    private toastr: ToastrService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const taskId = params.get('id');
      if (taskId) {
        this.loadTask(taskId);
        this.isGrower = this.keycloakService.hasRole('SELLER');
        this.loadMyBids(); 
        this.loadBidsRequiringAction(); 
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
      this.showBidsRequiringAction = this.isTaskOwner;
      
      if (this.shouldLoadBids()) {
        this.loadBids(taskId);
      }
      
      
      if (this.isGrower && !this.isTaskOwner) {
        this.loadMyBids();
      } else if (this.isTaskOwner) {
        this.loadBidsRequiringAction();
      }
      
      this.loading = false;
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
    const params = new IBidParams({
      pageIndex: 1,
      pageSize: 10,
      sort: 'priceAsc'
    });

    this.bidService.getBidsForTask(taskId, params).subscribe({
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

  loadMyBids(): void {
    const userId = this.keycloakService.getUserId();
    if (!userId) return;

    const params = new IBidParams({
      pageIndex: 1,
      pageSize: 10,
      sort: 'createdAtDesc'
    });

    this.bidService.getUserBids(params).subscribe({
      next: (response) => {
        this.myBids = response.dataList;
      },
      error: (error) => {
        console.error('Error loading user bids:', error);
      }
    });
  }

  loadBidsRequiringAction(): void {
    const userId = this.keycloakService.getUserId();
    if (!userId) return;

    const params = new IBidParams({
      pageIndex: 1,
      pageSize: 10,
      sort: 'createdAtDesc'
    });

    this.bidService.getBidsRequiringAction(params).subscribe({
      next: (response) => {
        this.bidsRequiringAction = response.dataList;
      },
      error: (error) => {
        console.error('Error loading bids requiring action:', error);
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

 handleBidSubmission(bidData: any): void {
    this.submittingBid = true;
    
    this.bidService.createBid(bidData).subscribe({
      next: (response) => {
        this.bids.unshift(response);
        this.myBids.unshift(response);
        this.toastr.success('Bid submitted successfully!');
        this.calculatePriceRange();
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

  viewBidDetails(bidId: string): void {
    this.bidService.getBidDetails(bidId).subscribe({
      next: (bid) => {
       
        console.log('Bid details:', bid);
      },
      error: (error) => {
        console.error('Error fetching bid details:', error);
        this.toastr.error('Failed to load bid details');
      }
    });
  }

  acceptBid(bidId: string): void {
  if (!confirm('Are you sure you want to accept this offer?')) return;

  this.bidService.updateBidStatus(bidId, 'ACCEPTED').subscribe({
    next: (updatedBid) => {
      this.updateBidInLists(updatedBid);
      this.updateTaskStatus('ACTIVE'); 
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
    next: (updatedBid) => {
      this.updateBidInLists(updatedBid);
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

  withdrawBid(bidId: string): void {
    console.log('Withdrow bid whit id : ', bidId)
    if (!confirm('Are you sure you want to withdraw this bid?')) return;

    this.bidService.withdrawBid(bidId).subscribe({
      next: () => {
        this.removeBidFromLists(bidId);
        this.toastr.info('Bid withdrawn successfully');
      },
      error: (error) => {
        this.toastr.error('Failed to withdraw bid');
        console.error(error);
      }
    });
  }

  createCounterOffer(bidId: string): void {
  const dialogRef = this.dialog.open(CounterOfferDialogComponent, {
    width: '500px',
    data: { bidId: bidId }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.bidService.createCounterOffer(bidId, result).subscribe({
        next: (updatedBid) => {
          this.updateBidInLists(updatedBid);
          this.toastr.success('Counter offer created successfully');
        },
        error: (error) => {
          this.toastr.error('Failed to create counter offer');
          console.error(error);
        }
      });
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

  private updateBidInLists(updatedBid: IBid): void {
  const updateBid = (bid: IBid) => bid.bidId === updatedBid.bidId ? updatedBid : bid;
  
  this.bids = this.bids.map(updateBid);
  this.myBids = this.myBids.map(updateBid);
  this.bidsRequiringAction = this.bidsRequiringAction.map(updateBid);
  this.calculatePriceRange();
}

  private removeBidFromLists(bidId: string): void {
    this.bids = this.bids.filter(bid => bid.bidId !== bidId);
    this.myBids = this.myBids.filter(bid => bid.bidId !== bidId);
    this.bidsRequiringAction = this.bidsRequiringAction.filter(bid => bid.bidId !== bidId);
    this.calculatePriceRange();
  }

  private rejectAllBids(): void {
    const pendingBids = this.bids.filter(bid => bid.status === 'PENDING');
    pendingBids.forEach(bid => {
      this.bidService.updateBidStatus(bid.bidId, 'REJECTED').subscribe({
        error: (error) => console.error(`Error rejecting bid ${bid.bidId}:`, error)
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
