import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CandidatoService } from '@/core/services/candidato.service';
import { CandidatoProfile } from '@/core/models/candidato.model';
import { ZardTableImports } from '@/shared/components/table/table.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardDialogService } from '@/shared/components/dialog/dialog.service';
import { CandidatoCompetenciasDialog } from './components/candidato-competencias-dialog/candidato-competencias-dialog';

@Component({
  selector: 'app-candidatos',
  standalone: true,
  imports: [ZardTableImports, ZardButtonComponent],
  templateUrl: './candidatos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'block w-full overflow-x-auto',
  },
})
export class Candidatos implements OnInit {
  private readonly candidatoService = inject(CandidatoService);
  private readonly dialogService = inject(ZardDialogService);

  readonly candidatos = signal<CandidatoProfile[]>([]);
  readonly isLoading = signal<boolean>(true);

  ngOnInit(): void {
    this.candidatoService.getCandidatos().subscribe({
      next: (res: any) => {
        if (res.dados) {
          this.candidatos.set(res.dados);
        }
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Erro ao buscar candidatos', err);
        this.isLoading.set(false);
      },
    });
  }

  openCompetencias(candidato: CandidatoProfile): void {
    this.dialogService.create({
      zTitle: `Perfil do Candidato #${candidato.id}`,
      zDescription: 'Detalhes e competências do candidato.',
      zContent: CandidatoCompetenciasDialog,
      zData: { 
        candidatoId: candidato.id,
        descricao: candidato.descricao,
        estado: candidato.estado,
        pais: candidato.pais,
        formacoes: candidato.formacoes
      },
      zCancelText: 'Fechar',
      zOkText: null,
    });
  }
}
