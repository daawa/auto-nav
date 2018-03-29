package test.nav;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import java.lang.Class;
import java.lang.Exception;
import java.lang.String;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class BaseIntent {
  private static PackageManager packageManager;

  private static ArrayList<NativeNav.PreGoListener> preGoListeners;

  private Context fromContext;

  private String component;

  protected Bundle params;

  private int flags;

  public BaseIntent(Context context, String component) {
     this.fromContext = context;
     this.component = component;
     params = new Bundle();
  }

  public void go() {
    Intent intent = intent();
    if(preGoListeners != null){
       for(NativeNav.PreGoListener listener: preGoListeners){
           if(listener.onPreGo(fromContext, intent)){
               return;
            }
       }
    }
    if (intentOpenable(intent)){
        fromContext.startActivity(intent);
    }
  }

  private Intent intent() {
    ComponentName comp = new ComponentName(fromContext, component);
    Intent intent = new Intent();
    intent.setComponent(comp);
    if(params.size() > 0){
           intent.putExtras(params);
     }
     if(!(fromContext instanceof Activity)){
         flags |= Intent.FLAG_ACTIVITY_NEW_TASK;
     }
     intent.addFlags(flags);
     return intent;
  }

  private boolean intentOpenable(Intent intent) {
    if(getPackageManager().resolveActivity(intent,0) == null){
           Toast toast = new Toast(fromContext);
           toast.setText(component);
           toast.setDuration(Toast.LENGTH_LONG);
           toast.show();
           return false;
     }
     return true;
  }

  private PackageManager getPackageManager() {
    if(packageManager == null){
        synchronized (BaseIntent.class){
            if(packageManager == null){
                 packageManager = fromContext.getPackageManager();
             }
         }
     }
     return packageManager;
  }

  protected String getStaticFieldValue(String qualifiedClassName, String f) {
    String key = null;
     try {
         Class klz = Class.forName(qualifiedClassName);
         Field field = klz.getDeclaredField(f);
         field.setAccessible(true);
         key = field.get(null).toString();
      } catch (Exception e){
         e.printStackTrace();
      }
      return key;
  }

  public static void setPreGoListeners(ArrayList<NativeNav.PreGoListener> listeners) {
    preGoListeners = listeners;
  }
}
