package ac.rs.bg.fon.backend.dto.impl;

import java.util.List;

import ac.rs.bg.fon.backend.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FormaDto implements Dto{

	private Long formaId;
	private String naziv;
	private List<PoljeFormeDto> polja;
	
}
