import { ChangeDetectionStrategy, Component, signal, inject } from '@angular/core';
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
} from '@ng-icons/lucide';

import { ZardSeparatorComponent } from '@/shared/components/separator/separator.component';
import { ZardSidebarImports } from '@/shared/components/sidebar/sidebar.imports';
import { AuthService } from '@/core/services/auth.service';

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
    }),
  ],
})
export class MainLayout {
  private router = inject(Router);
  private authService = inject(AuthService);

  readonly activeTitle = signal('Home');

  readonly navItems: readonly NavItem[] = [
    { title: 'Home', path: '/dashboard', icon: 'lucideHouse' },
    { title: 'Vagas', path: '/dashboard', icon: 'lucideCalendar' },
  ];

  constructor() {
    this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        const currentRoute = event.urlAfterRedirects;
        const activeItem =
          this.navItems.find((item) => currentRoute.startsWith(item.path)) || this.navItems[0];
        this.activeTitle.set(activeItem.title);
      });
  }

  logout() {
    this.router.navigate(['/login']);
  }
}
