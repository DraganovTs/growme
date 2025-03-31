import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environments';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private apiUrl = environment.productApi; 

  constructor(private http: HttpClient) {}

 
  uploadImage(imageFile: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', imageFile);
    return this.http.post<{ url: string }>(`${this.apiUrl}/upload-image`, formData);
  }

  getExistingImages(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/existing-images`);
  }
}