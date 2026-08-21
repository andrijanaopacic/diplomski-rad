package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto implements Dto {
	private String token;
	private KorisnikDto korisnik;
	private String poruka;
}
