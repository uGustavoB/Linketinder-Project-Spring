import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, of, map, catchError } from 'rxjs';
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

  checkSession(): Observable<boolean> {
    if (this.isAuthenticated() && this.currentUser()) {
      return of(true);
    }

    return this.http.get<ApiResponse<any>>(`${environment.apiUrl}/perfil`).pipe(
      map((response) => {
        if (response && response.dados) {
          this.isAuthenticated.set(true);
          this.currentUser.set({
            id: response.dados.id,
            email: response.dados.email,
            nome: response.dados.nome,
            sobrenome: response.dados.sobrenome || null,
            tipoUsuario: response.dados.tipoUsuario,
          });
          return true;
        }
        this.isAuthenticated.set(false);
        this.currentUser.set(null);
        return false;
      }),
      catchError(() => {
        this.isAuthenticated.set(false);
        this.currentUser.set(null);
        return of(false);
      }),
    );
  }
}
