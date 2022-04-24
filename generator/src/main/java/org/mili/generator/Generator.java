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
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.mili.generator.config.ConfigType;
import org.mili.generator.config.ForType;
import org.mili.generator.config.TemplateType;
import org.mili.generator.model.ClassType;
import org.mili.generator.model.EnumType;
import org.mili.generator.model.InterfaceType;
import org.mili.generator.model.MemberType;
import org.mili.generator.model.ModelType;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.bind.JAXB;

/**
 * @author Michael Lieshoff
 */
public class Generator {

  private String basePackage;
  private String generatedPackage;

  private Map<Types, Set<TemplateType>> templatesByType = new HashMap<>();

  private File outDir;
  private File testOutDir;
  private File templatesDir;

  private ModelType modelType;

  private Map<ClassType, List<ClassType>> inheritations = new HashMap<>();
  private Map<InterfaceType, List<InterfaceType>> interfaceInheritations = new HashMap<>();
  private Map<InterfaceType, List<InterfaceType>> superInterfaceInheritations = new HashMap<>();
  private Map<String, ClassType> classes = new TreeMap<>();
  private Map<String, InterfaceType> interfaces = new TreeMap<>();
  private Map<String, EnumType> enums = new TreeMap<>();
  private Map<String, Object> baseContext = new HashMap<>();

  private Set<String> usedTypes = new TreeSet<>();

  public void start(String outDirName, String testOutDirName, String configFilename, String modelFilename,
                    String templatesDirName)
      throws IOException {
    System.out.println("base dir: " + new File("").getAbsolutePath());
    outDir = new File(outDirName);
    testOutDir = new File(testOutDirName);
    templatesDir = new File(templatesDirName);
    FileUtils.deleteQuietly(testOutDir);
    FileUtils.forceMkdir(testOutDir);
    FileUtils.deleteQuietly(outDir);
    FileUtils.forceMkdir(outDir);
    modelType = JAXB.unmarshal(new File(modelFilename), ModelType.class);
    ConfigType configType = JAXB.unmarshal(new File(configFilename), ConfigType.class);
    for (ForType forType : configType.getFor()) {
      Types types = Types.valueOf(forType.getType());
      Set<TemplateType> set = templatesByType.get(types);
      if (set == null) {
        set = new HashSet<>();
        templatesByType.put(types, set);
      }
      for (TemplateType templateType : forType.getTemplate()) {
        baseContext.put("templatePackage" + templateType.getId(), templateType.getPackage());
        set.add(templateType);
      }
    }
    processModelType(configType, modelType);

    System.out.println("*** report");
    for (Entry<String, ClassType> entry : classes.entrySet()) {
      System.out.println();
      ClassType classType = entry.getValue();
      System.out.print(classType.getName());
      if (isNotBlank(classType.getExtends())) {
        System.out.print(" -> " + classType.getExtends());
      }
      if (isNotBlank(classType.getImplements())) {
        System.out.print(" -> " + classType.getImplements());
      }
      System.out.println();
      System.out.printf("%-35s %-8s %-30s %-60s\n",
          "Field",
          "Req/Opt",
          "Default",
          "Source"
      );
      for (MemberType memberType : getFieldList(classType)) {
        System.out.printf("%-35s %-8s %-30s %-60s\n",
            memberType.getName(),
            memberType.isRequired() ? "REQUIRED" : "OPTIONAL",
            memberType.getDefaultValue(),
            memberType.getInfo() == null ? "" : memberType.getInfo()
        );
      }
    }
        /*
        for (Entry<String, InterfaceType> entry : interfaces.entrySet()) {
            System.out.println();
            InterfaceType interfaceType = entry.getValue();
            System.out.print(Utils.getNameOrAlias(interfaceType));
            if (isNotBlank(interfaceType.getExtends())) {
                System.out.print(" -> " + interfaceType.getExtends());
            }
            System.out.println();
            System.out.printf("%-35s %-8s %-30s %-60s\n",
                "Field",
                "Req/Opt",
                "Default",
                "Source"
            );
            for (MemberType memberType : interfaceType.getMember()) {
                System.out.printf("%-35s %-8s %-30s %-60s\n",
                    memberType.getName(),
                    memberType.isRequired() ? "REQUIRED" : "OPTIONAL",
                    memberType.getDefaultValue(),
                    memberType.getInfo()
                    );
            }
        }
        */

  }

