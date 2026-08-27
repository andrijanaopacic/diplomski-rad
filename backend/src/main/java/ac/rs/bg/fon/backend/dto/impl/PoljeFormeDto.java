package ac.rs.bg.fon.backend.dto.impl;

import java.util.List;

import ac.rs.bg.fon.backend.dto.Dto;
import ac.rs.bg.fon.backend.entity.impl.TipPolja;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoljeFormeDto implements Dto{

	private Long poljeFormeId;
	private String naziv;
	private boolean obavezno;
	private TipPolja tip;
}
