package net.sourcewalker.olv.messages.calls;

import java.nio.ByteBuffer;

import net.sourcewalker.olv.messages.MessageConstants;
import net.sourcewalker.olv.messages.SimpleLiveViewCall;

public class SetScreenMode extends SimpleLiveViewCall {
	
	public SetScreenMode(int mode, boolean auto) {
		super(MessageConstants.MSG_SETSCREENMODE);
		byte val = (byte) mode;
		val <<= 1;
		if (auto) {
			val |=1;
		}
		buffer = ByteBuffer.allocate(1);
		buffer.put(val);
	}
}
