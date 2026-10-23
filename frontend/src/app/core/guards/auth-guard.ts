import { inject } from '@angular/core';
import { Router, type CanActivateFn } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  return authService.checkSession().pipe(
    map((isAuth) => {
      if (isAuth) {
        return true;
      }
      return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
    }),
  );
};
