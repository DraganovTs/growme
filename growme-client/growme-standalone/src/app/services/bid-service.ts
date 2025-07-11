import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, Observable, throwError } from "rxjs";
import { environment } from "../environment/environments";
import { KeycloakService } from "./keycloak.service";
import { Bid } from "../shared/model/bid";

@Injectable({
  providedIn: 'root'
})
export class BidService {
  private apiUrl = `${environment.taskApi}bids`;

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  createBid(bidData: any): Observable<Bid> {
  const userId = this.keycloakService.getUserId();
  if (!userId) {
    return throwError(() => new Error('User not authenticated'));
  }

  const payload = {
    ...bidData,
    price: Number(bidData.price).toFixed(2),
    deliveryMethod: bidData.deliveryMethod.toUpperCase(),
    userId,
    status: 'PENDING'
  };

  return this.http.post<Bid>(this.apiUrl, payload).pipe(
    catchError(error => {
      console.error('API Error:', error);
      return throwError(() => error);
    })
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
