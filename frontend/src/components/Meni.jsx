import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Meni() {
  const { korisnik, isLoggedIn, logout } = useAuth()

  const jeAdmin = korisnik?.uloga === 'ADMIN'
  const jeAdminIliUrednik = korisnik?.uloga === 'ADMIN' || korisnik?.uloga === 'UREDNIK'

  return (
    <nav className="menu">
      <Link to="/" className="menu-brand">Upravljanje događajima</Link>

      <div className="menu-nav">
        {jeAdminIliUrednik && <Link to="/dogadjaji">Događaji</Link>}
        {jeAdminIliUrednik && <Link to="/evidentiranje">Evidentiranje</Link>}
        {jeAdmin && <Link to="/urednici">Urednici</Link>}
      </div>

      <div className="menu-nalog">
        {isLoggedIn && <span className="menu-korisnik">{korisnik?.username}</span>}
        {isLoggedIn ? (
          <button onClick={logout} className="menu-logout">Odjavi se</button>
        ) : (
          <Link to="/login" className="menu-logout menu-login-link">Prijavi se</Link>
        )}
      </div>
    </nav>
  )
}