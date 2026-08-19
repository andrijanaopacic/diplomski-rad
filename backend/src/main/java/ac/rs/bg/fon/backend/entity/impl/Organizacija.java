package ac.rs.bg.fon.backend.entity.impl;

import ac.rs.bg.fon.backend.entity.MainEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizacija", uniqueConstraints = {
		@UniqueConstraint(name = "uk_pib", columnNames = "pib"),
		@UniqueConstraint(name = "uk_mb", columnNames = "mb")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Organizacija implements MainEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long organizacijaId;
	
	@Column(nullable = false, length = 150)
	private String naziv;
	
	@Column(nullable = false)
	private Long pib;
	
	@Column(nullable = false)
	private Long mb;
	
	@Column(nullable = false, length = 200)
	private String adresa;

}
