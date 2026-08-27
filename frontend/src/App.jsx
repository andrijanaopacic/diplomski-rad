import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'

import Login from './pages/Login'
import RegisterUcesnik from './pages/RegisterUcesnik'
import RegisterOrganizacija from './pages/RegisterOrganizacija'
import Home from './pages/Home'
import UredniciPage from './pages/Urednici'
import DogadjajiPage from './pages/Dogadjaji'
import DogadjajDetaljiPage from './pages/DogadjajDetalji'
import PrijavaAktivnostiPage from './pages/PrijavaAktivnosti'
import PrijavaFormaPage from './pages/PrijavaForma'
import PregledPrijavaPage from './pages/PregledPrijava'
import EvidentiranjePage from './pages/Evidentiranje'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/registracija-ucesnik" element={<RegisterUcesnik />} />
          <Route path="/registracija-organizacija" element={<RegisterOrganizacija />} />
          <Route path="/" element={<Home />} />
          <Route
            path="/urednici"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <UredniciPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dogadjaji"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'UREDNIK']}>
                <DogadjajiPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dogadjaji/:id"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'UREDNIK']}>
                <DogadjajDetaljiPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/prijava/:dogadjajId"
            element={
              <ProtectedRoute allowedRoles={['UCESNIK']}>
                <PrijavaAktivnostiPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/prijava/:dogadjajId/aktivnost/:aktivnostId"
            element={
              <ProtectedRoute allowedRoles={['UCESNIK']}>
                <PrijavaFormaPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dogadjaji/:dogadjajId/aktivnost/:aktivnostId/prijave"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'UREDNIK']}>
                <PregledPrijavaPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/evidentiranje"
            element={
              <ProtectedRoute allowedRoles={['ADMIN', 'UREDNIK']}>
                <EvidentiranjePage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}