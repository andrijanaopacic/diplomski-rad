package ac.rs.bg.fon.backend.entity.impl;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "qr_kod")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QRKod implements MainEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long qrId;

	@Column(nullable = false, unique = true, length = 100)
	private String kod;

	@Column(length = 500)
	private String url;

	@Column(nullable = false)
	private boolean iskoriscen = false;

	private LocalDateTime vremeDolaska;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prijava_id", nullable = false, unique = true)
	private Prijava prijava;
}