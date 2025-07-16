import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { CategoryService } from 'src/app/services/category-service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';
import { ICategory } from 'src/app/shared/model/product';
import { ITaskParams } from 'src/app/shared/model/taskparams';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule, FormsModule , RouterModule ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit{

    tasks: any[] = [];
  loading = false;
  taskParams = new ITaskParams();
  totalCount = 0;
  totalPages = 1;
  productTypes: ICategory[] = [];

  statusOptions = [
    'active',
    'pending',
    'completed',
    'cancelled'
  ];

  constructor(
    private taskService: TaskService,
    private keycloakService: KeycloakService,
    private router: Router,
    private toastr: ToastrService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
    this.loadCategories();
  }

     private loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (data) => {
        this.productTypes = data;
      },
      error: (err) => {
        console.error('Failed to load categories', err);
        this.toastr.error('Failed to load product categories. Please try again.');
      }
    });
  }

  loadTasks(): void {
    this.loading = true;
    
    this.taskService.getTasks(this.taskParams).subscribe({
      next: (response) => {
        this.tasks = response.dataList || [];
        this.totalCount = response.totalCount || 0;
        this.totalPages = Math.ceil(this.totalCount / this.taskParams.pageSize);
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        this.toastr.error('Failed to load tasks. Please try again.');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.taskParams.pageIndex = 1;
    this.loadTasks();
  }

  changePage(page: number): void {
    if (page < 1 || page > this.totalPages) return;
    this.taskParams.pageIndex = page;
    this.loadTasks();
  }

  getPages(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    let startPage = 1;
    let endPage = this.totalPages;

    if (this.totalPages > maxVisiblePages) {
      const halfVisible = Math.floor(maxVisiblePages / 2);
      startPage = Math.max(1, this.taskParams.pageIndex - halfVisible);
      endPage = Math.min(this.totalPages, startPage + maxVisiblePages - 1);

      if (endPage - startPage + 1 < maxVisiblePages) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return pages;
  }

  canCreateTask(): boolean {
    return this.keycloakService.hasRole('BUYER');
  }

  navigateToCreateTask(): void {
    if (!this.keycloakService.getUserId()) {
      this.toastr.warning('Please login to create a grow task');
      this.router.navigate(['/login']);
      return;
    }

    if (!this.canCreateTask()) {
      this.toastr.error('You need buyer privileges to create tasks');
      return;
    }

    this.router.navigate(['/tasks/create']);
  }
}