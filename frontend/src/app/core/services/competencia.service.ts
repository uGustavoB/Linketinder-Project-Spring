import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Competencia } from '../models/competencia.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class CompetenciaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.competenciasApiUrl}/competencia`;

  getCompetencias(): Observable<ApiResponse<Competencia[]>> {
    return this.http.get<ApiResponse<Competencia[]>>(this.apiUrl);
  }
}
