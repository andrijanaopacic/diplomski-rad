import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import { useAuth } from '../context/AuthContext'

export default function JavniDogadjaji() {
  const [tekst, setTekst] = useState('')
  const [dogadjaji, setDogadjaji] = useState([])
  const [infoTekst, setInfoTekst] = useState('')

  const { isLoggedIn, korisnik } = useAuth()
  const navigate = useNavigate()

  const pretrazi = useCallback(async (vrednost) => {
    try {
      const response = await api.get('/dogadjaj/javno/pretraga', { params: { tekst: vrednost } })
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

  function prijaviSe(dogadjaj) {
    if (!isLoggedIn) {
      navigate('/login')
      return
    }
    if (korisnik?.uloga === 'UCESNIK') {
      navigate(`/prijava/${dogadjaj.dogadjajId}`)
    }
  }

  return (
    <>
      <h1>Predstojeći događaji</h1>

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

      <div className="dogadjaji-grid">
        {dogadjaji.map((d) => (
          <div key={d.dogadjajId} className="dogadjaj-kartica">
            <div
              className="dogadjaj-slika"
              style={d.slika ? { backgroundImage: `url(${d.slika})` } : undefined}
            >
              {!d.slika && <span className="dogadjaj-slika-placeholder">Bez slike</span>}
            </div>
            <div className="dogadjaj-sadrzaj">
              <h3>{d.naziv}</h3>
              <p className="dogadjaj-organizacija">Organizator: {d.organizacijaNaziv}</p>
              <p className="dogadjaj-opis">{d.opis}</p>
              <div className="dogadjaj-akcije">
                <button onClick={() => prijaviSe(d)}>Aktivnosti</button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </>
  )
}