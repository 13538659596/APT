package com.wjstudio;

import com.example.DIActivity;
import com.example.DIView;
import com.example.ReplaceActivity;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;


import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;


@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    private Elements elementUtils;
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 规定需要处理的注解
        return Collections.singleton(ReplaceActivity.class.getCanonicalName());
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("AnnotationProcessor");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ReplaceActivity.class);
        for (Element element : elements) {
            String activityName = getActivityName((TypeElement)element);
            ReplaceActivity replaceActivity = element.getAnnotation(ReplaceActivity.class);
            // 判断是否Class
            TypeElement typeElement = (TypeElement) element;
            List<? extends Element> members = elementUtils.getAllMembers(typeElement);
            MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("get")
                    .addModifiers(Modifier.PUBLIC)
                    .addStatement("$T.out.println($S+$S)", System.class, activityName,replaceActivity.value())
                    .returns(Map.class)
                    .addStatement("return activitys");
            for (Element item : members) {
                ReplaceActivity activity=item.getAnnotation(ReplaceActivity.class);
                if (activity == null) {
                    continue;
                }
            }


            ClassName Wrapper = ClassName.get("com.songwenju.aptproject", "AppActivityWrapper");

            MethodSpec getInstance = MethodSpec.methodBuilder("getInstance")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)

                    .returns(Wrapper)

                    .addStatement("return instance")
                    .build();



            MethodSpec AppActivityWrapper = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("activitys.put($S,$S)",replaceActivity.value(),activityName)
                    .build();

            TypeSpec.Builder typeSpec = TypeSpec.classBuilder("AppActivityWrapper")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(bindViewMethodSpecBuilder.build())
                    .addMethod(AppActivityWrapper)
                    .addMethod(getInstance);


            //创建变量
            FieldSpec fieldSpec = FieldSpec.builder(HashMap.class,"activitys")
                    .addModifiers(Modifier.PUBLIC)
                    .initializer("new HashMap<String, String>();")
                    .build();


            //创建变量
            FieldSpec instance = FieldSpec.builder(Wrapper,"instance")
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new AppActivityWrapper()")
                    .build();

            typeSpec.addField(fieldSpec);
            typeSpec.addField(instance);





            JavaFile javaFile = JavaFile.builder(getPackageName(typeElement), typeSpec.build()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
   /* private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }*/
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
    }
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }



    private String getActivityName(TypeElement variableElement) {
        String packageName = getPackageName(variableElement);
        return packageName+"."+ variableElement.getSimpleName().toString();
    }

    private String getPackageName(TypeElement variableElement) {
        String packageName = processingEnv.getElementUtils().getPackageOf(variableElement).getQualifiedName().toString();
        System.out.println("-------packageName--------"+packageName);
        return packageName;
    }
}
