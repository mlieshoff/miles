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
import org.mili.generator.model.ClassType;
import org.mili.generator.model.ModelType;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.StringWriter;

/**
 * @author Michael Lieshoff
 */
public class Generator {

    private String mainPackage;

    public void start() {
        ModelType modelType = JAXB.unmarshal(new File("generator/src/test/java/org/mili/generator/cr-api.xml"), ModelType.class);
        processModelType(modelType);
    }

    private void processModelType(ModelType modelType) {
        mainPackage = modelType.getPackage();
        processClasses(modelType);
    }

    private void processClasses(ModelType modelType) {
        for (ClassType classType : modelType.getClazz()) {
            processClass(classType);
        }
    }

    private void processClass(ClassType classType) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        Template template = velocityEngine.getTemplate("generator/src/main/resources/json-pojo.vm");
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("package", mainPackage);
        velocityContext.put("classname", classType.getName());
        velocityContext.put("fieldlist", classType.getMember());
        velocityContext.put("StringUtils", StringUtils.class);
        StringWriter stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        System.out.println(stringWriter);
    }

    public static void main(String[] args) {
        new Generator().start();
    }

}
