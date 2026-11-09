import { Competencia } from './competencia.model';

export interface Vaga {
  id: number;
  nome: string;
  descricao: string;
  local: string;
  nivel: string;
  empresaId?: number | null;
  competenciaIds: number[];
  competencias?: Competencia[];
}
