package com.example;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
public class NewIntentProcessor extends AbstractProcessor {
    private static final String METHOD_PREFIX = "start";
    private static final ClassName classIntent = ClassName.get("android.content", "Intent");
    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private static final ClassName classComponentName = ClassName.get("android.content", "ComponentName");

    private Filer filer;
    private Messager messager;
    private Elements elements;
    private Map<String, String> activityesWithPackage;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        activityesWithPackage = new HashMap<>();
    }


    /**
     *
     * @param set the annotations we could deal with,
     *            same as what {@link #getSupportedAnnotationTypes} returns.
     *
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "begin process annotation");
        if(set != null && !set.isEmpty()){
            for(TypeElement e : set){
                messager.printMessage(Diagnostic.Kind.NOTE, " set:" + e.getSimpleName());
            }
        } else {
            // when set is null or empty, it is a round having nothing to do with this processor.
            return false;
        }

        try {
            /**
             * 1. find all annotated classes
             */
            for (Element element : roundEnvironment.getElementsAnnotatedWith(NewIntent.class)) {
                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "NewIntent can only be applied to class");
                    return true;
                }

                TypeElement typeElement = (TypeElement) element;
                activityesWithPackage.put(
                        typeElement.getSimpleName().toString(),
                        elements.getPackageOf(typeElement).getQualifiedName().toString());

            }

            /**
             * 2. generate a class
             */
            TypeSpec.Builder navigatorBuilder = TypeSpec.classBuilder("Navigator").addModifiers(Modifier.PUBLIC, Modifier.FINAL);
            for (Map.Entry<String, String> entry : activityesWithPackage.entrySet()) {
                String activityName = entry.getKey();
                String packageName = entry.getValue();
                ClassName activityClass = ClassName.get(packageName, activityName);

                MethodSpec.Builder methodSpecBuilder = MethodSpec
                        .methodBuilder(METHOD_PREFIX + activityName);

                MethodSpec methodSpec = methodSpecBuilder
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(classIntent)
                        .addParameter(classContext, "context")
                        .addStatement("$T comp = new $T($L, $S)", classComponentName, classComponentName, "context", activityClass.toString())
                        .addStatement("$T intent = new $T()", classIntent, classIntent)
                        .addStatement("intent.setComponent(comp)")
                        .addStatement("return intent")
                        .build();
                messager.printMessage(Diagnostic.Kind.NOTE,methodSpec.toString());
                navigatorBuilder.addMethod(methodSpec);
            }

            /**
             * 3. write generated class to file
             */
            JavaFile.builder("com.nav.one", navigatorBuilder.build()).build().writeTo(filer);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public Set<String> getSupportedOptions() {
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(NewIntent.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
        //return super.getSupportedSourceVersion();
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }
}
