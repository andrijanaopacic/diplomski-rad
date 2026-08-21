import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'

export default function RegisterUcesnik() {
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [poruka, setPoruka] = useState('')
  const [loading, setLoading] = useState(false)

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    try {
      const response = await api.post('/auth/register', form)
      setPoruka(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom registracije.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  if (poruka) {
    return (
      <div className="auth-page">
        <div className="auth-form">
          <h1>Proveri mejl</h1>
          <p>{poruka}</p>
          <Link to="/login">Nazad na prijavu</Link>
        </div>
      </div>
    )
  }

  return (
    <div className="auth-page">
      <form onSubmit={handleSubmit} className="auth-form" noValidate>
        <h1>Registracija - učesnik</h1>

        {error && <p className="error">{error}</p>}

        <label>
          Korisničko ime
          <input name="username" value={form.username} onChange={handleChange} />
          {fieldErrors.username && <small className="field-error">{fieldErrors.username}</small>}
        </label>

        <label>
          Email
          <input type="text" name="email" value={form.email} onChange={handleChange} />
          {fieldErrors.email && <small className="field-error">{fieldErrors.email}</small>}
        </label>

        <label>
          Lozinka
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
          />
          {fieldErrors.password && <small className="field-error">{fieldErrors.password}</small>}
        </label>

        <button type="submit" disabled={loading}>
          {loading ? 'Registracija...' : 'Registruj se'}
        </button>

        <p className="auth-links">
          Već imaš nalog? <Link to="/login">Prijavi se</Link>
        </p>
      </form>
    </div>
  )
}