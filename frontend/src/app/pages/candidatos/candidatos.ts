import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CandidatoService } from '@/core/services/candidato.service';
import { CandidatoProfile } from '@/core/models/candidato.model';
import { ZardTableImports } from '@/shared/components/table/table.imports';


@Component({
  selector: 'app-candidatos',
  standalone: true,
  imports: [ZardTableImports],
  templateUrl: './candidatos.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    class: 'block w-full overflow-x-auto',
  },
})
export class Candidatos implements OnInit {
  private candidatoService = inject(CandidatoService);

  readonly candidatos = signal<CandidatoProfile[]>([]);
  readonly isLoading = signal<boolean>(true);

  ngOnInit(): void {
    this.candidatoService.getCandidatos().subscribe({
      next: (res: any) => {
        if (res.dados) {
          this.candidatos.set(res.dados);
        }
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Erro ao buscar candidatos', err);
        this.isLoading.set(false);
      },
    });
  }
}
