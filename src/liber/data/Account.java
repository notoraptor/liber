package liber.data;

import liber.enumeration.AccountState;
import liber.enumeration.ContactData;
import liber.request.Response;
import liber.request.client.ContactDataUpdatedRequest;

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
	public void sentInfosToContact(Contact contact) {
		if(!contact.accountFirstnameSent) try {
			Response response = new ContactDataUpdatedRequest(contact, ContactData.firstname, info.firstname()).justSend();
			if(response.good())
				contact.accountFirstnameSent = true;
		} catch (Exception ignored) {}
		if(!contact.accountLastnameSent) try {
			Response response = new ContactDataUpdatedRequest(contact, ContactData.lastname, info.lastname()).justSend();
			if(response.good())
				contact.accountLastnameSent = true;
		} catch (Exception ignored) {}
		if(!contact.accountStatusSent) try {
			Response response = new ContactDataUpdatedRequest(contact, ContactData.status, info.status()).justSend();
			if(response.good())
				contact.accountStatusSent = true;
		} catch (Exception ignored) {}
		if(!contact.accountPhotoSent) try {
			Response response = new ContactDataUpdatedRequest(contact, ContactData.photo, info.photo()).justSend();
			if(response.good())
				contact.accountPhotoSent = true;
		} catch (Exception ignored) {}
	}
}
