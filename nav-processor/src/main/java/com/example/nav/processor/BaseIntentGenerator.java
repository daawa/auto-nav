package com.example.nav.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import static com.example.nav.processor.NavProcessor.listenerName;
import static com.example.nav.processor.NavProcessor.navigatorClassName;
import static com.example.nav.processor.NavProcessor.navigatorPackageName;

/**
 * Created by zhangzhenwei on 2017/8/10.
 */


//import android.app.Activity;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.widget.Toast;
//import java.lang.reflect.Field;
//
//android.support.v7.app.AppCompatActivity

public class BaseIntentGenerator {

    ClassName classContext = ClassName.get("android.content", "Context");
    ClassName classComponentName = ClassName.get("android.content", "ComponentName");
    ClassName classIntent = ClassName.get("android.content", "Intent");
    ClassName classPackageManager = ClassName.get("android.content.pm", "PackageManager");
    ClassName classBundle = ClassName.get("android.os", "Bundle");
    ClassName classToast = ClassName.get("android.widget", "Toast");

    TypeName listenerList = ParameterizedTypeName.get(ClassName.get(ArrayList.class), ClassName.get(navigatorPackageName, navigatorClassName, listenerName));

    //ClassName classField = ClassName.get("java.lang.reflect", "Field");

    ClassName classActivity = ClassName.get("android.app", "Activity");

    public void generateBaseIntent(Filer filer) {

        ClassName baseIntentClass = ClassName.get("nav.base", "BaseIntent");

        TypeSpec.Builder builder = TypeSpec.classBuilder(baseIntentClass).addModifiers(Modifier.PUBLIC);
        builder.addField(classContext, "fromContext", Modifier.PRIVATE)
                .addField(String.class, "component", Modifier.PRIVATE)
                .addField(classBundle, "params", Modifier.PROTECTED)
                .addField(int.class, "flags", Modifier.PRIVATE)
                .addField(classPackageManager, "packageManager", Modifier.PRIVATE, Modifier.STATIC);

        MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                .addParameter(classContext, "context")
                .addParameter(String.class, "component")
                .addCode(CodeBlock.of(
                                " this.fromContext = context;\n" +
                                " this.component = component;\n" +
                                " params = new $T();\n",
                         classBundle))
                .build();
        builder.addMethod(constructor);

        MethodSpec go = MethodSpec.methodBuilder("go").addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.of(
                        "$T intent = intent();\n" +
                                "if(preGoListeners != null){\n" +
                                "   for($T listener: preGoListeners){\n" +
                                "       if(listener.onPreGo(fromContext, intent)){\n" +
                                "           return;\n" +
                                "        }\n" +
                                "   }\n" +
                                "}\n" +
                                "if (intentOpenable(intent)){\n" +
                                "    fromContext.startActivity(intent);\n" +
                                "}\n",
                        classIntent, ClassName.get(navigatorPackageName, navigatorClassName, listenerName)))
                .build();
        builder.addMethod(go);

        MethodSpec intent = MethodSpec.methodBuilder("intent").addModifiers(Modifier.PRIVATE)
                .returns(classIntent)
                .addCode(
                        "$T comp = new $T(fromContext, component);\n" +
                                "$T intent = new $T();\n" +
                                "intent.setComponent(comp);\n" +
                                "if(params.size() > 0){\n" +
                                "       intent.putExtras(params);\n" +
                                " }\n" +

                                " if(!(fromContext instanceof $T)){\n" +
                                "     flags |= $T.FLAG_ACTIVITY_NEW_TASK;\n" +
                                " }\n" +
                                " intent.addFlags(flags);\n" +

                                " return intent;\n",
                        classComponentName, classComponentName, classIntent,
                        classIntent, classActivity, classIntent)
                .build();
        builder.addMethod(intent);

        MethodSpec intentOpenable = MethodSpec.methodBuilder("intentOpenable").addModifiers(Modifier.PRIVATE)
                .addParameter(classIntent, "intent")
                .returns(boolean.class)
                .addCode("if(getPackageManager().resolveActivity(intent,0) == null){\n" +
                                "       $T toast = new $T(fromContext);\n" +
                                "       toast.setText(component);\n" +
                                "       toast.setDuration($T.LENGTH_LONG);\n" +
                                "       toast.show();\n" +
                                "       return false;\n" +
                                " }\n" +
                                " return true;\n",
                        classToast, classToast, classToast)
                .build();
        builder.addMethod(intentOpenable);


        MethodSpec getPackageManager = MethodSpec.methodBuilder("getPackageManager")
                .addModifiers(Modifier.PRIVATE)
                .returns(classPackageManager)
                .addCode("if(packageManager == null){\n" +
                                "    synchronized ($T.class){\n" +
                                "        if(packageManager == null){\n" +
                                "             packageManager = fromContext.getPackageManager();\n" +
                                "         }\n" +
                                "     }\n" +
                                " }\n" +
                                " return packageManager;\n",
                        baseIntentClass)
                .build();
        builder.addMethod(getPackageManager);


        MethodSpec getStaticFieldValue = MethodSpec.methodBuilder("getStaticFieldValue")
                .addModifiers(Modifier.PROTECTED)
                .returns(String.class)
                .addParameter(String.class, "qualifiedClassName")
                .addParameter(String.class, "f")
                .addCode("$T key = null;\n" +
                                " try {\n" +
                                "     $T klz = $T.forName(qualifiedClassName);\n" +
                                "     $T field = klz.getDeclaredField(f);\n" +
                                "     field.setAccessible(true);\n" +
                                "     key = field.get(null).toString();\n" +
                                "  } catch ($T e){\n" +
                                "     e.printStackTrace();\n" +
                                "  }\n" +
                                "  return key;\n",
                        String.class, Class.class, Class.class, Field.class, Exception.class)
                .build();

        builder.addMethod(getStaticFieldValue);

        builder.addField(
                listenerList,
                "preGoListeners",
                Modifier.PRIVATE, Modifier.STATIC);
        builder.addMethod(MethodSpec.methodBuilder("setPreGoListeners")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(listenerList,"listeners")
                .addCode("preGoListeners = listeners;\n", "").build());

        try {
            JavaFile.builder(baseIntentClass.packageName(), builder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
