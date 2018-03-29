package test.test.navprocessor;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import com.example.annotation.AutoWireNav;
import com.example.annotation.IntentParam;

import test.nav.NativeNav;

@AutoWireNav(name="Main")
public class MainActivity extends BaseActivity {

    @IntentParam(name = "mainKey1", type = "string")
    private static final String main_key_1 = "mainkey1";
    @IntentParam(type="float")
    private static final String main_key2 = "mainkey2";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.text);
        tv.setText(stringFromJNI());

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NativeNav.toSecond(MainActivity.this)
                        .param_(new TestParcel())
                        .param2_("ttt")
                        .baseParamA_("aaaa")
                        .baseParamBilli_(11.f)
                        .go();
            }
        });

        if (getIntent().getExtras() != null) {
            ((TextView) findViewById(R.id.param1)).setText(main_key_1 + ":" + getIntent().getExtras().getString(main_key_1));
            ((TextView) findViewById(R.id.param2)).setText(main_key2 + ":" + getIntent().getExtras().getString(main_key2));
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static class TestParcel implements Parcelable{
        int a = 3;
        String b = "value in a  parcelable";

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(a);
            dest.writeString(b);
        }

        public static final Creator<TestParcel> CREATOR = new Creator<TestParcel>() {
            @Override
            public TestParcel createFromParcel(Parcel source) {
                TestParcel parcel = new TestParcel();
                parcel.a = source.readInt();
                parcel.b = source.readString();
                return parcel;
            }

            @Override
            public TestParcel[] newArray(int size) {
                return new TestParcel[size];
            }
        };


    }
}
