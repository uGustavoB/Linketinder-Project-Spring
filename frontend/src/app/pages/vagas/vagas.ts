import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { ZardTableImports } from '@/shared/components/table/table.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardSelectImports } from '@/shared/components/select/select.imports';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { toast } from 'ngx-sonner';

import { VagaService } from '@/core/services/vaga.service';
import { AuthService } from '@/core/services/auth.service';
import { CompetenciaService } from '@/core/services/competencia.service';
import { Vaga } from '@/core/models/vaga.model';
import { Competencia, VinculoRequest } from '@/core/models/competencia.model';
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

  readonly vagas = signal<Vaga[]>([]);
  readonly isLoadingVagas = signal<boolean>(true);

  readonly isCreatingVaga = signal<boolean>(false);
  readonly isSaving = signal<boolean>(false);
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
        toast.error('Não foi possível carregar as competências disponíveis.');
      }
    });
  }

  protected onNewVaga(): void {
    this.isCreatingVaga.set(true);
    if (this.globalCompetencias().length === 0) {
      this.loadCompetencias();
    }
  }

  protected onCancelForm(): void {
    this.isCreatingVaga.set(false);
    this.vagaForm.reset();
    this.selectedCompetencias.set([]);
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
        console.error('Erro ao criar competência:', err);
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

    this.vagaService.createVaga(vagaDados).subscribe({
      next: (res: any) => {
        const vagaCriadaId = res.dados?.id || res.data?.id;
        const selecionadas = this.selectedCompetencias();

        if (selecionadas.length > 0 && vagaCriadaId) {
          const vinculos: VinculoRequest[] = selecionadas.map(id => ({
            competenciaId: parseInt(id),
            nivel: 'MEDIO'
          }));

          this.competenciaService.vincularVaga(vagaCriadaId, vinculos).subscribe({
            next: () => {
              toast.success('Vaga criada e competências vinculadas com sucesso!');
              this.isSaving.set(false);
              this.onCancelForm();
              this.fetchVagas(); // Atualiza a lista
            },
            error: (err: any) => {
              toast.error('Vaga criada, mas falhou ao vincular competências.');
              this.isSaving.set(false);
              this.onCancelForm();
              this.fetchVagas();
            }
          });
        } else {
          toast.success(res.mensagem || 'Vaga criada com sucesso!');
          this.isSaving.set(false);
          this.onCancelForm();
          this.fetchVagas();
        }
      },
      error: (err: any) => {
        this.isSaving.set(false);
        const mensagens = handleServerErrors(this.vagaForm, err);
        mensagens.forEach((msg) => toast.error(msg));
      },
    });
  }
}
