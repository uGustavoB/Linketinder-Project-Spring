import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { ZardTableImports } from '@/shared/components/table/table.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardSelectImports } from '@/shared/components/select/select.imports';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardAlertDialogService } from '@/shared/components/alert-dialog/alert-dialog.service';
import { toast } from 'ngx-sonner';
import { catchError, forkJoin, of, switchMap } from 'rxjs';

import { VagaService } from '@/core/services/vaga.service';
import { AuthService } from '@/core/services/auth.service';
import { CompetenciaService } from '@/core/services/competencia.service';
import { Vaga } from '@/core/models/vaga.model';
import { CandidatoProfile } from '@/core/models/candidato.model';
import { Competencia, VinculoRequest, VinculoCompetencia } from '@/core/models/competencia.model';
import { MatchEmpresa } from '@/core/models/match.model';
import { handleServerErrors, getFormValidationErrors } from '@/shared/utils/form-utils';
import { ZardDialogService } from '@/shared/components/dialog/dialog.service';
import { CandidatoCompetenciasDialog } from '../candidatos/components/candidato-competencias-dialog/candidato-competencias-dialog';

@Component({
  selector: 'app-vagas',
  standalone: true,
  imports: [ZardTableImports, ZardButtonComponent, ZardSelectImports, ZardCardImports, CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './vagas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'block w-full overflow-x-auto',
  },
})
export class Vagas implements OnInit {
  private fb = inject(FormBuilder);
  private vagaService = inject(VagaService);
  public authService = inject(AuthService);
  private competenciaService = inject(CompetenciaService);
  private router = inject(Router);
  private alertDialog = inject(ZardAlertDialogService);
  private dialogService = inject(ZardDialogService);

  // Listagem de vagas
  readonly vagas = signal<Vaga[]>([]);
  readonly isLoadingVagas = signal<boolean>(true);
  readonly filterMode = signal<'TODAS' | 'SUGERIDAS'>('SUGERIDAS');

  // Formulário
  readonly isFormOpen = signal<boolean>(false);
  readonly isSaving = signal<boolean>(false);
  readonly selectedVagaId = signal<number | null>(null);
  private existingVinculos = signal<VinculoCompetencia[]>([]);

  // Visualização de Candidatos da Vaga
  readonly viewingCandidatosVagaId = signal<number | null>(null);
  readonly viewingCandidatosMode = signal<'INTERESSADOS' | 'SUGERIDOS' | 'MATCHES'>('INTERESSADOS');
  readonly candidatosVaga = signal<any[]>([]);
  readonly matchesVaga = signal<MatchEmpresa[]>([]);
  readonly isLoadingCandidatos = signal<boolean>(false);

  // Competências
  readonly globalCompetencias = signal<Competencia[]>([]);
  readonly selectedCompetencias = signal<string[]>([]);
  readonly novaCompetenciaNome = signal<string>('');
  readonly isCreatingCompetencia = signal<boolean>(false);

