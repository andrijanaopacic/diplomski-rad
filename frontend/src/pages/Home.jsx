import Meni from '../components/Meni'
import JavniDogadjaji from '../components/JavniDogadjaji'
import { useAuth } from '../context/AuthContext'
import OAplikaciji from '../components/OAplikaciji'

export default function Home() {
  const { korisnik } = useAuth()

  const jeAdminIliUrednik = korisnik?.uloga === 'ADMIN' || korisnik?.uloga === 'UREDNIK'

  return (
    <div className="page page-wide">
      <Meni />

      {jeAdminIliUrednik ? (
        <>
          <h1>Zdravo, {korisnik?.username}!</h1>
          <p>Uloga: {korisnik?.uloga}</p>
          <p>Email: {korisnik?.email}</p>
          <OAplikaciji jeAdminIliUrednik={true} />
        </>
      ) : (
        <JavniDogadjaji />
      )}
    </div>
  )
}