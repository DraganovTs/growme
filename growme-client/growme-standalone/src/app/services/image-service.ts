import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { catchError, Observable } from 'rxjs';
import { environment } from '../environment/environments';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class ImageService {
  private apiUrl = environment.productApi; 

  constructor(private http: HttpClient) {}

 
  getImageUrl(filename: string): string {
    if (!filename) return this.getDefaultImageUrl();
    
    let cleanName = decodeURIComponent(filename);

    cleanName = cleanName.replace(/\.\.\//g, '')
                       .replace(/\.\.\\/g, '');
    
    cleanName = cleanName.split('/').pop() || cleanName;
    
    return `${this.apiUrl}/images/${encodeURIComponent(cleanName).replace(/%2F/g, '/')}`;
}

  getDefaultImageUrl(): string {
    return 'assets/images/default-product.jpg';
  }

  uploadImage(file: File): Observable<{filename: string}> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<{filename: string}>(
      `${this.apiUrl}/upload-image`, 
      formData
    ).pipe(
      catchError(error => {
        console.error('Upload failed!', error);
        throw new Error('Upload failed');
      })
    );
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

  export interface ImageUploadResponse {
    filename: string;
    originalName?: string;
    url?: string;
  }