package ac.rs.bg.fon.backend.repository.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ac.rs.bg.fon.backend.entity.impl.Dogadjaj;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;

public interface DogadjajRepository extends JpaRepository<Dogadjaj, Long>{

	boolean existsByNazivAndOrganizacija(String naziv, Organizacija organizacija);
	
	@Query("SELECT d FROM Dogadjaj d WHERE d.organizacija = :org " +
		       "AND LOWER(d.naziv) LIKE LOWER(CONCAT('%', :tekst, '%'))")
	List<Dogadjaj> pretraziDogadjaje(@Param("org") Organizacija org, @Param("tekst") String tekst);
	
	boolean existsByNazivAndOrganizacijaAndDogadjajIdNot(String naziv, Organizacija organizacija, Long dogadjajId);
	
	@Query("SELECT DISTINCT a.dogadjaj FROM Aktivnost a " +
			"WHERE a.datumOdrzavanja >= :danas " +
			"AND LOWER(a.dogadjaj.naziv) LIKE LOWER(CONCAT('%', :tekst, '%'))")
	List<Dogadjaj> javnaPretragaDogadjaja(@Param("tekst") String tekst, @Param("danas") LocalDate danas);
	
	@Query("SELECT DISTINCT a.dogadjaj FROM Aktivnost a " +
			"WHERE a.datumOdrzavanja >= :danas " +
			"AND LOWER(a.dogadjaj.naziv) LIKE LOWER(CONCAT('%', :tekst, '%'))")
	List<Dogadjaj> javnaPretragaDogadjaja(@Param("tekst") String tekst, @Param("danas") LocalDateTime danas);
}
