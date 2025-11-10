import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { companyService } from '../services/companyService';
import type { Company, CompanyUpdateDTO } from '../types';

const CompanyProfile: React.FC = () => {
  const [company, setCompany] = useState<Company | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const { userId } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<CompanyUpdateDTO>({
    name: '',
    password: '',
    cnpj: '',
    address: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!userId) return;
    
    try {
      const companyData = await companyService.getProfile(userId);
      setCompany(companyData);
      
      setFormData({
        name: companyData.name,
        password: '',
        cnpj: companyData.cnpj,
        address: companyData.address || '',
      });
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userId) return;

    setSaving(true);
    try {
      const dataToSend = { ...formData };
      if (!dataToSend.password || dataToSend.password.trim() === '') {
        delete dataToSend.password;
      }
      
      const updatedCompany = await companyService.updateProfile(userId, dataToSend);
      setCompany(updatedCompany);
      setIsEditing(false);
      alert('Perfil atualizado com sucesso!');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao atualizar perfil. Tente novamente.');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (company) {
      setFormData({
        name: company.name,
        password: '',
        cnpj: company.cnpj,
        address: company.address || '',
      });
    }
    setIsEditing(false);
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-xl text-gray-600">Carregando...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <button onClick={() => navigate('/company/dashboard')} className="btn-back">
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-black-800">Perfil da Empresa</h1>
          <div className="w-24"></div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-8">
          {!isEditing ? (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Informações da Empresa</h2>
                <button 
                  onClick={() => setIsEditing(true)}
                  className="btn-primary"
                >
                  Editar Perfil
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="border-l-4 border-purple-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Nome da Empresa</span>
                  <span className="text-lg text-gray-900">{company?.name}</span>
                </div>

                <div className="border-l-4 border-purple-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Email</span>
                  <span className="text-lg text-gray-900">{company?.email}</span>
                </div>

                <div className="border-l-4 border-purple-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">CNPJ</span>
                  <span className="text-lg text-gray-900">{company?.cnpj}</span>
                </div>

                {company?.address && (
                  <div className="border-l-4 border-purple-500 pl-4 md:col-span-2">
                    <span className="text-sm text-gray-500 font-medium block mb-1">Endereço</span>
                    <span className="text-lg text-gray-900">{company.address}</span>
                  </div>
                )}
              </div>
            </>
          ) : (
            <form onSubmit={handleSubmit}>
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Editar Perfil</h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="md:col-span-2">
                  <label htmlFor="name" className="block text-gray-700 font-medium mb-2">Nome da Empresa *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="password" className="block text-gray-700 font-medium mb-2">Nova Senha (deixe em branco para manter)</label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="••••••••"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="cnpj" className="block text-gray-700 font-medium mb-2">CNPJ *</label>
                  <input
                    type="text"
                    id="cnpj"
                    name="cnpj"
                    value={formData.cnpj}
                    onChange={handleChange}
                    required
                    placeholder="00.000.000/0000-00"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>

                <div className="md:col-span-2">
                  <label htmlFor="address" className="block text-gray-700 font-medium mb-2">Endereço</label>
                  <input
                    type="text"
                    id="address"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    placeholder="Rua, número, bairro, cidade - UF"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  />
                </div>
              </div>

              <div className="flex gap-4 mt-8">
                <button 
                  type="button" 
                  onClick={handleCancel}
                  className="btn-secondary flex-1"
                  disabled={saving}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className="btn-primary flex-1"
                  disabled={saving}
                >
                  {saving ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  );
};

export default CompanyProfile;
