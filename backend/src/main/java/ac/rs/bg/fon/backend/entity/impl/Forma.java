package ac.rs.bg.fon.backend.entity.impl;

import java.util.ArrayList;
import java.util.List;

import ac.rs.bg.fon.backend.entity.MainEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "forma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Forma implements MainEntity {
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long formaId;
 
	@Column(nullable = false, length = 150)
	private String naziv;
 
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aktivnost_id", nullable = false, unique = true)
	private Aktivnost aktivnost;
 
	@OneToMany(mappedBy = "forma", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PoljeForme> poljaForme = new ArrayList<>();
}
