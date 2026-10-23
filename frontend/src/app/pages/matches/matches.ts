import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ZardTableImports } from '@/shared/components/table/table.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardDialogService } from '@/shared/components/dialog/dialog.service';
import { toast } from 'ngx-sonner';
import { forkJoin, of, catchError } from 'rxjs';

import { AuthService } from '@/core/services/auth.service';
import { VagaService } from '@/core/services/vaga.service';
import { CandidatoService } from '@/core/services/candidato.service';
import { MatchCandidato, MatchEmpresa } from '@/core/models/match.model';
import { CandidatoProfile } from '@/core/models/candidato.model';
import { CandidatoCompetenciasDialog } from '../candidatos/components/candidato-competencias-dialog/candidato-competencias-dialog';

@Component({
  selector: 'app-matches',
  standalone: true,
  imports: [ZardTableImports, ZardButtonComponent, ZardCardImports, CommonModule],
  templateUrl: './matches.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'block w-full overflow-x-auto',
  },
})
export class Matches implements OnInit {
  public authService = inject(AuthService);
  private vagaService = inject(VagaService);
  private candidatoService = inject(CandidatoService);
  private dialogService = inject(ZardDialogService);

  readonly isLoading = signal<boolean>(true);
  readonly matchesCandidato = signal<MatchCandidato[]>([]);
  readonly matchesEmpresa = signal<MatchEmpresa[]>([]);

  ngOnInit(): void {
    this.fetchMatches();
  }

  public fetchMatches(): void {
    this.isLoading.set(true);
    const user = this.authService.currentUser();

    if (user?.tipoUsuario === 'CANDIDATO') {
      this.candidatoService.getMatchesDoCandidato().subscribe({
        next: (res) => {
          const dados = res.dados || [];
          this.matchesCandidato.set(dados);
          this.isLoading.set(false);
        },
        error: () => {
          toast.error('Erro ao buscar seus matches.');
          this.isLoading.set(false);
        },
      });
    } else if (user?.tipoUsuario === 'EMPRESA') {
      const empresaId = user.id;
      this.vagaService.getVagas(empresaId).subscribe({
        next: (res) => {
          const vagas = res.dados || [];
          if (vagas.length === 0) {
            this.matchesEmpresa.set([]);
            this.isLoading.set(false);
            return;
          }

          const requests$ = vagas.map((v) =>
            this.vagaService.getMatchesDaVaga(v.id).pipe(
              catchError(() => of({ dados: [] as MatchEmpresa[] }))
            )
          );

          forkJoin(requests$).subscribe({
            next: (results) => {
              const allMatches: MatchEmpresa[] = [];
              results.forEach((r, index) => {
                const list = r?.dados || [];
                const vaga = vagas[index];
                list.forEach((item) => {
                  allMatches.push({
                    ...item,
                    vagaId: vaga.id,
                    vagaNome: vaga.nome,
                  });
                });
              });
              this.matchesEmpresa.set(allMatches);
              this.isLoading.set(false);
            },
            error: () => {
              toast.error('Erro ao buscar matches das vagas.');
              this.isLoading.set(false);
            },
          });
        },
        error: () => {
          toast.error('Erro ao carregar vagas da empresa.');
          this.isLoading.set(false);
        },
      });
    } else {
      this.isLoading.set(false);
    }
  }

  public openCompetencias(candidato: CandidatoProfile): void {
    this.dialogService.create({
      zTitle: `Perfil do Candidato #${candidato.id}`,
      zDescription: 'Detalhes e competências do candidato.',
      zContent: CandidatoCompetenciasDialog,
      zData: {
        candidatoId: candidato.id,
        descricao: candidato.descricao,
        estado: candidato.estado,
        pais: candidato.pais,
        formacoes: candidato.formacoes,
      },
      zCancelText: 'Fechar',
      zOkText: null,
    });
  }
}
