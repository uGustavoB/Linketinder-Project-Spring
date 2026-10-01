import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardIdDirective } from '@/shared/core';
import { RegisterService } from '../../core/services/register.service';
import { toast } from 'ngx-sonner';
import { handleServerErrors, getFormValidationErrors } from '@/shared/utils/form-utils';
import { CommonModule } from '@angular/common';

@Component({
  imports: [
    ZardCardImports,
    ZardButtonComponent,
    ReactiveFormsModule,
    CommonModule,
  ],
  selector: 'app-register',
  styleUrl: './register.css',
  templateUrl: './register.html',
})
export class Register {
  private fb = inject(FormBuilder);
  private registerService = inject(RegisterService);
  private router = inject(Router);

  public userType = signal<'CANDIDATO' | 'EMPRESA'>('CANDIDATO');
  public isLoading = signal(false);

  public candidatoForm = this.fb.nonNullable.group({
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

  public empresaForm = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    cnpj: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]],
    pais: ['', Validators.required],
    estado: ['', Validators.required],
    cep: ['', Validators.required],
    descricao: [''],
  });

  protected setType(type: 'CANDIDATO' | 'EMPRESA') {
    this.userType.set(type);
    this.candidatoForm.reset();
    this.empresaForm.reset();
  }

  protected onSubmit(): void {
    const isCandidato = this.userType() === 'CANDIDATO';
    const form = isCandidato ? this.candidatoForm : this.empresaForm;

    if (form.invalid) {
      form.markAllAsTouched();
      const erros = getFormValidationErrors(form);
      erros.forEach((e: string) => toast.error(e));
      return;
    }

    this.isLoading.set(true);
    const dados = form.getRawValue();

    const request$ = isCandidato
      ? this.registerService.registerCandidato(dados)
      : this.registerService.registerEmpresa(dados);

    request$.subscribe({
      next: (res: any) => {
        toast.success(res.mensagem || 'Cadastro realizado com sucesso!');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.isLoading.set(false);
        const mensagens = handleServerErrors(form, err);
        mensagens.forEach((msg) => toast.error(msg));
      },
    });
  }

  protected onLogin(): void {
    this.router.navigate(['/login']);
  }
}
