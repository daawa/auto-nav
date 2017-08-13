package com.example.nav.processor;

import com.example.ActivityIntentModel;
import com.example.annotation.IntentParam;
import com.example.annotation.NewIntent;
import com.google.auto.service.AutoService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class NavProcessor extends AbstractProcessor {
    private static final String METHOD_PREFIX = "to";
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private static final ClassName classComponentName = ClassName.get("android.content", "ComponentName");

    private Filer filer;
    private Messager messager;
    private Elements elementsUtil;

    private Set<ActivityIntentModel> activityModels;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elementsUtil = processingEnvironment.getElementUtils();
        activityModels = new HashSet<>();
    }


    /**
     * @param set              the annotations we could deal with,
     *                         same as what {@link #getSupportedAnnotationTypes} returns.
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "begin process annotation");
        if (set != null && !set.isEmpty()) {
            for (TypeElement e : set) {
                messager.printMessage(Diagnostic.Kind.NOTE, "Accepted annotation set:" + e.getSimpleName());
            }
        } else {
            // when set is null or empty, it is a round having nothing to do with this processor.
            return false;
        }

        new BaseIntentGenerator().generateBaseIntent(filer);

        for (Element element : roundEnvironment.getElementsAnnotatedWith(NewIntent.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                messager.printMessage(Diagnostic.Kind.ERROR, "NewIntent can only be applied to class");
                return true;
            }

            TypeElement typeElement = (TypeElement) element;
            ActivityIntentModel activityModel = getActivityModel(typeElement);
            activityModels.add(activityModel);
        }

        createNavigator(activityModels);
        return true;
    }


    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(NewIntent.class.getCanonicalName()
        );
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    private ActivityIntentModel getActivityModel(TypeElement typeElement) {
        ActivityIntentModel activityModel = new ActivityIntentModel(
                elementsUtil.getPackageOf(typeElement).getQualifiedName().toString(),
                typeElement.getSimpleName().toString());

        List<? extends Element> list = typeElement.getEnclosedElements();
        if (list != null) {
            for (Element e : list) {
                IntentParam intentParam = e.getAnnotation(IntentParam.class); //AnnotationMirror mirror = e.getAnnotationMirrors().get(0);
                if (intentParam != null) {
                    if (e.getKind() != ElementKind.FIELD || e.getModifiers() == null || !e.getModifiers().contains(Modifier.STATIC)) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "IntentParam can only be applied to STATIC FIELD");
                        continue;
                    }
                    messager.printMessage(Diagnostic.Kind.NOTE, "IntentParam annotated element:" + e.getSimpleName());

                    ActivityIntentModel.ParamModel paramModel = new ActivityIntentModel.ParamModel();
                    paramModel.fieldName = e.getSimpleName().toString();
                    //TypeMirror fieldType = e.asType();
                    //paramModel.type = fieldType;
                    paramModel.type = intentParam.type();
                    paramModel.generatedPropName = intentParam.name();
                    activityModel.addParamModel(paramModel);

                }
            }
        }

        return activityModel;
    }

    private void createNavigator(Set<ActivityIntentModel> activityModels) {
        TypeSpec.Builder navigatorBuilder = TypeSpec.classBuilder("Navigator").addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (ActivityIntentModel model : activityModels) {
            createActivityIntent(model);

            /**
             *         public static MainActivityIntent toMainActivity(Context context){
             *             MainActivityIntent intent = new MainActivityIntent(context, "com.com.MainActivity");
             *             return intent;
             *         }
             */

            ClassName returnType = ClassName.get(model.getPackageName(), model.getIntentClzName());
            MethodSpec.Builder methodSpecBuilder = MethodSpec
                    .methodBuilder(METHOD_PREFIX + model.getClzName());
            methodSpecBuilder.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(classContext, "context")
                    .returns(ClassName.get(model.getPackageName(), model.getIntentClzName()))
                    .addStatement("$T intent = new $T($L,$S)", returnType, returnType, "context", model.getQualifiedName())
                    .addStatement("return intent");

            navigatorBuilder.addMethod(methodSpecBuilder.build());
        }

        try {
            JavaFile.builder("nav.base.one", navigatorBuilder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void createActivityIntent(ActivityIntentModel model) {
        ClassName baseIntent = ClassName.get("nav.base", "BaseIntent");
        ClassName activityIntent = ClassName.get(model.getPackageName(), model.getIntentClzName());
        TypeSpec.Builder activityIntentBuilder = TypeSpec.classBuilder(activityIntent).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(baseIntent);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(classContext, "context")
                .addParameter(String.class, "component")
                .addStatement("super($L, $L)", "context", "component");

        activityIntentBuilder.addMethod(constructor.build());


        for (ActivityIntentModel.ParamModel paramModel : model.getParamModelList()) {

            /**
             *     public final MainActivityIntent param1_(String arg){
             *          String key = getStaticFieldValue("com.com","MainActivity","param1");
             *          params.putString(key, arg);
             *          return this;
             *      }
             *
             */

            String methodName = Strings.isNullOrEmpty(paramModel.generatedPropName)? paramModel.fieldName : paramModel.generatedPropName;
            MethodSpec.Builder methodSpecBuilder = MethodSpec
                    .methodBuilder(methodName + "_");

            MethodSpec methodSpec = methodSpecBuilder
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addParameter(getTypeNameFor(paramModel.type), "arg")
                    .addStatement("$T key = getStaticFieldValue($S, $S, $S)", ClassName.get(String.class), model.getPackageName(), model.getClzName(), paramModel.fieldName)
                    .addStatement("params.put" + getSimpleTypeName(paramModel.type) + "($L,$L)", "key", "arg")
                    .addStatement("return this")
                    .returns(activityIntent)
                    .build();

            activityIntentBuilder.addMethod(methodSpec);
        }


        try {
            JavaFile.builder(activityIntent.packageName(), activityIntentBuilder.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private TypeName getTypeNameFor(String arg){
        switch (arg){
            case "string":
                return TypeName.get(String.class);
            case "int":
                return TypeName.get(int.class);
            case "byte":
                return TypeName.get(byte.class);
            case "long":
                return TypeName.get(long.class);
            case "float":
                return TypeName.get(float.class);
            case "double":
                return TypeName.get(float.class);
            case "parcelable":
                return ClassName.get("android.os","Parcelable");

            default:{
                messager.printMessage(Diagnostic.Kind.NOTE," return default 'Serializable' for" + arg);
                return TypeName.get(Serializable.class);
            }

        }
    }

//    private String getSimpleTypeName(Type typeMirror) {
//        String qualifiedName = typeMirror.toString();
//        if (typeEqual(String.class, qualifiedName)) {
//            return "String";
//        } else if (typeEqual(byte.class, qualifiedName)) {
//            return "Byte";
//        } else if (typeEqual(int.class, qualifiedName)) {
//            return "Int";
//        } else if (typeEqual(long.class, qualifiedName)) {
//            return "Long";
//        } else if (typeEqual(String.class, qualifiedName)) {
//            return "Float";
//        } else if (typeEqual(String.class, qualifiedName)) {
//            return "Double";
//        }
//
//        return "Serializable";
//    }

    public String getSimpleTypeName(String original) {
        if (Strings.isNullOrEmpty(original)) {
            return original;
        }
        int dot = original.lastIndexOf(".");
        if(dot > 0 && dot < original.length() -1){
            original = original.substring(dot + 1);
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private boolean typeEqual(Class clz, String qualifiedName) {
        return clz.getName().equals(qualifiedName);
    }
}
