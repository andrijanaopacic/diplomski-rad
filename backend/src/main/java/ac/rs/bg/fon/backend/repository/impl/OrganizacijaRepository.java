package ac.rs.bg.fon.backend.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.Organizacija;

public interface OrganizacijaRepository extends JpaRepository<Organizacija, Long>{

	boolean existsByPib(Long pib);
	 
	boolean existsByMb(Long mb);
}
