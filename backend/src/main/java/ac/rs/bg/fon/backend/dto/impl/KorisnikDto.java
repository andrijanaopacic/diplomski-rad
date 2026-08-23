package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import ac.rs.bg.fon.backend.entity.impl.Organizacija;
import ac.rs.bg.fon.backend.entity.impl.Uloga;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KorisnikDto implements Dto{

	private long korisnikId;
	private String username;
	private String email;
	private Uloga uloga;
	private boolean enabled;
	
}
