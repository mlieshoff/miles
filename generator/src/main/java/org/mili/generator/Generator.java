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
import org.mili.generator.model.ModelType;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Lieshoff
 */
public class Generator {

    private String mainPackage;

    private Map<Types, Set<String>> templatesByType = new HashMap<>();

    private static final File out = new File("/home/micha/dev/jcrapi2/src/main/java/jcrapi2/model");
    private static final File testOut = new File("/home/micha/dev/jcrapi2/src/test/java/jcrapi2/model");

    public void start() throws IOException {
        FileUtils.deleteQuietly(testOut);
        FileUtils.forceMkdir(testOut);
        FileUtils.deleteQuietly(out);
        FileUtils.forceMkdir(out);
        ModelType modelType = JAXB.unmarshal(new File("/home/micha/dev/jcrapi2/src/main/resources/model.xml"), ModelType.class);
        ConfigType configType = JAXB.unmarshal(new File("/home/micha/dev/clash-royal-data-model/cr-api-config.xml"), ConfigType.class);
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
            processClass(classType);
        }
        for (EnumType enumType : modelType.getEnum()) {
            processEnum(enumType);
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
            velocityContext.put("classname", classType.getName());
            velocityContext.put("extends", classType.getExtends());
            velocityContext.put("fieldlist", classType.getMember());
            velocityContext.put("constlist", classType.getConst());
            velocityContext.put("StringUtils", StringUtils.class);
            velocityContext.put("Utils", Utils.class);
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
            velocityContext.put("classname", enumType.getName());
            velocityContext.put("constlist", enumType.getConst());
            velocityContext.put("implements", enumType.getImplements());
            velocityContext.put("StringUtils", StringUtils.class);
            velocityContext.put("Utils", Utils.class);
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

}
