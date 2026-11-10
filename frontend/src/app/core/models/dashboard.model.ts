export interface CandidatosPorCompetencia {
  competenciaId: number;
  nomeCompetencia: string;
  totalCandidatos: number;
}

export interface DashboardCandidato {
  tipoUsuario: 'CANDIDATO';
  totalVagasSugeridas: number;
  totalCurtidasEnviadas: number;
  totalCurtidasRecebidas: number;
  totalMatches: number;
  totalCompetencias: number;
}

export interface DashboardEmpresa {
  tipoUsuario: 'EMPRESA';
  totalVagasAtivas: number;
  totalCurtidasRecebidas: number;
  totalCurtidasEnviadas: number;
  totalMatches: number;
  totalCompetencias: number;
  candidatosPorCompetencia: CandidatosPorCompetencia[];
}

export type DashboardData = DashboardCandidato | DashboardEmpresa;
