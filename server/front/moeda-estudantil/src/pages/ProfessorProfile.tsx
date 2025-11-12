import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { professorService } from '../services/professorService';
import type { Professor } from '../types';

const ProfessorProfile: React.FC = () => {
  const [professor, setProfessor] = useState<Professor | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const { userId } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    department: '',
    password: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!userId) return;
    
    try {
      const professorData = await professorService.getProfile(userId);
      setProfessor(professorData);
      
      setFormData({
        name: professorData.name,
        department: professorData.department,
        password: '',
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
      const dataToSend: any = { ...formData };
      if (!dataToSend.password || dataToSend.password.trim() === '') {
        delete dataToSend.password;
      }
      
      const updatedProfessor = await professorService.updateProfile(userId, dataToSend);
      setProfessor(updatedProfessor);
      setIsEditing(false);
      alert('Perfil atualizado com sucesso!');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao atualizar perfil. Tente novamente.');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (professor) {
      setFormData({
        name: professor.name,
        department: professor.department,
        password: '',
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
          <button onClick={() => navigate('/professor/dashboard')} className="text-gray-600 hover:text-gray-900 font-medium flex items-center gap-2 transition-colors">
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-gray-800">Meu Perfil</h1>
          <div className="w-24"></div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-8">
          {!isEditing ? (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Informações Pessoais</h2>
                <button
                  onClick={() => setIsEditing(true)}
                  className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-6 rounded-lg transition-colors duration-200"
                >
                  Editar Perfil
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Nome Completo</span>
                  <span className="text-lg text-gray-900">{professor?.name}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Email</span>
                  <span className="text-lg text-gray-900">{professor?.email}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">CPF</span>
                  <span className="text-lg text-gray-900">{professor?.cpf}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Departamento</span>
                  <span className="text-lg text-gray-900">{professor?.department}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Instituição</span>
                  <span className="text-lg text-gray-900">
                    {professor?.institutionName || professor?.institution?.name || 'Não informada'}
                  </span>
                </div>

                <div className="border-l-4 border-green-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Saldo de Moedas</span>
                  <span className="text-2xl font-bold text-green-600">
                    {professor?.balance || 0} moedas
                  </span>
                </div>
              </div>
            </>
          ) : (
            <form onSubmit={handleSubmit}>
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Editar Perfil</h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="md:col-span-2">
                  <label htmlFor="name" className="block text-gray-700 font-medium mb-2">Nome Completo *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="department" className="block text-gray-700 font-medium mb-2">Departamento *</label>
                  <input
                    type="text"
                    id="department"
                    name="department"
                    value={formData.department}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
              </div>

              <div className="flex gap-4 mt-8">
                <button 
                  type="button" 
                  onClick={handleCancel}
                  className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-3 px-4 rounded-lg transition-colors duration-200"
                  disabled={saving}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-4 rounded-lg transition-colors duration-200"
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

export default ProfessorProfile;
