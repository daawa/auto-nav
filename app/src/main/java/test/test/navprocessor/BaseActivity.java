package test.test.navprocessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.annotation.AutoWireNav;
import com.example.annotation.IntentParam;


@AutoWireNav
public class BaseActivity extends AppCompatActivity {

    @IntentParam
    private static String baseParamA = "base_a";
    @IntentParam(name = "baseParamBilli", type = "float")
    private static String baseParamB = "base_b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if(getIntent().getExtras() != null){
            String msg = "received: base param A: " + getIntent().getExtras().getString(baseParamA)
                    + " base param B: " + getIntent().getExtras().getFloat(baseParamB);

            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
        }
    }
}
