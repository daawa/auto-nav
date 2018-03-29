package test.nav;

import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import test.test.navprocessor.BaseActivityIntent;
import test.test.navprocessor.MainActivityIntent;
import test.test.navprocessor.SecondActivityIntent;

public final class NativeNav {
  private static ArrayList<PreGoListener> preGoListeners;

  static {
    preGoListeners = new ArrayList<PreGoListener>();
    BaseIntent.setPreGoListeners(preGoListeners);
  }

  public static SecondActivityIntent toSecond(Context context) {
    SecondActivityIntent intent = new SecondActivityIntent(context,"test.test.navprocessor.SecondActivity");
    return intent;
  }

  public static MainActivityIntent toMain(Context context) {
    MainActivityIntent intent = new MainActivityIntent(context,"test.test.navprocessor.MainActivity");
    return intent;
  }

  public static BaseActivityIntent toBaseActivity(Context context) {
    BaseActivityIntent intent = new BaseActivityIntent(context,"test.test.navprocessor.BaseActivity");
    return intent;
  }

  public static void addPreGoListener(PreGoListener listener) {
    if(listener != null){ preGoListeners.add(listener);}
  }

  public static void removePreGoListener(PreGoListener listener) {
    if(listener != null){ preGoListeners.remove(listener);}
  }

  public interface PreGoListener {
    boolean onPreGo(Context fromContext, Intent intent);
  }
}
