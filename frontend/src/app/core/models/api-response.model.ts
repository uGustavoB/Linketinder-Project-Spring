export interface ApiResponse<T> {
  codigo?: number;
  status?: number;
  dados: T;
  mensagem: string;
  tipo?: string;
}
