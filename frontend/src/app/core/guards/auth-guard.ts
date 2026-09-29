import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const router: Router = inject(Router);
  const token: string | null = localStorage.getItem('auth_token');

  if (!token) {
    return router.parseUrl('/login');
  }

  return true;
};
