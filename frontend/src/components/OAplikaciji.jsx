export default function OAplikaciji({ jeAdminIliUrednik }) {
  return (
    <div className="o-aplikaciji">
      <h2>Kako funkcioniše aplikacija</h2>

      {jeAdminIliUrednik ? (
        <ol>
          <li>Kreirajte događaj, pa unutar njega jednu ili više aktivnosti.</li>
          <li>Po želji, definišite formu za prijavu za svaku aktivnost.</li>
          <li>Pratite ko se prijavio i ko je na listi čekanja.</li>
          <li>Na samom događaju, otvorite "Evidentiranje" i skenirajte QR kod svakog učesnika.</li>
        </ol>
      ) : (
        <ol>
          <li>Pronađite događaj koji vas zanima na listi ispod.</li>
          <li>Otvorite ga i izaberite konkretnu aktivnost.</li>
          <li>Popunite formu za prijavu, ako je organizator zahteva.</li>
          <li>Na mejl stiže potvrda sa QR kodom - pokažite ga na ulasku na događaj.</li>
        </ol>
      )}
    </div>
  )
}