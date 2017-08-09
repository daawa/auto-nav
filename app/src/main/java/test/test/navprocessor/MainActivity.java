package test.test.navprocessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.annotation.IntentParam;
import com.example.annotation.NewIntent;

import nav.base.one.Navigator;

@NewIntent
public class MainActivity extends AppCompatActivity {

    @IntentParam
    private static final String mainkey1 = "mainkey1";
    @IntentParam
    private static final String mainkey2 = "mainkey2";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.toSecondActivityIntent(MainActivity.this)
                        .test_("test")
                        .ttt_("ttt")
                        .go();
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
