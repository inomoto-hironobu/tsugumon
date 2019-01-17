package site.saishin.tsugumon;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.servlet.ServletRequest;

public final class Util {
	private Util() {
	}

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

}
