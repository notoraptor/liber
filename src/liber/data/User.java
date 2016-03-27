package liber.data;

import liber.exception.AddressException;

import java.net.InetAddress;
import java.util.Random;

public class User extends BasicUser {
	private boolean userAccountExists;
	private Address address;
	private String secret;
	public User(Liberaddress liberaddress) {
		super(liberaddress);
		generateSecret();
		userAccountExists = true;
		try {
			address = new Address(liberaddress);
		} catch (AddressException e) {
			if(e.responseIsErrorUsername())
				userAccountExists = false;
			address = new Address();
		}
	}
	public User(Liberaddress liberaddress, String theSecret) {
		super(liberaddress);
		assert theSecret != null;
		secret = theSecret;
		userAccountExists = true;
		try {
			address = new Address(liberaddress);
		} catch (AddressException e) {
			if(e.responseIsErrorUsername())
				userAccountExists = false;
			address = new Address();
		}
	}
	public boolean exists() {
		return userAccountExists;
	}
	public String secret() {
		return secret;
	}
	public InetAddress ip() {
		return address.ip();
	}
	public int port() {
		return address.port();
	}
	public boolean secretIs(String tested) {
		return secret.equals(tested);
	}
	public boolean hasAddress() {
		return address.has();
	}
	public boolean addressIsDistant() {
		return address.isDistant();
	}
	public void updateAddress() {
		try {
			address = new Address(liberaddress());
		} catch (AddressException e) {
			address = new Address();
		}
	}
	private void generateSecret() {
		Random random = new Random();
		StringBuilder s = new StringBuilder();
		int count = random.nextInt(10) + 1;
		for (int i = 0; i < count; ++i) {
			long value;
			do {
				value = random.nextInt();
			} while (value == 0);
			if (value < 0) value = -value;
			s.append(value);
		}
		secret = s.toString();
	}
}
