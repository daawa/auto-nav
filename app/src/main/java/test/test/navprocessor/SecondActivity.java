package test.test.navprocessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.annotation.IntentParam;
import com.example.annotation.IntentParam2;
import com.example.annotation.NewIntent;

import nav.base.one.Navigator;


@NewIntent
public class SecondActivity extends AppCompatActivity {


    @IntentParam
    private static final String test = "key1";
    @IntentParam
    private static final String ttt= "key2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.toMainActivity(SecondActivity.this)
                        .mainkey1_("djfkds")
                        .mainkey2_("skfjdklfs")
                        .go();
            }
        });
    }
}
