package co.thewalrus.walrusmod.command.exception;

@SuppressWarnings("serial")
public class WrongUsageException extends RuntimeException {
	public WrongUsageException(String msg) {
		super(msg);
	}
}
