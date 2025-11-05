import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { professorService } from '../services/professorService';
import type { Student } from '../types';

const ProfessorTransfer: React.FC = () => {
  const [students, setStudents] = useState<Student[]>([]);
  const [selectedStudent, setSelectedStudent] = useState<number>(0);
  const [amount, setAmount] = useState<number>(0);
  const [reason, setReason] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [balance, setBalance] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!userId) return;
    
    try {
      const [studentsData, balanceData] = await Promise.all([
        professorService.getStudents(),
        professorService.getBalance(userId)
      ]);
      setStudents(studentsData);
      setBalance(balanceData);
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!userId || selectedStudent === 0) return;
    
    if (amount <= 0) {
      alert('O valor deve ser maior que zero');
      return;
    }
    
    if (amount > balance) {
      alert('Saldo insuficiente');
      return;
    }
    
    if (reason.trim().length < 10) {
      alert('O motivo deve ter pelo menos 10 caracteres');
      return;
    }

    setSubmitting(true);
    
    try {
      await professorService.transferCoins(userId, {
        studentId: selectedStudent,
        amount,
        reason
      });
      
      alert('Moedas enviadas com sucesso! O aluno receberá uma notificação por email.');
      navigate('/professor/dashboard');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao enviar moedas. Tente novamente.');
    } finally {
      setSubmitting(false);
    }
  };

  const filteredStudents = students.filter(student =>
    student.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    student.email.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-xl text-gray-600">Carregando...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-white shadow-sm border-b border-gray-200 mb-8">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button 
            onClick={() => navigate('/professor/dashboard')} 
            className="mb-4 text-gray-600 hover:text-gray-900 font-medium flex items-center gap-2 transition-colors"
          >
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Enviar Moedas para Aluno</h1>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 pb-12">
        <div className="bg-gradient-to-r from-green-500 to-emerald-600 rounded-xl shadow-lg p-6 text-white mb-8">
          <span className="text-lg">Seu saldo: <strong className="text-2xl font-bold">{balance} moedas</strong></span>
        </div>

        <form onSubmit={handleSubmit} className="bg-white rounded-xl shadow-md p-8 space-y-6">
          <div>
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              Buscar Aluno
            </label>
            <input
              type="text"
              id="search"
              placeholder="Digite o nome ou email do aluno..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
            />
          </div>

          <div>
            <label htmlFor="student" className="block text-sm font-medium text-gray-700 mb-2">
              Selecionar Aluno *
            </label>
            <select
              id="student"
              value={selectedStudent}
              onChange={(e) => setSelectedStudent(parseInt(e.target.value))}
              required
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
            >
              <option value={0}>Selecione um aluno</option>
              {filteredStudents.map((student) => (
                <option key={student.id} value={student.id}>
                  {student.name} - {student.course} ({student.email})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="amount" className="block text-sm font-medium text-gray-700 mb-2">
              Quantidade de Moedas *
            </label>
            <input
              type="number"
              id="amount"
              value={amount || ''}
              onChange={(e) => setAmount(parseInt(e.target.value) || 0)}
              required
              min="1"
              max={balance}
              placeholder="Ex: 50"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200"
            />
          </div>

          <div>
            <label htmlFor="reason" className="block text-sm font-medium text-gray-700 mb-2">
              Motivo do Reconhecimento *
            </label>
            <textarea
              id="reason"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              required
              minLength={10}
              rows={5}
              placeholder="Descreva o motivo pelo qual este aluno está sendo reconhecido (mínimo 10 caracteres)..."
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent transition-all duration-200 resize-none"
            />
            <small className="text-gray-500 text-sm mt-1 block">{reason.length} caracteres</small>
          </div>

          <button 
            type="submit" 
            disabled={submitting} 
            className="w-full bg-green-600 hover:bg-green-700 text-white font-medium py-3 px-6 rounded-lg shadow-sm transition-all duration-200 hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {submitting ? 'Enviando...' : 'Enviar Moedas'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ProfessorTransfer;
