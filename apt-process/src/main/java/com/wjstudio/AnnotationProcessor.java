package com.wjstudio;

import com.example.ReplaceActivity;
import com.google.auto.service.AutoService;


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
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ReplaceActivity.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("-----------------process-----------------------");



        Map<String, List<TypeElement>> cacheMap = new HashMap<>();

        //遍历所有AppProject注解
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(ReplaceActivity.class);
        for (Element element : elementSet) {

            TypeElement variableElement = (TypeElement) element;
            String  activityName = getActivityName(variableElement);
            List<TypeElement> list = cacheMap.get(activityName);
            if (list == null) {
                list=new ArrayList<>();
                cacheMap.put(activityName, list);
            }

            list.add(variableElement);
            System.out.println("--------->"+variableElement.getSimpleName().toString());
        }

        String pkg = "com.songwenju.aptproject";
        String clsName = "AppActivityWrapper";
        List<String> lines = new LinkedList<>();
        List<String> mapLines = new LinkedList<>();
        lines.add("import android.text.TextUtils;");
        lines.add("import java.util.HashMap;");
        lines.add("import java.util.Map;");
        lines.add("import java.util.List;");
        lines.add("import java.util.LinkedList;");
        lines.add("import android.content.Context;");


        try {
            Filer filer = processingEnv.getFiler();
            JavaFileObject javaFileObject = filer.createSourceFile(pkg + "." + clsName);
            Writer writer = javaFileObject.openWriter();

            Set<String> activityNameSets = cacheMap.keySet();
            if (activityNameSets != null) {
                for (String activityName : activityNameSets) {
                    List<TypeElement> caheElements = cacheMap.get(activityName);
                    if (caheElements.size() == 1) {
                        TypeElement typeElement = caheElements.get(0);
                        //AppProject appProject = typeElement.getAnnotation(AppProject.class);
                        ReplaceActivity replaceActivity = typeElement.getAnnotation(ReplaceActivity.class);

                        if (replaceActivity != null) {
                            lines.add("import " +  getActivityName(typeElement) + ";");
                            lines.add("import "  + replaceActivity.value()+ ";");

                            String key = replaceActivity.value().substring(
                                    replaceActivity.value().lastIndexOf(".") + 1);

                            mapLines.add("\tappActivityMap = new HashMap<>();");
                            String content = "";
                                String values = "new String[]{";

                                content = "         appActivityMap.put(" + key + ".class," + "\n"
                                        + "new AppActivity(" +values + "," +  "\"" + getActivityName(typeElement)   + "\"));";
                            mapLines.add(content);
                            mapLines.add("\tappActivityMaps.add(appActivityMap);\n");
                        }

                    }
                }
            }

            lines.add("/**\n\n" +
                    " * AUTO GENERATE,DO NOT MODIFY\n" +
                    " * AUTHOR:huangxueshi\n\n" +
                    " */");
            lines.add("public class " + clsName + "  {");
            lines.add("   private static final AppActivityWrapper ourInstance = new AppActivityWrapper();");
            lines.add("   public static AppActivityWrapper getInstance() {\n" +
                    "           return ourInstance;\n" +
                    "       }");

            lines.add("   private List<Map<Class,AppActivity>> appActivityMaps = new LinkedList<>();");
            lines.add("   Map<Class,AppActivity> appActivityMap = new HashMap<>();");

            //添加构造方法
            lines.add("   private AppActivityWrapper() {");
            for (String mapLine : mapLines) {
                lines.add(mapLine);
            }
            lines.add("   }");

            //添加getter
            lines.add("public AppActivity get(Context context,Class actviityCls) {\n" +
                    "      AppActivity result = null;\n" +
                    "       for (Map<Class,AppActivity> appActivityMap : appActivityMaps) {\n" +
                    "           AppActivity appActivity =  appActivityMap.get(actviityCls);\n" +
                    "           if (appActivity != null) {\n" +
                  //  "               LogPrint.d(actviityCls + \" appActivity----->\" + appActivity.getActivityClass());\n" +
                    "               String appProject = PropertiesLoader.getProjectPropFileName();\n" +
                    "               String[] values = appActivity.getAppProjects();\n" +
                    "               if (values != null) {\n" +
                    "                   //所有项目都使用这个activity\n" +
                    "                    if (values.length >= 1 && !TextUtils.isEmpty(values[0])) {\n" +
                    "                        if (values[0].startsWith(\"*/\")) {\n" +
                    "                            String projectName = values[0].split(\"/\")[1];\n" +
                    "                            String configProjectName = PropertiesHelper.getInstance(context)\n" +
                   // "                                    .getProperty(ProjectConfigConstant.CONFIG_PROJECT_NAME);\n" +
                   // "                            LogPrint.d(\"projectName=\" + projectName + \",configProjectName=\" + configProjectName);\n" +
                    "                            if (projectName.equals(configProjectName)) {\n" +
                    //"                                LogPrint.d(actviityCls + \" real activity----->\" + appActivity.getActivityClass());\n" +
                    "                                return appActivity;\n" +
                    "                            }\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                    //特定项目使用指定的activity\n" +
                    "                    for (String value : values) {\n" +
                    "                       if (appProject.equals(value)) {\n" +
                    "                           result =  appActivity;\n" +
                    "                           break;\n" +
                    "                       }\n" +
                    "                   }\n" +
                    "               }\n" +
                    "           }\n" +
                    "       }\n" +
                    "\n" +
                    //"        LogPrint.d(actviityCls + \" real activity----->\" + result != null ? result.getActivityClass() : null);\n" +
                    "        return result;\n" +
                    "  }\n");
            lines.add("}");

            writeLines(writer,lines);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return false;
    }

    private void writeLines(Writer writer, List<String> headers ) {

        try {
           for (String header : headers) {
               writer.write(header);
               writer.write("\n");
           }
        } catch (IOException e) {
            e.printStackTrace();
        }


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
