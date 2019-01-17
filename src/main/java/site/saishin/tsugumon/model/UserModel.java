package site.saishin.tsugumon.model;

import javax.xml.bind.annotation.XmlRootElement;

import site.saishin.tsugumon.entity.User;

@XmlRootElement
public class UserModel {
	private String ipAddress;
	private Integer accessed;
	private Boolean available;
	private Boolean registered;
	
	public UserModel(User user, boolean available, int accessed) {
		this.ipAddress = user.ipAddress;
		this.accessed = accessed;
		this.available = available;
	}

	public UserModel() {}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer getAccessed() {
		return accessed;
	}

	public void setAccessed(Integer accessed) {
		this.accessed = accessed;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}
	
}
