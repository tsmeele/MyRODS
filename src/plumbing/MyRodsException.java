package plumbing;

import java.io.IOException;

public class MyRodsException extends IOException {
	private static final long serialVersionUID = 4223621072277349290L;
	
	public MyRodsException(String message) {
		super("IrodsMessage: " + message);
	}
	

}
