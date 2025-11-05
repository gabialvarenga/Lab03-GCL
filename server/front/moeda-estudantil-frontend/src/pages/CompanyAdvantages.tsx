import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { companyService } from '../services/companyService';
import type { Advantage } from '../types';

const CompanyAdvantages: React.FC = () => {
  const [advantages, setAdvantages] = useState<Advantage[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadAdvantages();
  }, []);

  const loadAdvantages = async () => {
    if (!userId) return;
    
    try {
      const data = await companyService.getAdvantages(userId);
      setAdvantages(data);
    } catch (error) {
      console.error('Erro ao carregar vantagens:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!userId || !deleteId) return;

    try {
      await companyService.deleteAdvantage(userId, deleteId);
      setAdvantages(advantages.filter(adv => adv.id !== deleteId));
      setDeleteId(null);
      alert('Vantagem excluída com sucesso!');
    } catch (error) {
      alert('Erro ao excluir vantagem. Tente novamente.');
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <button onClick={() => navigate('/company/dashboard')} className="btn-back">
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-black-800">Minhas Vantagens</h1>
          <button onClick={() => navigate('/company/advantages/new')} className="btn-primary">
            ➕ Nova Vantagem
          </button>
        </div>

        {advantages.length === 0 ? (
          <div className="bg-white rounded-lg shadow-md p-12 text-center">
            <p className="text-gray-600 text-lg mb-6">Você ainda não cadastrou nenhuma vantagem</p>
            <button onClick={() => navigate('/company/advantages/new')} className="btn-primary">
              Cadastrar Primeira Vantagem
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {advantages.map((advantage) => (
              <div key={advantage.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow">
                {advantage.photo && (
                  <img src={advantage.photo} alt={advantage.name} className="w-full h-48 object-cover" />
                )}
                <div className="p-6">
                  <h3 className="text-xl font-bold text-gray-800 mb-2">{advantage.name}</h3>
                  <p className="text-gray-600 mb-4 line-clamp-3">{advantage.description}</p>
                  <div className="flex justify-between items-center mb-4">
                    <span className="text-2xl font-bold text-black-600">{advantage.costInCoins} moedas</span>
                  </div>
                  <div className="flex gap-2">
                    <button 
                      onClick={() => navigate(`/company/advantages/edit/${advantage.id}`)} 
                      className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
                    >
                      Editar
                    </button>
                    <button 
                      onClick={() => setDeleteId(advantage.id)} 
                      className="flex-1 bg-red-500 hover:bg-red-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
                    >
                     Excluir
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {deleteId && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onClick={() => setDeleteId(null)}>
            <div className="bg-white rounded-lg p-8 max-w-md w-full mx-4" onClick={(e) => e.stopPropagation()}>
              <h2 className="text-2xl font-bold text-gray-800 mb-4">Confirmar Exclusão</h2>
              <p className="text-gray-600 mb-4">Tem certeza que deseja excluir esta vantagem?</p>
              <p className="text-red-600 font-medium mb-6">⚠️ Esta ação não pode ser desfeita</p>
              <div className="flex gap-4">
                <button onClick={() => setDeleteId(null)} className="btn-secondary flex-1">
                  Cancelar
                </button>
                <button onClick={handleDelete} className="bg-red-500 hover:bg-red-600 text-white font-medium py-3 px-6 rounded-lg transition-colors flex-1">
                  Confirmar Exclusão
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CompanyAdvantages;
