package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import ac.rs.bg.fon.backend.entity.impl.TipPolja;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IzmeniPoljeFormeDto implements Dto {

	private Long poljeFormeId;
	 
	@NotBlank(message = "Naziv polja je obavezan.")
	@Size(max = 150, message = "Naziv polja ne sme biti duži od 150 karaktera.")
	private String naziv;
 
	private boolean obavezno;
 
	@NotNull(message = "Tip polja je obavezan.")
	private TipPolja tip;
}