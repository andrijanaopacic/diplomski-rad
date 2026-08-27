import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'

export default function IzmeniDogadjajModal({ dogadjajId, onClose, onEdited }) {
  const navigate = useNavigate()
  const [form, setForm] = useState({ naziv: '', opis: '', slika: '' })
  const [ucitavanje, setUcitavanje] = useState(true)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    async function ucitaj() {
      try {
        const response = await api.get(`/dogadjaj/${dogadjajId}`)
        setForm({
          naziv: response.data.naziv,
          opis: response.data.opis,
          slika: response.data.slika || '',
        })
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri učitavanju događaja.')
      } finally {
        setUcitavanje(false)
      }
    }
    ucitaj()
  }, [dogadjajId])

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    try {
      const response = await api.put(`/dogadjaj/${dogadjajId}`, form)
      onEdited(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom izmene događaja.')
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
            <h1>Izmeni događaj</h1>

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
              {loading ? 'Čuvanje...' : 'Sačuvaj izmene'}
            </button>

            <button
              type="button"
              onClick={() => navigate(`/dogadjaji/${dogadjajId}`)}
              className="sekundarno-dugme"
            >
              Upravljaj aktivnostima i formama →
            </button>
          </form>
        )}
      </div>
    </div>
  )
}