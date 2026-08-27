package ac.rs.bg.fon.backend.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Dogadjaj;
import jakarta.persistence.LockModeType;

public interface AktivnostRepository extends JpaRepository<Aktivnost, Long>{

	boolean existsByNazivAndDogadjaj(String naziv, Dogadjaj dogadjaj);
	 
	boolean existsByNazivAndDogadjajAndAktivnostIdNot(String naziv, Dogadjaj dogadjaj, Long aktivnostId);
 
	boolean existsByDogadjaj(Dogadjaj dogadjaj);
 
	@Query("SELECT a FROM Aktivnost a WHERE a.dogadjaj = :dogadjaj " +
			"AND LOWER(a.naziv) LIKE LOWER(CONCAT('%', :tekst, '%'))")
	List<Aktivnost> pretraziAktivnosti(@Param("dogadjaj") Dogadjaj dogadjaj, @Param("tekst") String tekst);
 
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT a FROM Aktivnost a WHERE a.aktivnostId = :id")
	Optional<Aktivnost> ucitajSaZakljucavanjem(@Param("id") Long id);

}
