package kissxml;

public class XmlReaderException extends RuntimeException {
	private static final long serialVersionUID = -8184354760198652231L;

	public XmlReaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public XmlReaderException(String message) {
		this(message, null);
	}

	
}
