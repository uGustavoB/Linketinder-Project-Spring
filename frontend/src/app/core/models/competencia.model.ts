export interface Competencia {
  id: number;
  nome: string;
}

export interface VinculoCompetencia {
  idVinculo: number;
  competencia: Competencia;
  nivel: string;
}

export interface VinculoRequest {
  competenciaId: number;
  nivel?: 'BASICO' | 'MEDIO' | 'AVANCADO' | string;
}
