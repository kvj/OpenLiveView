package net.sourcewalker.olv.messages;

import java.nio.ByteBuffer;

public class SimpleLiveViewCall extends LiveViewCall {
	
	protected ByteBuffer buffer = null;
	
	public SimpleLiveViewCall(byte type) {
		super(type);
	}
	@Override
	protected byte[] getPayload() {
		return buffer.array();
	}

}
