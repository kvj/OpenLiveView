package net.sourcewalker.olv;

import net.sourcewalker.olv.messages.impl.CapsRequest;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class LiveViewText extends Activity {

    private TextView textView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView = (TextView) findViewById(R.id.text);

        byte[] msg = new CapsRequest().getEncoded();
        textView.setText(new String(msg));
    }
}
