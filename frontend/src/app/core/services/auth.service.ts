import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse, UserData } from '../models/auth.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  public isAuthenticated = signal<boolean>(false);
  public currentUser = signal<UserData | null>(null);

  constructor() {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((response) => {
        console.log('Login Response do Backend:', response);
        if (response && response.dados) {
          this.isAuthenticated.set(true);
          this.currentUser.set(response.dados);
        }
      }),
    );
  }

  logout(): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => {
        this.isAuthenticated.set(false);
        this.currentUser.set(null);
      }),
    );
  }
}
