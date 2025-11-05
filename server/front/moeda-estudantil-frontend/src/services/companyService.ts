import api from './api';
import type { Company, Advantage, AdvantageRequestDTO } from '../types';

export const companyService = {
  getProfile: async (id: number): Promise<Company> => {
    const response = await api.get<Company>(`/companies/${id}`);
    return response.data;
  },

  getAdvantages: async (companyId: number): Promise<Advantage[]> => {
    const response = await api.get<Advantage[]>(`/advantages/company/${companyId}`);
    return response.data;
  },

  createAdvantage: async (companyId: number, data: AdvantageRequestDTO): Promise<Advantage> => {
    const advantageData = {
      ...data,
      companyId: companyId
    };
    const response = await api.post<Advantage>('/advantages', advantageData);
    return response.data;
  },

  updateAdvantage: async (_companyId: number, advantageId: number, data: AdvantageRequestDTO): Promise<Advantage> => {
    const response = await api.put<Advantage>(`/advantages/${advantageId}`, data);
    return response.data;
  },

  deleteAdvantage: async (_companyId: number, advantageId: number): Promise<void> => {
    await api.delete(`/advantages/${advantageId}`);
  }
};
