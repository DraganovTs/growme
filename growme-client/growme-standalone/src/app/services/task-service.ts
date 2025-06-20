import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../environment/environments";
import { KeycloakService } from "./keycloak.service";

Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.taskApi}/tasks`;

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  createTask(taskData: any): Observable<any> {
    // Add user ID from Keycloak to the task data
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      throw new Error('User not authenticated');
    }

    const completeTaskData = {
      ...taskData,
      userId: userId
    };

    return this.http.post(`${this.apiUrl}`, completeTaskData);
  }

  getTask(taskId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${taskId}`);
  }

    getTasks(params: any = {}): Observable<any> {
    let httpParams = new HttpParams();

    // Add pagination params
    if (params.page) {
      httpParams = httpParams.set('page', params.page.toString());
    }
    if (params.limit) {
      httpParams = httpParams.set('limit', params.limit.toString());
    }

    // Add filtering params
    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    if (params.category) {
      httpParams = httpParams.set('category', params.category);
    }

    // Add sorting params
    if (params.sort) {
      httpParams = httpParams.set('sort', params.sort);
    }

    return this.http.get(this.apiUrl, { params: httpParams });
  }

  updateTaskStatus(taskId: string, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${taskId}/status`, { status });
  }

  cancelTask(taskId: string): Observable<any> {
    return this.updateTaskStatus(taskId, 'cancelled');
  }

  getUserTasks(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/${userId}`);
  }

  searchTasks(query: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/search`, { params: { q: query } });
  }
}