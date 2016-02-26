package liber.data;

import liber.enumeration.ContactData;

import java.util.Base64;

/**
 liber
 ${PACKAGE_NAME} - 21/02/2016
 **/
public class UserInfo {
	private String firstname;
	private String lastname;
	private String photo; //Base64 encoded.
	private String status;
	public UserInfo() {}
	public UserInfo(String firstname, String lastname, String photo, String status) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.photo = photo;
		this.status = status;
	}
	public String firstname() {
		return firstname;
	}
	public String lastname() {
		return lastname;
	}
	public String photo() {
		return photo;
	}
	public String status() {
		return status;
	}
	public void setData(ContactData dataType, String dataValue) {
		assert dataType != null && dataValue != null;
		switch (dataType) {
			case firstname:
				this.firstname = dataValue;
				break;
			case lastname:
				this.lastname = dataValue;
				break;
			case photo:
				this.photo = dataValue;
				break;
			case status:
			case contactStatus:
				this.status = dataValue;
				break;
			default:
				break;
		}
	}
	public void deleteData(ContactData dataType) {
		assert dataType != null;
		switch (dataType) {
			case firstname:
				this.firstname = null;
				break;
			case lastname:
				this.lastname = null;
				break;
			case photo:
				this.photo = null;
				break;
			case status:
			case contactStatus:
				this.status = null;
				break;
			default:
				break;
		}
	}
	public boolean hasPhoto() {
		return photo != null && !photo.isEmpty();
	}
	public byte[] photoBytes() {
		byte[] data = null;
		if(hasPhoto()) data = Base64.getDecoder().decode(photo.getBytes());
		return data;
	}
	public void setPhoto(StringBuilder photoContent) {
		photo = photoContent == null ? null : photoContent.toString();
	}
}
