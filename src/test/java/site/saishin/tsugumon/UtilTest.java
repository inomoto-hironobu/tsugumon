package site.saishin.tsugumon;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.servlet.ServletRequest;

public class UtilTest {

	//@Test
	public void test() throws Exception {
		ServletRequest req = null;
		String actual = TsugumonUtil.tranformForBase64From(req);
		String base64 = null;
		try {
			byte[] addr;
			byte[] tmpaddr = InetAddress.getByName("127.0.0.1").getAddress();
			if(tmpaddr.length == 4) {
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
		assertEquals(base64, actual);
	}
}
