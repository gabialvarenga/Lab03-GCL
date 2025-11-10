import api from './api';
import type { Institution } from '../types';

export const institutionService = {
  getAll: async (): Promise<Institution[]> => {
    const response = await api.get<Institution[]>('/institutions');
    return response.data;
  },

  getById: async (id: number): Promise<Institution> => {
    const response = await api.get<Institution>(`/institutions/${id}`);
    return response.data;
  }
};
