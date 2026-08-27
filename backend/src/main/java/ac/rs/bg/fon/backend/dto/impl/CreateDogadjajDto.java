package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
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
public class CreateDogadjajDto implements Dto{

	@NotBlank(message = "Naziv je obavezan.")
	@Size(max = 150, message = "Naziv ne sme biti duži od 150 karaktera.")
	private String naziv;
 
	@NotBlank(message = "Opis je obavezan.")
	@Size(max = 2000, message = "Opis ne sme biti duži od 2000 karaktera.")
	private String opis;
 
	@Size(max = 500, message = "Link ka slici ne sme biti duži od 500 karaktera.")
	private String slika;
}
