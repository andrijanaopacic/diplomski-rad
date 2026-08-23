import { useState } from 'react'
import api from '../api/axios'

export default function DodajUrednikaModal({ onClose, onCreated }) {
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
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
      const response = await api.post('/korisnik/urednik', form)
      onCreated(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom kreiranja urednika.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          <h1>Dodaj urednika</h1>

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
            {loading ? 'Kreiranje...' : 'Kreiraj urednika'}
          </button>
        </form>
      </div>
    </div>
  )
}