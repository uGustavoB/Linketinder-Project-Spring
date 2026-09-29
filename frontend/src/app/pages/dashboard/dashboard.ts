import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ZardSkeletonComponent } from '@/shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [ZardSkeletonComponent],
  templateUrl: './dashboard.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard {}
