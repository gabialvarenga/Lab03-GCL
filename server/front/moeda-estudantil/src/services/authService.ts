import api from './api';
import type { LoginCredentials, LoginResponse, StudentRegistrationDTO, CompanyRegistrationDTO } from '../types';

export const authService = {
  login: async (credentials: LoginCredentials): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    localStorage.removeItem('userEmail');
  },

  registerStudent: async (data: StudentRegistrationDTO): Promise<void> => {
    await api.post('/students', data);
  },

  registerCompany: async (data: CompanyRegistrationDTO): Promise<void> => {
    await api.post('/companies', data);
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  },

  getUserRole: (): string | null => {
    return localStorage.getItem('userRole');
  },

  getUserId: (): number | null => {
    const id = localStorage.getItem('userId');
    return id ? parseInt(id) : null;
  }
};
