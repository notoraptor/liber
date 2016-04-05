package liber.card.textable;

import liber.Utils;
import liber.card.Libercard;
import liber.data.Account;
import liber.data.Liberaddress;
import liber.enumeration.AccountState;
import liber.enumeration.ContactData;

import java.util.HashMap;

public class TextableAccount extends Textable<Account> {
	static private String liberaddress = "liberaddress";
	static private String state = "state";
	static private String firstname = "firstname";
	static private String lastname = "lastname";
	static private String photo = "photo";
	static private String status = "status";
	static private String publicKey = "publicKey";
	static private String privateKey = "privateKey";
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
		map.put(privateKey, Utils.encodeString(get().privateKeyToString()));
		map.put(publicKey, Utils.encodeString(get().publicKeyToString()));
	}
	@Override
	public Account fromText(HashMap<String, String> map) throws Exception {
		String pub = map.get(publicKey);
		String priv = map.get(privateKey);
		Account account = new Account(
			new Liberaddress(map.get(liberaddress)),
			AccountState.valueOf(map.get(state)),
			Utils.decodeString(priv),
			Utils.decodeString(pub)
		);
		account.update(ContactData.firstname, map.get(firstname));
		account.update(ContactData.lastname, map.get(lastname));
		account.update(ContactData.photo, map.get(photo));
		account.update(ContactData.status, map.get(status));
		return account;
	}
}
