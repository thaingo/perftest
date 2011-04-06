package org.membase.perf.exception;

public class InsufficientMachinesException extends Exception {
	private static final long serialVersionUID = 8004866308311795517L;

	public InsufficientMachinesException() {
        super();
    }
	
	public InsufficientMachinesException(String message) {
        super(message);
    }
}
