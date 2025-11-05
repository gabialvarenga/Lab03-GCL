import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import type { Advantage } from '../types';

const StudentAdvantages: React.FC = () => {
  const [advantages, setAdvantages] = useState<Advantage[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedAdvantage, setSelectedAdvantage] = useState<Advantage | null>(null);
  const [purchaseLoading, setPurchaseLoading] = useState(false);
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadAdvantages();
  }, []);

  const loadAdvantages = async () => {
    try {
      const data = await studentService.getAdvantages();
      setAdvantages(data);
    } catch (error) {
      console.error('Erro ao carregar vantagens:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePurchase = async () => {
    if (!selectedAdvantage || !userId) return;

    setPurchaseLoading(true);
    try {
      const response = await studentService.purchaseAdvantage({
        advantageId: selectedAdvantage.id,
        studentId: userId
      });
      
      alert(`Compra realizada com sucesso! Seu código de resgate é: ${response.code}\nVerifique seu email para mais detalhes.`);
      setSelectedAdvantage(null);
      navigate('/student/dashboard');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao realizar compra. Verifique seu saldo.');
    } finally {
      setPurchaseLoading(false);
    }
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
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <button 
            onClick={() => navigate('/student/dashboard')} 
            className="mb-4 text-gray-600 hover:text-gray-900 font-medium flex items-center gap-2 transition-colors"
          >
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-gray-900">Catálogo de Vantagens</h1>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {advantages.length === 0 ? (
            <div className="col-span-full text-center py-12">
              <p className="text-gray-500 text-lg">Nenhuma vantagem disponível no momento</p>
            </div>
          ) : (
            advantages.map((advantage) => (
              <div key={advantage.id} className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200 flex flex-col">
                {advantage.photo && (
                  <img 
                    src={advantage.photo} 
                    alt={advantage.name} 
                    className="w-full h-48 object-cover"
                  />
                )}
                <div className="p-6 flex flex-col flex-grow">
                  <h3 className="text-xl font-semibold text-gray-900 mb-2">{advantage.name}</h3>
                  <p className="text-sm text-gray-500 mb-3">Por: {advantage.companyName || advantage.company?.name}</p>
                  <p className="text-gray-600 mb-4 flex-grow">{advantage.description}</p>
                  <div className="flex justify-between items-center pt-4 border-t border-gray-200">
                    <span className="text-2xl font-bold text-black-600">{advantage.costInCoins} 🪙</span>
                    <button 
                      onClick={() => setSelectedAdvantage(advantage)} 
                      className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2 px-6 rounded-lg transition-colors duration-200"
                    >
                      Resgatar
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {selectedAdvantage && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4" onClick={() => setSelectedAdvantage(null)}>
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-8" onClick={(e) => e.stopPropagation()}>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Confirmar Resgate</h2>
            <p className="text-gray-600 mb-2">Você está prestes a resgatar:</p>
            <h3 className="text-xl font-semibold text-gray-900 mb-4">{selectedAdvantage.name}</h3>
            <p className="text-3xl font-bold text-black-600 mb-2">{selectedAdvantage.costInCoins} moedas</p>
            <p className="text-gray-600 mb-4">{selectedAdvantage.description}</p>
            <p className="bg-yellow-50 border-l-4 border-yellow-400 text-yellow-800 p-3 mb-6 rounded">
              ⚠️ Esta ação não pode ser desfeita
            </p>
            <div className="flex gap-3">
              <button 
                onClick={() => setSelectedAdvantage(null)} 
                className="flex-1 bg-gray-500 hover:bg-gray-600 text-white font-medium py-3 px-4 rounded-lg transition-colors duration-200 disabled:opacity-60"
                disabled={purchaseLoading}
              >
                Cancelar
              </button>
              <button 
                onClick={handlePurchase} 
                className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-4 rounded-lg transition-colors duration-200 disabled:opacity-60"
                disabled={purchaseLoading}
              >
                {purchaseLoading ? 'Processando...' : 'Confirmar'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentAdvantages;