  public vagaForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descricao: ['', Validators.required],
    local: ['', Validators.required],
    nivel: ['', Validators.required],
  });

  ngOnInit(): void {
    this.fetchVagas();
  }

  public onViewCandidatos(vaga: Vaga): void {
    this.viewingCandidatosVagaId.set(vaga.id);
    this.viewingCandidatosMode.set('INTERESSADOS');
    this.fetchCandidatosVaga();
  }

  public fetchCandidatosVaga(): void {
    const vagaId = this.viewingCandidatosVagaId();
    if (!vagaId) return;

    this.isLoadingCandidatos.set(true);
    const mode = this.viewingCandidatosMode();

    if (mode === 'MATCHES') {
      this.vagaService.getMatchesDaVaga(vagaId).subscribe({
        next: (res: any) => {
          const data = res.dados || res.data || [];
          this.matchesVaga.set(data);
          this.isLoadingCandidatos.set(false);
        },
        error: () => {
          toast.error('Erro ao buscar matches da vaga.');
          this.isLoadingCandidatos.set(false);
        },
      });
      return;
    }

    const request$ = mode === 'SUGERIDOS'
      ? this.vagaService.getCandidatosSugeridos(vagaId)
      : this.vagaService.getCandidatosInteressados(vagaId);

    request$.subscribe({
      next: (res: any) => {
        const data = res.dados || res.data || [];
        this.candidatosVaga.set(data);
        this.isLoadingCandidatos.set(false);
      },
      error: () => {
        toast.error('Erro ao buscar candidatos.');
        this.isLoadingCandidatos.set(false);
      },
    });
  }

  public toggleCandidatosMode(mode: 'INTERESSADOS' | 'SUGERIDOS' | 'MATCHES'): void {
    if (this.viewingCandidatosMode() !== mode) {
      this.viewingCandidatosMode.set(mode);
      this.fetchCandidatosVaga();
    }
  }

  public curtirCandidato(candidato: CandidatoProfile): void {
    const vagaId = this.viewingCandidatosVagaId();
    if (!vagaId) return;

    this.vagaService.curtirCandidato(vagaId, candidato.id).subscribe({
      next: (res: any) => {
        const data = res.dados || res.data;
        if (data && data.match) {
          toast.success('Deu Match! 🎉 O contato do candidato foi liberado na aba Matches.');
        } else {
          toast.success('Interesse registrado no candidato!');
        }
        this.candidatosVaga.update((list) => list.filter((c) => c.id !== candidato.id));
      },
      error: () => toast.error('Erro ao curtir candidato.'),
    });
  }

  public curtirVaga(vaga: Vaga): void {
    this.vagaService.curtirVaga(vaga.id).subscribe({
      next: (res: any) => {
        const data = res.dados || res.data;
        if (data && data.match) {
          toast.success('Deu Match! 🎉 Contato da empresa disponível em Meus Matches.');
        } else {
          toast.success('Interesse registrado na vaga!');
        }
        this.vagas.update((list) => list.filter((v) => v.id !== vaga.id));
      },
      error: () => toast.error('Erro ao curtir vaga.'),
    });
  }

  public onBackToVagas(): void {
    this.viewingCandidatosVagaId.set(null);
    this.candidatosVaga.set([]);
    this.matchesVaga.set([]);
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

  public fetchVagas(): void {
    this.isLoadingVagas.set(true);
    const user = this.authService.currentUser();
    const isEmpresa = user?.tipoUsuario === 'EMPRESA';
    const empresaId = isEmpresa ? user.id : undefined;

    let request$ = this.vagaService.getVagas(empresaId);

    if (!isEmpresa && this.filterMode() === 'SUGERIDAS') {
      request$ = this.vagaService.getVagasSugeridasCandidato();
    }

    request$.subscribe({
      next: (res: any) => {
        const vagasData: Vaga[] = res.dados || res.data || [];
        if (vagasData.length === 0) {
          this.vagas.set([]);
          this.isLoadingVagas.set(false);
          return;
        }

        // Busca as competências de cada vaga via ms-competencia de forma resiliente
        const compRequests$ = vagasData.map((vaga) =>
          this.competenciaService.getCompetenciasVaga(vaga.id).pipe(
            catchError(() => of(null))
          )
        );

        forkJoin(compRequests$).subscribe({
          next: (compResults) => {
            compResults.forEach((compRes, index) => {
              const vinculos = compRes?.dados || compRes?.data || [];
              vagasData[index].competencias = vinculos.map((v: VinculoCompetencia) => v.competencia);
            });
            this.vagas.set(vagasData);
            this.isLoadingVagas.set(false);
          },
          error: () => {
            this.vagas.set(vagasData);
            this.isLoadingVagas.set(false);
          },
        });
      },
      error: (err: any) => {
        console.error('Erro ao buscar vagas', err);
        toast.error('Erro ao buscar vagas.');
        this.isLoadingVagas.set(false);
      },
    });
  }

  public toggleFilterMode(mode: 'TODAS' | 'SUGERIDAS'): void {
    if (this.filterMode() !== mode) {
      this.filterMode.set(mode);
      this.fetchVagas();
    }
  }

  private loadCompetencias(): void {
    this.competenciaService.getCompetencias().subscribe({
      next: (res: any) => {
        const data = res.data || res.dados;
        if (data) {
          this.globalCompetencias.set(data);
        }
      },
      error: (err: any) => {
        console.error('Erro ao buscar competências:', err);
      },
    });
  }

  protected onNewVaga(): void {
    this.selectedVagaId.set(null);
    this.existingVinculos.set([]);
    this.isFormOpen.set(true);
    if (this.globalCompetencias().length === 0) {
      this.loadCompetencias();
    }
  }

  protected onEditVaga(vaga: Vaga): void {
    this.selectedVagaId.set(vaga.id);
    this.vagaForm.patchValue({
      nome: vaga.nome,
      descricao: vaga.descricao,
      local: vaga.local,
      nivel: vaga.nivel,
    });

    this.isFormOpen.set(true);
    if (this.globalCompetencias().length === 0) {
      this.loadCompetencias();
    }

    this.competenciaService.getCompetenciasVaga(vaga.id).subscribe({
      next: (res: any) => {
        const vinculos = res.data || res.dados || [];
        this.existingVinculos.set(vinculos);
        const idsSelecionados = vinculos.map((v: VinculoCompetencia) => v.competencia.id.toString());
        this.selectedCompetencias.set(idsSelecionados);
      },
      error: () => toast.error('Falha ao carregar as competências desta vaga.'),
    });
  }

  protected onDeleteVaga(vaga: Vaga): void {
    this.alertDialog.confirm({
      zTitle: 'Deletar Vaga',
      zDescription: `Tem certeza que deseja deletar a vaga "${vaga.nome}"? Esta ação não pode ser desfeita.`,
      zOkText: 'Sim, Deletar',
      zCancelText: 'Cancelar',
      zOkDestructive: true,
      zOnOk: () => {
        this.vagaService.deleteVaga(vaga.id).subscribe({
          next: () => {
            toast.success('Vaga deletada com sucesso.');
            this.fetchVagas();
          },
          error: () => toast.error('Falha ao deletar a vaga.'),
        });
      },
    });
  }

  protected onCancelForm(): void {
    this.isFormOpen.set(false);
    this.selectedVagaId.set(null);
    this.vagaForm.reset();
    this.selectedCompetencias.set([]);
    this.existingVinculos.set([]);
  }

  protected onAddCompetencia(): void {
    const nome = this.novaCompetenciaNome().trim();
    if (!nome) return;

    this.isCreatingCompetencia.set(true);
    this.competenciaService.criarCompetencia(nome).subscribe({
      next: (res: any) => {
        const nova = res.data || res.dados;
        if (nova) {
          this.globalCompetencias.update((list) => [...list, nova]);
          this.selectedCompetencias.update((selected) => [...selected, nova.id.toString()]);
          this.novaCompetenciaNome.set('');
          toast.success('Competência ' + nova.nome + ' adicionada com sucesso!');
        }
        this.isCreatingCompetencia.set(false);
      },
      error: () => {
        toast.error('Falha ao adicionar nova competência.');
        this.isCreatingCompetencia.set(false);
      },
    });
  }

  protected onSubmit(): void {
    if (this.vagaForm.invalid) {
      this.vagaForm.markAllAsTouched();
      const erros = getFormValidationErrors(this.vagaForm);
      erros.forEach((e: string) => toast.error(e));
      return;
    }

    this.isSaving.set(true);
    const vagaDados = this.vagaForm.getRawValue();
    const isEdit = !!this.selectedVagaId();
    const request$ = isEdit
      ? this.vagaService.updateVaga(this.selectedVagaId()!, vagaDados)
      : this.vagaService.createVaga(vagaDados);

    request$.subscribe({
      next: (res: any) => {
        const vagaCriadaId = res.dados?.id || res.data?.id || this.selectedVagaId();
        this.syncCompetencias(vagaCriadaId);
      },
      error: (err: any) => {
        this.isSaving.set(false);
        const mensagens = handleServerErrors(this.vagaForm, err);
        mensagens.forEach((msg) => toast.error(msg));
      },
    });
  }

  private syncCompetencias(vagaId: number): void {
    const desiredIds = this.selectedCompetencias().map((id) => parseInt(id));
    const existing = this.existingVinculos();

    const toRemove = existing.filter((v) => !desiredIds.includes(v.competencia.id));
    const existingIds = existing.map((v) => v.competencia.id);
    const toAddIds = desiredIds.filter((id) => !existingIds.includes(id));

    const removeObs$ =
      toRemove.length > 0
        ? forkJoin(toRemove.map((v) => this.competenciaService.removerVinculoVaga(v.idVinculo)))
        : of([]);

    removeObs$
      .pipe(
        switchMap(() => {
          if (toAddIds.length > 0) {
            const vinculos: VinculoRequest[] = toAddIds.map((id) => ({ competenciaId: id }));
            return this.competenciaService.vincularVaga(vagaId, vinculos);
          }
          return of([]);
        }),
      )
      .subscribe({
        next: () => {
          toast.success(this.selectedVagaId() ? 'Vaga atualizada com sucesso!' : 'Vaga criada com sucesso!');
          this.isSaving.set(false);
          this.onCancelForm();
          this.fetchVagas();
        },
        error: () => {
          toast.error('Vaga salva, mas falhou ao sincronizar algumas competências.');
          this.isSaving.set(false);
          this.onCancelForm();
          this.fetchVagas();
        },
      });
  }
}
