import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/axios'
import DodajUrednikaModal from '../components/DodajUrednikaModal'
import IzmeniUrednikaModal from '../components/IzmeniUrednikaModal'

export default function UredniciPage() {
  const [tekst, setTekst] = useState('')
  const [urednici, setUrednici] = useState([])
  const [poruka, setPoruka] = useState('')
  const [infoTekst, setInfoTekst] = useState('') 

  const [otvorenDodaj, setOtvorenDodaj] = useState(false)
  const [otvorenIzmeniId, setOtvorenIzmeniId] = useState(null)

  const pretrazi = useCallback(async (vrednost) => {
    try {
      const response = await api.get('/korisnik/urednik/pretraga', {
        params: { tekst: vrednost },
      })
      setUrednici(response.data.urednici)
      setInfoTekst('')
    } catch (err) {
      setUrednici([])
      setInfoTekst(err.response?.data?.message || 'Nema pronađenih urednika.')
    }
  }, [])
  
  useEffect(() => {
    const timer = setTimeout(() => {
      pretrazi(tekst)
    }, 300)
    return () => clearTimeout(timer)
  }, [tekst, pretrazi])

  function osveziListu() {
    pretrazi(tekst)
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

  return (
    <div className="page">
      <nav className="menu">
        <Link to="/">← Nazad na početnu</Link>
      </nav>

      <h1>Urednici</h1>

      {poruka && <p className="success">{poruka}</p>}

      <div className="urednici-toolbar">
        <div className="search-wrapper">
          <input
            type="text"
            placeholder="Pretraži po imenu ili email-u..."
            value={tekst}
            onChange={(e) => setTekst(e.target.value)}
            className="urednici-search"
          />
          {tekst && (
            <button
              type="button"
              className="search-clear"
              onClick={() => setTekst('')}
              title="Prikaži sve"
            >
              ×
            </button>
          )}
        </div>
        <button onClick={() => setOtvorenDodaj(true)}>Dodaj urednika</button>
      </div>

      {infoTekst && <p className="info-text">{infoTekst}</p>}

      <ul className="urednici-lista">
        {urednici.map((u) => (
          <li key={u.korisnikId} className="urednik-red">
            <span className="urednik-email">{u.username} — {u.email}</span>
            <button onClick={() => setOtvorenIzmeniId(u.korisnikId)}>Uredi</button>
          </li>
        ))}
      </ul>

      {otvorenDodaj && (
        <DodajUrednikaModal
          onClose={() => setOtvorenDodaj(false)}
          onCreated={handleCreated}
        />
      )}

      {otvorenIzmeniId && (
        <IzmeniUrednikaModal
          urednikId={otvorenIzmeniId}
          onClose={() => setOtvorenIzmeniId(null)}
          onEdited={handleEdited}
        />
      )}
    </div>
  )
}