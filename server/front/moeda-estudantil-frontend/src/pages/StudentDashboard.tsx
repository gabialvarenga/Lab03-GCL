import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import type { Student } from '../types';

const StudentDashboard: React.FC = () => {
  const [student, setStudent] = useState<Student | null>(null);
  const [loading, setLoading] = useState(true);
  const { userId, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadStudentData();
  }, []);

  const loadStudentData = async () => {
    if (!userId) return;
    
    try {
      const data = await studentService.getProfile(userId);
      setStudent(data);
    } catch (error) {
      console.error('Erro ao carregar dados do aluno:', error);
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
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Bem-vindo, {student?.name}!</h1>
          <div className="bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl shadow-lg p-8 text-white">
            <h3 className="text-lg font-medium mb-2 opacity-90">Saldo Atual</h3>
            <p className="text-5xl font-bold">{student?.balance || 0} <span className="text-2xl">moedas</span></p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-blue-500"
            onClick={() => navigate('/student/advantages')}
          >
            <div className="text-4xl mb-4">🎁</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Catálogo de Vantagens</h3>
            <p className="text-gray-600">Veja as vantagens disponíveis e resgate com suas moedas</p>
          </div>

          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-blue-500"
            onClick={() => navigate('/student/statement')}
          >
            <div className="text-4xl mb-4">📊</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Extrato</h3>
            <p className="text-gray-600">Consulte suas transações e histórico de moedas</p>
          </div>

          <div 
            className="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition-shadow duration-200 cursor-pointer border border-gray-200 hover:border-blue-500"
            onClick={() => navigate('/student/profile')}
          >
            <div className="text-4xl mb-4">👤</div>
            <h3 className="text-xl font-semibold text-gray-900 mb-2">Meu Perfil</h3>
            <p className="text-gray-600">Visualize e edite suas informações pessoais</p>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-6">
          <h3 className="text-xl font-semibold text-gray-900 mb-6">Informações do Aluno</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="border-l-4 border-blue-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Curso</span>
              <span className="text-lg text-gray-900">{student?.course}</span>
            </div>
            <div className="border-l-4 border-blue-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Instituição</span>
              <span className="text-lg text-gray-900">{student?.institution?.name}</span>
            </div>
            <div className="border-l-4 border-blue-500 pl-4">
              <span className="text-sm text-gray-500 font-medium block mb-1">Email</span>
              <span className="text-lg text-gray-900">{student?.email}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
