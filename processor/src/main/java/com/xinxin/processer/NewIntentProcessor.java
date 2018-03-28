package com.xinxin.processer;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.xinxin.annotation.NewIntent;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class NewIntentProcessor extends AbstractProcessor{

    private static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    private static final ClassName INTENT = ClassName.get("android.content", "Intent");

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element element : roundEnvironment.getElementsAnnotatedWith(NewIntent.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
                String className = element.getSimpleName().toString();

                MethodSpec methodSpec = MethodSpec.methodBuilder("start" + className)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(CONTEXT, "context")
                        .addStatement("$T intent = new $T(context, " + packageName + "." + className + ".class" + ")", INTENT, INTENT)
                        .addStatement("context.startActivity(intent)")
                        .build();

                TypeSpec typeSpec = TypeSpec.classBuilder("Navigator")
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodSpec)
                        .build();
                JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
                try {
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(NewIntent.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
