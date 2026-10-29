import { Vaga } from './vaga.model';
import { CandidatoProfile } from './candidato.model';

export interface CurtidaResponse {
  idCurtida: number;
  candidatoId: number;
  vagaId: number;
  candidatoCurtiuEm?: string;
  empresaCurtiuEm?: string;
  match: boolean;
}

export interface MatchCandidato {
  vaga: Vaga;
  empresaNome: string;
  empresaEmail: string;
  empresaPais?: string;
  empresaEstado?: string;
  empresaDescricao?: string;
  dataMatch: string;
}

export interface MatchEmpresa {
  vaga: Vaga;
  candidato: CandidatoProfile;
  candidatoNome: string;
  candidatoEmail: string;
  dataMatch: string;
}
