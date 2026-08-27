package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import ac.rs.bg.fon.backend.entity.impl.StatusPrijave;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AktivnostSaFormomDto implements Dto{

	private AktivnostDto aktivnost;
	private FormaDto forma;
	private StatusPrijave mojStatusPrijave;
}
