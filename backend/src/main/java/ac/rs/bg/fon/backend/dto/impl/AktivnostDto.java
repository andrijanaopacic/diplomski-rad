package ac.rs.bg.fon.backend.dto.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ac.rs.bg.fon.backend.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AktivnostDto implements Dto{

	private Long aktivnostId;
	private String naziv;
	private String opis;
	private LocalDateTime datumOdrzavanja;
	private LocalDateTime rokZaPrijavu;
	private int maksUcesnika;
	private String mestoOdrzavanja;
}
