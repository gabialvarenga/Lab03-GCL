import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';

// Auth Pages
import Login from './pages/Login';
import Register from './pages/Register';

// Student Pages
import StudentDashboard from './pages/StudentDashboard';
import StudentStatement from './pages/StudentStatement';
import StudentAdvantages from './pages/StudentAdvantages';
import StudentProfile from './pages/StudentProfile';

// Professor Pages
import ProfessorDashboard from './pages/ProfessorDashboard';
import ProfessorTransfer from './pages/ProfessorTransfer';
import ProfessorStatement from './pages/ProfessorStatement';

// Company Pages
import CompanyDashboard from './pages/CompanyDashboard';
import CompanyAdvantages from './pages/CompanyAdvantages';
import AdvantageForm from './pages/AdvantageForm';
import CompanyProfile from './pages/CompanyProfile';

import './App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Student Routes */}
          <Route
            path="/student/dashboard"
            element={
              <ProtectedRoute allowedRole="STUDENT">
                <StudentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/student/statement"
            element={
              <ProtectedRoute allowedRole="STUDENT">
                <StudentStatement />
              </ProtectedRoute>
            }
          />
          <Route
            path="/student/advantages"
            element={
              <ProtectedRoute allowedRole="STUDENT">
                <StudentAdvantages />
              </ProtectedRoute>
            }
          />
          <Route
            path="/student/profile"
            element={
              <ProtectedRoute allowedRole="STUDENT">
                <StudentProfile />
              </ProtectedRoute>
            }
          />

          {/* Professor Routes */}
          <Route
            path="/professor/dashboard"
            element={
              <ProtectedRoute allowedRole="PROFESSOR">
                <ProfessorDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/professor/transfer"
            element={
              <ProtectedRoute allowedRole="PROFESSOR">
                <ProfessorTransfer />
              </ProtectedRoute>
            }
          />
          <Route
            path="/professor/statement"
            element={
              <ProtectedRoute allowedRole="PROFESSOR">
                <ProfessorStatement />
              </ProtectedRoute>
            }
          />
          <Route
            path="/professor/profile"
            element={
              <ProtectedRoute allowedRole="PROFESSOR">
                <ProfessorDashboard />
              </ProtectedRoute>
            }
          />

          {/* Company Routes */}
          <Route
            path="/company/dashboard"
            element={
              <ProtectedRoute allowedRole="COMPANY">
                <CompanyDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/company/advantages"
            element={
              <ProtectedRoute allowedRole="COMPANY">
                <CompanyAdvantages />
              </ProtectedRoute>
            }
          />
          <Route
            path="/company/advantages/new"
            element={
              <ProtectedRoute allowedRole="COMPANY">
                <AdvantageForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/company/advantages/edit/:id"
            element={
              <ProtectedRoute allowedRole="COMPANY">
                <AdvantageForm />
              </ProtectedRoute>
            }
          />
          <Route
            path="/company/profile"
            element={
              <ProtectedRoute allowedRole="COMPANY">
                <CompanyProfile />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
