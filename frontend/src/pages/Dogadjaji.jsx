import { useState, useEffect, useCallback } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import api from '../api/axios'
import DodajDogadjajModal from '../components/DodajDogadjajModal'
import IzmeniDogadjajModal from '../components/IzmeniDogadjajModal'
import Meni from '../components/Meni'

export default function DogadjajiPage() {
  const [tekst, setTekst] = useState('')
  const [dogadjaji, setDogadjaji] = useState([])
  const [poruka, setPoruka] = useState('')
  const [infoTekst, setInfoTekst] = useState('')

  const [otvorenDodaj, setOtvorenDodaj] = useState(false)
  const [otvorenIzmeniId, setOtvorenIzmeniId] = useState(null)

  const navigate = useNavigate()

  const pretrazi = useCallback(async (vrednost) => {
    try {
      const response = await api.get('/dogadjaj/pretraga', { params: { tekst: vrednost } })
      setDogadjaji(response.data.podaci)
      setInfoTekst('')
    } catch (err) {
      setDogadjaji([])
      setInfoTekst(err.response?.data?.message || 'Nema pronađenih događaja.')
    }
  }, [])

  useEffect(() => {
    const timer = setTimeout(() => pretrazi(tekst), 300)
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

  async function obrisi(id, naziv) {
    if (!window.confirm(`Da li sigurno želiš da obrišeš događaj "${naziv}"?`)) return

    try {
      const response = await api.delete(`/dogadjaj/${id}`)
      setPoruka(response.data.poruka)
      osveziListu()
      setTimeout(() => setPoruka(''), 4000)
    } catch (err) {
      alert(err.response?.data?.message || 'Greška prilikom brisanja događaja.')
    }
  }

  return (
    <div className="page page-wide">
      <Meni />

      <h1>Događaji</h1>

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
        <button onClick={() => setOtvorenDodaj(true)}>Dodaj događaj</button>
      </div>

      {infoTekst && <p className="info-text">{infoTekst}</p>}

      <div className="dogadjaji-grid">
        {dogadjaji.map((d) => (
          <div key={d.dogadjajId} className="dogadjaj-kartica">
            <div
              className="dogadjaj-slika"
              style={d.slika ? { backgroundImage: `url(${d.slika})` } : undefined}
              onClick={() => navigate(`/dogadjaji/${d.dogadjajId}`)}
            >
              {!d.slika && <span className="dogadjaj-slika-placeholder">Bez slike</span>}
            </div>
            <div className="dogadjaj-sadrzaj">
              <h3 onClick={() => navigate(`/dogadjaji/${d.dogadjajId}`)}>{d.naziv}</h3>
              <p className="dogadjaj-opis">{d.opis}</p>
              <Link to={`/dogadjaji/${d.dogadjajId}`} className="dogadjaj-detalji-link">
                Pogledaj detalje →
              </Link>
              <div className="dogadjaj-akcije">
                <button onClick={() => setOtvorenIzmeniId(d.dogadjajId)}>Uredi</button>
                <button className="danger" onClick={() => obrisi(d.dogadjajId, d.naziv)}>Obriši</button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {otvorenDodaj && (
        <DodajDogadjajModal onClose={() => setOtvorenDodaj(false)} onCreated={handleCreated} />
      )}

      {otvorenIzmeniId && (
        <IzmeniDogadjajModal
          dogadjajId={otvorenIzmeniId}
          onClose={() => setOtvorenIzmeniId(null)}
          onEdited={handleEdited}
        />
      )}
    </div>
  )
}