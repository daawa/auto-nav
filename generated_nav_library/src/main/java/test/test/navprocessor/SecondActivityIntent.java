package test.test.navprocessor;

import android.content.Context;
import android.os.Parcelable;
import java.lang.String;
import test.nav.BaseIntent;

public final class SecondActivityIntent extends BaseIntent {
  public SecondActivityIntent(Context context, String component) {
    super(context, component);
  }

  public final SecondActivityIntent param_(Parcelable arg) {
    String key = getStaticFieldValue("test.test.navprocessor.SecondActivity", "test");
    params.putParcelable(key,arg);
    return this;
  }

  public final SecondActivityIntent param2_(String arg) {
    String key = getStaticFieldValue("test.test.navprocessor.SecondActivity", "ttt");
    params.putString(key,arg);
    return this;
  }

  public final SecondActivityIntent baseParamA_(String arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamA");
    params.putString(key,arg);
    return this;
  }

  public final SecondActivityIntent baseParamBilli_(float arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamB");
    params.putFloat(key,arg);
    return this;
  }
}
