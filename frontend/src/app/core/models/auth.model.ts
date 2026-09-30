export interface LoginRequest {
  email: string;
  senha?: string;
}

export interface UserData {
  token?: string;
  email: string;
  nome: string;
  sobrenome: string | null;
  tipoUsuario: 'EMPRESA' | 'CANDIDATO';
}

export interface LoginResponse {
  codigo: number;
  dados: UserData;
  mensagem: string;
  tipo: string;
}
