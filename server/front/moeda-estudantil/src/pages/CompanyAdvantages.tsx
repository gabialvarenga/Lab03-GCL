import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { companyService } from '../services/companyService';
import type { Advantage } from '../types';

interface AdvantageCardProps {
  advantage: Advantage;
  onEdit: () => void;
  onDelete: () => void;
  onReactivate?: () => void;
  isExhausted: boolean;
}

const AdvantageCard: React.FC<AdvantageCardProps> = ({ 
  advantage, 
  onEdit, 
  onDelete, 
  onReactivate,
  isExhausted 
}) => {
  return (
    <div className={`bg-white rounded-lg shadow-md overflow-hidden hover:shadow-xl transition-shadow ${
      isExhausted ? 'opacity-75 border-2 border-red-300' : ''
    }`}>
      {advantage.photo && (
        <img src={advantage.photo} alt={advantage.name} className="w-full h-48 object-cover" />
      )}
      <div className="p-6">
        <h3 className="text-xl font-bold text-gray-800 mb-2">{advantage.name}</h3>
        <p className="text-gray-600 mb-4 line-clamp-3">{advantage.description}</p>
        <div className="flex justify-between items-center mb-4">
          <span className="text-2xl font-bold text-purple-600">{advantage.costInCoins} moedas</span>
        </div>
        
        {advantage.availableQuantity !== undefined && advantage.availableQuantity !== null && (
          <div className="mb-4">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Disponível:</span>
              <span className={`font-semibold ${
                advantage.availableQuantity > 10 ? 'text-green-600' : 
                advantage.availableQuantity > 0 ? 'text-yellow-600' : 'text-red-600'
              }`}>
                {advantage.availableQuantity} {advantage.availableQuantity === 1 ? 'cupom' : 'cupons'}
              </span>
            </div>
            <div className="w-full bg-gray-200 rounded-full h-2 mt-2">
              <div 
                className={`h-2 rounded-full ${
                  advantage.availableQuantity > 10 ? 'bg-green-500' : 
                  advantage.availableQuantity > 0 ? 'bg-yellow-500' : 'bg-red-500'
                }`}
                style={{ width: `${Math.min((advantage.availableQuantity / 50) * 100, 100)}%` }}
              ></div>
            </div>
          </div>
        )}
        
        {advantage.availableQuantity === undefined || advantage.availableQuantity === null ? (
          <div className="mb-4 text-sm text-gray-500 italic">
            ♾️ Quantidade ilimitada
          </div>
        ) : null}
        
        {isExhausted && (
          <div className="mb-4 bg-red-50 border border-red-200 rounded-lg p-3">
            <p className="text-red-700 text-sm font-medium">
              🚫 Vantagem esgotada - Não visível para alunos
            </p>
          </div>
        )}
        
        <div className="flex gap-2">
          {isExhausted && onReactivate ? (
            <button 
              onClick={onReactivate}
              className="flex-1 bg-green-500 hover:bg-green-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
            >
              🔄 Reativar
            </button>
          ) : (
            <button 
              onClick={onEdit}
              className="flex-1 bg-blue-500 hover:bg-blue-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
            >
              Editar
            </button>
          )}
          <button 
            onClick={onDelete}
            className="flex-1 bg-red-500 hover:bg-red-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
          >
            Excluir
          </button>
        </div>
      </div>
    </div>
  );
};

