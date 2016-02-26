package liber.card.textable;

import liber.card.Libercard;
import liber.data.Account;
import liber.data.Liberaddress;
import liber.data.UserInfo;
import liber.enumeration.AccountState;

import java.util.HashMap;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class TextableAccount extends Textable<Account> {
	static private String liberaddress = "liberaddress";
	static private String state = "state";
	static private String firstname = "firstname";
	static private String lastname = "lastname";
	static private String photo = "photo";
	static private String status = "status";
	public TextableAccount(Libercard libercard, Account account) {
		super(libercard, account);
	}
	@Override
	public String name() {
		return "account";
	}
	@Override
	public String[] fields() {
		return new String[]{liberaddress, state, firstname, lastname, photo, status};
	}
	@Override
	public void toText(HashMap<String, String> map) {
		map.put(liberaddress, get().liberaddress().toString());
		map.put(state, get().state().toString());
		map.put(firstname, get().info().firstname());
		map.put(lastname, get().info().lastname());
		map.put(photo, get().info().photo());
		map.put(status, get().info().status());
	}
	@Override
	public Account fromText(HashMap<String, String> map) throws Exception {
		Account account = new Account(new Liberaddress(map.get(liberaddress)), AccountState.valueOf(map.get(state)));
		account.update(new UserInfo(map.get(firstname), map.get(lastname), map.get(photo), map.get(status)));
		return account;
	}
}
