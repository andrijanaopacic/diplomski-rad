package ac.rs.bg.fon.backend.repository.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Korisnik;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long>{
	
	Optional<Korisnik> findByUsername(String username);
	
	Optional<Korisnik> findByEmail(String email);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);

}
