import { useAuth } from '../context/AuthContext'

export default function Home() {
  const { korisnik, logout } = useAuth()

  return (
    <div className="page">
      <h1>Zdravo, {korisnik?.username}!</h1>
      <p>Uloga: {korisnik?.uloga}</p>
      <p>Email: {korisnik?.email}</p>
      <button onClick={logout}>Odjavi se</button>
    </div>
  )
}