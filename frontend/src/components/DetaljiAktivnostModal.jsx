import { useState, useEffect } from 'react'
import api from '../api/axios'
import { formatDatum } from '../utils/format'

export default function DetaljiAktivnostModal({ dogadjajId, aktivnostId, onClose }) {
  const [aktivnost, setAktivnost] = useState(null)
  const [ucitavanje, setUcitavanje] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function ucitaj() {
      try {
        const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}`)
        setAktivnost(response.data.podaci)
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri učitavanju aktivnosti.')
      } finally {
        setUcitavanje(false)
      }
    }
    ucitaj()
  }, [dogadjajId, aktivnostId])

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        {ucitavanje ? (
          <p>Učitavanje...</p>
        ) : error ? (
          <p className="error">{error}</p>
        ) : (
          <div className="auth-form">
            <h1>Detalji aktivnosti</h1>

            <label>
              Naziv
              <input value={aktivnost.naziv} disabled />
            </label>

            <label>
              Opis
              <textarea value={aktivnost.opis} rows={3} disabled />
            </label>

            <label>
              Datum održavanja
              <input value={formatDatum(aktivnost.datumOdrzavanja)} disabled />
            </label>

            <label>
              Rok za prijavu
              <input value={formatDatum(aktivnost.rokZaPrijavu)} disabled />
            </label>

            <label>
              Maksimalan broj učesnika
              <input value={aktivnost.maksUcesnika} disabled />
            </label>

            <label>
              Mesto održavanja
              <input value={aktivnost.mestoOdrzavanja} disabled />
            </label>

            <button type="button" onClick={onClose}>
              Zatvori
            </button>
          </div>
        )}
      </div>
    </div>
  )
}