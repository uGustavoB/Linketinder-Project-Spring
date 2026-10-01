import { ChangeDetectionStrategy, Component, signal, computed, inject } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd, RouterLink } from '@angular/router';
import { filter } from 'rxjs/operators';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideCalendar,
  lucideHouse,
  lucideInbox,
  lucideLogOut,
  lucideSearch,
  lucideSettings,
  lucideUser,
  lucideBuilding2,
  lucideBriefcase,
  lucideFileText,
} from '@ng-icons/lucide';

import { ZardSeparatorComponent } from '@/shared/components/separator/separator.component';
import { ZardSidebarImports } from '@/shared/components/sidebar/sidebar.imports';
import { AuthService } from '@/core/services/auth.service';
import { toast } from 'ngx-sonner';

interface NavItem {
  readonly title: string;
  readonly path: string;
  readonly icon: string;
}

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, ZardSidebarImports, ZardSeparatorComponent, NgIcon],
  templateUrl: './main-layout.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [
    provideIcons({
      lucideCalendar,
      lucideHouse,
      lucideInbox,
      lucideLogOut,
      lucideSearch,
      lucideSettings,
      lucideUser,
      lucideBuilding2,
      lucideBriefcase,
      lucideFileText,
    }),
  ],
})
export class MainLayout {
  private router = inject(Router);
  public authService = inject(AuthService);

  readonly activeTitle = signal('Home');

  private readonly candidateItems: NavItem[] = [
    { title: 'Painel', path: '/dashboard', icon: 'lucideHouse' },
    { title: 'Explorar Vagas', path: '/vagas', icon: 'lucideSearch' },
    { title: 'Meu Perfil', path: '/perfil', icon: 'lucideFileText' },
  ];

  private readonly companyItems: NavItem[] = [
    { title: 'Painel', path: '/dashboard', icon: 'lucideHouse' },
    { title: 'Vagas Publicadas', path: '/vagas', icon: 'lucideBriefcase' },
    { title: 'Candidatos', path: '/candidatos', icon: 'lucideSearch' },
    { title: 'Perfil da Empresa', path: '/perfil', icon: 'lucideFileText' },
  ];

  readonly currentNavItems = computed(() => {
    const user = this.authService.currentUser();
    return user?.tipoUsuario === 'EMPRESA' ? this.companyItems : this.candidateItems;
  });

  constructor() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const currentRoute = event.urlAfterRedirects;
        const navs = this.currentNavItems();
        const activeItem = navs.find((item) => currentRoute.startsWith(item.path)) || navs[0];
        if (activeItem) {
          this.activeTitle.set(activeItem.title);
        }
      });
  }

  logout() {
    this.authService.logout().subscribe({
      next: (res: any) => {
        toast.success(res.mensagem || 'Logout realizado com sucesso');
        this.router.navigate(['/login']);
      },
      error: () => {
        this.authService.isAuthenticated.set(false);
        this.authService.currentUser.set(null);
        this.router.navigate(['/login']);
      },
    });
  }
}
