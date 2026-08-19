import { useState } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()

  const verified = searchParams.get('verified')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const response = await api.post('/auth/login', { username, password })
      login(response.data)
      navigate('/')
    } catch (err) {
      const poruka = err.response?.data?.message || 'Greška prilikom prijave.'
      setError(poruka)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <h1>Prijava</h1>

        {verified === 'success' && (
          <p className="success">Nalog je aktiviran. Sada se možeš prijaviti.</p>
        )}
        {verified === 'error' && (
          <p className="error">Link za potvrdu nije validan ili je istekao.</p>
        )}

        {error && <p className="error">{error}</p>}

        <label>
          Korisničko ime
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </label>

        <label>
          Lozinka
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Prijavljivanje...' : 'Prijavi se'}
        </button>

        <p className="auth-links">
          Nemaš nalog? <Link to="/registracija-ucesnik">Registruj se kao učesnik</Link>
          {' ili '}
          <Link to="/registracija-organizacija">registruj organizaciju</Link>
        </p>
      </form>
    </div>
  )
}