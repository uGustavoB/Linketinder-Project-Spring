import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, FormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { PerfilService } from '@/core/services/perfil.service';
import { CandidatoService } from '@/core/services/candidato.service';
import { EmpresaService } from '@/core/services/empresa.service';
import { AuthService } from '@/core/services/auth.service';
import { ZardAlertDialogService } from '@/shared/components/alert-dialog/alert-dialog.service';
import { toast } from 'ngx-sonner';
import { handleServerErrors, getFormValidationErrors } from '@/shared/utils/form-utils';
import { CommonModule } from '@angular/common';
import { CompetenciaService } from '@/core/services/competencia.service';
import { Competencia, VinculoCompetencia, VinculoRequest } from '@/core/models/competencia.model';
import { Formacao } from '@/core/models/candidato.model';

@Component({
  imports: [ZardCardImports, ZardButtonComponent, ReactiveFormsModule, FormsModule, CommonModule],
  selector: 'app-perfil',
  styleUrl: './perfil.css',
  templateUrl: './perfil.html',
})
export class Perfil implements OnInit {
  private fb = inject(FormBuilder);
  private perfilService = inject(PerfilService);
  private candidatoService = inject(CandidatoService);
  private empresaService = inject(EmpresaService);
  private authService = inject(AuthService);
  private alertDialog = inject(ZardAlertDialogService);
  private router = inject(Router);
  private competenciaService = inject(CompetenciaService);

  public isLoading = signal(false);
  public isFetching = signal(true);
  public isDeleting = signal(false);
  public userType = signal<'CANDIDATO' | 'EMPRESA'>('CANDIDATO');

  // Competências
  public competenciasCatalogo = signal<Competencia[]>([]);
  public competenciasCandidato = signal<VinculoCompetencia[]>([]);
  public isAddingCompetencia = signal(false);
  public novaCompetenciaNome = signal('');
  public isCreatingCompetencia = signal(false);

  // Formações Acadêmicas
  public formacoesCandidato = signal<Formacao[]>([]);
  public isAddingFormacao = signal(false);

  public competenciaForm = this.fb.nonNullable.group({
    competenciaId: [0, [Validators.required, Validators.min(1)]],
    nivel: ['BASICO', Validators.required],
  });

  public formacaoForm = this.fb.nonNullable.group({
    instituicao: ['', Validators.required],
    curso: ['', Validators.required],
    nivel: ['Graduação', Validators.required],
    status: ['Em Andamento', Validators.required],
    dataInicio: [''],
    dataConclusao: [''],
  });

