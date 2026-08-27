package ac.rs.bg.fon.backend.repository.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Aktivnost;
import ac.rs.bg.fon.backend.entity.impl.Forma;

public interface FormaRepository extends JpaRepository<Forma, Long>{

	boolean existsByAktivnost(Aktivnost aktivnost);
	 
	Optional<Forma> findByAktivnost(Aktivnost aktivnost);
}
