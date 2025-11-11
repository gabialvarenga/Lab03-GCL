import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/authService';
import { institutionService } from '../services/institutionService';
import { validateCPF, formatCPF, validateCNPJ, formatCNPJ, validateRG, formatRG } from '../utils/validators';
import type { Institution } from '../types';

type UserType = 'student' | 'company';

const Register: React.FC = () => {
  const [userType, setUserType] = useState<UserType>('student');
  const [institutions, setInstitutions] = useState<Institution[]>([]);
  const [selectedInstitution, setSelectedInstitution] = useState<Institution | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [cpfError, setCpfError] = useState('');
  const [rgError, setRgError] = useState('');
  const [cnpjError, setCnpjError] = useState('');
  const navigate = useNavigate();

  // Student form data
  const [studentData, setStudentData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    cpf: '',
    rg: '',
    address: '',
    course: '',
    institutionId: 0,
  });

  // Company form data
  const [companyData, setCompanyData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    cnpj: '',
    address: '',
  });

  useEffect(() => {
    if (userType === 'student') {
      loadInstitutions();
    }
  }, [userType]);

  const loadInstitutions = async () => {
    try {
      console.log('🔍 Buscando instituições...');
      const data = await institutionService.getAll();
      console.log('✅ Instituições recebidas:', data);
      console.log('📊 Total:', data.length);
      setInstitutions(data);
    } catch (err) {
      console.error('❌ Erro ao carregar instituições:', err);
    }
  };

  const handleStudentChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    
    if (name === 'institutionId') {
      const instId = parseInt(value);
      const institution = institutions.find(i => i.id === instId) || null;
      setSelectedInstitution(institution);
      setStudentData((prev) => ({
        ...prev,
        institutionId: instId,
        course: '' // Limpa o curso ao trocar de instituição
      }));
    } else if (name === 'cpf') {
      const formatted = formatCPF(value);
      setStudentData((prev) => ({ ...prev, cpf: formatted }));
      
      // Valida apenas se tiver 14 caracteres (formato completo)
      if (formatted.length === 14) {
        if (!validateCPF(formatted)) {
          setCpfError('CPF inválido');
        } else {
          setCpfError('');
        }
      } else {
        setCpfError('');
      }
    } else if (name === 'rg') {
      const formatted = formatRG(value);
      setStudentData((prev) => ({ ...prev, rg: formatted }));
      
      if (!validateRG(formatted) && formatted.length > 0) {
        setRgError('RG deve ter entre 7 e 14 caracteres');
      } else {
        setRgError('');
      }
    } else {
      setStudentData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleCompanyChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    
    if (name === 'cnpj') {
      const formatted = formatCNPJ(value);
      setCompanyData((prev) => ({ ...prev, cnpj: formatted }));
      
      // Valida apenas se tiver 18 caracteres (formato completo)
      if (formatted.length === 18) {
        if (!validateCNPJ(formatted)) {
          setCnpjError('CNPJ inválido');
        } else {
          setCnpjError('');
        }
      } else {
        setCnpjError('');
      }
    } else {
      setCompanyData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (userType === 'student') {
      // Validações
      if (studentData.institutionId === 0) {
        setError('Selecione uma instituição');
        return;
      }

      if (!validateCPF(studentData.cpf)) {
        setError('CPF inválido');
        setCpfError('CPF inválido');
        return;
      }

      if (!validateRG(studentData.rg)) {
        setError('RG inválido - deve ter entre 7 e 14 caracteres');
        setRgError('RG deve ter entre 7 e 14 caracteres');
        return;
      }

      setLoading(true);
      try {
        const { confirmPassword, ...registrationData } = studentData;
        // Remove formatação do CPF antes de enviar
        const dataToSend = {
          ...registrationData,
          cpf: registrationData.cpf.replace(/\D/g, '')
        };
        await authService.registerStudent(dataToSend);
        alert('Cadastro realizado com sucesso! Faça login para continuar.');
        navigate('/login');
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erro ao realizar cadastro. Tente novamente.');
      } finally {
        setLoading(false);
      }
    } else {
      // Validação CNPJ
      if (!validateCNPJ(companyData.cnpj)) {
        setError('CNPJ inválido');
        setCnpjError('CNPJ inválido');
        return;
      }

      setLoading(true);
      try {
        const { confirmPassword, ...registrationData } = companyData;
        // Remove formatação do CNPJ antes de enviar
        const dataToSend = {
          ...registrationData,
          cnpj: registrationData.cnpj.replace(/\D/g, '')
        };
        await authService.registerCompany(dataToSend);
        alert('Cadastro realizado com sucesso! Faça login para continuar.');
        navigate('/login');
      } catch (err: any) {
        setError(err.response?.data?.message || 'Erro ao realizar cadastro. Tente novamente.');
      } finally {
        setLoading(false);
      }
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-2xl w-full bg-white rounded-2xl shadow-xl p-8">
        <h1 className="text-3xl font-bold text-center text-gray-900 mb-8">Criar Conta</h1>

        <div className="grid grid-cols-2 gap-4 mb-8">
          <button
            type="button"
            className={`flex flex-col items-center justify-center py-6 px-4 rounded-xl border-2 transition-all duration-200 ${
              userType === 'student'
                ? 'border-blue-600 bg-blue-50 text-blue-700'
                : 'border-gray-200 hover:border-gray-300 text-gray-600'
            }`}
            onClick={() => setUserType('student')}
          >
            <span className="text-4xl mb-2">👨‍🎓</span>
            <span className="font-medium">Sou Aluno</span>
          </button>
          <button
            type="button"
            className={`flex flex-col items-center justify-center py-6 px-4 rounded-xl border-2 transition-all duration-200 ${
              userType === 'company'
                ? 'border-blue-600 bg-blue-50 text-blue-700'
                : 'border-gray-200 hover:border-gray-300 text-gray-600'
            }`}
            onClick={() => setUserType('company')}
          >
            <span className="text-4xl mb-2">🏢</span>
            <span className="font-medium">Sou Empresa</span>
          </button>
        </div>

        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit}>
          {userType === 'student' ? (
            <>
              {/* Student Form */}
              <div className="mb-4">
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                  Nome Completo *
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  value={studentData.name}
                  onChange={handleStudentChange}
                  required
                  placeholder="Seu nome completo"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                  Email *
                </label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={studentData.email}
                  onChange={handleStudentChange}
                  required
                  placeholder="seu@email.com"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
                  Senha *
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={studentData.password}
                  onChange={handleStudentChange}
                  required
                  minLength={6}
                  placeholder="Mínimo 6 caracteres"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label htmlFor="cpf" className="block text-sm font-medium text-gray-700 mb-2">
                    CPF *
                  </label>
                  <input
                    type="text"
                    id="cpf"
                    name="cpf"
                    value={studentData.cpf}
                    onChange={handleStudentChange}
                    required
                    placeholder="000.000.000-00"
                    maxLength={14}
                    className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 ${
                      cpfError ? 'border-red-500' : 'border-gray-300'
                    }`}
                  />
                  {cpfError && <small className="text-red-500 text-xs mt-1 block">{cpfError}</small>}
                </div>

                <div>
                  <label htmlFor="rg" className="block text-sm font-medium text-gray-700 mb-2">
                    RG *
                  </label>
                  <input
                    type="text"
                    id="rg"
                    name="rg"
                    value={studentData.rg}
                    onChange={handleStudentChange}
                    required
                    placeholder="00.000.000-0"
                    maxLength={15}
                    className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 ${
                      rgError ? 'border-red-500' : 'border-gray-300'
                    }`}
                  />
                  {rgError && <small className="text-red-500 text-xs mt-1 block">{rgError}</small>}
                </div>
              </div>

              <div className="mb-4">
                <label htmlFor="address" className="block text-sm font-medium text-gray-700 mb-2">
                  Endereço *
                </label>
                <input
                  type="text"
                  id="address"
                  name="address"
                  value={studentData.address}
                  onChange={handleStudentChange}
                  required
                  placeholder="Rua, número, bairro, cidade"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="institutionId" className="block text-sm font-medium text-gray-700 mb-2">
                  Instituição de Ensino *
                </label>
                <select
                  id="institutionId"
                  name="institutionId"
                  value={studentData.institutionId}
                  onChange={handleStudentChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                >
                  <option value={0}>Selecione sua instituição</option>
                  {institutions.length > 0 ? (
                    institutions.map((inst) => (
                      <option key={inst.id} value={inst.id}>
                        {inst.name}
                      </option>
                    ))
                  ) : (
                    <option value={0} disabled>Carregando instituições...</option>
                  )}
                </select>
                {institutions.length === 0 && (
                  <small className="text-orange-500 text-sm mt-1 block">⚠️ Nenhuma instituição carregada. Verifique o console (F12).</small>
                )}
              </div>

              <div className="mb-6">
                <label htmlFor="course" className="block text-sm font-medium text-gray-700 mb-2">
                  Curso *
                </label>
                {!selectedInstitution || studentData.institutionId === 0 ? (
                  <div className="w-full px-4 py-3 border border-gray-200 rounded-lg bg-gray-50 text-gray-500">
                    Selecione uma instituição primeiro
                  </div>
                ) : selectedInstitution.availableCourses && selectedInstitution.availableCourses.length > 0 ? (
                  <select
                    id="course"
                    name="course"
                    value={studentData.course}
                    onChange={handleStudentChange}
                    required
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                  >
                    <option value="">Selecione seu curso</option>
                    {selectedInstitution.availableCourses.map((course, index) => (
                      <option key={index} value={course}>
                        {course}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    type="text"
                    id="course"
                    name="course"
                    value={studentData.course}
                    onChange={handleStudentChange}
                    required
                    placeholder="Ex: Ciência da Computação"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                  />
                )}
                <small className="text-gray-500 text-xs mt-1 block">
                  {selectedInstitution && selectedInstitution.availableCourses && selectedInstitution.availableCourses.length > 0
                    ? 'Selecione um curso da lista'
                    : 'Digite o nome do seu curso'}
                </small>
              </div>
            </>
          ) : (
            <>
              {/* Company Form */}
              <div className="mb-4">
                <label htmlFor="company-name" className="block text-sm font-medium text-gray-700 mb-2">
                  Nome da Empresa *
                </label>
                <input
                  type="text"
                  id="company-name"
                  name="name"
                  value={companyData.name}
                  onChange={handleCompanyChange}
                  required
                  placeholder="Nome da sua empresa"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="company-email" className="block text-sm font-medium text-gray-700 mb-2">
                  Email *
                </label>
                <input
                  type="email"
                  id="company-email"
                  name="email"
                  value={companyData.email}
                  onChange={handleCompanyChange}
                  required
                  placeholder="contato@empresa.com"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="company-password" className="block text-sm font-medium text-gray-700 mb-2">
                  Senha *
                </label>
                <input
                  type="password"
                  id="company-password"
                  name="password"
                  value={companyData.password}
                  onChange={handleCompanyChange}
                  required
                  minLength={6}
                  placeholder="Mínimo 6 caracteres"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200"
                />
              </div>

              <div className="mb-4">
                <label htmlFor="cnpj" className="block text-sm font-medium text-gray-700 mb-2">
                  CNPJ *
                </label>
                <input
                  type="text"
                  id="cnpj"
                  name="cnpj"
                  value={companyData.cnpj}
                  onChange={handleCompanyChange}
                  required
                  placeholder="00.000.000/0000-00"
                  maxLength={18}
                  className={`w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 ${
                    cnpjError ? 'border-red-500' : 'border-gray-300'
                  }`}
                />
                {cnpjError && <small className="text-red-500 text-xs mt-1 block">{cnpjError}</small>}
                {!cnpjError && <small className="text-gray-600 text-xs mt-1 block">Formato: 00.000.000/0000-00</small>}
              </div>

              <div className="mb-6">
                <label htmlFor="address" className="block text-sm font-medium text-gray-700 mb-2">
                  Endereço
                </label>
                <textarea
                  id="address"
                  name="address"
                  value={companyData.address}
                  onChange={handleCompanyChange}
                  rows={3}
                  placeholder="Rua, número, bairro, cidade - UF (opcional)"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200 resize-none"
                />
              </div>
            </>
          )}

          <button 
            type="submit" 
            disabled={loading} 
            className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-6 rounded-lg shadow-sm transition-all duration-200 hover:shadow-md disabled:opacity-60 disabled:cursor-not-allowed"
          >
            {loading ? 'Cadastrando...' : 'Criar Conta'}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-600">
            Já tem uma conta? <Link to="/login" className="text-blue-600 hover:text-blue-700 font-medium">Fazer login</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
