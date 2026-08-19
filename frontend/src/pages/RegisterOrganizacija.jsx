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
      const payload = { ...form, pib: Number(form.pib), mb: Number(form.mb) }
      await api.post('/auth/register-organizacija', payload)
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
            Organizacija je kreirana. Poslali smo link za potvrdu naloga na{' '}
            <strong>{form.email}</strong>. Klikni na njega, pa se vrati i prijavi kao Admin.
          </p>
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
          </label>

          <label>
            PIB
            <input name="pib" value={form.pib} onChange={handleChange} />
          </label>

          <label>
            Matični broj
            <input name="mb" value={form.mb} onChange={handleChange} />
          </label>

          <label>
            Adresa
            <input name="adresa" value={form.adresa} onChange={handleChange} />
          </label>
        </fieldset>

        <fieldset>
          <legend>Tvoj administratorski nalog</legend>

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