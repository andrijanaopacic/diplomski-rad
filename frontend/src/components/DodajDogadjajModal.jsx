import { useState } from 'react'
import api from '../api/axios'

export default function DodajDogadjajModal({ onClose, onCreated }) {
  const [form, setForm] = useState({ naziv: '', opis: '', slika: '' })
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
      const response = await api.post('/dogadjaj', form)
      onCreated(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom kreiranja događaja.')
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
          <h1>Dodaj događaj</h1>

          {error && <p className="error">{error}</p>}

          <label>
            Naziv
            <input name="naziv" value={form.naziv} onChange={handleChange} />
            {fieldErrors.naziv && <small className="field-error">{fieldErrors.naziv}</small>}
          </label>

          <label>
            Opis
            <textarea name="opis" value={form.opis} onChange={handleChange} rows={4} />
            {fieldErrors.opis && <small className="field-error">{fieldErrors.opis}</small>}
          </label>

          <label>
            Link ka slici (opciono)
            <input name="slika" value={form.slika} onChange={handleChange} placeholder="https://..." />
            {fieldErrors.slika && <small className="field-error">{fieldErrors.slika}</small>}
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Kreiranje...' : 'Kreiraj događaj'}
          </button>
        </form>
      </div>
    </div>
  )
}