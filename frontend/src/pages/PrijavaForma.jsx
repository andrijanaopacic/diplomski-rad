import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import Meni from '../components/Meni'
import { formatDatum } from '../utils/format'

const TIP_U_INPUT = {
  STRING: 'text',
  INTEGER: 'number',
  DOUBLE: 'number',
  LOCAL_DATE: 'date',
}

const NAZIV_STATUSA = {
  POTVRDJENA: 'Potvrđena',
  NA_CEKANJU: 'Na listi čekanja',
}

export default function PrijavaFormaPage() {
  const { dogadjajId, aktivnostId } = useParams()
  const navigate = useNavigate()

  const [ucitavanje, setUcitavanje] = useState(true)
  const [aktivnost, setAktivnost] = useState(null)
  const [forma, setForma] = useState(null)
  const [mojStatus, setMojStatus] = useState(null)
  const [odgovori, setOdgovori] = useState({})

  const [poruka, setPoruka] = useState('')
  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

async function ucitaj() {
  try {
    const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/javno/${aktivnostId}`)
    setAktivnost(response.data.podaci.aktivnost)
    setForma(response.data.podaci.forma)
    setMojStatus(response.data.podaci.mojStatusPrijave)
  } catch (err) {
    setError(err.response?.data?.message || 'Greška pri učitavanju aktivnosti.')
  } finally {
    setUcitavanje(false)
  }
}

  useEffect(() => {
    ucitaj()
  }, [dogadjajId, aktivnostId])

  function izmeniOdgovor(poljeFormeId, vrednost) {
    setOdgovori({ ...odgovori, [poljeFormeId]: vrednost })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    const payload = {
      odgovori: forma
        ? forma.polja.map((p) => ({
            poljeFormeId: p.poljeFormeId,
            vrednost: odgovori[p.poljeFormeId] || '',
          }))
        : [],
    }

    try {
      const response = await api.post(`/aktivnost/${aktivnostId}/prijava`, payload)
      setPoruka(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom slanja prijave.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  async function handleOtkazi() {
    if (!window.confirm('Da li sigurno želiš da otkažeš prijavu?')) return

    setLoading(true)
    try {
      const response = await api.delete(`/aktivnost/${aktivnostId}/prijava`)
      setPoruka(response.data.poruka)
      setMojStatus(null)
    } catch (err) {
      alert(err.response?.data?.message || 'Greška prilikom otkazivanja prijave.')
    } finally {
      setLoading(false)
    }
  }

  if (ucitavanje) {
    return <div className="page"><p>Učitavanje...</p></div>
  }

  if (error && !aktivnost) {
    return (
      <div className="page page-wide">
        <Meni />
        <div className="sadrzaj-centriran">
          <p className="error">{error}</p>
          <Link to={`/prijava/${dogadjajId}`} className="dogadjaj-detalji-link">← Nazad na aktivnosti</Link>
        </div>
      </div>
    )
  }

  if (poruka) {
    return (
      <div className="page">
        <Meni />
        <div className="sadrzaj-centriran">
          <p className="success">{poruka}</p>
          <button onClick={() => navigate('/')}>Nazad na početnu</button>
        </div>
      </div>
    )
  }

  const razlogGreske = fieldErrors.razlog

  return (
    <div className="page page-wide">
      <Meni />

      <div className="sadrzaj-centriran">
        <Link to={`/prijava/${dogadjajId}`} className="dogadjaj-detalji-link">← Nazad na aktivnosti</Link>

        <h1>{aktivnost.naziv}</h1>
        <p>{aktivnost.opis}</p>
        <p className="info-text">
          {formatDatum(aktivnost.datumOdrzavanja)} — {aktivnost.mestoOdrzavanja}
        </p>

        {mojStatus ? (
          <div className="auth-form">
            <h1>Vaša prijava</h1>
            <p className="success">Status: {NAZIV_STATUSA[mojStatus] || mojStatus}</p>
            <button className="danger" onClick={handleOtkazi} disabled={loading}>
              {loading ? 'Otkazivanje...' : 'Otkaži prijavu'}
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="auth-form" noValidate>
            <h1>Prijava</h1>

            {error && <p className="error">{error}</p>}
            {razlogGreske && <p className="error">{razlogGreske}</p>}

            {forma ? (
              forma.polja.map((polje) => (
                <label key={polje.poljeFormeId}>
                  {polje.naziv}{polje.obavezno && ' *'}
                  <input
                    type={TIP_U_INPUT[polje.tip] || 'text'}
                    value={odgovori[polje.poljeFormeId] || ''}
                    onChange={(e) => izmeniOdgovor(polje.poljeFormeId, e.target.value)}
                  />
                  {fieldErrors[polje.naziv] && (
                    <small className="field-error">{fieldErrors[polje.naziv]}</small>
                  )}
                </label>
              ))
            ) : (
              <p className="info-text">Ova aktivnost nema formu - prijava se vrši jednim klikom.</p>
            )}

            <button type="submit" disabled={loading}>
              {loading ? 'Slanje...' : 'Pošalji prijavu'}
            </button>
          </form>
        )}
      </div>
    </div>
  )
}