import { useState } from 'react'
import api from '../api/axios'

export default function DodajAktivnostModal({ dogadjajId, onClose, onCreated }) {
  const [form, setForm] = useState({
    naziv: '',
    opis: '',
    datumOdrzavanja: '',
    rokZaPrijavu: '',
    maksUcesnika: '',
    mestoOdrzavanja: '',
  })
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

    const payload = { ...form, maksUcesnika: Number(form.maksUcesnika) }

    try {
      const response = await api.post(`/dogadjaj/${dogadjajId}/aktivnost`, payload)
      onCreated(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom kreiranja aktivnosti.')
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
          <h1>Dodaj aktivnost</h1>

          {error && <p className="error">{error}</p>}

          <label>
            Naziv
            <input name="naziv" value={form.naziv} onChange={handleChange} />
            {fieldErrors.naziv && <small className="field-error">{fieldErrors.naziv}</small>}
          </label>

          <label>
            Opis
            <textarea name="opis" value={form.opis} onChange={handleChange} rows={3} />
            {fieldErrors.opis && <small className="field-error">{fieldErrors.opis}</small>}
          </label>

          <label>
            Datum održavanja
            <input type="datetime-local" name="datumOdrzavanja" value={form.datumOdrzavanja} onChange={handleChange} />
            {fieldErrors.datumOdrzavanja && <small className="field-error">{fieldErrors.datumOdrzavanja}</small>}
          </label>

          <label>
            Rok za prijavu
            <input type="datetime-local" name="rokZaPrijavu" value={form.rokZaPrijavu} onChange={handleChange} />
            {fieldErrors.rokZaPrijavu && <small className="field-error">{fieldErrors.rokZaPrijavu}</small>}
          </label>

          <label>
            Maksimalan broj učesnika
            <input type="number" name="maksUcesnika" value={form.maksUcesnika} onChange={handleChange} />
            {fieldErrors.maksUcesnika && <small className="field-error">{fieldErrors.maksUcesnika}</small>}
          </label>

          <label>
            Mesto održavanja
            <input name="mestoOdrzavanja" value={form.mestoOdrzavanja} onChange={handleChange} />
            {fieldErrors.mestoOdrzavanja && <small className="field-error">{fieldErrors.mestoOdrzavanja}</small>}
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Kreiranje...' : 'Kreiraj aktivnost'}
          </button>
        </form>
      </div>
    </div>
  )
}