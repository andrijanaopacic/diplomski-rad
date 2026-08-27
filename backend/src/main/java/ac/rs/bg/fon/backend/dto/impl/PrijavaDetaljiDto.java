package ac.rs.bg.fon.backend.dto.impl;

import java.time.LocalDateTime;
import java.util.List;

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
public class PrijavaDetaljiDto implements Dto{

	private Long prijavaId;
	private String korisnickoIme;
	private String korisnikEmail;
	private StatusPrijave statusPrijave;
	private LocalDateTime datumPrijave;
	private boolean dosao;
	private LocalDateTime vremeDolaska; 
	private List<OdgovorPregledDto> odgovori;
}
