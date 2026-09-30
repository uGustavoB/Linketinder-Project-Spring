import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Vaga } from '../models/vaga.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class VagaService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/vaga`;

  getVagas(): Observable<ApiResponse<Vaga[]>> {
    return this.http.get<ApiResponse<Vaga[]>>(this.apiUrl);
  }
}
