import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Scanner } from '@yudiel/react-qr-scanner'
import api from '../api/axios'
import Meni from '../components/Meni'

export default function EvidentiranjePage() {
  const [searchParams] = useSearchParams()

  const [kod, setKod] = useState('')
  const [poruka, setPoruka] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [kameraAktivna, setKameraAktivna] = useState(true)
  const [aktivnostId, setAktivnostId] = useState(searchParams.get('aktivnostId') || null)
  const [prijave, setPrijave] = useState([])

  async function ucitajListu(idAktivnosti) {
    try {
      const response = await api.get(`/aktivnost/${idAktivnosti}/prijava`)
      setPrijave(response.data.podaci)
    } catch (err) {
      setPrijave([])
    }
  }

  useEffect(() => {
    if (aktivnostId) ucitajListu(aktivnostId)
  }, [])

  
  async function posaljiKod(vrednost) {
    if (!vrednost || loading) return

    setError('')
    setPoruka('')
    setLoading(true)

    try {
      
      const response = await api.post('/evidentiranje-prisustva', { kod: vrednost })
      setPoruka(response.data.poruka)
      setKod('')

      const novaAktivnostId = response.data.aktivnostId
      setAktivnostId(novaAktivnostId)
      await ucitajListu(novaAktivnostId)
    } catch (err) {
      const data = err.response?.data
      const razlog = data?.fieldErrors?.razlog
      setError(razlog ? `${data.message} ${razlog}` : (data?.message || 'Greška prilikom evidentiranja.'))
    } finally {
      setLoading(false)
    }
  }

  function handleScan(rezultat) {
    if (rezultat && rezultat.length > 0) {
      posaljiKod(rezultat[0].rawValue)
    }
  }

  async function handleRucniUnos(e) {
    e.preventDefault()
    posaljiKod(kod)
  }

  const dosli = prijave.filter((p) => p.dosao)
  const nisuDosli = prijave.filter((p) => !p.dosao)

  return (
    <div className="page page-wide">
      <Meni />

      <div className="sadrzaj-centriran">
        <h1>Evidentiranje prisustva</h1>

        {poruka && <p className="success">{poruka}</p>}
        {error && <p className="error">{error}</p>}

        <div style={{ textAlign: 'center', marginBottom: '16px' }}>
          <button type="button" onClick={() => setKameraAktivna(!kameraAktivna)}>
            {kameraAktivna ? 'Sakrij kameru' : 'Prikaži kameru'}
          </button>
        </div>

        {kameraAktivna && (
          <div className="qr-skener-okvir">
            <Scanner onScan={handleScan} onError={(err) => console.error(err)} />
          </div>
        )}

        <p className="info-text">Ili unesi kod ručno:</p>

        <form onSubmit={handleRucniUnos} className="auth-form" noValidate>
          <label>
            QR kod
            <input value={kod} onChange={(e) => setKod(e.target.value)} />
          </label>

          <button type="submit" disabled={loading}>
            {loading ? 'Evidentiranje...' : 'Evidentiraj prisustvo'}
          </button>
        </form>
      </div>

      {aktivnostId && (
        <div className="sadrzaj-centriran">
          <h2 className="aktivnosti-sekcija-naslov">
            Prisutni ({dosli.length} od {prijave.length})
          </h2>

          <ul className="urednici-lista">
            {dosli.map((p) => (
              <li key={p.prijavaId} className="urednik-red">
                <span className="urednik-email">✓ {p.korisnickoIme}</span>
              </li>
            ))}
          </ul>

          {nisuDosli.length > 0 && (
            <>
              <h2 className="aktivnosti-sekcija-naslov">Još nisu stigli</h2>
              <ul className="urednici-lista">
                {nisuDosli.map((p) => (
                  <li key={p.prijavaId} className="urednik-red">
                    <span className="urednik-email">{p.korisnickoIme}</span>
                  </li>
                ))}
              </ul>
            </>
          )}
        </div>
      )}
    </div>
  )
}