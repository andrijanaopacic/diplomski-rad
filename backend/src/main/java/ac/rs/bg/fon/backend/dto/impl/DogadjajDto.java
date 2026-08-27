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
public class DogadjajDto implements Dto {

	private Long dogadjajId;
	private String naziv;
	private String opis;
	private String slika;
	private String organizacijaNaziv;
}
