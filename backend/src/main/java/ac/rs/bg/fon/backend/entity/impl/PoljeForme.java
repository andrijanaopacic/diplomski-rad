package ac.rs.bg.fon.backend.entity.impl;

import java.util.List;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "polje_forme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PoljeForme implements MainEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long poljeFormeId;
 
	@Column(nullable = false, length = 150)
	private String naziv;
 
	@Column(nullable = false)
	private boolean obavezno;
 
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TipPolja tip;
 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forma_id", nullable = false)
	private Forma forma;
}
