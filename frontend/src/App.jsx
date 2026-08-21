import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'

import Login from './pages/Login'
import RegisterUcesnik from './pages/RegisterUcesnik'
import RegisterOrganizacija from './pages/RegisterOrganizacija'
import Home from './pages/Home'

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/registracija-ucesnik" element={<RegisterUcesnik />} />
          <Route path="/registracija-organizacija" element={<RegisterOrganizacija />} />
          <Route path="/" element={<Home />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}