package ac.rs.bg.fon.backend.exception;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {
	private Instant timestamp;
	private int status;
	private String message;
	
	public ApiError(int status, String message) {
		this.timestamp = Instant.now();
		this.status = status;
		this.message = message;
	}
}
