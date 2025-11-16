import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import type { Transaction } from '../types';

const StudentStatement: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [balance, setBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [isFiltering, setIsFiltering] = useState(false);
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async (filterStartDate?: string, filterEndDate?: string) => {
    if (!userId) return;
    
    try {
      const [transactionsData, profileData] = await Promise.all([
        studentService.getTransactions(userId, filterStartDate, filterEndDate),
        studentService.getProfile(userId)
      ]);
      setTransactions(transactionsData);
      setBalance(profileData.coinBalance || 0);
    } catch (error) {
      console.error('Erro ao carregar extrato:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = () => {
    if (startDate && endDate) {
      setIsFiltering(true);
      // Converter datas para formato ISO esperado pelo backend
      const startDateTime = new Date(startDate).toISOString().slice(0, 19);
      const endDateTime = new Date(endDate + 'T23:59:59').toISOString().slice(0, 19);
      loadData(startDateTime, endDateTime);
    }
  };

  const handleClearFilter = () => {
    setStartDate('');
    setEndDate('');
    setIsFiltering(false);
    loadData();
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
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
      <div className="bg-white shadow-sm border-b border-gray-200 mb-8">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button 
            onClick={() => navigate('/student/dashboard')} 
            className="mb-4 text-gray-600 hover:text-gray-900 font-medium flex items-center gap-2 transition-colors"
          >
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Extrato de Conta</h1>
        </div>
      </div>

      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 pb-12">
        <div className="bg-gradient-to-r from-blue-500 to-indigo-600 rounded-xl shadow-lg p-8 text-white mb-8">
          <h3 className="text-lg font-medium mb-2 opacity-90">Saldo Atual</h3>
          <p className="text-5xl font-bold">{balance} <span className="text-2xl">moedas</span></p>
        </div>

        <div className="bg-white rounded-xl shadow-md p-6 mb-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Filtrar por Data</h3>
          <div className="flex flex-wrap gap-4 items-end">
            <div className="flex-1 min-w-[200px]">
              <label htmlFor="startDate" className="block text-sm font-medium text-gray-700 mb-2">
                Data Inicial
              </label>
              <input
                type="date"
                id="startDate"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div className="flex-1 min-w-[200px]">
              <label htmlFor="endDate" className="block text-sm font-medium text-gray-700 mb-2">
                Data Final
              </label>
              <input
                type="date"
                id="endDate"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div className="flex gap-2">
              <button
                onClick={handleFilter}
                disabled={!startDate || !endDate}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors font-medium"
              >
                Filtrar
              </button>
              {isFiltering && (
                <button
                  onClick={handleClearFilter}
                  className="px-6 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors font-medium"
                >
                  Limpar
                </button>
              )}
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-md overflow-hidden">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-xl font-semibold text-gray-900">Histórico de Transações</h2>
          </div>
          
          {transactions.length === 0 ? (
            <div className="p-12 text-center">
              <p className="text-gray-500 text-lg">Nenhuma transação encontrada</p>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              {transactions.map((transaction) => {
                // Determina se é recebimento ou gasto para o aluno
                const isIncoming = transaction.type === 'RECEIVED' || transaction.receiverId === userId;
                const isOutgoing = transaction.type === 'SENT' || transaction.type === 'REDEEMED';
                
                return (
                  <div key={transaction.id} className="p-6 hover:bg-gray-50 transition-colors">
                    <div className="flex justify-between items-start">
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-2">
                          <span className="text-2xl">
                            {transaction.type === 'RECEIVED' && '💰'}
                            {transaction.type === 'REDEEMED' && '🎁'}
                            {transaction.type === 'SENT' && '�'}
                          </span>
                          <span className="font-semibold text-gray-900">
                            {transaction.type === 'RECEIVED' && 'Moedas Recebidas'}
                            {transaction.type === 'REDEEMED' && 'Resgate de Vantagem'}
                            {transaction.type === 'SENT' && 'Transferência Enviada'}
                          </span>
                        </div>
                        <p className="text-gray-600 mb-1">{transaction.reason}</p>
                        {transaction.senderName && isIncoming && (
                          <p className="text-sm text-gray-500">
                            👨‍🏫 Enviado por: <span className="font-medium">{transaction.senderName}</span>
                          </p>
                        )}
                        {transaction.receiverName && isOutgoing && (
                          <p className="text-sm text-gray-500">Para: {transaction.receiverName}</p>
                        )}
                        <p className="text-sm text-gray-500 mt-1">{formatDate(transaction.date)}</p>
                      </div>
                      <div className={`text-2xl font-bold ml-4 ${isOutgoing ? 'text-green-600' : 'text-red-600'}`}>
                        {isOutgoing ? '+' : '-'}
                        {transaction.amount}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudentStatement;
