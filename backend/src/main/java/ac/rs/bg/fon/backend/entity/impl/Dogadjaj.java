package ac.rs.bg.fon.backend.entity.impl;

import ac.rs.bg.fon.backend.entity.MainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dogadjaj", uniqueConstraints = {
		@UniqueConstraint(name = "uk_naziv_organizacija", columnNames = { "naziv", "organizacija_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dogadjaj implements MainEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dogadjajId;
 
	@Column(nullable = false, length = 150)
	private String naziv;
 
	@Column(nullable = false, length = 2000)
	private String opis;
	
	@Column(length = 500)
	private String slika;
 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organizacija_id", nullable = false)
	private Organizacija organizacija;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kreirao_id", nullable = false)
	private Korisnik kreirao;
}
