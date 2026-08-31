package ac.rs.bg.fon.backend.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Prijava;
import ac.rs.bg.fon.backend.entity.impl.StatusPrijave;

public interface PrijavaRepository extends JpaRepository<Prijava, Long>{
	
	boolean existsByKorisnikAndAktivnostAndStatusPrijaveNot(Korisnik korisnik, Aktivnost aktivnost, StatusPrijave statusPrijave);
 
	Optional<Prijava> findByKorisnikAndAktivnostAndStatusPrijaveNot(Korisnik korisnik, Aktivnost aktivnost, StatusPrijave statusPrijave);
 
	long countByAktivnostAndStatusPrijave(Aktivnost aktivnost, StatusPrijave statusPrijave);
	
	Optional<Prijava> findFirstByAktivnostAndStatusPrijaveOrderByDatumPrijaveAsc(Aktivnost aktivnost, StatusPrijave statusPrijave);
 
	List<Prijava> findByAktivnost(Aktivnost aktivnost);
	 
	boolean existsByAktivnost(Aktivnost aktivnost);
}
