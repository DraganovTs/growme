import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { catchError, map, Observable, throwError } from "rxjs";
import { environment } from "../environment/environments";
import { KeycloakService } from "./keycloak.service";
import { BidResponseListDTO, BidStatus, IBid } from "../shared/model/bid";

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

      getBidsForTask(taskId: string, params: any = {}): Observable<BidResponseListDTO> {
    let httpParams = new HttpParams()
      .set('pageIndex', params.pageIndex?.toString() || '1')
      .set('pageSize', params.pageSize?.toString() || '10')
      .set('sort', params.sort || 'createdAtDesc');

    if (params.userId) httpParams = httpParams.set('userId', params.userId);
    if (params.status) httpParams = httpParams.set('status', params.status);

    return this.http.get<BidResponseListDTO>(`${this.apiUrl}/task/${taskId}`, { params: httpParams }).pipe(
      map(response => ({
        ...response,
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

  updateBidStatus(bidId: string, status: BidStatus): Observable<IBid> {
  const userId = this.keycloakService.getUserId();
    if (!userId) {
      return throwError(() => new Error('User not authenticated'));
    }

  const updateRequest = {
    bidStatus: status,
    userId: userId
  };

  return this.http.patch<IBid>(
    `${this.apiUrl}/${bidId}/status`, 
    updateRequest
  ).pipe(
    catchError(error => {
      console.error('Error updating bid status:', error);
      return throwError(() => error);
    })
  );
}

   getUserBids(params: any = {}): Observable<BidResponseListDTO> {
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      return throwError(() => new Error('User not authenticated'));
    }

    let httpParams = new HttpParams()
      .set('pageIndex', params.pageIndex?.toString() || '1')
      .set('pageSize', params.pageSize?.toString() || '10')
      .set('sort', params.sort || 'createdAtDesc')
      .set('userId', userId);

    if (params.status) httpParams = httpParams.set('status', params.status);

    return this.http.get<BidResponseListDTO>(`${this.apiUrl}/my-bids`, { params: httpParams }).pipe(
      map(response => ({
        ...response,
        dataList: response.dataList || [],
        totalCount: response.totalCount || 0,
        pageIndex: response.pageIndex || 0,
        pageSize: response.pageSize || 20,
        totalPages: response.totalPages || 1
      })),
      catchError(error => {
        console.error('Error fetching user bids:', error);
        return throwError(() => error);
      })
    );
  }

    getBidDetails(bidId: string): Observable<IBid> {
    return this.http.get<IBid>(`${this.apiUrl}/${bidId}`).pipe(
      catchError(error => {
        console.error('Error fetching bid details:', error);
        return throwError(() => error);
      })
    );
  }

  getBidsRequiringAction(params: any = {}): Observable<BidResponseListDTO> {
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      return throwError(() => new Error('User not authenticated'));
    }

    let httpParams = new HttpParams()
      .set('pageIndex', params.pageIndex?.toString() || '1')
      .set('pageSize', params.pageSize?.toString() || '10')
      .set('sort', params.sort || 'createdAtDesc')
      .set('userId',userId);

    return this.http.get<BidResponseListDTO>(`${this.apiUrl}/requires-action`, { params: httpParams }).pipe(
      map(response => ({
        ...response,
        dataList: response.dataList || [],
        totalCount: response.totalCount || 0,
        pageIndex: response.pageIndex || 0,
        pageSize: response.pageSize || 20,
        totalPages: response.totalPages || 1
      })),
      catchError(error => {
        console.error('Error fetching bids requiring action:', error);
        return throwError(() => error);
      })
    );
  }

 withdrawBid(bidId: string): Observable<void> {
  const userId = this.keycloakService.getUserId();
  if (!userId) {
    return throwError(() => new Error('User not authenticated'));
  }

  if (userId == null) {
    console.log("userId is null!");
}

  const params = new HttpParams().set('userId', userId);

  return this.http.delete<void>(`${this.apiUrl}/${bidId}`, { params }).pipe(
    catchError(error => {
      console.error('Error withdrawing bid:', error);
      return throwError(() => error);
    })
  );
}

  createCounterOffer(bidId: string, counterOfferData: any): Observable<IBid> {
    const userId = this.keycloakService.getUserId();
    if (!userId) {
      return throwError(() => new Error('User not authenticated'));
    }

    const params = new HttpParams().set('userId' , userId);

    return this.http.post<IBid>(`${this.apiUrl}/${bidId}/counter-offer`, counterOfferData , {params}).pipe(
      catchError(error => {
        console.error('Error creating counter offer:', error);
        return throwError(() => error);
      })
    );
  }
}