  public perfilForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    sobrenome: ['', Validators.required],
    dataNascimento: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    cpf: ['', Validators.required],
    senha: ['', [Validators.minLength(6)]],
    pais: ['', Validators.required],
    estado: ['', Validators.required],
    cep: ['', Validators.required],
    descricao: [''],
  });

  public empresaForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cnpj: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.minLength(6)]],
    pais: ['', Validators.required],
    estado: ['', Validators.required],
    cep: ['', Validators.required],
    descricao: [''],
  });

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user?.tipoUsuario === 'EMPRESA') {
      this.userType.set('EMPRESA');
    } else if (user?.id) {
      this.loadCompetencias(user.id);
    }

    this.perfilService.getPerfil().subscribe({
      next: (res: any) => {
        if (res.dados) {
          if (res.dados.tipoUsuario === 'EMPRESA' || this.userType() === 'EMPRESA') {
            this.empresaForm.patchValue({
              nome: res.dados.nome,
              cnpj: res.dados.cnpj,
              email: res.dados.email,
              pais: res.dados.pais,
              estado: res.dados.estado,
              cep: res.dados.cep,
              descricao: res.dados.descricao,
            });
          } else {
            this.perfilForm.patchValue({
              nome: res.dados.nome,
              sobrenome: res.dados.sobrenome,
              dataNascimento: res.dados.dataNascimento,
              email: res.dados.email,
              cpf: res.dados.cpf,
              pais: res.dados.pais,
              estado: res.dados.estado,
              cep: res.dados.cep,
              descricao: res.dados.descricao,
            });
            if (res.dados.formacoes) {
              this.formacoesCandidato.set(res.dados.formacoes);
            }
          }
        }
        this.isFetching.set(false);
      },
      error: (err: any) => {
        toast.error('Erro ao buscar dados do perfil.');
        console.error(err);
        this.isFetching.set(false);
      },
    });
  }

  protected onSubmit(): void {
    const isCandidato = this.userType() === 'CANDIDATO';
    const form = isCandidato ? this.perfilForm : this.empresaForm;

    if (form.invalid) {
      form.markAllAsTouched();
      const erros = getFormValidationErrors(form);
      erros.forEach((e: string) => toast.error(e));
      return;
    }

    const user = this.authService.currentUser();
    if (!user || !user.id) {
      toast.error('Sessão inválida, faça login novamente.');
      return;
    }

    this.isLoading.set(true);
    const rawDados = form.getRawValue();
    const dados: Record<string, any> = { ...rawDados };

    // Se senha estiver em branco, remove para não alterar no backend
    if (!dados['senha'] || dados['senha'].trim() === '') {
      delete dados['senha'];
    }

    const request$ = isCandidato
      ? this.candidatoService.updateCandidato(user.id, dados)
      : this.empresaService.updateEmpresa(user.id, dados);

    request$.subscribe({
      next: (res: any) => {
        toast.success(res.mensagem || 'Perfil atualizado com sucesso!');
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.isLoading.set(false);
        const mensagens = handleServerErrors(form, err);
        mensagens.forEach((msg) => toast.error(msg));
      },
    });
  }

  protected onDelete(): void {
    this.alertDialog.confirm({
      zTitle: 'Deletar Conta',
      zDescription:
        'Você tem certeza que deseja deletar permanentemente sua conta? Essa ação não poderá ser desfeita e todos os seus dados serão perdidos.',
      zOkText: 'Sim, deletar conta',
      zCancelText: 'Cancelar',
      zOkDestructive: true,
      zOnOk: () => {
        const user = this.authService.currentUser();
        if (!user || !user.id) return;

        this.isDeleting.set(true);
        const isCandidato = this.userType() === 'CANDIDATO';
        const request$ = isCandidato
          ? this.candidatoService.deleteCandidato(user.id)
          : this.empresaService.deleteEmpresa(user.id);

        request$.subscribe({
          next: (res: any) => {
            toast.success(res.mensagem || 'Conta deletada com sucesso.');
            this.authService.isAuthenticated.set(false);
            this.authService.currentUser.set(null);
            this.router.navigate(['/login']);
          },
          error: (err: any) => {
            this.isDeleting.set(false);
            const erroMsg = err.error?.mensagem || 'Erro ao deletar conta.';
            toast.error(erroMsg);
          },
        });
      },
    });
  }

  private loadCompetencias(candidatoId: number) {
    this.competenciaService.getCompetencias().subscribe((res) => {
      if (res.dados) this.competenciasCatalogo.set(res.dados);
    });
    this.competenciaService.getCompetenciasCandidato(candidatoId).subscribe((res) => {
      if (res.dados) this.competenciasCandidato.set(res.dados);
    });
  }

  protected onAddNovaCompetencia(): void {
    const nome = this.novaCompetenciaNome().trim();
    if (!nome) return;

    this.isCreatingCompetencia.set(true);
    this.competenciaService.criarCompetencia(nome).subscribe({
      next: (res: any) => {
        const nova = res.data || res.dados;
        if (nova) {
          this.competenciasCatalogo.update((list) => [...list, nova]);
          this.competenciaForm.patchValue({ competenciaId: nova.id });
          this.novaCompetenciaNome.set('');
          toast.success(`Competência "${nova.nome}" cadastrada no catálogo!`);
        }
        this.isCreatingCompetencia.set(false);
      },
      error: () => {
        toast.error('Erro ao cadastrar competência.');
        this.isCreatingCompetencia.set(false);
      },
    });
  }

  protected addCompetencia() {
    if (this.competenciaForm.invalid) return;
    const user = this.authService.currentUser();
    if (!user || !user.id) return;

    this.isAddingCompetencia.set(true);
    const val = this.competenciaForm.getRawValue();
    const compId = Number(val.competenciaId);

    const request: VinculoRequest[] = [
      {
        competenciaId: compId,
        nivel: val.nivel,
      },
    ];

    this.competenciaService.vincularCandidato(user.id, request).subscribe({
      next: () => {
        this.loadCompetencias(user.id!);
        this.competenciaForm.reset({ competenciaId: 0, nivel: 'BASICO' });
        this.isAddingCompetencia.set(false);
        toast.success('Competência vinculada com sucesso!');
      },
      error: (err) => {
        this.isAddingCompetencia.set(false);
        const mensagens = handleServerErrors(this.competenciaForm, err);
        mensagens.forEach((msg) => toast.error(msg));
      },
    });
  }

  protected removeCompetencia(vinculoId: number) {
    const user = this.authService.currentUser();
    if (!user || !user.id) return;

    this.competenciaService.removerVinculoCandidato(vinculoId).subscribe({
      next: () => {
        this.loadCompetencias(user.id!);
        toast.success('Competência removida!');
      },
      error: (err) => {
        toast.error(err.error?.mensagem || 'Erro ao remover competência.');
      },
    });
  }

  protected addFormacao(): void {
    if (this.formacaoForm.invalid) {
      this.formacaoForm.markAllAsTouched();
      return;
    }

    this.isAddingFormacao.set(true);
    const val = this.formacaoForm.getRawValue();
    const payload = {
      instituicao: val.instituicao,
      curso: val.curso,
      nivel: val.nivel,
      status: val.status,
      dataInicio: val.dataInicio || null,
      dataConclusao: val.dataConclusao || null,
    };

    this.candidatoService.adicionarFormacao(payload).subscribe({
      next: (res) => {
        const nova = res.dados;
        if (nova) {
          this.formacoesCandidato.update((list) => [...list, nova]);
        }
        this.formacaoForm.reset({
          instituicao: '',
          curso: '',
          nivel: 'Graduação',
          status: 'Em Andamento',
          dataInicio: '',
          dataConclusao: '',
        });
        this.isAddingFormacao.set(false);
        toast.success('Formação acadêmica adicionada!');
      },
      error: (err) => {
        this.isAddingFormacao.set(false);
        toast.error(err.error?.mensagem || 'Erro ao adicionar formação.');
      },
    });
  }

  protected removeFormacao(id: number): void {
    this.candidatoService.removerFormacao(id).subscribe({
      next: () => {
        this.formacoesCandidato.update((list) => list.filter((f) => f.id !== id));
        toast.success('Formação removida!');
      },
      error: (err) => {
        toast.error(err.error?.mensagem || 'Erro ao remover formação.');
      },
    });
  }

  protected formatNivel(nivel: string): string {
    const niveis: Record<string, string> = {
      BASICO: 'Básico',
      MEDIO: 'Médio',
      AVANCADO: 'Avançado',
    };
    return niveis[nivel] || nivel;
  }
}
