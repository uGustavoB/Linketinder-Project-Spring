import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Competencia, VinculoCompetencia, VinculoRequest } from '../models/competencia.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class CompetenciaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.competenciasApiUrl}/competencias`;

  // 1. Catálogo Global
  getCompetencias(): Observable<ApiResponse<Competencia[]>> {
    return this.http.get<ApiResponse<Competencia[]>>(this.apiUrl);
  }

  criarCompetencia(nome: string): Observable<ApiResponse<Competencia>> {
    return this.http.post<ApiResponse<Competencia>>(this.apiUrl, { nome });
  }

  // 2. Vínculos do Candidato
  vincularCandidato(
    candidatoId: number,
    vinculos: VinculoRequest[],
  ): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.post<ApiResponse<VinculoCompetencia[]>>(
      `${this.apiUrl}/candidato/${candidatoId}`,
      vinculos,
    );
  }

  getCompetenciasCandidato(candidatoId: number): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.get<ApiResponse<VinculoCompetencia[]>>(
      `${this.apiUrl}/candidato/${candidatoId}`,
    );
  }

  removerVinculoCandidato(vinculoId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/candidato/vinculo/${vinculoId}`);
  }

  // 3. Vínculos da Empresa
  vincularEmpresa(
    empresaId: number,
    vinculos: VinculoRequest[],
  ): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.post<ApiResponse<VinculoCompetencia[]>>(
      `${this.apiUrl}/empresa/${empresaId}`,
      vinculos,
    );
  }

  getCompetenciasEmpresa(empresaId: number): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.get<ApiResponse<VinculoCompetencia[]>>(`${this.apiUrl}/empresa/${empresaId}`);
  }

  removerVinculoEmpresa(vinculoId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/empresa/vinculo/${vinculoId}`);
  }

  // 4. Vínculos da Vaga
  vincularVaga(
    vagaId: number,
    vinculos: VinculoRequest[],
  ): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.post<ApiResponse<VinculoCompetencia[]>>(
      `${this.apiUrl}/vaga/${vagaId}`,
      vinculos,
    );
  }

  getCompetenciasVaga(vagaId: number): Observable<ApiResponse<VinculoCompetencia[]>> {
    return this.http.get<ApiResponse<VinculoCompetencia[]>>(`${this.apiUrl}/vaga/${vagaId}`);
  }

  removerVinculoVaga(vinculoId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/vaga/vinculo/${vinculoId}`);
  }
}
