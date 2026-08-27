package ac.rs.bg.fon.backend.entity.impl;

import java.time.LocalDate;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "odgovor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Odgovor implements MainEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long odgovorId;
	
	@Column(nullable = false)
	private String vrednost;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prijava_id", nullable = false)
	private Prijava prijava;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forma_id", nullable = false)
	private Forma forma;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "polje_forme_id", nullable = false)
	private PoljeForme poljeForme;
}
