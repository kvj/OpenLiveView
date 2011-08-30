package net.sourcewalker.olv.messages;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Rasterizer;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

public abstract class LiveViewCall extends LiveViewMessage {

    /**
     * Header consists of two bytes and a int value (4 bytes).
     */
    private static final int HEADER_LENGTH = 6;

    public LiveViewCall(byte id) {
        super(id);
    }

    protected abstract byte[] getPayload();

    public byte[] getEncoded() {
        byte[] payload = getPayload();
        int msgLength = payload.length + HEADER_LENGTH;
        ByteBuffer msgBuffer = ByteBuffer.allocate(msgLength);
        msgBuffer.order(ByteOrder.BIG_ENDIAN);
        msgBuffer.put(getId());
        msgBuffer.put((byte) 4);
        msgBuffer.putInt(payload.length);
        msgBuffer.put(payload);
        return msgBuffer.array();
    }
    
    protected void putString(ByteBuffer buffer, byte[] str) {
    	buffer.putShort((short) str.length);
    	buffer.put(str);
    }
    
    protected byte[] stringToByteArray(String str) {
    	return stringToByteArray(str, false);
    }
    
    protected boolean needBitmap(String str) {
    	try {
    		if (null == str) {
				return false;
			}
    		char[] chars = str.toCharArray();
    		for (char c : chars) {
    			int i = (int)c;
    			System.err.println("needBitmap: "+c+", "+i);
				if (i>128) {
					return true;
				}
			}
    		return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return true;
    }
    
//    private String toBin(char b) {
//		StringBuilder sb = new StringBuilder();
//		for (int j = 0; j < 8; j++) {
//			sb.append((b & 1)>0? '1': '0');
//			b >>= 1;
//		}
//		return sb.toString();
//    }
//    
    protected byte[] stringToByteArray(String str, boolean viaImage) {
    	if (null == str) {
			return new byte[0];
		}
    	try {
    		if (viaImage) {
				TextPaint textPaint = new TextPaint();
				textPaint.setTextSize(12);
				textPaint.setAntiAlias(false);
				textPaint.setDither(false);
				textPaint.setSubpixelText(false);
				textPaint.setColor(Color.WHITE);
				int desiredWidth = (int) StaticLayout.getDesiredWidth(str, textPaint);
				if (desiredWidth > 128) {
					desiredWidth = 128;
				}
				StaticLayout layout = new StaticLayout(str, textPaint, desiredWidth, Alignment.ALIGN_NORMAL, 1, 1, true);
				Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Config.ALPHA_8);
				bitmap.setDensity(0);
				Canvas canvas = new Canvas(bitmap);
				layout.draw(canvas);
				ByteBuffer buffer = ByteBuffer.allocate(layout.getWidth()*bitmap.getHeight());
				bitmap.copyPixelsToBuffer(buffer);
				byte[] image = buffer.array();
				short bytes = (short) Math.ceil(image.length/8.0);
				Log.i(getClass().getSimpleName(), "Rendered text: "+layout.getWidth()+"x"+layout.getHeight()+", "+bytes);
				ByteBuffer result = ByteBuffer.allocate(5+bytes);
				result.put((byte) 0xff);
				result.put((byte) layout.getWidth());
				result.put((byte) 0x10);
				result.putShort((short) 0);
				for (int i = 0; i < image.length;) {
					char b = 0;
					for (int j = 0; j < 8 && i<image.length; j++, i++) {
						if (j>0) {
							b >>= 1;
						}
						if (image[i]!=0) {
							b |= 128;
						} else {
							b &= 254;
						}
					}
					result.put((byte) b);
				}
				Log.i(getClass().getSimpleName(), "Rendered image: "+image.length+", "+result.capacity());
	    		bitmapToByteArray(bitmap);
				return result.array();
			}
        	return str.getBytes("iso-8859-1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
    }
    
    protected byte[] bitmapToByteArray(Bitmap bitmap) {
    	if (null == bitmap) {
			return new byte[0];
		}
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	try {
    		OutputStream stream2 = new FileOutputStream("/sdcard/pack.png");
        	bitmap.compress(CompressFormat.PNG, 100, stream);
        	bitmap.compress(CompressFormat.PNG, 100, stream2);
        	stream.close();
        	stream2.close();
        	return stream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return new byte[0];
    }

}
