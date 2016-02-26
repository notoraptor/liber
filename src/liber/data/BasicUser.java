package liber.data;

import liber.recipient.Liberserver;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
abstract class BasicUser {
	static private int lastId = 0;
	static private int nextId() {
		return ++lastId;
	}
	private int id;
	private Liberaddress liberaddress;
	public BasicUser(Liberaddress liberaddress) {
		assert liberaddress != null;
		this.liberaddress = liberaddress;
		this.id = nextId();
	}
	public Liberaddress liberaddress() {
		return liberaddress;
	}
	public Liberserver liberserver() {
		return liberaddress.liberserver();
	}
	public String username() {
		return liberaddress.username();
	}
	public int id() {
		return id;
	}
}
