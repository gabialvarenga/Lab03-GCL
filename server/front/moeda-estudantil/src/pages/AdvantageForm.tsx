import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { companyService } from '../services/companyService';

const AdvantageForm: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const isEdit = !!id;
  
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    costInCoins: 0,
    photo: '',
  });
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(isEdit);
  const { userId } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isEdit && userId) {
      loadAdvantage();
    }
  }, [isEdit, id, userId]);

  const loadAdvantage = async () => {
    if (!userId || !id) return;
    
    try {
      const advantages = await companyService.getAdvantages(userId);
      const advantage = advantages.find(adv => adv.id === parseInt(id));
      
      if (advantage) {
        setFormData({
          name: advantage.name,
          description: advantage.description,
          costInCoins: advantage.costInCoins,
          photo: advantage.photo || '',
        });
        if (advantage.photo) {
          setPreviewUrl(advantage.photo);
        }
      }
    } catch (error) {
      console.error('Erro ao carregar vantagem:', error);
    } finally {
      setLoadingData(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'costInCoins' ? parseInt(value) || 0 : value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      // Validar tipo de arquivo
      if (!file.type.startsWith('image/')) {
        alert('Por favor, selecione apenas arquivos de imagem');
        return;
      }
      
      // Validar tamanho (max 5MB)
      if (file.size > 5 * 1024 * 1024) {
        alert('A imagem deve ter no máximo 5MB');
        return;
      }
      
      setSelectedFile(file);
      
      // Criar preview
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRemoveImage = () => {
    setSelectedFile(null);
    setPreviewUrl('');
    setFormData(prev => ({ ...prev, photo: '' }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!userId) return;
    
    if (formData.costInCoins <= 0) {
      alert('O custo deve ser maior que zero');
      return;
    }

    setLoading(true);
    
    try {
      let photoUrl = formData.photo;
      
      // Se há um arquivo selecionado, fazer upload primeiro
      if (selectedFile) {
        const uploadFormData = new FormData();
        uploadFormData.append('file', selectedFile);
        
        try {
          const uploadResponse = await companyService.uploadImage(uploadFormData);
          photoUrl = uploadResponse.url;
        } catch (uploadError: any) {
          alert(uploadError.response?.data?.message || 'Erro ao fazer upload da imagem');
          setLoading(false);
          return;
        }
      }
      
      const advantageData = {
        ...formData,
        photo: photoUrl,
      };
      
      if (isEdit && id) {
        await companyService.updateAdvantage(userId, parseInt(id), advantageData);
        alert('Vantagem atualizada com sucesso!');
      } else {
        await companyService.createAdvantage(userId, advantageData);
        alert('Vantagem cadastrada com sucesso!');
      }
      navigate('/company/advantages');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao salvar vantagem. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  if (loadingData) {
    return <div className="flex items-center justify-center min-h-screen">Carregando...</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-2xl mx-auto">
        <div className="flex items-center gap-4 mb-8">
          <button onClick={() => navigate('/company/advantages')} className="btn-back">
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-black-800">{isEdit ? 'Editar Vantagem' : 'Nova Vantagem'}</h1>
        </div>

        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-md p-8">
          <div className="mb-6">
            <label htmlFor="name" className="block text-gray-700 font-medium mb-2">Nome da Vantagem *</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              placeholder="Ex: Desconto de 20% no restaurante"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <div className="mb-6">
            <label htmlFor="description" className="block text-gray-700 font-medium mb-2">Descrição *</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
              rows={5}
              placeholder="Descreva detalhadamente a vantagem oferecida..."
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent resize-none"
            />
          </div>

          <div className="mb-6">
            <label htmlFor="costInCoins" className="block text-gray-700 font-medium mb-2">Custo em Moedas *</label>
            <input
              type="number"
              id="costInCoins"
              name="costInCoins"
              value={formData.costInCoins}
              onChange={handleChange}
              required
              min="1"
              placeholder="Ex: 100"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent"
            />
          </div>

          <div className="mb-6">
            <label htmlFor="photo" className="block text-gray-700 font-medium mb-2">Foto da Vantagem</label>
            <div className="space-y-4">
              <input
                type="file"
                id="photo"
                name="photo"
                accept="image/*"
                onChange={handleFileChange}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-purple-50 file:text-purple-700 hover:file:bg-purple-100"
              />
              <p className="text-sm text-gray-500">Formatos aceitos: JPG, PNG, GIF (máx. 5MB)</p>
              
              {previewUrl && (
                <div className="relative mt-4 border border-gray-300 rounded-lg overflow-hidden">
                  <img src={previewUrl} alt="Preview" className="w-full h-64 object-cover" />
                  <button
                    type="button"
                    onClick={handleRemoveImage}
                    className="absolute top-2 right-2 bg-red-600 text-white p-2 rounded-full hover:bg-red-700 transition-colors"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                      <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                    </svg>
                  </button>
                </div>
              )}
            </div>
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? 'Salvando...' : (isEdit ? 'Atualizar Vantagem' : 'Cadastrar Vantagem')}
          </button>
        </form>
      </div>
    </div>
  );
};

export default AdvantageForm;
