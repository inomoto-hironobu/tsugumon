package site.saishin.tsugumon.model;

public final class BaseDataInfo {
	private BaseDataInfo() {}
	private Long totalUser;
	private Long totalEnquete;
	private Long totalAnswer;
	public Long getTotalUser() {
		return totalUser;
	}
	public Long getTotalEnquete() {
		return totalEnquete;
	}
	public Long getTotalAnswer() {
		return totalAnswer;
	}
	public static final class Builder {
		Long totalUser;
		Long totalEnquete;
		Long totalAnswer;
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
		public BaseDataInfo build() {
			BaseDataInfo ins = new BaseDataInfo();
			ins.totalUser = this.totalUser;
			ins.totalEnquete = this.totalEnquete;
			ins.totalAnswer = this.totalAnswer;
			return ins;
		}
	}
	
}
