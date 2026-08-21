package ac.rs.bg.fon.backend.exception;

import java.util.Map;

import lombok.Getter;

@Getter
public class ValidacijaException extends RuntimeException{

	private final Map<String, String> fieldErrors;
	 
	public ValidacijaException(String message, Map<String, String> fieldErrors) {
		super(message);
		this.fieldErrors = fieldErrors;
	}
}
