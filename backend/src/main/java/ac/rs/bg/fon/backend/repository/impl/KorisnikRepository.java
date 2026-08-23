package ac.rs.bg.fon.backend.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long>{
	
	Optional<Korisnik> findByUsername(String username);
	
	Optional<Korisnik> findByEmail(String email);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);
	
	@Query("SELECT k FROM Korisnik k WHERE k.organizacija = :org AND k.uloga = 'UREDNIK' " +
		       "AND (LOWER(k.username) LIKE LOWER(CONCAT('%', :tekst, '%')) " +
		       "OR LOWER(k.email) LIKE LOWER(CONCAT('%', :tekst, '%')))")
	List<Korisnik> pretraziUrednike(@Param("org") Organizacija org, @Param("tekst") String tekst);

}
