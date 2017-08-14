package com.github.daawa.nav;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangzhenwei on 2017/8/9.
 */

public class ActivityIntentModel {
    private String packageName;
    private String clzName;
    private String classIntentName;

    private String qualifiedName;

    private List<ParamModel> paramModelList;

    public ActivityIntentModel(String packageName, String clzName) {
        this.packageName = packageName;
        this.clzName = clzName ;
        this.qualifiedName = packageName + "." + clzName;
        this.classIntentName = clzName + "Intent";
        paramModelList = new ArrayList<>();
    }

    public void addParamModel(ParamModel model){
        paramModelList.add(model);
    }

    public void addParamModels(List<ParamModel> models){
        if(models != null){
            paramModelList.addAll(models);
        }
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

    public List<ParamModel> getParamModelList(){
        return paramModelList;
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

    public String getQualifiedName(){
        return qualifiedName;
    }

    public static class ParamModel {
        public String fieldName;
        public String type;
        public String generatedPropName;

        public String qualifiedClassName;
    }


}
