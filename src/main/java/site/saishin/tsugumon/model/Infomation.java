package site.saishin.tsugumon.model;

import java.util.Timer;

import javax.xml.bind.annotation.XmlRootElement;

import site.saishin.tsugumon.TsugumonConstants;

@XmlRootElement
public class Infomation {
	Timer timer;
	public int getMaxSelectAnswerSize() {
		return TsugumonConstants.MAX_SELECT_ANSWER_SIZE;
	}
}
