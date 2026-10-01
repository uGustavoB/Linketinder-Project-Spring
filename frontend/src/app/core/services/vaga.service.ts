import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Vaga } from '../models/vaga.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class VagaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/vaga`;

  getVagas(empresaId?: number): Observable<ApiResponse<Vaga[]>> {
    let params = new HttpParams();
    if (empresaId) {
      params = params.set('empresaId', empresaId.toString());
    }
    return this.http.get<ApiResponse<Vaga[]>>(this.apiUrl, { params });
  }

  createVaga(vaga: any): Observable<ApiResponse<Vaga>> {
    return this.http.post<ApiResponse<Vaga>>(this.apiUrl, vaga);
  }

  updateVaga(id: number, vaga: any): Observable<ApiResponse<Vaga>> {
    return this.http.put<ApiResponse<Vaga>>(`${this.apiUrl}/${id}`, vaga);
  }

  deleteVaga(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`);
  }
}
