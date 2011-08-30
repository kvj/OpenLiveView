package net.sourcewalker.olv.messages.calls;

import net.sourcewalker.olv.messages.LiveViewCall;
import net.sourcewalker.olv.messages.MessageConstants;

public class GetScreenMode extends LiveViewCall {

	public GetScreenMode() {
		super(MessageConstants.MSG_GETSCREENMODE);
	}

	@Override
	protected byte[] getPayload() {
		return new byte[0];
	}
}
