package net.sourcewalker.olv.messages.calls;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import net.sourcewalker.olv.messages.LiveViewCall;
import net.sourcewalker.olv.messages.MessageConstants;

public class DisplayPanel extends LiveViewCall{

	ByteBuffer buffer;
	
	public DisplayPanel(String topText, String bottomText, 
			Bitmap bitmap, boolean alertUser) {
		super(MessageConstants.MSG_DISPLAYPANEL);
		try {
			byte[] _topText = stringToByteArray(topText);
			byte[] _bottomText = stringToByteArray(bottomText);
			byte[] _bitmap = bitmapToByteArray(bitmap);
			buffer = ByteBuffer.allocate(
					15+_topText.length+_bottomText.length+_bitmap.length);
			buffer.put((byte) 0);
			buffer.putShort((short) 0);
			buffer.putShort((short) 0);
			buffer.putShort((short) 0);
			buffer.put(alertUser? (byte) 80 | 1: (byte) 80);
			buffer.put((byte) 0);
			putString(buffer, _topText);
			buffer.putShort((short) 0);
			putString(buffer, _bottomText);
			buffer.put(_bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected byte[] getPayload() {
		return buffer.array();
	}

}
