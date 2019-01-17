package site.saishin.tsugumon.model;

import java.util.List;


public class HomeModel {
	private EnqueteModel ownEnquete;
	private List<AnswerModel> answers;
	public EnqueteModel getOwnEnquete() {
		return ownEnquete;
	}
	public void setOwnEnquete(EnqueteModel ownEnquete) {
		this.ownEnquete = ownEnquete;
	}
	public List<AnswerModel> getAnswers() {
		return answers;
	}
	public void setAnswers(List<AnswerModel> answers) {
		this.answers = answers;
	}
}
