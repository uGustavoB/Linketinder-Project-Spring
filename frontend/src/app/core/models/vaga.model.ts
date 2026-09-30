export interface Vaga {
  id: number;
  nome: string;
  descricao: string;
  local: string;
  nivel: string;
  empresaId: number;
  competenciaIds: number[];
}
