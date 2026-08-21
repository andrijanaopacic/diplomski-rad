package ac.rs.bg.fon.backend.exception;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyError {
	private Instant timestamp;
	private int status;
	private String message;
	private Map<String, String> fieldErrors;
	
	public MyError(int status, String message) {
		this(status, message, null);
	}
 
	public MyError(int status, String message, Map<String, String> fieldErrors) {
		this.timestamp = Instant.now();
		this.status = status;
		this.message = message;
		this.fieldErrors = fieldErrors;
	}
}
