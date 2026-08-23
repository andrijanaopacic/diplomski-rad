import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Home() {
  const { korisnik, logout } = useAuth()

 
  const jeAdmin = korisnik?.uloga === 'ADMIN'

  return (
    <div className="page">
      <nav className="menu">
        <span className="menu-brand">Upravljanje događajima</span>
        <div className="menu-links">
          {jeAdmin && <Link to="/urednici">Urednici</Link>}
          <button onClick={logout} className="menu-logout">Odjavi se</button>
        </div>
      </nav>

      <h1>Zdravo, {korisnik?.username}!</h1>
      <p>Uloga: {korisnik?.uloga}</p>
      <p>Email: {korisnik?.email}</p>
    </div>
  )
}