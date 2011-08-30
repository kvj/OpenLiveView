package net.sourcewalker.olv.messages.calls;

import net.sourcewalker.olv.messages.LiveViewCall;
import net.sourcewalker.olv.messages.MessageConstants;

public class ClearDisplay extends LiveViewCall {

	public ClearDisplay() {
		super(MessageConstants.MSG_CLEARDISPLAY);
	}

	@Override
	protected byte[] getPayload() {
		return new byte[0];
	}
}
