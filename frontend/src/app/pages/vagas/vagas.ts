import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ZardTableImports } from '@/shared/components/table/table.imports';
import { VagaService } from '@/core/services/vaga.service';
import { AuthService } from '@/core/services/auth.service';
import { Vaga } from '@/core/models/vaga.model';

@Component({
  selector: 'app-vagas',
  standalone: true,
  imports: [ZardTableImports],
  templateUrl: './vagas.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'block w-full overflow-x-auto',
  },
})
export class Vagas implements OnInit {
  private vagaService = inject(VagaService);
  private authService = inject(AuthService);

  readonly vagas = signal<Vaga[]>([]);
  readonly isLoading = signal<boolean>(true);

  ngOnInit(): void {
    const user = this.authService.currentUser();
    const empresaId = user?.tipoUsuario === 'EMPRESA' ? user.id : undefined;
    console.log('Fetching vagas for empresaId:', user);
    this.vagaService.getVagas(empresaId).subscribe({
      next: (res) => {
        if (res.dados) {
          this.vagas.set(res.dados);
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar vagas', err);
        this.isLoading.set(false);
      },
    });
  }
}
