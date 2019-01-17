package site.saishin.tsugumon.util;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class Timerset {
	Set<String> set = new HashSet<>();
	Instant time;

	public boolean add(String ipAddress) {
		return set.add(ipAddress);
	}
	public boolean contains(Object obj) {
		return set.contains(obj);
	}
	public void clear() {
		time = Instant.now();
		set.clear();
	}
	public int size() {
		return set.size();
	}
	public Instant getTime() {
		return time;
	}
}
