import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'

export default function RegisterUcesnik() {
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      await api.post('/auth/register', form)
      setSuccess(true)
    } catch (err) {
      const poruka = err.response?.data?.message || 'Greška prilikom registracije.'
      setError(poruka)
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="auth-page">
        <div className="auth-form">
          <h1>Proveri mejl</h1>
          <p>
            Poslali smo ti link za potvrdu naloga na <strong>{form.email}</strong>.
            Klikni na njega da aktiviraš nalog, pa se onda vrati i prijavi.
          </p>
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
        </label>

        <label>
          Email
          <input type="text" name="email" value={form.email} onChange={handleChange} />
        </label>

        <label>
          Lozinka
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
          />
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