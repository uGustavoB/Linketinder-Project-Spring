import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { MatchEmpresa } from '../models/match.model';

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/empresa`;

  getMatchesDaEmpresa(): Observable<ApiResponse<MatchEmpresa[]>> {
    return this.http.get<ApiResponse<MatchEmpresa[]>>(`${this.apiUrl}/matches`);
  }

  updateEmpresa(id: number, dados: any): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}`, dados);
  }

  deleteEmpresa(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
  }
}
