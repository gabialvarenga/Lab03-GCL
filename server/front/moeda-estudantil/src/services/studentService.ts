import api from './api';
import type { Student, Transaction, Advantage, PurchaseDTO, PurchaseResponse, StudentUpdateDTO } from '../types';

export const studentService = {
  getProfile: async (id: number): Promise<Student> => {
    const response = await api.get<Student>(`/students/${id}`);
    return response.data;
  },

  updateProfile: async (id: number, data: StudentUpdateDTO): Promise<Student> => {
    const response = await api.put<Student>(`/students/${id}`, data);
    return response.data;
  },

  deleteProfile: async (id: number): Promise<void> => {
    await api.delete(`/students/${id}`);
  },

  getTransactions: async (id: number, startDate?: string, endDate?: string): Promise<Transaction[]> => {
    let url = `/students/${id}/transactions`;
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

  getAdvantages: async (): Promise<Advantage[]> => {
    const response = await api.get<Advantage[]>('/advantages');
    return response.data;
  },

  purchaseAdvantage: async (data: PurchaseDTO): Promise<PurchaseResponse> => {
    const response = await api.post<PurchaseResponse>('/students/purchase', data);
    return response.data;
  }
};
