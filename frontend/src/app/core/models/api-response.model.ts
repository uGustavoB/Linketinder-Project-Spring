export interface ApiResponse<T> {
  codigo: number;
  dados: T;
  mensagem: string;
  tipo: string;
}
