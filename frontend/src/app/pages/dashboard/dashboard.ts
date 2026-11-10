import { ChangeDetectionStrategy, Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NgIcon, provideIcons } from '@ng-icons/core';
import {
  lucideBriefcase,
  lucideHeart,
  lucideSparkles,
  lucideFlame,
  lucideAward,
  lucideUsers,
  lucideChartBar,
  lucideArrowRight,
  lucideSearch,
  lucideFileText,
  lucideInbox,
} from '@ng-icons/lucide';
import { toast } from 'ngx-sonner';

import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardSkeletonComponent } from '@/shared/components/skeleton/skeleton.component';
import { AuthService } from '@/core/services/auth.service';
import { DashboardService } from '@/core/services/dashboard.service';
import { DashboardCandidato, DashboardEmpresa } from '@/core/models/dashboard.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    NgIcon,
    ZardCardImports,
    ZardButtonComponent,
    ZardSkeletonComponent,
  ],
  templateUrl: './dashboard.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [
    provideIcons({
      lucideBriefcase,
      lucideHeart,
      lucideSparkles,
      lucideFlame,
      lucideAward,
      lucideUsers,
      lucideChartBar,
      lucideArrowRight,
      lucideSearch,
      lucideFileText,
      lucideInbox,
    }),
  ],
})
export class Dashboard implements OnInit {
  private dashboardService = inject(DashboardService);
  public authService = inject(AuthService);

  readonly isLoading = signal<boolean>(true);
  readonly dashboardCandidato = signal<DashboardCandidato | null>(null);
  readonly dashboardEmpresa = signal<DashboardEmpresa | null>(null);

  readonly maxCandidatosCompetencia = computed<number>(() => {
    const lista = this.dashboardEmpresa()?.candidatosPorCompetencia ?? [];
    if (lista.length === 0) {
      return 1;
    }
    const maior = Math.max(...lista.map((item) => item.totalCandidatos));
    return maior > 0 ? maior : 1;
  });

  ngOnInit(): void {
    this.fetchDashboard();
  }

  public fetchDashboard(): void {
    this.isLoading.set(true);
    this.dashboardService.getDashboard().subscribe({
      next: (res) => {
        const dados = res.dados || res.data;
        if (dados) {
          if (dados.tipoUsuario === 'CANDIDATO') {
            this.dashboardCandidato.set(dados);
            this.dashboardEmpresa.set(null);
          } else if (dados.tipoUsuario === 'EMPRESA') {
            this.dashboardEmpresa.set(dados);
            this.dashboardCandidato.set(null);
          }
        }
        this.isLoading.set(false);
      },
      error: () => {
        toast.error('Não foi possível carregar os indicadores do painel.');
        this.isLoading.set(false);
      },
    });
  }

  public getBarWidthPercent(totalCandidatos: number): number {
    if (!totalCandidatos || totalCandidatos <= 0) {
      return 0;
    }
    return Math.round((totalCandidatos / this.maxCandidatosCompetencia()) * 100);
  }
}
