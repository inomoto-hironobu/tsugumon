package site.saishin.tsugumon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import site.saishin.tsugumon.entity.User;

public final class TsugumonUtil {
	private TsugumonUtil() {}

	private static final Logger logger = LoggerFactory.getLogger(TsugumonUtil.class);
	/**
	 * 
	 * */
	public static String tranformForBase64From(ServletRequest req) throws Exception {
		if (req == null) {
			throw new IllegalArgumentException("null");
		}
		String base64 = null;
		try {
			byte[] addr;
			byte[] tmpaddr = InetAddress.getByName(req.getRemoteAddr()).getAddress();
			if (tmpaddr.length == 4) {
				addr = new byte[16];
				addr[12] = tmpaddr[0];
				addr[13] = tmpaddr[1];
				addr[14] = tmpaddr[2];
				addr[15] = tmpaddr[3];
			} else {
				addr = tmpaddr;
			}
			base64 = new String(Base64.getEncoder().encode(addr));
		} catch (UnknownHostException e) {
			throw new Exception(e);
		}
		return base64;
	}
	public static String getAddr(HttpServletRequest req, Set<String> proxys) {
		String xff = req.getHeader("X-Forwarded-For");
		if(xff != null) {
			logger.info(xff);
			proxys.add(req.getRemoteAddr());
			return xff;
		}
		return req.getRemoteAddr();
	}
	public static boolean checkAvailable(String addr, int count, Set<String> availableUsers) {
		if (availableUsers.contains(addr)) {
			return true;
		} else {
			boolean available;
			//
			available = count <= TsugumonConstants.MAX_SELECT_ANSWER_SIZE ;
			if (available) {
				availableUsers.add(addr);
			}
			return available;
		}
	}
}
