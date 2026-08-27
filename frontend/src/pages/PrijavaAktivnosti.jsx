import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import api from '../api/axios'
import Meni from '../components/Meni'
import { formatDatum } from '../utils/format'

export default function PrijavaAktivnostiPage() {
  const { dogadjajId } = useParams()
  const navigate = useNavigate()

  const [dogadjaj, setDogadjaj] = useState(null)
  const [dogadjajError, setDogadjajError] = useState('')

  const [tekst, setTekst] = useState('')
  const [aktivnosti, setAktivnosti] = useState([])
  const [infoTekst, setInfoTekst] = useState('')

  useEffect(() => {
    async function ucitajDogadjaj() {
      try {
        const response = await api.get(`/dogadjaj/javno/${dogadjajId}`)
        setDogadjaj(response.data)
      } catch (err) {
        setDogadjajError(err.response?.data?.message || 'Greška pri učitavanju događaja.')
      }
    }
    ucitajDogadjaj()
  }, [dogadjajId])

  const pretrazi = useCallback(async (vrednost) => {
    try {
      const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/javno/pretraga`, {
        params: { tekst: vrednost },
      })
      setAktivnosti(response.data.podaci)
      setInfoTekst('')
    } catch (err) {
      setAktivnosti([])
      setInfoTekst(err.response?.data?.message || 'Nema pronađenih aktivnosti.')
    }
  }, [dogadjajId])

  useEffect(() => {
    const timer = setTimeout(() => pretrazi(tekst), 300)
    return () => clearTimeout(timer)
  }, [tekst, pretrazi])

  if (dogadjajError) {
    return (
      <div className="page">
        <Meni />
        <p className="error">{dogadjajError}</p>
        <Link to="/" className="dogadjaj-detalji-link">← Nazad na događaje</Link>
      </div>
    )
  }

  return (
    <div className="page page-wide">
      <Meni />
      <Link to="/" className="dogadjaj-detalji-link">← Nazad na događaje</Link>

      {dogadjaj && (
        <>
          <h1>{dogadjaj.naziv}</h1>
          <p className="dogadjaj-organizacija">Organizator: {dogadjaj.organizacijaNaziv}</p>
          <p>{dogadjaj.opis}</p>
        </>
      )}

      <h2 className="aktivnosti-sekcija-naslov">Aktivnosti</h2>

      <div className="urednici-toolbar">
        <div className="search-wrapper">
          <input
            type="text"
            placeholder="Pretraži po nazivu..."
            value={tekst}
            onChange={(e) => setTekst(e.target.value)}
            className="urednici-search"
          />
          {tekst && (
            <button type="button" className="search-clear" onClick={() => setTekst('')} title="Prikaži sve">
              ×
            </button>
          )}
        </div>
      </div>

      {infoTekst && <p className="info-text">{infoTekst}</p>}

      <ul className="urednici-lista">
        {aktivnosti.map((a) => (
          <li key={a.aktivnostId} className="urednik-red">
            <span className="urednik-email">
              {a.naziv} — {formatDatum(a.datumOdrzavanja)} — {a.mestoOdrzavanja}
            </span>
            <button onClick={() => navigate(`/prijava/${dogadjajId}/aktivnost/${a.aktivnostId}`)}>
              Prijavi se
            </button>
          </li>
        ))}
      </ul>
    </div>
  )
}