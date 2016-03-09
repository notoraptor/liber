package liber.data;

import liber.enumeration.ContactData;

interface KnownUser {
	UserInfo info();
	String username();
	default String appellation() {
		UserInfo info = info();
		String appellation = info.firstname();
		if (info.lastname() != null) {
			if (appellation == null) appellation = info.lastname();
			else appellation += " " + info.lastname();
		}
		if (appellation == null) appellation = username();
		else appellation += " (" + username() + ")";
		return appellation;
	}
	default void update(ContactData data, String value) {
		info().setData(data, value);
	}
	default void update(UserInfo info) {
		info().setData(ContactData.firstname, info.firstname());
		info().setData(ContactData.lastname, info.lastname());
		info().setData(ContactData.photo, info.photo());
		info().setData(ContactData.status, info.status());
	}
	default void delete(ContactData data) {
		info().deleteData(data);
	}
}
