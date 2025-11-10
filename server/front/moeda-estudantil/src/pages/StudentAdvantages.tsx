import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import type { Advantage } from '../types';

const StudentAdvantages: React.FC = () => {
  const [advantages, setAdvantages] = useState<Advantage[]>([]);
  const [filteredAdvantages, setFilteredAdvantages] = useState<Advantage[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedAdvantage, setSelectedAdvantage] = useState<Advantage | null>(null);
  const [purchaseLoading, setPurchaseLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [maxBudget, setMaxBudget] = useState<string>('');
  const [sortBy, setSortBy] = useState<'none' | 'price-asc' | 'price-desc'>('none');
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadAdvantages();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [advantages, searchTerm, maxBudget, sortBy]);

  const loadAdvantages = async () => {
    try {
      const data = await studentService.getAdvantages();
      setAdvantages(data);
      setFilteredAdvantages(data);
    } catch (error) {
      console.error('Erro ao carregar vantagens:', error);
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...advantages];

    // Filtro por nome
    if (searchTerm.trim()) {
      filtered = filtered.filter(adv => 
        adv.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        adv.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        adv.companyName?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filtro por orçamento
    if (maxBudget && !Number.isNaN(Number(maxBudget))) {
      filtered = filtered.filter(adv => adv.costInCoins <= Number(maxBudget));
    }

    // Ordenação
    if (sortBy === 'price-asc') {
      filtered.sort((a, b) => a.costInCoins - b.costInCoins);
    } else if (sortBy === 'price-desc') {
      filtered.sort((a, b) => b.costInCoins - a.costInCoins);
    }

    setFilteredAdvantages(filtered);
  };

  const clearFilters = () => {
    setSearchTerm('');
    setMaxBudget('');
    setSortBy('none');
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
        {/* Barra de Busca e Filtros */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-8">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Filtrar e Buscar</h2>
          
          <div className="grid grid-cols-1 md:grid-cols-12 gap-4">
            {/* Campo de Busca */}
            <div className="md:col-span-6">
              <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
                Buscar vantagem
              </label>
              <input
                id="search"
                type="text"
                placeholder="Nome da vantagem, empresa ou descrição"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 placeholder-gray-400"
              />
            </div>

            {/* Filtro de Orçamento */}
            <div className="md:col-span-3">
              <label htmlFor="budget" className="block text-sm font-medium text-gray-700 mb-2">
                Custo máximo
              </label>
              <div className="relative">
                <input
                  id="budget"
                  type="number"
                  min="0"
                  placeholder="Ex: 100"
                  value={maxBudget}
                  onChange={(e) => setMaxBudget(e.target.value)}
                  className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 placeholder-gray-400"
                />
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 text-sm pointer-events-none">
                  moedas
                </span>
              </div>
            </div>

            {/* Ordenação */}
            <div className="md:col-span-3">
              <label htmlFor="sort" className="block text-sm font-medium text-gray-700 mb-2">
                Ordenar por
              </label>
              <select
                id="sort"
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value as 'none' | 'price-asc' | 'price-desc')}
                className="w-full px-4 py-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all text-gray-900 bg-white"
              >
                <option value="none">Relevância</option>
                <option value="price-asc">Menor preço</option>
                <option value="price-desc">Maior preço</option>
              </select>
            </div>
          </div>

          {/* Informações e Botão Limpar */}
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 mt-5 pt-5 border-t border-gray-200">
            <p className="text-sm text-gray-600 font-medium">
              {(() => {
                const total = advantages.length;
                const found = filteredAdvantages.length;
                const label = total === 1 ? 'vantagem' : 'vantagens';
                
                return found === total 
                  ? `Exibindo ${total} ${label}`
                  : `Encontradas ${found} de ${total} ${label}`;
              })()}
            </p>
            {(searchTerm || maxBudget || sortBy !== 'none') && (
              <button
                onClick={clearFilters}
                className="inline-flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
                Limpar filtros
              </button>
            )}
          </div>
        </div>

        {/* Grid de Vantagens */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredAdvantages.length === 0 ? (
            <div className="col-span-full text-center py-16">
              <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-gray-100 mb-4">
                <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                </svg>
              </div>
              <p className="text-gray-500 text-lg font-medium">
                {advantages.length === 0 
                  ? 'Nenhuma vantagem disponível no momento'
                  : 'Nenhuma vantagem encontrada com os filtros aplicados'
                }
              </p>
              {advantages.length > 0 && (
                <p className="text-gray-400 text-sm mt-2">
                  Tente ajustar os filtros para ver mais resultados
                </p>
              )}
            </div>
          ) : (
            filteredAdvantages.map((advantage) => (
              <div key={advantage.id} className="bg-white rounded-lg border border-gray-200 overflow-hidden hover:shadow-md hover:border-gray-300 transition-all duration-200 flex flex-col">
                {advantage.photo && (
                  <div className="relative h-48 bg-gray-100">
                    <img 
                      src={advantage.photo} 
                      alt={advantage.name} 
                      className="w-full h-full object-cover"
                    />
                  </div>
                )}
                <div className="p-5 flex flex-col flex-grow">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 line-clamp-2">{advantage.name}</h3>
                  <p className="text-xs text-gray-500 font-medium mb-3 uppercase tracking-wide">
                    {advantage.companyName || advantage.company?.name}
                  </p>
                  <p className="text-sm text-gray-600 mb-4 flex-grow line-clamp-3">{advantage.description}</p>
                  <div className="flex justify-between items-center pt-4 border-t border-gray-100">
                    <div className="flex flex-col">
                      <span className="text-xs text-gray-500 font-medium">Custo</span>
                      <span className="text-xl font-bold text-gray-900">{advantage.costInCoins} moedas</span>
                    </div>
                    <button 
                      onClick={() => setSelectedAdvantage(advantage)} 
                      className="bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5 px-5 rounded-lg transition-colors duration-200 text-sm"
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
