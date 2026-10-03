import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CandidatoCompetenciasDialog } from './candidato-competencias-dialog';

describe('CandidatoCompetenciasDialog', () => {
  let component: CandidatoCompetenciasDialog;
  let fixture: ComponentFixture<CandidatoCompetenciasDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandidatoCompetenciasDialog],
    }).compileComponents();

    fixture = TestBed.createComponent(CandidatoCompetenciasDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
