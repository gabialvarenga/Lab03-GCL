import api from './api';
import type { Professor, Transaction, Student, TransferCoinsDTO } from '../types';

export const professorService = {
  getProfile: async (id: number): Promise<Professor> => {
    const response = await api.get<Professor>(`/teachers/${id}`);
    return response.data;
  },

  getTransactions: async (id: number, startDate?: string, endDate?: string): Promise<Transaction[]> => {
    let url = `/teachers/${id}/transactions`;
    const params = new URLSearchParams();
    
    if (startDate && endDate) {
      params.append('startDate', startDate);
      params.append('endDate', endDate);
    }
    
    if (params.toString()) {
      url += `?${params.toString()}`;
    }
    
    const response = await api.get<Transaction[]>(url);
    return response.data;
  },

  getBalance: async (id: number): Promise<number> => {
    const response = await api.get<number>(`/teachers/${id}/balance`);
    return response.data;
  },

  getStudents: async (professorId: number): Promise<Student[]> => {
    const response = await api.get<Student[]>(`/teachers/${professorId}/students`);
    return response.data;
  },

  transferCoins: async (professorId: number, data: TransferCoinsDTO): Promise<void> => {
    await api.post(`/teachers/${professorId}/transfer`, data);
  },

  updateProfile: async (id: number, data: any): Promise<Professor> => {
    const response = await api.put<Professor>(`/teachers/${id}`, data);
    return response.data;
  }
};
