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
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const { userId } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<StudentUpdateDTO>({
    name: '',
    cpf: '',
    rg: '',
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
        cpf: studentData.cpf,
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
      const updatedStudent = await studentService.updateProfile(userId, formData);
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
        cpf: student.cpf,
        rg: student.rg,
        address: student.address,
        course: student.course,
        institutionId: student.institutionId || student.institution?.id,
      });
    }
    setIsEditing(false);
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
          <h1 className="text-3xl font-bold text-blue-800">Meu Perfil</h1>
          <div className="w-24"></div>
        </div>

        <div className="bg-white rounded-xl shadow-md p-8">
          {!isEditing ? (
            <>
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">Informações Pessoais</h2>
                <button 
                  onClick={() => setIsEditing(true)}
                  className="btn-primary"
                >
                  Editar Perfil
                </button>
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
                <div>
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
                  <label htmlFor="cpf" className="block text-gray-700 font-medium mb-2">CPF *</label>
                  <input
                    type="text"
                    id="cpf"
                    name="cpf"
                    value={formData.cpf}
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
    </div>
  );
};

export default StudentProfile;
