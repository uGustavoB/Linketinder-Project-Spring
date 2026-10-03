export interface Formacao {
  id: number;
  instituicao: string;
  curso: string;
  nivel: string;
  status: string;
  dataInicio: string | null;
  dataConclusao: string | null;
}

export interface CandidatoProfile {
  id: number;
  pais: string;
  estado: string;
  descricao: string;
  formacoes: Formacao[];
}
