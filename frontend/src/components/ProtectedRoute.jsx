import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'


export default function ProtectedRoute({ children, allowedRoles }) {
  const { isLoggedIn, korisnik } = useAuth()

  if (!isLoggedIn) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(korisnik?.uloga)) {
    return <Navigate to="/" replace />
  }

  return children
}