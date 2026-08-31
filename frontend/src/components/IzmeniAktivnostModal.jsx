import { useState, useEffect } from 'react'
import api from '../api/axios'

export default function IzmeniAktivnostModal({ dogadjajId, aktivnostId, onClose, onEdited }) {
  const [form, setForm] = useState({
    naziv: '',
    opis: '',
    datumOdrzavanja: '',
    rokZaPrijavu: '',
    maksUcesnika: '',
    mestoOdrzavanja: '',
  })
  const [ucitavanje, setUcitavanje] = useState(true)
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [poruka, setPoruka] = useState('')

 
useEffect(() => {
  async function ucitaj() {
    try {
          const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}`)
          setForm({
            naziv: response.data.podaci.naziv,
            opis: response.data.podaci.opis,
            datumOdrzavanja: response.data.podaci.datumOdrzavanja,
            rokZaPrijavu: response.data.podaci.rokZaPrijavu,
            maksUcesnika: response.data.podaci.maksUcesnika,
            mestoOdrzavanja: response.data.podaci.mestoOdrzavanja,
          })
          setPoruka(response.data.poruka)
          setTimeout(() => setPoruka(''), 4000)
        } catch (err) {
      setError(err.response?.data?.message || 'Greška pri učitavanju aktivnosti.')
    } finally {
      setUcitavanje(false)
    }
  }
  ucitaj()
}, [dogadjajId, aktivnostId])

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
      const response = await api.put(`/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}`, payload)
      onEdited(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom izmene aktivnosti.')
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
            <h1>Izmeni aktivnost</h1>

            {poruka && <p className="success">{poruka}</p>}
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
              {loading ? 'Čuvanje...' : 'Sačuvaj izmene'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}