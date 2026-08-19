package ac.rs.bg.fon.backend.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ac.rs.bg.fon.backend.entity.impl.Korisnik;
import ac.rs.bg.fon.backend.repository.impl.KorisnikRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

	private final KorisnikRepository korisnikRepository;
	

	public AppUserDetailsService(KorisnikRepository korisnikRepository) {
		this.korisnikRepository = korisnikRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Korisnik korisnik = korisnikRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Korisnik sa korisničkim imenom '" + username + "' nije pronađen."));
		return new User(korisnik.getUsername(), korisnik.getPasswordHash(), korisnik.isEnabled(), true, true, true, List.of(new SimpleGrantedAuthority("ROLE_" + korisnik.getUloga().name())));
	}
}
