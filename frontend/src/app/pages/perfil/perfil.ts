import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { PerfilService } from '@/core/services/perfil.service';
import { AuthService } from '@/core/services/auth.service';
import { ZardAlertDialogService } from '@/shared/components/alert-dialog/alert-dialog.service';
import { toast } from 'ngx-sonner';
import { handleServerErrors, getFormValidationErrors } from '@/shared/utils/form-utils';
import { CommonModule } from '@angular/common';

@Component({
  imports: [ZardCardImports, ZardButtonComponent, ReactiveFormsModule, CommonModule],
  selector: 'app-perfil',
  styleUrl: './perfil.css',
  templateUrl: './perfil.html',
})
export class Perfil implements OnInit {
  private fb = inject(FormBuilder);
  private perfilService = inject(PerfilService);
  private authService = inject(AuthService);
  private alertDialog = inject(ZardAlertDialogService);
  private router = inject(Router);

  public isLoading = signal(false);
  public isFetching = signal(true);
  public isDeleting = signal(false);

  public perfilForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    sobrenome: ['', Validators.required],
    dataNascimento: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    cpf: ['', Validators.required],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    pais: ['', Validators.required],
    estado: ['', Validators.required],
    cep: ['', Validators.required],
    descricao: [''],
  });

  ngOnInit(): void {
    this.perfilService.getPerfil().subscribe({
      next: (res: any) => {
        if (res.dados) {
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
    if (this.perfilForm.invalid) {
      this.perfilForm.markAllAsTouched();
      const erros = getFormValidationErrors(this.perfilForm);
      erros.forEach((e: string) => toast.error(e));
      return;
    }

    const user = this.authService.currentUser();
    if (!user || !user.id) {
      toast.error('Sessão inválida, faça login novamente.');
      return;
    }

    this.isLoading.set(true);
    const dados = this.perfilForm.getRawValue();

    this.perfilService.updateCandidato(user.id, dados).subscribe({
      next: (res: any) => {
        toast.success(res.mensagem || 'Perfil atualizado com sucesso!');
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.isLoading.set(false);
        const mensagens = handleServerErrors(this.perfilForm, err);
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
        this.perfilService.deleteCandidato(user.id).subscribe({
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
}
