/**
 * Funções de validação de documentos brasileiros
 */

/**
 * Valida CPF (apenas formato)
 * Aceita: 000.000.000-00 ou 00000000000
 */
export const validateCPF = (cpf: string): boolean => {
  // Remove caracteres não numéricos
  const cleanCPF = cpf.replace(/\D/g, '');
  
  // Verifica se tem 11 dígitos
  if (cleanCPF.length !== 11) {
    return false;
  }
  
  // Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
  if (/^(\d)\1{10}$/.test(cleanCPF)) {
    return false;
  }
  
  // Validação dos dígitos verificadores
  let sum = 0;
  let remainder;
  
  // Valida primeiro dígito
  for (let i = 1; i <= 9; i++) {
    sum += parseInt(cleanCPF.substring(i - 1, i)) * (11 - i);
  }
  remainder = (sum * 10) % 11;
  if (remainder === 10 || remainder === 11) remainder = 0;
  if (remainder !== parseInt(cleanCPF.substring(9, 10))) return false;
  
  // Valida segundo dígito
  sum = 0;
  for (let i = 1; i <= 10; i++) {
    sum += parseInt(cleanCPF.substring(i - 1, i)) * (12 - i);
  }
  remainder = (sum * 10) % 11;
  if (remainder === 10 || remainder === 11) remainder = 0;
  if (remainder !== parseInt(cleanCPF.substring(10, 11))) return false;
  
  return true;
};

/**
 * Formata CPF para o padrão 000.000.000-00
 */
export const formatCPF = (cpf: string): string => {
  const cleanCPF = cpf.replace(/\D/g, '');
  
  if (cleanCPF.length <= 3) return cleanCPF;
  if (cleanCPF.length <= 6) return `${cleanCPF.slice(0, 3)}.${cleanCPF.slice(3)}`;
  if (cleanCPF.length <= 9) return `${cleanCPF.slice(0, 3)}.${cleanCPF.slice(3, 6)}.${cleanCPF.slice(6)}`;
  
  return `${cleanCPF.slice(0, 3)}.${cleanCPF.slice(3, 6)}.${cleanCPF.slice(6, 9)}-${cleanCPF.slice(9, 11)}`;
};

/**
 * Valida CNPJ (apenas formato)
 * Aceita: 00.000.000/0000-00 ou 00000000000000
 */
export const validateCNPJ = (cnpj: string): boolean => {
  // Remove caracteres não numéricos
  const cleanCNPJ = cnpj.replace(/\D/g, '');
  
  // Verifica se tem 14 dígitos
  if (cleanCNPJ.length !== 14) {
    return false;
  }
  
  // Verifica se todos os dígitos são iguais
  if (/^(\d)\1{13}$/.test(cleanCNPJ)) {
    return false;
  }
  
  // Validação dos dígitos verificadores
  let size = cleanCNPJ.length - 2;
  let numbers = cleanCNPJ.substring(0, size);
  const digits = cleanCNPJ.substring(size);
  let sum = 0;
  let pos = size - 7;
  
  // Valida primeiro dígito
  for (let i = size; i >= 1; i--) {
    sum += parseInt(numbers.charAt(size - i)) * pos--;
    if (pos < 2) pos = 9;
  }
  let result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
  if (result !== parseInt(digits.charAt(0))) return false;
  
  // Valida segundo dígito
  size = size + 1;
  numbers = cleanCNPJ.substring(0, size);
  sum = 0;
  pos = size - 7;
  for (let i = size; i >= 1; i--) {
    sum += parseInt(numbers.charAt(size - i)) * pos--;
    if (pos < 2) pos = 9;
  }
  result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
  if (result !== parseInt(digits.charAt(1))) return false;
  
  return true;
};

/**
 * Formata CNPJ para o padrão 00.000.000/0000-00
 */
export const formatCNPJ = (cnpj: string): string => {
  const cleanCNPJ = cnpj.replace(/\D/g, '');
  
  if (cleanCNPJ.length <= 2) return cleanCNPJ;
  if (cleanCNPJ.length <= 5) return `${cleanCNPJ.slice(0, 2)}.${cleanCNPJ.slice(2)}`;
  if (cleanCNPJ.length <= 8) return `${cleanCNPJ.slice(0, 2)}.${cleanCNPJ.slice(2, 5)}.${cleanCNPJ.slice(5)}`;
  if (cleanCNPJ.length <= 12) return `${cleanCNPJ.slice(0, 2)}.${cleanCNPJ.slice(2, 5)}.${cleanCNPJ.slice(5, 8)}/${cleanCNPJ.slice(8)}`;
  
  return `${cleanCNPJ.slice(0, 2)}.${cleanCNPJ.slice(2, 5)}.${cleanCNPJ.slice(5, 8)}/${cleanCNPJ.slice(8, 12)}-${cleanCNPJ.slice(12, 14)}`;
};

/**
 * Valida RG (apenas formato básico - aceita vários formatos)
 * RG pode ter de 7 a 14 caracteres (números e letras)
 */
export const validateRG = (rg: string): boolean => {
  // Remove espaços e caracteres especiais, mas mantém letras e números
  const cleanRG = rg.replace(/[^a-zA-Z0-9]/g, '');
  
  // RG deve ter entre 7 e 14 caracteres
  return cleanRG.length >= 7 && cleanRG.length <= 14;
};

/**
 * Formata RG para um padrão básico (mantém como está ou adiciona pontos/traços)
 */
export const formatRG = (rg: string): string => {
  // Mantém apenas números e letras
  const cleanRG = rg.replace(/[^a-zA-Z0-9]/g, '');
  
  // Formato comum: 00.000.000-0 (MG) ou similar
  if (cleanRG.length <= 2) return cleanRG;
  if (cleanRG.length <= 5) return `${cleanRG.slice(0, 2)}.${cleanRG.slice(2)}`;
  if (cleanRG.length <= 8) return `${cleanRG.slice(0, 2)}.${cleanRG.slice(2, 5)}.${cleanRG.slice(5)}`;
  
  return `${cleanRG.slice(0, 2)}.${cleanRG.slice(2, 5)}.${cleanRG.slice(5, 8)}-${cleanRG.slice(8)}`;
};
