package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUrednikDto implements Dto{

	@NotBlank(message = "Korisničko ime je obavezno polje.")
	@Size(min = 3, max = 50, message = "Korisničko ime mora imati između 3 i 50 karaktera.")
	private String username;
 
	@NotBlank(message = "Email je obavezan.")
	@Size(max = 100, message = "Email ne sme biti duži od 100 karaktera.")
	@Email(message = "Email mora biti u ispravnom formatu.")
	private String email;
 
	@NotBlank(message = "Lozinka je obavezna.")
	@Size(min = 6, max = 100, message = "Lozinka mora imati najmanje 6 karaktera.")
	private String password;
}
