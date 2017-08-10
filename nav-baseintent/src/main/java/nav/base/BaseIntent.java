package nav.base;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by zhangzhenwei on 2017/8/9.
 */

public class BaseIntent {
    private Context fromContext;
    private String component;
    //private Map<String, Object> params;
    protected Bundle params;
    private int flags = 0;

    private static PackageManager packageManager;

    public BaseIntent(Context context, String component){
        this.fromContext = context;
        this.component = component;
        params = new Bundle();
    }

    public void go(){

        Intent intent = intent();
        if (intentOpenable(intent)){
//            if (fragment != null ){
//                fragment.startActivity(intent);
//            } else if (from != null) {
//                (from).startActivity(intent);
//            }
            fromContext.startActivity(intent);
        }
    }

    private Intent intent(){
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


    private boolean intentOpenable(Intent intent){
        if(getPackageManager().resolveActivity(intent,0) == null){
            Toast toast = new Toast(fromContext);
//            View v = LayoutInflater.from(fromContext).inflate(R.layout.toast_large,null);
//            ((TextView)v.findViewById(R.id.component_name)).setText(component);
//            toast.setView(v);
            toast.setText(component);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        return true;
    }


    private  PackageManager getPackageManager(){
        if(packageManager == null){
            synchronized (BaseIntent.class){
                if(packageManager == null){
                    packageManager = fromContext.getPackageManager();
                }
            }
        }

        return packageManager;
    }

    protected String getStaticFieldValue(String pck, String clz, String f){
        String key = null;
        try {
            Class klz = Class.forName(pck+"."+ clz);
            Field field = klz.getDeclaredField(f);
            field.setAccessible(true);
            key = field.get(null).toString();
        } catch (Exception e){

        }

        return key;
    }
}
