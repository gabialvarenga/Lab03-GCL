import api from './api';
import type { Professor, Transaction, Student, TransferCoinsDTO } from '../types';

export const professorService = {
  getProfile: async (id: number): Promise<Professor> => {
    const response = await api.get<Professor>(`/professors/${id}`);
    return response.data;
  },

  getTransactions: async (id: number): Promise<Transaction[]> => {
    const response = await api.get<Transaction[]>(`/professors/${id}/transactions`);
    return response.data;
  },

  getBalance: async (id: number): Promise<number> => {
    const response = await api.get<{ balance: number }>(`/professors/${id}/balance`);
    return response.data.balance;
  },

  getStudents: async (): Promise<Student[]> => {
    const response = await api.get<Student[]>('/students');
    return response.data;
  },

  transferCoins: async (professorId: number, data: TransferCoinsDTO): Promise<void> => {
    await api.post(`/professors/${professorId}/transfer`, data);
  }
};
