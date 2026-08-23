package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IzmeniUrednikaDto implements Dto{
 
	@NotBlank(message = "Email je obavezan.")
	@Size(max = 100, message = "Email ne sme biti duži od 100 karaktera.")
	@Email(message = "Email mora biti u ispravnom formatu.")
	private String email;
	
	private boolean enabled;
	
	private String password;
}