const CompanyAdvantages: React.FC = () => {
  const [advantages, setAdvantages] = useState<Advantage[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [reactivateId, setReactivateId] = useState<number | null>(null);
  const [reactivateQuantity, setReactivateQuantity] = useState<string>('10');
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadAdvantages();
  }, []);

  const loadAdvantages = async () => {
    if (!userId) return;
    
    try {
      // Passa showQuantity=true para empresas verem a quantidade disponível
      const data = await companyService.getAdvantages(userId, true);
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

  const handleReactivate = async () => {
    if (!reactivateId) return;

    const quantity = parseInt(reactivateQuantity);
    if (isNaN(quantity) || quantity <= 0) {
      alert('Por favor, insira uma quantidade válida (maior que 0)');
      return;
    }

    try {
      const updated = await companyService.reactivateAdvantage(reactivateId, quantity);
      setAdvantages(advantages.map(adv => adv.id === reactivateId ? updated : adv));
      setReactivateId(null);
      setReactivateQuantity('10');
      alert(`Vantagem reativada com sucesso! ${quantity} cupons adicionados.`);
    } catch (error) {
      alert('Erro ao reativar vantagem. Tente novamente.');
    }
  };

  // Separar vantagens ativas e esgotadas
  const activeAdvantages = advantages.filter(adv => 
    adv.availableQuantity === null || 
    adv.availableQuantity === undefined || 
    adv.availableQuantity > 0
  );
  
  const exhaustedAdvantages = advantages.filter(adv => 
    adv.availableQuantity !== null && 
    adv.availableQuantity !== undefined && 
    adv.availableQuantity === 0
  );

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
          <>
            {/* Seção de Vantagens Ativas */}
            {activeAdvantages.length > 0 && (
              <div className="mb-12">
                <h2 className="text-2xl font-bold text-gray-800 mb-6">
                  ✅ Vantagens Ativas ({activeAdvantages.length})
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {activeAdvantages.map((advantage) => (
                    <AdvantageCard 
                      key={advantage.id} 
                      advantage={advantage} 
                      onEdit={() => navigate(`/company/advantages/edit/${advantage.id}`)}
                      onDelete={() => setDeleteId(advantage.id)}
                      isExhausted={false}
                    />
                  ))}
                </div>
              </div>
            )}

            {/* Seção de Vantagens Esgotadas */}
            {exhaustedAdvantages.length > 0 && (
              <div>
                <h2 className="text-2xl font-bold text-red-600 mb-6">
                  🚫 Vantagens Esgotadas ({exhaustedAdvantages.length})
                </h2>
                <p className="text-gray-600 mb-4">
                  Estas vantagens não estão mais disponíveis para os alunos. Você pode reativá-las adicionando mais cupons.
                </p>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {exhaustedAdvantages.map((advantage) => (
                    <AdvantageCard 
                      key={advantage.id} 
                      advantage={advantage} 
                      onEdit={() => navigate(`/company/advantages/edit/${advantage.id}`)}
                      onDelete={() => setDeleteId(advantage.id)}
                      onReactivate={() => setReactivateId(advantage.id)}
                      isExhausted={true}
                    />
                  ))}
                </div>
              </div>
            )}
          </>
        )}

        {/* Modal de Confirmação de Exclusão */}
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

        {/* Modal de Reativação */}
        {reactivateId && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onClick={() => setReactivateId(null)}>
            <div className="bg-white rounded-lg p-8 max-w-md w-full mx-4" onClick={(e) => e.stopPropagation()}>
              <h2 className="text-2xl font-bold text-gray-800 mb-4">🔄 Reativar Vantagem</h2>
              <p className="text-gray-600 mb-6">
                Quantos cupons você deseja adicionar para reativar esta vantagem?
              </p>
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Quantidade de cupons
                </label>
                <input
                  type="number"
                  min="1"
                  value={reactivateQuantity}
                  onChange={(e) => setReactivateQuantity(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                  placeholder="Ex: 10"
                />
              </div>
              <div className="flex gap-4">
                <button 
                  onClick={() => {
                    setReactivateId(null);
                    setReactivateQuantity('10');
                  }} 
                  className="btn-secondary flex-1"
                >
                  Cancelar
                </button>
                <button 
                  onClick={handleReactivate} 
                  className="bg-green-500 hover:bg-green-600 text-white font-medium py-3 px-6 rounded-lg transition-colors flex-1"
                >
                  Reativar
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
