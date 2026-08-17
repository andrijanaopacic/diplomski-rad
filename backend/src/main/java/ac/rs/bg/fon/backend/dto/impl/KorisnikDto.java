package ac.rs.bg.fon.backend.dto.impl;

import ac.rs.bg.fon.backend.dto.Dto;
import ac.rs.bg.fon.backend.entity.impl.Uloga;

public class KorisnikDto implements Dto{

	private long korinsikId;
	private String username;
	private String email;
	private Uloga uloga;
	
	public KorisnikDto() {
		// TODO Auto-generated constructor stub
	}

	public KorisnikDto(long korinsikId, String username, String email, Uloga uloga) {
		super();
		this.korinsikId = korinsikId;
		this.username = username;
		this.email = email;
		this.uloga = uloga;
	}

	public long getKorinsikId() {
		return korinsikId;
	}

	public void setKorinsikId(long korinsikId) {
		this.korinsikId = korinsikId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Uloga getUloga() {
		return uloga;
	}

	public void setUloga(Uloga uloga) {
		this.uloga = uloga;
	}

	
	
	
}
