import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { professorService } from '../services/professorService';
import type { Professor } from '../types';

const ProfessorDashboard: React.FC = () => {
  const [professor, setProfessor] = useState<Professor | null>(null);
  const [loading, setLoading] = useState(true);
  const { userId, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadProfessorData();
  }, []);

  const loadProfessorData = async () => {
    if (!userId) return;
    
    try {
      const data = await professorService.getProfile(userId);
      setProfessor(data);
    } catch (error) {
      console.error('Erro ao carregar dados do professor:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-xl text-gray-600">Carregando...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h2 className="text-xl font-semibold text-gray-900">Sistema de Mérito Estudantil</h2>
            <button 
              onClick={handleLogout} 
              className="bg-gray-500 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition-colors duration-200"
            >
              Sair
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Bem-vindo, Professor(a) {professor?.name}!</h1>
          <div className="bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl shadow-lg p-8 text-white">
            <h3 className="text-lg font-medium mb-2 opacity-90">Saldo Atual</h3>
            <p className="text-5xl font-bold mb-2">{professor?.balance || 0} <span className="text-2xl">moedas</span></p>
            <p className="text-sm opacity-90">💡 Você recebe 1.000 moedas por semestre</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-green-500"
            onClick={() => navigate('/professor/transfer')}
          >
            <div className="text-4xl mb-4">💸</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Enviar Moedas</h3>
            <p className="text-gray-600">Reconheça seus alunos enviando moedas</p>
          </div>

          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-green-500"
            onClick={() => navigate('/professor/statement')}
          >
            <div className="text-4xl mb-4">📊</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Extrato</h3>
            <p className="text-gray-600">Consulte suas transações e histórico de distribuição</p>
          </div>

          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-green-500"
            onClick={() => navigate('/professor/profile')}
          >
            <div className="text-4xl mb-4">👤</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Meu Perfil</h3>
            <p className="text-gray-600">Visualize suas informações</p>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-6">
          <h3 className="text-xl font-semibold text-gray-900 mb-6">Informações do Professor</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="border-l-4 border-green-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Departamento</span>
              <span className="text-lg text-gray-900">{professor?.department}</span>
            </div>
            <div className="border-l-4 border-green-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Instituição</span>
              <span className="text-lg text-gray-900">{professor?.institutionName || professor?.institution?.name || 'Não informada'}</span>
            </div>
            <div className="border-l-4 border-green-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Email</span>
              <span className="text-lg text-gray-900">{professor?.email}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfessorDashboard;
