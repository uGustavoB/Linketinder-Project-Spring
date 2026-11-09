import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const empresaGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    if (authService.currentUser()?.tipoUsuario === 'EMPRESA') {
      return true;
    }
    return router.createUrlTree(['/dashboard']);
  }

  return authService.checkSession().pipe(
    map((isAuth) => {
      if (isAuth && authService.currentUser()?.tipoUsuario === 'EMPRESA') {
        return true;
      }
      return router.createUrlTree(['/dashboard']);
    }),
  );
};
