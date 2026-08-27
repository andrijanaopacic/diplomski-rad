package ac.rs.bg.fon.backend.repository.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Prijava;
import ac.rs.bg.fon.backend.entity.impl.QRKod;

public interface QRKodRepository extends JpaRepository<QRKod, Long> {

	Optional<QRKod> findByKod(String kod);
	
	Optional<QRKod> findByPrijava(Prijava prijava);
}