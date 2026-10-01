export interface ApiResponse<T> {
  codigo?: number;
  status?: number;
  dados?: T;
  data?: T;
  mensagem: string;
  tipo?: string;
}
