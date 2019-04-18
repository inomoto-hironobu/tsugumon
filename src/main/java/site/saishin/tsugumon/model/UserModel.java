package site.saishin.tsugumon.model;

import java.util.List;
import java.util.stream.Collectors;

import site.saishin.tsugumon.entity.User;


public class UserModel {
	private String ipAddress;
	private EnqueteModel ownEnquete;
	private List<AnswerModel> answers;
	public UserModel(User user) {
		this.ipAddress = user.ipAddress;
		this.ownEnquete = new EnqueteModel(user.enquete);
		this.answers = user.answers.stream().map((answer) -> {
			return new AnswerModel(answer);
		}).collect(Collectors.toList());
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public EnqueteModel getOwnEnquete() {
		return ownEnquete;
	}
	public List<AnswerModel> getAnswers() {
		return answers;
	}
}
