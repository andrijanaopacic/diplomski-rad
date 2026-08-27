package ac.rs.bg.fon.backend.entity.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ac.rs.bg.fon.backend.entity.MainEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prijava")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prijava implements MainEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long prijavaId;
	
	@Column(nullable = false)
	private LocalDateTime datumPrijave;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 2000)
	private StatusPrijave statusPrijave;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "korisnik_id", nullable = false)
	private Korisnik korisnik;
 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aktivnost_id", nullable = false)
	private Aktivnost aktivnost;

}
