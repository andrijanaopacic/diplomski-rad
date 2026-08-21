import { useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'

const PRAZNA_FORMA = {
  nazivOrganizacije: '',
  pib: '',
  mb: '',
  adresa: '',
  username: '',
  email: '',
  password: '',
}

export default function RegisterOrganizacija() {
  const [form, setForm] = useState(PRAZNA_FORMA)
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
      const response = await api.post('/auth/register-organizacija', form)
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
        <h1>Registracija - organizacija</h1>

        {error && <p className="error">{error}</p>}

        <fieldset>
          <legend>Podaci o organizaciji</legend>

          <label>
            Naziv organizacije
            <input
              name="nazivOrganizacije"
              value={form.nazivOrganizacije}
              onChange={handleChange}
            />
            {fieldErrors.nazivOrganizacije && (
              <small className="field-error">{fieldErrors.nazivOrganizacije}</small>
            )}
          </label>

          <label>
            PIB
            <input name="pib" value={form.pib} onChange={handleChange} />
            {fieldErrors.pib && <small className="field-error">{fieldErrors.pib}</small>}
          </label>

          <label>
            Matični broj
            <input name="mb" value={form.mb} onChange={handleChange} />
            {fieldErrors.mb && <small className="field-error">{fieldErrors.mb}</small>}
          </label>

          <label>
            Adresa
            <input name="adresa" value={form.adresa} onChange={handleChange} />
            {fieldErrors.adresa && <small className="field-error">{fieldErrors.adresa}</small>}
          </label>
        </fieldset>

        <fieldset>
          <legend>Tvoj administratorski nalog</legend>

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
        </fieldset>

        <button type="submit" disabled={loading}>
          {loading ? 'Registracija...' : 'Kreiraj organizaciju'}
        </button>

        <p className="auth-links">
          Već imaš nalog? <Link to="/login">Prijavi se</Link>
        </p>
      </form>
    </div>
  )
}