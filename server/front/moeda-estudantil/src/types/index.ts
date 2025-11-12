
export type UserRole = 'STUDENT' | 'TEACHER' | 'COMPANY';
export type TransactionType = 'SENT' | 'RECEIVED' | 'REDEEMED';

export interface User {
  id: number;
  email: string;
  role: UserRole;
}

// Student Types
export interface Student {
  id: number;
  name: string;
  email: string;
  cpf: string;
  rg: string;
  address: string;
  course: string;
  balance?: number;
  coinBalance?: number;
  institution?: Institution;
  institutionId?: number;
  institutionName?: string;
}

export interface StudentRegistrationDTO {
  name: string;
  email: string;
  password: string;
  cpf: string;
  rg: string;
  address: string;
  course: string;
  institutionId: number;
}

export interface StudentUpdateDTO {
  name?: string;
  email?: string;
  password?: string;
  rg?: string;
  address?: string;
  course?: string;
  institutionId?: number;
}

// Professor Types
export interface Professor {
  id: number;
  name: string;
  email: string;
  cpf: string;
  department: string;
  balance: number;
  institutionId?: number;
  institutionName?: string;
  // Mantido para compatibilidade com código existente que acessa professor.institution.name
  institution?: {
    id?: number;
    name?: string;
  };
}

// Company Types
export interface Company {
  id: number;
  name: string;
  email: string;
  cnpj: string;
  address?: string;
}

export interface CompanyRegistrationDTO {
  name: string;
  email: string;
  password: string;
  cnpj: string;
  address?: string;
}

export interface CompanyUpdateDTO {
  name?: string;
  email?: string;
  password?: string;
  cnpj?: string;
  address?: string;
}

// Institution Types
export interface Institution {
  id: number;
  name: string;
  availableCourses?: string[];
  address?: string;
  createdAt?: string;
}

// Advantage Types
export interface Advantage {
  id: number;
  name: string;
  description: string;
  costInCoins: number;
  availableQuantity?: number; // null ou undefined = ilimitado
  photo?: string;
  photoName?: string;
  photoType?: string;
  companyId: number;
  companyName: string;
  company?: Company;
  timesRedeemed?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface AdvantageRequestDTO {
  name: string;
  description: string;
  costInCoins: number;
  availableQuantity?: number;
  photo?: string;
  photoName?: string;
  photoType?: string;
  companyId?: number;
}

// Transaction Types
export interface Transaction {
  id: number;
  amount: number;
  date: string;
  type: TransactionType;
  reason: string;
  senderId?: number;
  senderName?: string;
  receiverId?: number;
  receiverName?: string;
}

export interface TransferCoinsDTO {
  studentId: number;
  amount: number;
  reason: string;
}

// Auth Types
export interface LoginCredentials {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  role: UserRole;
  userId: number;
  email: string;
}

// Purchase Types
export interface PurchaseDTO {
  advantageId: number;
  studentId: number;
}

export interface PurchaseResponse {
  code: string;
  advantage: Advantage;
  student: Student;
  purchaseDate: string;
}
