package org.mili.generator;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static java.util.Collections.emptyList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.mili.generator.config.ConfigType;
import org.mili.generator.config.ForType;
import org.mili.generator.config.TemplateType;
import org.mili.generator.model.ClassType;
import org.mili.generator.model.EnumType;
import org.mili.generator.model.MemberType;
import org.mili.generator.model.ModelType;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXB;

/**
 * @author Michael Lieshoff
 */
public class Generator {

    private String mainPackage;

    private Map<Types, Set<String>> templatesByType = new HashMap<>();

//    private static final File out = new File("/home/micha/dev/jcrapi2/src/main/java/jcrapi2/model");
//    private static final File testOut = new File("/home/micha/dev/jcrapi2/src/test/java/jcrapi2/model");

//    private static final File out = new File("/home/micha/dev/jcrapi/src/main/java/jcrapi/model");
//    private static final File testOut = new File("/home/micha/dev/jcrapi/src/test/java/jcrapi/model");

    private static final File out = new File("/home/micha/dev/bytediscover/git/bdta_maven/bytediscoverTAWeb/src/com/bytediscover/ta/web/illusion/v2/fastsearch/model");
    private static final File testOut = new File("/home/micha/dev/bytediscover/git/bdta_maven/bytediscoverTAWeb/test/com/bytediscover/ta/web/illusion/v2/fastsearch/model");

    private ModelType modelType;

    private Map<ClassType, List<ClassType>> inheritations = new HashMap<>();
    private Map<String, ClassType> classes = new HashMap<>();

    public void start() throws IOException {
        FileUtils.deleteQuietly(testOut);
        FileUtils.forceMkdir(testOut);
        FileUtils.deleteQuietly(out);
        FileUtils.forceMkdir(out);
//        ModelType modelType = JAXB.unmarshal(new File("/home/micha/dev/jcrapi/src/main/resources/model.xml"), ModelType.class);
//        ConfigType configType = JAXB.unmarshal(new File("/home/micha/dev/jcrapi/src/main/resources/config.xml"), ConfigType.class);
        modelType = JAXB.unmarshal(new File("/home/micha/dev/bytediscover/git/bdta_maven/bytediscoverTAWeb/models/illusion/fast-search-model.xml"), ModelType.class);
        ConfigType configType = JAXB.unmarshal(new File("/home/micha/dev/bytediscover/git/bdta_maven/bytediscoverTAWeb/models/illusion/fast-search-config.xml"), ConfigType.class);
        for (ForType forType : configType.getFor()) {
            Types types = Types.valueOf(forType.getType());
            Set<String> set = templatesByType.get(types);
            if (set == null) {
                set = new HashSet<>();
                templatesByType.put(types, set);
            }
            for (TemplateType templateType : forType.getTemplate()) {
                set.add(templateType.getName());
            }
        }
        processModelType(configType, modelType);
    }

    private void processModelType(ConfigType configType, ModelType modelType) throws IOException {
        mainPackage = configType.getPackage();
        processClasses(modelType);
    }

    private void processClasses(ModelType modelType) throws IOException {
        for (ClassType classType : modelType.getClazz()) {
            classes.put(classType.getName(), classType);
        }
        for (ClassType classType : modelType.getClazz()) {
            setInheritations(classType);
        }
        for (ClassType classType : modelType.getClazz()) {
            processClass(classType);
        }
        for (EnumType enumType : modelType.getEnum()) {
            processEnum(enumType);
        }
    }

    private void setInheritations(ClassType classTypeToCheck) {
        if (classTypeToCheck.getExtends() != null) {
            ClassType superClassType = classes.get(classTypeToCheck.getExtends());
            List<ClassType> classTypes = inheritations.get(superClassType);
            if (classTypes == null) {
                classTypes = new ArrayList<>();
                inheritations.put(superClassType, classTypes);
            }
            classTypes.add(classTypeToCheck);
        }
    }

    private void processClass(ClassType classType) throws IOException {
        Set<String> templateNames = templatesByType.get(Types.CLASS);
        for (String templateName : templateNames) {
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template template = velocityEngine.getTemplate(templateName);
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("package", mainPackage);
            if (templateName.contains("test")) {
                velocityContext.put("testClassname", classType.getName() + "Test");
            }
            velocityContext.put("class", classType);
            velocityContext.put("classname", classType.getName());
            velocityContext.put("extends", classType.getExtends());
            velocityContext.put("fieldlist", classType.getMember());
            velocityContext.put("constlist", classType.getConst());
            velocityContext.put("StringUtils", StringUtils.class);
            velocityContext.put("Utils", Utils.class);
            velocityContext.put("modelType", modelType);
            velocityContext.put("generator", this);
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            if (templateName.contains("test")) {
                FileUtils.write(new File(testOut, classType.getName() + "Test.java"), stringWriter.toString(), "UTF-8");
            } else {
                FileUtils.write(new File(out, classType.getName() + ".java"), stringWriter.toString(), "UTF-8");
            }
            System.out.println(classType.getName());
        }
    }

    private void processEnum(EnumType enumType) throws IOException {
        Set<String> templateNames = templatesByType.get(Types.ENUM);
        for (String templateName : templateNames) {
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template template = velocityEngine.getTemplate(templateName);
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("package", mainPackage);
            if (templateName.contains("test")) {
                velocityContext.put("testClassname", enumType.getName() + "Test");
            }
            velocityContext.put("class", enumType);
            velocityContext.put("classname", enumType.getName());
            velocityContext.put("constlist", enumType.getConst());
            velocityContext.put("implements", enumType.getImplements());
            velocityContext.put("StringUtils", StringUtils.class);
            velocityContext.put("Utils", Utils.class);
            velocityContext.put("modelType", modelType);
            velocityContext.put("generator", this);
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            if (templateName.contains("test")) {
                FileUtils.write(new File(testOut, enumType.getName() + "Test.java"), stringWriter.toString(), "UTF-8");
            } else {
                FileUtils.write(new File(out, enumType.getName() + ".java"), stringWriter.toString(), "UTF-8");
            }
            System.out.println(enumType.getName());
        }
    }

    public static void main(String[] args) throws IOException {
        new Generator().start();
    }

    public List<ClassType> findInherits(MemberType memberType) {
        if (memberType.getType().startsWith("list")) {
            String superType = memberType.getType().replace("list(", "").replace(")", "");
            ClassType superClassType = classes.get(superType);
            List<ClassType> list = inheritations.get(superClassType);
            if (list == null) {
                return emptyList();
            }
            return list;
        }
        return emptyList();
    }

    public ClassType getGenericType(MemberType memberType) {
        String typeName = memberType.getType().replace("list(", "").replace(")", "");
        return classes.get(typeName);
    }

}
