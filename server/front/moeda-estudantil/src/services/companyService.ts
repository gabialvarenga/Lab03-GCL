import api from './api';
import type { Company, Advantage, AdvantageRequestDTO, CompanyUpdateDTO } from '../types';

export const companyService = {
  getProfile: async (id: number): Promise<Company> => {
    const response = await api.get<Company>(`/companies/${id}`);
    return response.data;
  },

  updateProfile: async (id: number, data: CompanyUpdateDTO): Promise<Company> => {
    const response = await api.put<Company>(`/companies/${id}`, data);
    return response.data;
  },

  deleteProfile: async (id: number): Promise<void> => {
    await api.delete(`/companies/${id}`);
  },

  getAdvantages: async (companyId: number, showQuantity: boolean = true): Promise<Advantage[]> => {
    const response = await api.get<Advantage[]>(`/advantages/company/${companyId}`, {
      params: { showQuantity }
    });
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
  },

  reactivateAdvantage: async (advantageId: number, quantity: number): Promise<Advantage> => {
    const response = await api.patch<Advantage>(`/advantages/${advantageId}/reactivate`, null, {
      params: { quantity }
    });
    return response.data;
  },

  uploadImage: async (formData: FormData): Promise<{ url: string }> => {
    const response = await api.post<{ url: string }>('/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }
};
