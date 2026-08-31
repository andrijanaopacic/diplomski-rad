import { useState, useEffect } from 'react'
import api from '../api/axios'

const TIPOVI = [
  { vrednost: 'STRING', naziv: 'Tekst' },
  { vrednost: 'INTEGER', naziv: 'Ceo broj' },
  { vrednost: 'DOUBLE', naziv: 'Decimalni broj' },
  { vrednost: 'LOCAL_DATE', naziv: 'Datum' },
]

export default function FormaModal({ dogadjajId, aktivnostId, onClose, onSaved }) {
  const [ucitavanje, setUcitavanje] = useState(true)
  const [postojiForma, setPostojiForma] = useState(false)

  const [naziv, setNaziv] = useState('')
  const [polja, setPolja] = useState([])

  const [error, setError] = useState('')
  const [fieldErrors, setFieldErrors] = useState({})
  const [loading, setLoading] = useState(false)

  const [lokalnaPoruka, setLokalnaPoruka] = useState('')

  const putanja = `/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}/forma`


  useEffect(() => {
    async function ucitaj() {
      try {
        const response = await api.get(putanja)
        setNaziv(response.data.podaci.naziv)
        setPolja(response.data.podaci.polja)
        setPostojiForma(true)
      } catch (err) {
        const poruka = err.response?.data?.message
        if (poruka === 'Sistem ne može da učita formu za prijavu.') {
          setPostojiForma(false)
        } else {
          setError(poruka || 'Greška pri učitavanju forme.')
        }
      } finally {
        setUcitavanje(false)
      }
    }
    ucitaj()
  }, [])

  function prikaziLokalnuPoruku(tekst) {
    setLokalnaPoruka(tekst)
    setTimeout(() => setLokalnaPoruka(''), 3000)
  }

  function dodajPolje() {
    setPolja([...polja, { naziv: '', obavezno: false, tip: 'STRING' }])
  }

  function izmeniPolje(indeks, izmene) {
    setPolja(polja.map((p, i) => (i === indeks ? { ...p, ...izmene } : p)))
  }

  function ukloniPolje(indeks) {
    setPolja(polja.filter((_, i) => i !== indeks))
  }

  async function handleKreiraj(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    try {
      const response = await api.post(putanja, { naziv })
      setNaziv(response.data.podaci.naziv)
      setPolja(response.data.podaci.polja)
      setPostojiForma(true)
      prikaziLokalnuPoruku(response.data.poruka)
      onSaved(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom kreiranja forme.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  async function handleSacuvaj(e) {
    e.preventDefault()
    setError('')
    setFieldErrors({})
    setLoading(true)

    try {
      const response = await api.put(putanja, { naziv, polja })
      setPolja(response.data.podaci.polja)
      prikaziLokalnuPoruku(response.data.poruka)
      onSaved(response.data.poruka)
    } catch (err) {
      const data = err.response?.data
      setError(data?.message || 'Greška prilikom izmene forme.')
      setFieldErrors(data?.fieldErrors || {})
    } finally {
      setLoading(false)
    }
  }

  async function handleObrisiFormu() {
      if (!window.confirm('Da li sigurno želite da obrišete celu formu za prijavu?')) return

      try {
        const response = await api.delete(putanja)
        onSaved(response.data.poruka)
        onClose()
      } catch (err) {
        const data = err.response?.data
        const razlog = data?.fieldErrors?.razlog
        alert(razlog ? `${data.message}\n${razlog}` : (data?.message || 'Greška prilikom brisanja forme.'))
      }
  }

  const dodatneGreske = Object.entries(fieldErrors).filter(([kljuc]) => kljuc !== 'naziv')

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-box modal-box-wide" onClick={(e) => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>×</button>

        {ucitavanje ? (
          <p>Učitavanje...</p>
        ) : !postojiForma ? (
          <form onSubmit={handleKreiraj} className="auth-form" noValidate>
            <h1>Dodaj formu za prijavu</h1>
            <p className="info-text">Ova aktivnost još nema formu za prijavu.</p>

            {lokalnaPoruka && <p className="success">{lokalnaPoruka}</p>}
            {error && <p className="error">{error}</p>}

            <label>
              Naziv forme
              <input value={naziv} onChange={(e) => setNaziv(e.target.value)} />
              {fieldErrors.naziv && <small className="field-error">{fieldErrors.naziv}</small>}
            </label>

            <button type="submit" disabled={loading}>
              {loading ? 'Kreiranje...' : 'Kreiraj formu'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleSacuvaj} className="auth-form" noValidate>
            <h1>Uredi formu za prijavu</h1>

            {lokalnaPoruka && <p className="success">{lokalnaPoruka}</p>}
            {error && <p className="error">{error}</p>}
            {dodatneGreske.length > 0 && (
              <p className="error">
                {dodatneGreske.map(([k, v]) => <span key={k}>{v} </span>)}
              </p>
            )}

            <label>
              Naziv forme
              <input value={naziv} onChange={(e) => setNaziv(e.target.value)} />
              {fieldErrors.naziv && <small className="field-error">{fieldErrors.naziv}</small>}
            </label>

            <div className="polja-forme-lista">
              <p className="polja-forme-naslov">Polja forme</p>

              {polja.length === 0 && (
                <p className="info-text">Forma trenutno nema nijedno polje.</p>
              )}

              {polja.map((polje, indeks) => (
                <div key={indeks} className="polje-forme-red">
                  <input
                    placeholder="Naziv polja"
                    value={polje.naziv}
                    onChange={(e) => izmeniPolje(indeks, { naziv: e.target.value })}
                    className="polje-forme-naziv"
                  />
                  <div className="polje-forme-red-donji">
                    <select
                      value={polje.tip}
                      onChange={(e) => izmeniPolje(indeks, { tip: e.target.value })}
                    >
                      {TIPOVI.map((t) => (
                        <option key={t.vrednost} value={t.vrednost}>{t.naziv}</option>
                      ))}
                    </select>
                    <label className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={polje.obavezno}
                        onChange={(e) => izmeniPolje(indeks, { obavezno: e.target.checked })}
                      />
                      Obavezno
                    </label>
                    <button type="button" className="danger" onClick={() => ukloniPolje(indeks)}>
                      Ukloni
                    </button>
                  </div>
                </div>
              ))}

              <button type="button" onClick={dodajPolje} className="dodaj-polje-dugme">
                + Dodaj polje
              </button>
            </div>

            <div className="forma-akcije">
              <button type="submit" disabled={loading}>
                {loading ? 'Čuvanje...' : 'Sačuvaj izmene'}
              </button>
              <button type="button" className="danger" onClick={handleObrisiFormu}>
                Obriši formu
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  )
}