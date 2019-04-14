package site.saishin.tsugumon.util;

import java.nio.ByteBuffer;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import site.saishin.tsugumon.TsugumonConstants;

public class ByteBufferPoolFactory extends BasePooledObjectFactory<ByteBuffer> {

	@Override
	public ByteBuffer create() throws Exception {
		return ByteBuffer.allocate(TsugumonConstants.MAX_ENQUETE_JSON_SIZE);
	}
	@Override
	public PooledObject<ByteBuffer> wrap(ByteBuffer byteBuffer) {
		return new DefaultPooledObject<ByteBuffer>(byteBuffer);
	}
	@Override
	public void passivateObject(PooledObject<ByteBuffer> p) {
		ByteBuffer b = p.getObject();
		b.clear();
	}
}
