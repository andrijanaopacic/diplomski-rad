import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import api from '../api/axios'
import Meni from '../components/Meni'
import PrijavaDetaljiModal from '../components/PrijavaDetaljiModal'

export default function PregledPrijavaPage() {
  const { dogadjajId, aktivnostId } = useParams()

  const [prijave, setPrijave] = useState([])
  const [infoTekst, setInfoTekst] = useState('')
  const [otvorenaPrijavaId, setOtvorenaPrijavaId] = useState(null)

  async function ucitaj() {
    try {
      const response = await api.get(`/dogadjaj/${dogadjajId}/aktivnost/${aktivnostId}/prijava`)
      setPrijave(response.data.podaci)
      setInfoTekst('')
    } catch (err) {
      setPrijave([])
      setInfoTekst(err.response?.data?.message || 'Nema pronađenih prijava.')
    }
  }

  useEffect(() => {
    ucitaj()
  }, [dogadjajId, aktivnostId])

  const potvrdjene = prijave.filter((p) => p.statusPrijave === 'POTVRDJENA')
  const naCekanju = prijave.filter((p) => p.statusPrijave === 'NA_CEKANJU')

  function redPrijave(p) {
    return (
      <li
        key={p.prijavaId}
        className="urednik-red"
        onClick={() => setOtvorenaPrijavaId(p.prijavaId)}
        style={{ cursor: 'pointer' }}
      >
        <span className="urednik-email">
          {p.korisnickoIme} {p.dosao && '✓ (došao/la)'}
        </span>
      </li>
    )
  }

  return (
    <div className="page page-wide">
      <Meni />
      <Link to={`/dogadjaji/${dogadjajId}`} className="dogadjaj-detalji-link">← Nazad na aktivnosti</Link>

      <h1>Pregled prijava</h1>

      <Link to={`/evidentiranje?aktivnostId=${aktivnostId}`} className="dogadjaj-detalji-link">
        Idi na skeniranje/evidentiranje za ovu aktivnost →
      </Link>

      {infoTekst && <p className="info-text">{infoTekst}</p>}

      <h2 className="aktivnosti-sekcija-naslov">Potvrđene ({potvrdjene.length})</h2>
      <ul className="urednici-lista">
        {potvrdjene.map(redPrijave)}
      </ul>

      <h2 className="aktivnosti-sekcija-naslov">Na listi čekanja ({naCekanju.length})</h2>
      <ul className="urednici-lista">
        {naCekanju.map(redPrijave)}
      </ul>

      {otvorenaPrijavaId && (
        <PrijavaDetaljiModal
          dogadjajId={dogadjajId}
          aktivnostId={aktivnostId}
          prijavaId={otvorenaPrijavaId}
          onClose={() => setOtvorenaPrijavaId(null)}
        />
      )}
    </div>
  )
}