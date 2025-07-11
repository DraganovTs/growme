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
  if (!userId) {
    throw new Error('User not authenticated');
  }

  
  const requestBody = {
    taskId: bidData.taskId,
    price: Number(bidData.price).toFixed(2), 
    message: bidData.message,
    proposedHarvestDate: bidData.proposedHarvestDate,
    deliveryMethod: bidData.deliveryMethod.toUpperCase(), 
    deliveryIncluded: bidData.deliveryIncluded,
    userId: userId,
    status: 'PENDING', 
    userName: bidData.userName || 'Anonymous Grower'
  };

  console.log('Final API payload:', requestBody);
  return this.http.post(this.apiUrl, requestBody);
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