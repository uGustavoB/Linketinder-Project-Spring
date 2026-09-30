import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register').then((m) => m.Register),
  },
  {
    path: '',
    loadComponent: () => import('./layouts/main-layout/main-layout').then((m) => m.MainLayout),
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'vagas',
        loadComponent: () => import('./pages/vagas/vagas').then((m) => m.Vagas),
      },
      {
        path: 'candidatos',
        loadComponent: () => import('./pages/candidatos/candidatos').then((m) => m.Candidatos),
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
