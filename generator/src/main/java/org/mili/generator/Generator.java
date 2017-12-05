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

    public void start() {
        ModelType modelType = JAXB.unmarshal(new File("generator/src/test/java/org/mili/generator/paymentwall.xml"), ModelType.class);
        ConfigType configType = JAXB.unmarshal(new File("generator/src/test/java/org/mili/generator/paymentwall-config.xml"), ConfigType.class);
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

    private void processModelType(ConfigType configType, ModelType modelType) {
        mainPackage = configType.getPackage();
        processClasses(modelType);
    }

    private void processClasses(ModelType modelType) {
        for (ClassType classType : modelType.getClazz()) {
            processClass(classType);
        }
        for (EnumType enumType : modelType.getEnum()) {
            processEnum(enumType);
        }
    }

    private void processClass(ClassType classType) {
        Set<String> templateNames = templatesByType.get(Types.CLASS);
        for (String templateName : templateNames) {
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template template = velocityEngine.getTemplate(templateName);
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("package", mainPackage);
            velocityContext.put("classname", classType.getName());
            velocityContext.put("fieldlist", classType.getMember());
            velocityContext.put("StringUtils", StringUtils.class);
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            System.out.println(stringWriter);
        }
    }

    private void processEnum(EnumType enumType) {
        Set<String> templateNames = templatesByType.get(Types.ENUM);
        for (String templateName : templateNames) {
            VelocityEngine velocityEngine = new VelocityEngine();
            velocityEngine.init();
            Template template = velocityEngine.getTemplate(templateName);
            VelocityContext velocityContext = new VelocityContext();
            velocityContext.put("package", mainPackage);
            velocityContext.put("classname", enumType.getName());
            velocityContext.put("constlist", enumType.getConst());
            velocityContext.put("implements", enumType.getImplements());
            velocityContext.put("StringUtils", StringUtils.class);
            StringWriter stringWriter = new StringWriter();
            template.merge(velocityContext, stringWriter);
            System.out.println(stringWriter);
        }
    }

    public static void main(String[] args) {
        new Generator().start();
    }

}
