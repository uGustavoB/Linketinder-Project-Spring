export interface LoginRequest {
  email: string;
  senha?: string;
}

export interface LoginResponse {
  codigo: number;
  dados: {
    token: string;
  };
  mensagem: string;
  tipo: string;
}
