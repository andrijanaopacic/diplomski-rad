import { useState, useEffect, useCallback } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import api from '../api/axios'
import DodajAktivnostModal from '../components/DodajAktivnostModal'
import IzmeniAktivnostModal from '../components/IzmeniAktivnostModal'
import FormaModal from '../components/FormaModal'
import Meni from '../components/Meni'
import { formatDatum } from '../utils/format'
import DetaljiAktivnostModal from '../components/DetaljiAktivnostModal'

export default function DogadjajDetaljiPage() {
  const { id: dogadjajId } = useParams()
  const navigate = useNavigate()

  const [dogadjaj, setDogadjaj] = useState(null)
  const [dogadjajError, setDogadjajError] = useState('')

  const [tekst, setTekst] = useState('')
  const [aktivnosti, setAktivnosti] = useState([])
  const [poruka, setPoruka] = useState('')
  const [infoTekst, setInfoTekst] = useState('')

  const [otvorenDodaj, setOtvorenDodaj] = useState(false)
  const [otvorenIzmeniId, setOtvorenIzmeniId] = useState(null)
  const [otvorenDetaljiId, setOtvorenDetaljiId] = useState(null)
  const [otvorenaFormaZaId, setOtvorenaFormaZaId] = useState(null)


  useEffect(() => {
      async function ucitajDogadjaj() {
        try {
          const response = await api.get(`/dogadjaj/${dogadjajId}`)
          setDogadjaj(response.data.podaci)
          setPoruka(response.data.poruka)
          setTimeout(() => setPoruka(''), 4000)
        } catch (err) {
          setDogadjajError(err.response?.data?.message || 'Greška pri učitavanju događaja.')
        }
      }
      ucitajDogadjaj()
    }, [dogadjajId])

  const pretrazi = useCallback(async (vrednost, prikaziPoruku = true) => {
      try {
        const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/pretraga`, {
          params: { tekst: vrednost },
        })
        setAktivnosti(response.data.podaci)
        if (prikaziPoruku) {
          setPoruka(response.data.poruka)
          setTimeout(() => setPoruka(''), 4000)
        }
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

  function osveziListu() {
    pretrazi(tekst, false)
  }

  function handleCreated(porukaSaServera) {
    setPoruka(porukaSaServera)
    setOtvorenDodaj(false)
    osveziListu()
    setTimeout(() => setPoruka(''), 4000)
  }

  function handleEdited(porukaSaServera) {
    setPoruka(porukaSaServera)
    setOtvorenIzmeniId(null)
    osveziListu()
    setTimeout(() => setPoruka(''), 4000)
  }

  async function obrisi(aktivnostId, naziv) {
    if (!window.confirm(`Da li sigurno želite da obrišete aktivnost "${naziv}"?`)) return

    try {
      const response = await api.delete(`/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}`)
      setPoruka(response.data.poruka)
      osveziListu()
      setTimeout(() => setPoruka(''), 4000)
    } catch (err) {
      const data = err.response?.data
      const tekstGreske = data?.message || 'Greška prilikom brisanja aktivnosti.'
      const razlog = data?.fieldErrors?.razlog
      alert(razlog ? `${tekstGreske}\n\nRazlog: ${razlog}` : tekstGreske)
    }
  }

  if (dogadjajError) {
    return (
      <div className="page">
        <Meni />
        <p className="error">{dogadjajError}</p>
        <Link to="/dogadjaji">← Nazad na događaje</Link>
      </div>
    )
  }

  if (!dogadjaj) {
    return <div className="page"><p>Učitavanje...</p></div>
  }

  return (
    <div className="page page-wide">
      <Meni />
      <Link to="/dogadjaji" className="dogadjaj-detalji-link">← Nazad na događaje</Link>

      {dogadjaj.slika && (
        <div className="dogadjaj-detalji-slika" style={{ backgroundImage: `url(${dogadjaj.slika})` }} />
      )}

      <h1>{dogadjaj.naziv}</h1>
      <p>{dogadjaj.opis}</p>

      <div className="aktivnosti-sekcija">
        <h2>Aktivnosti</h2>

        {poruka && <p className="success">{poruka}</p>}

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
          <button onClick={() => setOtvorenDodaj(true)}>Dodaj aktivnost</button>
        </div>

        {infoTekst && <p className="info-text">{infoTekst}</p>}

        <ul className="urednici-lista">
          {aktivnosti.map((a) => (
            <li key={a.aktivnostId} className="urednik-red">
              <span className="urednik-email">
                {a.naziv} — {formatDatum(a.datumOdrzavanja)} — {a.mestoOdrzavanja}
              </span>
              <div className="dogadjaj-akcije">
                <button onClick={() => setOtvorenDetaljiId(a.aktivnostId)}>Detalji</button>
                <button onClick={() => setOtvorenIzmeniId(a.aktivnostId)}>Uredi</button>
                <button onClick={() => setOtvorenaFormaZaId(a.aktivnostId)}>Forma</button>
                <button onClick={() => navigate(`/dogadjaji/${dogadjajId}/aktivnost/${a.aktivnostId}/prijave`)}>
                  Prijave
                </button>
                <button className="danger" onClick={() => obrisi(a.aktivnostId, a.naziv)}>Obriši</button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {otvorenDodaj && (
        <DodajAktivnostModal
          dogadjajId={dogadjajId}
          onClose={() => setOtvorenDodaj(false)}
          onCreated={handleCreated}
        />
      )}

      {otvorenIzmeniId && (
        <IzmeniAktivnostModal
          dogadjajId={dogadjajId}
          aktivnostId={otvorenIzmeniId}
          onClose={() => setOtvorenIzmeniId(null)}
          onEdited={handleEdited}
        />
      )}

      {otvorenDetaljiId && (
        <DetaljiAktivnostModal
          dogadjajId={dogadjajId}
          aktivnostId={otvorenDetaljiId}
          onClose={() => setOtvorenDetaljiId(null)}
        />
      )}

      {otvorenaFormaZaId && (
        <FormaModal
          dogadjajId={dogadjajId}
          aktivnostId={otvorenaFormaZaId}
          onClose={() => setOtvorenaFormaZaId(null)}
          onSaved={(porukaSaServera) => {
            setPoruka(porukaSaServera)
            setTimeout(() => setPoruka(''), 4000)
          }}
        />
      )}
    </div>
  )
}