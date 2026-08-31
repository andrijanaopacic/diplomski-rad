import { useState, useEffect } from 'react'
import api from '../api/axios'

export default function IzmeniUrednikaModal({ urednikId, onClose, onEdited }) {
  
  const [form, setForm] = useState({ email: '', enabled: true, password: '' })
  const [ucitavanje, setUcitavanje] = useState(true)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [poruka, setPoruka] = useState('')

  useEffect(() => {
    async function ucitaj() {
      try {
        const response = await api.get(`/korisnik/urednik/${urednikId}`)
        setForm({
          email: response.data.podaci.email,
          enabled: response.data.podaci.enabled,
          password: '',
        })
        setPoruka(response.data.poruka)
        setTimeout(() => setPoruka(''), 4000)
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri učitavanju urednika.')
      } finally {
        setUcitavanje(false)
      }
    }
    ucitaj()
  }, [urednikId])

  function handleChange(e) {
    const { name, value, type, checked } = e.target
    setForm({ ...form, [name]: type === 'checkbox' ? checked : value })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    const payload = {
      email: form.email,
      enabled: form.enabled,
      ...(form.password ? { password: form.password } : {}),
    }

    try {
      const response = await api.put(`/korisnik/urednik/${urednikId}`, payload)
      onEdited(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom izmene urednika.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        {ucitavanje ? (
          <p>Učitavanje...</p>
        ) : (
          <form onSubmit={handleSubmit} className="auth-form" noValidate>
            <h1>Izmeni urednika</h1>

            {poruka && <p className="success">{poruka}</p>}
            {error && <p className="error">{error}</p>}

            <label>
              Email
              <input type="text" name="email" value={form.email} onChange={handleChange} />
              {fieldErrors.email && <small className="field-error">{fieldErrors.email}</small>}
            </label>

            <label className="checkbox-label">
              <input
                type="checkbox"
                name="enabled"
                checked={form.enabled}
                onChange={handleChange}
              />
              Nalog aktivan
            </label>

            <label>
              Nova lozinka (ostavi prazno da je ne menjaš)
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
              />
              {fieldErrors.password && <small className="field-error">{fieldErrors.password}</small>}
            </label>

            <button type="submit" disabled={loading}>
              {loading ? 'Čuvanje...' : 'Sačuvaj izmene'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}