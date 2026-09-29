import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardIdDirective } from '@/shared/core';
import { AuthService } from '../../core/services/auth.service';
import { toast } from 'ngx-sonner';

@Component({
  imports: [ZardCardImports, ZardButtonComponent, ZardIdDirective, ReactiveFormsModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  public isLoading = signal(false);

  public loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]]
  });

  protected onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: (res) => {
        toast.success(res.mensagem || 'Login realizado com sucesso!');
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading.set(false);
        const erroMsg = err.error?.mensagem || 'Erro ao fazer login. Tente novamente.';
        toast.error(erroMsg);
        console.error('Login error:', err);
      }
    });
  }

  protected onSignUp(): void {
    alert('Redirect to Sign Up');
  }
}
