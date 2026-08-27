import { useState, useEffect } from 'react'
import api from '../api/axios'
import { formatDatum } from '../utils/format'

const NAZIV_STATUSA = {
  POTVRDJENA: 'Potvrđena',
  NA_CEKANJU: 'Na listi čekanja',
  OTKAZANA: 'Otkazana',
}

export default function PrijavaDetaljiModal({ dogadjajId, aktivnostId, prijavaId, onClose }) {
  const [ucitavanje, setUcitavanje] = useState(true)
  const [prijava, setPrijava] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    async function ucitaj() {
      try {
        const response = await api.get(
          `/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}/prijava/${prijavaId}`
        )
        setPrijava(response.data)
      } catch (err) {
        setError(err.response?.data?.message || 'Greška pri učitavanju prijave.')
      } finally {
        setUcitavanje(false)
      }
    }
    ucitaj()
  }, [dogadjajId, aktivnostId, prijavaId])

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
            <h1>{prijava.korisnickoIme}</h1>

            <p><strong>Email:</strong> {prijava.korisnikEmail}</p>
            <p><strong>Status:</strong> {NAZIV_STATUSA[prijava.statusPrijave] || prijava.statusPrijave}</p>
            <p><strong>Datum prijave:</strong> {formatDatum(prijava.datumPrijave)}</p>
            <p>
              <strong>Prisustvo:</strong>{' '}
              {prijava.dosao ? `Da (${formatDatum(prijava.vremeDolaska)})` : 'Još nije evidentirano'}
            </p>

            <div className="polja-forme-lista">
              <p className="polja-forme-naslov">Odgovori na formu</p>
              {prijava.odgovori.length === 0 ? (
                <p className="info-text">Nema odgovora (aktivnost nema formu).</p>
              ) : (
                prijava.odgovori.map((o, i) => (
                  <p key={i}>
                    <strong>{o.poljeNaziv}:</strong> {o.vrednost}
                  </p>
                ))
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}