  private void processModelType(ConfigType configType, ModelType modelType) throws IOException {
    generatedPackage = configType.getPackage();
    basePackage = configType.getBasePackage();
    baseContext.put("package", generatedPackage);
    baseContext.put("basepackage", basePackage);
    baseContext.put("StringUtils", StringUtils.class);
    baseContext.put("Utils", Utils.class);
    baseContext.put("modelType", modelType);
    baseContext.put("generator", this);
    processEnums(modelType);
    processInterfaces(modelType);
    processClasses(modelType);
  }

  private void processEnums(ModelType modelType) throws IOException {
    for (EnumType enumType : modelType.getEnum()) {
      enums.put(enumType.getName(), enumType);
    }
    for (EnumType enumType : modelType.getEnum()) {
      processEnum(enumType);
    }
  }

  private void processInterfaces(ModelType modelType) throws IOException {
    for (InterfaceType interfaceType : modelType.getInterface()) {
      interfaces.put(interfaceType.getName(), interfaceType);
    }
    for (InterfaceType interfaceType : modelType.getInterface()) {
      setInheritations(interfaceType);
    }
    for (InterfaceType interfaceType : modelType.getInterface()) {
      processInterface(interfaceType);
    }
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
  }

  private void setInheritations(InterfaceType interfaceTypeToCheck) {
    if (interfaceTypeToCheck.getExtends() != null) {
      InterfaceType superInterfaceType = interfaces.get(interfaceTypeToCheck.getExtends());
      List<InterfaceType> interfaceTypes = interfaceInheritations.get(superInterfaceType);
      if (interfaceTypes == null) {
        interfaceTypes = new ArrayList<>();
        interfaceInheritations.put(superInterfaceType, interfaceTypes);
      }
      interfaceTypes.add(interfaceTypeToCheck);

      interfaceTypes = superInterfaceInheritations.get(interfaceTypeToCheck);
      if (interfaceTypes == null) {
        interfaceTypes = new ArrayList<>();
        superInterfaceInheritations.put(interfaceTypeToCheck, interfaceTypes);
      }
      interfaceTypes.add(superInterfaceType);
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

  private void processInterface(InterfaceType interfaceType) throws IOException {
    fillUsedTypes(interfaceType.getMember());
    Set<TemplateType> templateTypes = templatesByType.get(Types.INTERFACE);
    for (TemplateType templateType : templateTypes) {
      String templateName = templateType.getName();
      VelocityEngine velocityEngine = new VelocityEngine();
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
      velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatesDir.getPath());
      velocityEngine.init();
      Template template = velocityEngine.getTemplate(templateName);
      VelocityContext velocityContext = createVelocityContext();
      if (templateName.contains("test")) {
        velocityContext.put("testClassname", interfaceType.getName() + "Test");
      }
      velocityContext.put("class", interfaceType);
      velocityContext.put("classname", interfaceType.getName());
      velocityContext.put("extends", interfaceType.getExtends());
      velocityContext.put("methodlist", interfaceType.getMethod());
      velocityContext.put("fieldlist", interfaceType.getMember());
      velocityContext.put("constlist", interfaceType.getConst());
      velocityContext.put("usedenums", getUsedEnums(interfaceType.getMember()));
      velocityContext.put("usedtypes", usedTypes);
      StringWriter stringWriter = new StringWriter();
      template.merge(velocityContext, stringWriter);
      writeFile(templateType, interfaceType.getName(), stringWriter);
    }
  }

  private void fillUsedTypes(List<MemberType> memberTypes) {
    usedTypes.clear();
    for (MemberType memberType : memberTypes) {
      String typeName = JavaTypeUtils.translateDslToJavaTypeSpec(memberType.getType());
      if (typeName.startsWith("Map")) {
        usedTypes.add("Map");
        usedTypes.add("LinkedHashMap");
      } else if (typeName.startsWith("List")) {
        usedTypes.add("List");
        usedTypes.add("ArrayList");
      } else if (typeName.startsWith("Set")) {
        usedTypes.add("Set");
        usedTypes.add("LinkedHashSet");
      } else {
        usedTypes.add(typeName);
      }
      List<String> genericTypes = JavaTypeUtils.getDslGenericType(typeName);
      if (!genericTypes.isEmpty()) {
        usedTypes.addAll(genericTypes);
      }
    }
    usedTypes.remove("Object");
    usedTypes.remove("String");
    usedTypes.remove("Boolean");
    usedTypes.remove("Char");
    usedTypes.remove("Short");
    usedTypes.remove("Integer");
    usedTypes.remove("Long");
    usedTypes.remove("Double");
    usedTypes.remove("Float");
  }

  private Collection<MemberType> getUsedEnums(List<MemberType> memberTypes) {
    Collection<MemberType> usedEnums = new HashSet<>();
    for (MemberType memberType : memberTypes) {
      if (enums.containsKey(memberType.getType())) {
        usedEnums.add(memberType);
      }
    }
    return usedEnums.stream().sorted((o1, o2) -> StringUtils.compare(o2.getName(), o1.getName())).collect(toList());
  }

  private VelocityContext createVelocityContext() {
    VelocityContext velocityContext = new VelocityContext();
    for (Entry<String, Object> entry : baseContext.entrySet()) {
      velocityContext.put(entry.getKey(), entry.getValue());
    }
    return velocityContext;
  }

  private void writeFile(TemplateType templateType, String name, StringWriter stringWriter) throws IOException {
    String filename = name + templateType.getSuffix();
    File file;
    if (templateType.isTest()) {
      if (isNotBlank(templateType.getPackage())) {
        File dir = new File(testOutDir, packageToDirname(templateType.getPackage()));
        dir.mkdirs();
        file = new File(dir, filename);
      } else {
        file = new File(testOutDir, filename);
      }
    } else {
      if (isNotBlank(templateType.getPackage())) {
        File dir = new File(outDir, packageToDirname(templateType.getPackage()));
        dir.mkdirs();
        file = new File(dir, filename);
      } else {
        file = new File(outDir, filename);
      }
    }
    FileUtils.write(file, stringWriter.toString(), "UTF-8");
    System.out.println(name);
  }

  private String packageToDirname(String packageName) {
    return packageName.replace(".", "/");
  }

  private void processClass(ClassType classType) throws IOException {
    fillUsedTypes(classType.getMember());
    Set<TemplateType> templateTypes = templatesByType.get(Types.CLASS);
    for (TemplateType templateType : templateTypes) {
      String templateName = templateType.getName();
      VelocityEngine velocityEngine = new VelocityEngine();
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
      velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatesDir.getPath());
      velocityEngine.init();
      Template template = velocityEngine.getTemplate(templateName);
      VelocityContext velocityContext = createVelocityContext();
      if (templateName.contains("test")) {
        velocityContext.put("testClassname", classType.getName() + "Test");
      }
      velocityContext.put("class", classType);
      velocityContext.put("classname", classType.getName());
      velocityContext.put("extends", classType.getExtends());
      velocityContext.put("implements", classType.getImplements());
      velocityContext.put("fieldlist", getFieldList(classType));
      velocityContext.put("constlist", classType.getConst());
      velocityContext.put("usedtypes", usedTypes);
      velocityContext.put("usedenums", getUsedEnums(classType.getMember()));
      velocityContext.put("requiredfieldswithoutdefault", getRequiredFieldListWithoutDefaults(classType));
      velocityContext.put("superrequiredfieldswithoutdefault", getSuperRequiredFieldListWithoutDefaults(classType));
      StringWriter stringWriter = new StringWriter();
      template.merge(velocityContext, stringWriter);
      writeFile(templateType, classType.getName(), stringWriter);
    }
  }

