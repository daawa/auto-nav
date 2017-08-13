package test.test.navprocessor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.annotation.IntentParam;
import com.example.annotation.NewIntent;

import nav.base.one.Navigator;


@NewIntent
public class SecondActivity extends AppCompatActivity {


    @IntentParam(name="param",type = "parcelable")
    private static final String test = "key1";
    @IntentParam(name="param2")
    private static final String ttt= "key2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.toMainActivity(SecondActivity.this)
                        .main_key2_(243.0f)
                        .mainKey1_("skfjdklfs")
                        .go();
            }
        });


        ((TextView)findViewById(R.id.param1)).setText(test + ":" + ((MainActivity.TestParcel)getIntent().getExtras().getParcelable(test)).b);
        ((TextView)findViewById(R.id.param2)).setText(ttt + ":" + getIntent().getExtras().getString(ttt));
    }
}
