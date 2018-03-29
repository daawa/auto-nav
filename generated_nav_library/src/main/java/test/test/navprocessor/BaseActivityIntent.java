package test.test.navprocessor;

import android.content.Context;
import java.lang.String;
import test.nav.BaseIntent;

public final class BaseActivityIntent extends BaseIntent {
  public BaseActivityIntent(Context context, String component) {
    super(context, component);
  }

  public final BaseActivityIntent baseParamA_(String arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamA");
    params.putString(key,arg);
    return this;
  }

  public final BaseActivityIntent baseParamBilli_(float arg) {
    String key = getStaticFieldValue("test.test.navprocessor.BaseActivity", "baseParamB");
    params.putFloat(key,arg);
    return this;
  }
}
