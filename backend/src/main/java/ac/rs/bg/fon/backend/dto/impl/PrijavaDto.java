package ac.rs.bg.fon.backend.dto.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class PrijavaDto implements Dto {
	private Long prijavaId;
	private LocalDateTime datumPrijave;
	private StatusPrijave statusPrijave;

}
