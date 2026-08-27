package ac.rs.bg.fon.backend.entity.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "aktivnost", uniqueConstraints = {
		@UniqueConstraint(name = "uk_naziv_dogadjaja", columnNames = { "naziv", "dogadjaj_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aktivnost implements MainEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long aktivnostId;
	
	@Column(nullable = false, length = 150)
	private String naziv;
 
	@Column(nullable = false, length = 2000)
	private String opis;
	
	@Column(nullable = false)
	private LocalDateTime datumOdrzavanja;
 
	@Column(nullable = false)
	private LocalDateTime rokZaPrijavu;
	
	
	private int maksUcesnika;
	
	@Column(nullable = false, length = 500)
	private String mestoOdrzavanja;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dogadjaj_id", nullable = false)
	private Dogadjaj dogadjaj;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kreirao_id", nullable = false)
	private Korisnik kreirao;
	
}