  private List<MemberType> getRequiredFieldListWithoutDefaults(ClassType classType) {
    List<MemberType> memberTypes = getFieldList(classType);
    for (Iterator<MemberType> iterator = memberTypes.iterator(); iterator.hasNext(); ) {
      MemberType memberType = iterator.next();
      if (!memberType.isRequired() || (memberType.isRequired() && isNotBlank(memberType.getDefaultValue()))) {
        iterator.remove();
      }
    }
    return memberTypes;
  }

  private List<MemberType> getSuperRequiredFieldListWithoutDefaults(ClassType classType) {
    List<MemberType> memberTypes = getSuperClassFieldList(classType);
    for (Iterator<MemberType> iterator = memberTypes.iterator(); iterator.hasNext(); ) {
      MemberType memberType = iterator.next();
      if (!memberType.isRequired() || (memberType.isRequired() && isNotBlank(memberType.getDefaultValue()))) {
        iterator.remove();
      }
    }
    return memberTypes;
  }

  private List<MemberType> getFieldList(ClassType classType) {
    Map<String, MemberType> map = new TreeMap<>();
    if (!classType.getMember().isEmpty()) {
      for (MemberType memberType : classType.getMember()) {
        map.put(memberType.getName(), memberType);
      }
    }
    if (isNotBlank(classType.getImplements())) {
      String[] intfs = classType.getImplements().replace(" ", "").split(",");
      for (String intf : intfs) {
        InterfaceType interfaceType = interfaces.get(intf);
        System.out.println(intf);
        if (!interfaceType.getMember().isEmpty()) {
          for (MemberType memberType : interfaceType.getMember()) {
            map.put(memberType.getName(), memberType);
          }
        }
        List<InterfaceType> inherited = superInterfaceInheritations.get(interfaceType);
        if (inherited != null && !inherited.isEmpty()) {
          for (InterfaceType interfaceType1 : inherited) {
            if (!interfaceType1.getMember().isEmpty()) {
              for (MemberType memberType : interfaceType1.getMember()) {
                map.put(memberType.getName(), memberType);
              }
            }
          }
        }
      }
    }
    return new ArrayList<>(map.values());
  }

