import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, map, Observable, throwError } from "rxjs";
import { environment } from "../environment/environments";
import { KeycloakService } from "./keycloak.service";
import { BidResponseListDTO, IBid } from "../shared/model/bid";

@Injectable({
  providedIn: 'root'
})
export class BidService {
  private apiUrl = `${environment.taskApi}bids`;

  constructor(
    private http: HttpClient,
    private keycloakService: KeycloakService
  ) { }

  createBid(bidData: any): Observable<IBid> {
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

  return this.http.post<IBid>(this.apiUrl, payload).pipe(
    catchError(error => {
      console.error('API Error:', error);
      return throwError(() => error);
    })
  );
}

    getBidsForTask(taskId: string, pageParams?: any): Observable<BidResponseListDTO> {
    let params = new HttpParams();
    if (pageParams) {
      Object.keys(pageParams).forEach(key => {
        params = params.append(key, pageParams[key]);
      });
    }
    
    return this.http.get<BidResponseListDTO>(`${this.apiUrl}/task/${taskId}`, { params }).pipe(
      map(response => ({
        dataList: response.dataList || [],
        totalCount: response.totalCount || 0,
        pageIndex: response.pageIndex || 0,
        pageSize: response.pageSize || 20,
        totalPages: response.totalPages || 1
      })),
      catchError(error => {
        console.error('Error fetching bids:', error);
        return throwError(() => error);
      })
    );
  }

  updateBidStatus(bidId: string, status: string): Observable<IBid> {
    return this.http.patch<IBid>(`${this.apiUrl}/${bidId}/status`, { status });
  }

  getUserBids(userId: string): Observable<BidResponseListDTO> {
    return this.http.get<BidResponseListDTO>(`${this.apiUrl}user/${userId}`);
  }

  getBidDetails(bidId: string): Observable<IBid> {
    return this.http.get<IBid>(`${this.apiUrl}/${bidId}`);
  }
}
