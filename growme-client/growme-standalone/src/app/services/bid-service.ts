import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";
import { environment } from "../environment/environments";
import { KeycloakService } from "./keycloak.service";

@Injectable({
  providedIn: 'root'
})
export class BidService {
  private apiUrl = `${environment.taskApi}bids`;

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  createBid(bidData: any): Observable<any> {
    
    const userId = this.keycloakService.getUserId();
    console.log('Creating bid with data:', bidData);
     console.log('User ID:', userId); 

    bidData.userId = userId;
    console.log('Final bid payload:', bidData);
    if (!userId) {
      console.error('User not authenticated'); 
      throw new Error('User not authenticated');
    }

  

    return this.http.post(this.apiUrl, bidData).pipe(
    tap(response => console.log('API response:', response)) 
  );
  }

  getBidsForTask(taskId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/task/${taskId}`);
  }

  updateBidStatus(bidId: string, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}${bidId}/status`, { status });
  }

  getUserBids(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}user/${userId}`);
  }

  getBidDetails(bidId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${bidId}`);
  }
}