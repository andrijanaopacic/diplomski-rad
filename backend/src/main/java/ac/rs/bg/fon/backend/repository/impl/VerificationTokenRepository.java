package ac.rs.bg.fon.backend.repository.impl;

import org.springframework.data.jpa.repository.JpaRepository;

import ac.rs.bg.fon.backend.entity.impl.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String>{

}
