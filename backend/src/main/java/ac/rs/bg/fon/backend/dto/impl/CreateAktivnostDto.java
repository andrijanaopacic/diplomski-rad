package ac.rs.bg.fon.backend.dto.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ac.rs.bg.fon.backend.dto.Dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAktivnostDto implements Dto {
 
	@NotBlank(message = "Naziv je obavezan.")
	@Size(max = 150, message = "Naziv ne sme biti duži od 150 karaktera.")
	private String naziv;
 
	@NotBlank(message = "Opis je obavezan.")
	@Size(max = 2000, message = "Opis ne sme biti duži od 2000 karaktera.")
	private String opis;
 
	@NotNull(message = "Datum održavanja je obavezan.")
	private LocalDateTime datumOdrzavanja;
 
	@NotNull(message = "Rok za prijavu je obavezan.")
	private LocalDateTime rokZaPrijavu;
 
	@Positive(message = "Maksimalan broj učesnika mora biti veći od nule.")
	private int maksUcesnika;
 
	@NotBlank(message = "Mesto održavanja je obavezno.")
	@Size(max = 500, message = "Mesto održavanja ne sme biti duže od 500 karaktera.")
	private String mestoOdrzavanja;
}
