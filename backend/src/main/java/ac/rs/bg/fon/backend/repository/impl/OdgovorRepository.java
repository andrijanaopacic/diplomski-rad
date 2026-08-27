package ac.rs.bg.fon.backend.repository.impl;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Odgovor;
import ac.rs.bg.fon.backend.entity.impl.PoljeForme;
import ac.rs.bg.fon.backend.entity.impl.Prijava;

public interface OdgovorRepository extends JpaRepository<Odgovor, Long> {

	List<Odgovor> findByPrijava(Prijava prijava);
	
	boolean existsByPoljeForme(PoljeForme poljeForme);
}
