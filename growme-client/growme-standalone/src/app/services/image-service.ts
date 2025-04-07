import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environments';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private apiUrl = environment.productApi; 

  constructor(private http: HttpClient) {}

 
  getImageUrl(filename: string): string {
    if (!filename) return this.getDefaultImageUrl();
    
    const cleanName = filename.split('/').pop() || filename;
    
    return `${this.apiUrl}/images/${encodeURIComponent(cleanName)}`;
  }

  getDefaultImageUrl(): string {
    return 'assets/images/default-product.jpg';
  }

  uploadImage(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${this.apiUrl}/upload-image`, formData);
  }

  getExistingImages(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/images`);
  }

  getRecentImages(): Observable<ImageDisplay[]> {
    return this.http.get<ImageDisplay[]>(`${this.apiUrl}/images/recent`);
  }
  
}

export interface ImageDisplay {
    filename: string;
    displayName: string;
    url: string;
  }