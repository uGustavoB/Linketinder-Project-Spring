import { FormGroup } from '@angular/forms';

export function handleServerErrors(form: FormGroup, err: any): string[] {
  const mensagens: string[] = [];

  if (err.status === 422 && err.error?.erros) {
    err.error.erros.forEach((e: any) => {
      const control = form.get(e.campo);
      if (control) {
        control.setErrors({ serverError: e.mensagem });
        control.markAsTouched();
      }
      mensagens.push(e.mensagem);
    });
  } else if (err.error?.mensagem) {
    mensagens.push(err.error.mensagem);
  } else {
    mensagens.push('Erro desconhecido ao processar requisição.');
  }

  return mensagens;
}

export function getFormValidationErrors(form: FormGroup): string[] {
  const mensagens: string[] = [];
  Object.keys(form.controls).forEach((key) => {
    const controlErrors = form.get(key)?.errors;
    if (controlErrors) {
      const fieldName = key.replace(/([A-Z])/g, ' $1').replace(/^./, (str) => str.toUpperCase());

      if (controlErrors['required']) {
        mensagens.push(`O campo ${fieldName} é obrigatório.`);
      }
      if (controlErrors['minlength']) {
        mensagens.push(
          `O campo ${fieldName} precisa ter no mínimo ${controlErrors['minlength'].requiredLength} caracteres.`,
        );
      }
      if (controlErrors['email']) {
        mensagens.push(`O campo ${fieldName} deve ser um email válido.`);
      }
    }
  });
  return mensagens;
}
