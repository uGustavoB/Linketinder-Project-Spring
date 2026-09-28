import { Component } from '@angular/core';
import { ZardCardImports } from '@/shared/components/card/card.imports';
import { ZardButtonComponent } from '@/shared/components/button/button.component';
import { ZardIdDirective } from '@/shared/core';

@Component({
  imports: [ZardCardImports, ZardButtonComponent, ZardIdDirective],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  protected onSignUp(): void {
    alert('Redirect to Sign Up');
  }
}

