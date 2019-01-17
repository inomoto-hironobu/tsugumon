package site.saishin.tsugumon.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

import site.saishin.tsugumon.TsugumonConstants;

public class AccessManager {

	public enum Strategy {
		LONG,MIDDLE,SHORT
	}
	private Map<String, MutableInt> applicationCycle = new HashMap<>();
	private Map<String, MutableInt> longCycle = new HashMap<>();
	private Map<String, MutableInt> middleCycle = new HashMap<>();
	private Map<String, MutableInt> shortCycle = new HashMap<>();
	
	/**
	 * 
	 * 許可されないならfalse
	 */
	public boolean access(String addr, Strategy strategy) {
		if (applicationCycle.containsKey(addr)) {
			return false;
		} else if (longCycle.containsKey(addr)) {
			longCycle.get(addr).increment();
			if (longCycle.get(addr).addAndGet(1) > TsugumonConstants.MIDDLE_TO_LONG_LIMIT) {
				synchronized (longCycle) {
					synchronized (applicationCycle) {
						applicationCycle.put(addr, longCycle.remove(addr));
					}
				}
			}
			switch (strategy) {
			case LONG:
				return true;
			default:
				return false;
			}
		} else if (middleCycle.containsKey(addr)) {
			if (middleCycle.get(addr).addAndGet(1) > TsugumonConstants.MIDDLE_TO_LONG_LIMIT) {
				synchronized (middleCycle) {
					synchronized (longCycle) {
						longCycle.put(addr, middleCycle.remove(addr));
					}
				}
			}
			switch (strategy) {
			case LONG:
				return true;
			case MIDDLE:
				return true;
			default:
				return false;
			}
		} else if (shortCycle.containsKey(addr)) {
			if (shortCycle.get(addr).addAndGet(1) > TsugumonConstants.SHORT_TO_MIDDLE_LIMIT) {
				synchronized (shortCycle) {
					synchronized (middleCycle) {
						middleCycle.put(addr, shortCycle.remove(addr));
					}
				}
			}
		} else {
			shortCycle.put(addr, new MutableInt(1));
		}
		return true;
	}

	public int count(String addr) {
		if(longCycle.containsKey(addr)) {
			return longCycle.get(addr).getValue();
		} else if(middleCycle.containsKey(addr)) {
			return middleCycle.get(addr).getValue();
		} else if(shortCycle.containsKey(addr)) {
			return shortCycle.get(addr).getValue();
		}
		return 0;
	}
	public void clearShort() {
		synchronized (shortCycle) {
			shortCycle.clear();
		}
	}

	public void clearMiddle() {
		synchronized (middleCycle) {
			middleCycle.clear();
		}
	}

	public void clearLong() {
		synchronized (longCycle) {
			longCycle.clear();
		}
	}
}
