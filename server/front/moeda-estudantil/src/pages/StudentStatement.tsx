import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import type { Transaction } from '../types';

const StudentStatement: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [balance, setBalance] = useState(0);
  const [loading, setLoading] = useState(true);
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!userId) return;
    
    try {
      const [transactionsData, profileData] = await Promise.all([
        studentService.getTransactions(userId),
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
              {transactions.map((transaction) => (
                <div key={transaction.id} className="p-6 hover:bg-gray-50 transition-colors">
                  <div className="flex justify-between items-start">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-2">
                        <span className="text-2xl">
                          {transaction.type === 'TRANSFER' && '💰'}
                          {transaction.type === 'PURCHASE' && '🎁'}
                          {transaction.type === 'CREDIT' && '💳'}
                        </span>
                        <span className="font-semibold text-gray-900">
                          {transaction.type === 'TRANSFER' && 'Recebimento de Moedas'}
                          {transaction.type === 'PURCHASE' && 'Compra de Vantagem'}
                          {transaction.type === 'CREDIT' && 'Crédito'}
                        </span>
                      </div>
                      <p className="text-gray-600 mb-1">{transaction.reason}</p>
                      {transaction.senderName && (
                        <p className="text-sm text-gray-500">De: {transaction.senderName}</p>
                      )}
                      {transaction.receiverName && (
                        <p className="text-sm text-gray-500">Para: {transaction.receiverName}</p>
                      )}
                      <p className="text-sm text-gray-500 mt-1">{formatDate(transaction.date)}</p>
                    </div>
                    <div className={`text-2xl font-bold ml-4 ${transaction.type === 'PURCHASE' ? 'text-red-600' : 'text-green-600'}`}>
                      {transaction.type === 'PURCHASE' ? '-' : '+'}
                      {transaction.amount}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudentStatement;
