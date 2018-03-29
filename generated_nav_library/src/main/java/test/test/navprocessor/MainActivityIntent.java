package test.test.navprocessor;

import android.content.Context;
import java.lang.String;
import test.nav.BaseIntent;

public final class MainActivityIntent extends BaseIntent {
  public MainActivityIntent(Context context, String component) {
    super(context, component);
  }

  public final MainActivityIntent mainKey1_(String arg) {
    String key = getStaticFieldValue("test.test.navprocessor.MainActivity", "main_key_1");
    params.putString(key,arg);
    return this;
  }

  public final MainActivityIntent main_key2_(float arg) {
    String key = getStaticFieldValue("test.test.navprocessor.MainActivity", "main_key2");
    params.putFloat(key,arg);
    return this;
  }

  public final MainActivityIntent baseParamA_(String arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamA");
    params.putString(key,arg);
    return this;
  }

  public final MainActivityIntent baseParamBilli_(float arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamB");
    params.putFloat(key,arg);
    return this;
  }
}
