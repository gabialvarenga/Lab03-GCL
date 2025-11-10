import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { studentService } from '../services/studentService';
import { institutionService } from '../services/institutionService';
import type { Student, Institution, StudentUpdateDTO } from '../types';

const StudentProfile: React.FC = () => {
  const [student, setStudent] = useState<Student | null>(null);
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [isEditing, setIsEditing] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const { userId, logout } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<StudentUpdateDTO>({
    name: '',
    password: '',
    address: '',
    course: '',
    institutionId: undefined,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    if (!userId) return;
    
    try {
      const [studentData, institutionsData] = await Promise.all([
        studentService.getProfile(userId),
        institutionService.getAll()
      ]);
      
      setStudent(studentData);
      setInstitutions(institutionsData);
      
      setFormData({
        name: studentData.name,
        password: '',
        rg: studentData.rg,
        address: studentData.address,
        course: studentData.course,
        institutionId: studentData.institutionId || studentData.institution?.id,
      });
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'institutionId' ? parseInt(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!userId) return;

    setSaving(true);
    try {
      const dataToSend = { ...formData };
      if (!dataToSend.password || dataToSend.password.trim() === '') {
        delete dataToSend.password;
      }
      
      const updatedStudent = await studentService.updateProfile(userId, dataToSend);
      setStudent(updatedStudent);
      setIsEditing(false);
      alert('Perfil atualizado com sucesso!');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao atualizar perfil. Tente novamente.');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (student) {
      setFormData({
        name: student.name,
        password: '',
        rg: student.rg,
        address: student.address,
        course: student.course,
        institutionId: student.institutionId || student.institution?.id,
      });
    }
    setIsEditing(false);
  };

  const handleDelete = async () => {
    if (!userId) return;

    setDeleting(true);
    try {
      await studentService.deleteProfile(userId);
      alert('Perfil excluído com sucesso!');
      logout();
      navigate('/login');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Erro ao excluir perfil. Tente novamente.');
    } finally {
      setDeleting(false);
      setShowDeleteModal(false);
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
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-4xl mx-auto">
        <div className="flex items-center justify-between mb-8">
          <button onClick={() => navigate('/student/dashboard')} className="btn-back">
            ← Voltar
          </button>
          <h1 className="text-3xl font-bold text-black-800">Meu Perfil</h1>
          <div className="w-24"></div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-8">
          {!isEditing ? (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Informações Pessoais</h2>
                <div className="flex gap-2">
                  <button
                    onClick={() => setIsEditing(true)}
                    className="btn-primary"
                  >
                    Editar Perfil
                  </button>
                  <button
                    onClick={() => setShowDeleteModal(true)}
                    className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-medium"
                  >
                    Excluir Perfil
                  </button>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Nome Completo</span>
                  <span className="text-lg text-gray-900">{student?.name}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Email</span>
                  <span className="text-lg text-gray-900">{student?.email}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">CPF</span>
                  <span className="text-lg text-gray-900">{student?.cpf}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">RG</span>
                  <span className="text-lg text-gray-900">{student?.rg}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Curso</span>
                  <span className="text-lg text-gray-900">{student?.course}</span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Instituição</span>
                  <span className="text-lg text-gray-900">
                    {student?.institution?.name || student?.institutionName || 'Não informada'}
                  </span>
                </div>

                <div className="border-l-4 border-blue-500 pl-4 md:col-span-2">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Endereço</span>
                  <span className="text-lg text-gray-900">{student?.address}</span>
                </div>

                <div className="border-l-4 border-green-500 pl-4">
                  <span className="text-sm text-gray-500 font-medium block mb-1">Saldo de Moedas</span>
                  <span className="text-2xl font-bold text-green-600">
                    {student?.balance || student?.coinBalance || 0} moedas
                  </span>
                </div>
              </div>

             
            </>
          ) : (
            <form onSubmit={handleSubmit}>
              <h2 className="text-2xl font-bold text-gray-900 mb-6">Editar Perfil</h2>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="md:col-span-2">
                  <label htmlFor="name" className="block text-gray-700 font-medium mb-2">Nome Completo *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="password" className="block text-gray-700 font-medium mb-2">Nova Senha (deixe em branco para manter)</label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="••••••••"
                                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    />
                                </div>

                                <div>
                                    <label htmlFor="rg" className="block text-gray-700 font-medium mb-2">RG *</label>
                                    <input
                                        type="text"
                                        id="rg"
                                        name="rg"
                                        value={formData.rg}
                                        onChange={handleChange}
                                        required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="rg" className="block text-gray-700 font-medium mb-2">RG *</label>
                  <input
                    type="text"
                    id="rg"
                    name="rg"
                    value={formData.rg}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div>
                  <label htmlFor="course" className="block text-gray-700 font-medium mb-2">Curso *</label>
                  <input
                    type="text"
                    id="course"
                    name="course"
                    value={formData.course}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                <div className="md:col-span-2">
                  <label htmlFor="institutionId" className="block text-gray-700 font-medium mb-2">Instituição *</label>
                  <select
                    id="institutionId"
                    name="institutionId"
                    value={formData.institutionId || ''}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  >
                    <option value="">Selecione uma instituição</option>
                    {institutions.map((inst) => (
                      <option key={inst.id} value={inst.id}>
                        {inst.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="md:col-span-2">
                  <label htmlFor="address" className="block text-gray-700 font-medium mb-2">Endereço *</label>
                  <input
                    type="text"
                    id="address"
                    name="address"
                    value={formData.address}
                    onChange={handleChange}
                    required
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>
              </div>

              <div className="flex gap-4 mt-8">
                <button 
                  type="button" 
                  onClick={handleCancel}
                  className="btn-secondary flex-1"
                  disabled={saving}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className="btn-primary flex-1"
                  disabled={saving}
                >
                  {saving ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-md w-full p-6">
            <h3 className="text-xl font-bold text-gray-900 mb-4">⚠️ Confirmar Exclusão</h3>
            <p className="text-gray-700 mb-6">
              Tem certeza que deseja excluir sua conta? Esta ação é <strong>permanente</strong> e não pode ser desfeita. 
              Todos os seus dados serão perdidos.
            </p>
            <div className="flex gap-4">
              <button
                onClick={() => setShowDeleteModal(false)}
                className="flex-1 px-4 py-2 bg-gray-200 text-gray-800 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                disabled={deleting}
              >
                Cancelar
              </button>
              <button
                onClick={handleDelete}
                className="flex-1 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-medium"
                disabled={deleting}
              >
                {deleting ? 'Excluindo...' : 'Confirmar Exclusão'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StudentProfile;
