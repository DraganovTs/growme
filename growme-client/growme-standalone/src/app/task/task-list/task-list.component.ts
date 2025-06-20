import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { KeycloakService } from 'src/app/services/keycloak.service';
import { TaskService } from 'src/app/services/task-service';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [CommonModule,ReactiveFormsModule, FormsModule , RouterModule],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.scss'
})
export class TaskListComponent implements OnInit{

  tasks: any[] = [];
  loading = false;
  searchTerm = '';
  selectedCategory = '';
  sortBy = 'newest';
  currentPage = 1;
  pageSize = 9;
  totalTasks = 0;
  totalPages = 1;

  categories = [
    'vegetables',
    'fruits',
    'herbs',
    'grains',
    'wine',
    'other'
  ];

   constructor(
    private taskService: TaskService,
    private keycloakService: KeycloakService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.loading = true;
    
    const params: any = {
      page: this.currentPage,
      limit: this.pageSize,
      sort: this.sortBy
    };

    if (this.searchTerm) {
      params.search = this.searchTerm;
    }

    if (this.selectedCategory) {
      params.category = this.selectedCategory;
    }

    this.taskService.getTasks(params).subscribe({
      next: (response: any) => {
        this.tasks = response.tasks || response; // Adapt based on your API response structure
        this.totalTasks = response.total || this.tasks.length;
        this.totalPages = Math.ceil(this.totalTasks / this.pageSize);
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
    this.currentPage = 1; 
    this.loadTasks();
  }

  changePage(page: number): void {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.loadTasks();
  }

  getPages(): number[] {
    const pages: number[] = [];
    const maxVisiblePages = 5;
    let startPage = 1;
    let endPage = this.totalPages;

    if (this.totalPages > maxVisiblePages) {
      const halfVisible = Math.floor(maxVisiblePages / 2);
      startPage = Math.max(1, this.currentPage - halfVisible);
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


