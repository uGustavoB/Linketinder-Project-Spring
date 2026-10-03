import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CompetenciaService } from '@/core/services/competencia.service';
import { VinculoCompetencia } from '@/core/models/competencia.model';
import { injectDialogData } from '@/shared/components/dialog/dialog.service';

export interface CandidatoDialogData {
  candidatoId: number;
  descricao?: string;
  estado?: string;
  pais?: string;
}

@Component({
  selector: 'app-candidato-competencias-dialog',
  standalone: true,
  imports: [],
  templateUrl: './candidato-competencias-dialog.html',
  styleUrl: './candidato-competencias-dialog.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CandidatoCompetenciasDialog implements OnInit {
  private readonly competenciaService = inject(CompetenciaService);
  public readonly dialogData = injectDialogData<CandidatoDialogData>();

  readonly competencias = signal<VinculoCompetencia[]>([]);
  readonly isLoading = signal<boolean>(true);

  ngOnInit() {
    this.competenciaService.getCompetenciasCandidato(this.dialogData.candidatoId).subscribe({
      next: (res) => {
        if (res.dados) {
          this.competencias.set(res.dados);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar competências do candidato', err);
        this.isLoading.set(false);
      },
    });
  }

  formatLocation(estado?: string, pais?: string): string {
    if (estado && pais) return estado + ' - ' + pais;
    if (estado) return estado;
    if (pais) return pais;
    return 'Localização não informada';
  }

  formatNivel(nivel: string): string {
    const niveis: Record<string, string> = {
      'BASICO': 'Básico',
      'MEDIO': 'Médio',
      'AVANCADO': 'Avançado',
    };
    return niveis[nivel] || nivel;
  }
}
