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
import { forkJoin, of, switchMap } from 'rxjs';

import { VagaService } from '@/core/services/vaga.service';
import { AuthService } from '@/core/services/auth.service';
import { CompetenciaService } from '@/core/services/competencia.service';
import { Vaga } from '@/core/models/vaga.model';
import { Competencia, VinculoRequest, VinculoCompetencia } from '@/core/models/competencia.model';
import { handleServerErrors, getFormValidationErrors } from '@/shared/utils/form-utils';

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

  // Listagem
  readonly vagas = signal<Vaga[]>([]);
  readonly isLoadingVagas = signal<boolean>(true);

  // Formulário
  readonly isFormOpen = signal<boolean>(false);
  readonly isSaving = signal<boolean>(false);
  readonly selectedVagaId = signal<number | null>(null);
  private existingVinculos = signal<VinculoCompetencia[]>([]);

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

  private fetchVagas(): void {
    this.isLoadingVagas.set(true);
    const user = this.authService.currentUser();
    const empresaId = user?.tipoUsuario === 'EMPRESA' ? user.id : undefined;
    
    this.vagaService.getVagas(empresaId).subscribe({
      next: (res: any) => {
        if (res.dados) {
          this.vagas.set(res.dados);
        }
        this.isLoadingVagas.set(false);
      },
      error: (err: any) => {
        console.error('Erro ao buscar vagas', err);
        toast.error('Erro ao buscar vagas.');
        this.isLoadingVagas.set(false);
      },
    });
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
      }
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
      nivel: vaga.nivel
    });
    
    this.isFormOpen.set(true);
    if (this.globalCompetencias().length === 0) {
      this.loadCompetencias();
    }

    // Carregar competências existentes
    this.competenciaService.getCompetenciasVaga(vaga.id).subscribe({
      next: (res: any) => {
        const vinculos = res.data || res.dados || [];
        this.existingVinculos.set(vinculos);
        const idsSelecionados = vinculos.map((v: VinculoCompetencia) => v.competencia.id.toString());
        this.selectedCompetencias.set(idsSelecionados);
      },
      error: () => toast.error('Falha ao carregar as competências desta vaga.')
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
          error: () => toast.error('Falha ao deletar a vaga.')
        });
      }
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
          this.globalCompetencias.update(list => [...list, nova]);
          this.selectedCompetencias.update(selected => [...selected, nova.id.toString()]);
          this.novaCompetenciaNome.set('');
          toast.success('Competência ' + nova.nome + ' adicionada com sucesso!');
        }
        this.isCreatingCompetencia.set(false);
      },
      error: (err: any) => {
        toast.error('Falha ao adicionar nova competência.');
        this.isCreatingCompetencia.set(false);
      }
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
    const desiredIds = this.selectedCompetencias().map(id => parseInt(id));
    const existing = this.existingVinculos();
    
    // Identifica vínculos para remover
    const toRemove = existing.filter(v => !desiredIds.includes(v.competencia.id));
    
    // Identifica novas competências para vincular
    const existingIds = existing.map(v => v.competencia.id);
    const toAddIds = desiredIds.filter(id => !existingIds.includes(id));

    const removeObs$ = toRemove.length > 0 
      ? forkJoin(toRemove.map(v => this.competenciaService.removerVinculoVaga(v.idVinculo)))
      : of([]);

    removeObs$.pipe(
      switchMap(() => {
        if (toAddIds.length > 0) {
          const vinculos: VinculoRequest[] = toAddIds.map(id => ({ competenciaId: id, nivel: 'MEDIO' }));
          return this.competenciaService.vincularVaga(vagaId, vinculos);
        }
        return of([]);
      })
    ).subscribe({
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
      }
    });
  }
}
