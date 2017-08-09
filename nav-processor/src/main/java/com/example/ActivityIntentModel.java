package com.example;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

/**
 * Created by zhangzhenwei on 2017/8/9.
 */

public class ActivityIntentModel {
    private String packageName;
    private String clzName;
    private String classIntentName;

    String qualifiedName;

    List<ParamModel> paramModelList;

    public ActivityIntentModel(String packageName, String clzName) {
        this.packageName = packageName;
        this.clzName = clzName ;
        this.classIntentName = clzName + "Intent";
        paramModelList = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {

        if (o != null && o instanceof ActivityIntentModel) {
            ActivityIntentModel b = (ActivityIntentModel) o;
            if (packageName != null && b.packageName != null && packageName.equals(b.packageName)
                    && clzName != null && b.clzName != null && clzName.equals(b.clzName)) {
                return true;
            }
        }

        return super.equals(o);
    }

    public String getPackageName(){
        return packageName;
    }

    public String getIntentClzName(){
        return classIntentName;
    }

    public String getClzName(){
        return clzName;
    }

    public static class ParamModel {
        String fieldName;
        TypeMirror type;
    }


}
