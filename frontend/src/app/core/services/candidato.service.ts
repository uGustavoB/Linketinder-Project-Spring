import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CandidatoProfile, Formacao } from '../models/candidato.model';
import { ApiResponse } from '../models/api-response.model';
import { MatchCandidato } from '../models/match.model';

@Injectable({
  providedIn: 'root'
})
export class CandidatoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/candidato`;

  getCandidatos(): Observable<ApiResponse<CandidatoProfile[]>> {
    return this.http.get<ApiResponse<CandidatoProfile[]>>(this.apiUrl);
  }

  getMatchesDoCandidato(): Observable<ApiResponse<MatchCandidato[]>> {
    return this.http.get<ApiResponse<MatchCandidato[]>>(`${this.apiUrl}/matches`);
  }

  adicionarFormacao(dados: any): Observable<ApiResponse<Formacao>> {
    return this.http.post<ApiResponse<Formacao>>(`${this.apiUrl}/formacoes`, dados);
  }

  removerFormacao(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/formacoes/${id}`);
  }

  updateCandidato(id: number, dados: any): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.apiUrl}/${id}`, dados);
  }

  deleteCandidato(id: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${this.apiUrl}/${id}`);
  }
}
