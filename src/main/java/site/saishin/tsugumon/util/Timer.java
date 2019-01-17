package site.saishin.tsugumon.util;

import java.time.Instant;


public class Timer {
	private Instant longCycle;
	private Instant shortCycle;
	private Instant middleCycle;
	public Instant getLongCycle() {
		return longCycle;
	}
	public void setLongCycle(Instant longCycle) {
		this.longCycle = longCycle;
	}
	public Instant getShortCycle() {
		return shortCycle;
	}
	public void setShortCycle(Instant shortCycle) {
		this.shortCycle = shortCycle;
	}
	public Instant getMiddleCycle() {
		return middleCycle;
	}
	public void setMiddleCycle(Instant middleCycle) {
		this.middleCycle = middleCycle;
	}
	
}
