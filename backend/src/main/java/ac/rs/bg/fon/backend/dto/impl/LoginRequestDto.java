package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto implements Dto{
	
	@NotBlank(message = "Korisničko ime je obavezno.")
	private String username;
 
	@NotBlank(message = "Lozinka je obavezna.")
	private String password;
    
}
