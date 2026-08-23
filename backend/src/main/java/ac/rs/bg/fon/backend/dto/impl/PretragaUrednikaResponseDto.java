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
public class PretragaUrednikaResponseDto implements Dto{

	private String poruka;
    private List<KorisnikDto> urednici;
}
