package site.saishin.tsugumon.model;

import java.time.Instant;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class BaseDataInfo {
	private BaseDataInfo() {}
	private Long totalUser;
	private Long totalEnquete;
	private Long totalAnswer;
	private Instant created;
	private Set<String> proxies;
	public Long getTotalUser() {
		return totalUser;
	}
	public Long getTotalEnquete() {
		return totalEnquete;
	}
	public Long getTotalAnswer() {
		return totalAnswer;
	}
	public Instant getCreated() {
		return created;
	}
	public Set<String> getProxies() {
		return proxies;
	}
	public static final class Builder {
		Long totalUser;
		Long totalEnquete;
		Long totalAnswer;
		Instant created;
		Set<String> proxies;
		public Builder totalUser(Long val) {
			this.totalUser = val;
			return this;
		}
		public Builder totalEnquete(Long val) {
			this.totalEnquete = val;
			return this;
		}
		public Builder totalAnswer(Long val) {
			this.totalAnswer = val;
			return this;
		}
		public Builder created(Instant val) {
			this.created = val;
			return this;
		}
		public Builder proxies(Set<String> val) {
			this.proxies = val;
			return this;
		}
		public BaseDataInfo build() {
			BaseDataInfo instance = new BaseDataInfo();
			instance.totalUser = this.totalUser;
			instance.totalEnquete = this.totalEnquete;
			instance.totalAnswer = this.totalAnswer;
			instance.created = this.created;
			instance.proxies = this.proxies;
			return instance;
		}
	}
	
}
