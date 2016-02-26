package liber.data;

import liber.enumeration.AccountState;

/**
 * Created by HPPC on 21/02/2016.
 */
public class Account extends BasicUser implements KnownUser {
	private UserInfo info;
	private AccountState state;
	public Account(Liberaddress liberaddress) {
		super(liberaddress);
		info = new UserInfo();
		state = AccountState.TO_CONFIRM;
	}
	public Account(Liberaddress liberaddress, AccountState accountState) {
		super(liberaddress);
		assert accountState != null;
		info = new UserInfo();
		state = accountState;
	}
	@Override
	public UserInfo info() {
		return info;
	}
	public AccountState state() {
		return state;
	}
	public void confirm() {
		state = AccountState.CONFIRMED;
	}
	public void setToDelete() {
		state = AccountState.TO_DELETE;
	}
	public void setToConfirm() {
		state = AccountState.TO_CONFIRM;
	}
	public boolean confirmed() {
		return state == AccountState.CONFIRMED;
	}
	public boolean toDelete() {
		return state == AccountState.TO_DELETE;
	}
	public boolean toConfirm() {
		return state == AccountState.TO_CONFIRM;
	}
}