  private List<MemberType> getSuperClassFieldList(ClassType classType) {
    Map<String, MemberType> map = new TreeMap<>();
    if (isNotBlank(classType.getExtends())) {
      String superClassName = classType.getExtends();
      ClassType superClassType = classes.get(superClassName);
      for (MemberType memberType : getFieldList(superClassType)) {
        map.put(memberType.getName(), memberType);
      }
    }
    return new ArrayList<>(map.values());
  }

  private void processEnum(EnumType enumType) throws IOException {
    usedTypes.clear();
    Set<TemplateType> templateTypes = templatesByType.get(Types.ENUM);
    for (TemplateType templateType : templateTypes) {
      String templateName = templateType.getName();
      VelocityEngine velocityEngine = new VelocityEngine();
      velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
      velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, templatesDir.getPath());
      velocityEngine.init();
      Template template = velocityEngine.getTemplate(templateName);
      VelocityContext velocityContext = createVelocityContext();
      if (templateName.contains("test")) {
        velocityContext.put("testClassname", enumType.getName() + "Test");
      }
      velocityContext.put("class", enumType);
      velocityContext.put("classname", enumType.getName());
      velocityContext.put("constlist", enumType.getConst());
      velocityContext.put("implements", enumType.getImplements());
      StringWriter stringWriter = new StringWriter();
      template.merge(velocityContext, stringWriter);
      writeFile(templateType, enumType.getName(), stringWriter);
    }
  }

  public static void main(String[] args) throws IOException {
    new Generator().start(args[0], args[1], args[2], args[3], args[4]);
  }

  public List<ClassType> findInherits(MemberType memberType) {
    if (memberType.getType().startsWith("list")) {
      String superType = memberType.getType().replace("list(", "").replace(")", "");
      if ("String".equals(superType)) {
        return emptyList();
      }
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
