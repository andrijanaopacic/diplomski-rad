import { useState } from 'react'
import { useNavigate, useSearchParams, Link } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()

  const verified = searchParams.get('verified')
  const verifikacionaPoruka = searchParams.get('poruka')

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    try {
      const response = await api.post('/auth/login', { username, password })
      login(response.data)
      alert(response.data.poruka)
      navigate('/')
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom prijave.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <h1>Prijava</h1>

        {verified === 'success' && (
          <p className="success">{verifikacionaPoruka}</p>
        )}
        {verified === 'error' && (
          <p className="error">{verifikacionaPoruka}</p>
        )}

        {error && <p className="error">{error}</p>}

        <label>
          Korisničko ime
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          {fieldErrors.username && <small className="field-error">{fieldErrors.username}</small>}
        </label>

        <label>
          Lozinka
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {fieldErrors.password && <small className="field-error">{fieldErrors.password}</small>}
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