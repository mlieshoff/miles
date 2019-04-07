package org.mili.generator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.google.gson.annotations.SerializedName;

import org.mili.generator.model.ClassType;
import org.mili.generator.model.MemberType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FromJavaClasses {

  private static Map<String, ClassType> classTypeMap = new TreeMap<>();

  private static final String PREFIX = "Constants";
  private static final String PACKAGE = "org.mili.generator.example.constants";

  public static void main(String[] args) throws Exception {
    Class<?> clazz = Class.forName("");
    analyze(clazz);

    for (Map.Entry<String, ClassType> entry : classTypeMap.entrySet()) {
      System.out.printf("    <class name=\"%s\">\n", entry.getKey());
      for (MemberType memberType : entry.getValue().getMember()) {
        System.out.printf("        <member name=\"%s\" ", memberType.getName());
        if (isNotBlank(memberType.getAlias())) {
          System.out.printf("alias=\"%s\" ", memberType.getAlias());
        }
        System.out.printf("type=\"%s\"/>\n", memberType.getType());
      }
      System.out.printf("    </class>\n\n");
    }
  }

  private static void analyze(Class<?> clazz) {
    String className = clazz.getSimpleName();
    if (!clazz.getSimpleName().equals(PREFIX)) {
      className = PREFIX + clazz.getSimpleName();
    }
    ClassType classType = findOrCreate(className);
    for (Field field : clazz.getDeclaredFields()) {
      MemberType memberType = new MemberType();
      memberType.setName(field.getName());
      if (field.getAnnotation(SerializedName.class) != null) {
        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        memberType.setAlias(serializedName.value());
      }
      Class<?> type = field.getType();
      if (type.isPrimitive()) {
        memberType.setType(type.getSimpleName());
      } else if (List.class.isAssignableFrom(type)) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        Class<?> typeClass = (Class<?>) stringListType.getActualTypeArguments()[0];
        if (JavaTypeUtils.isWrapper(typeClass)) {
          memberType.setType("list(" + typeClass.getSimpleName() + ")");
        } else {
          memberType.setType("list(" + PREFIX + typeClass.getSimpleName() + ")");
        }
        if (!JavaTypeUtils.isWrapper(typeClass) || typeClass.getPackage().getName().startsWith(PACKAGE)) {
          analyze(typeClass);
        }
      } else if (JavaTypeUtils.isWrapper(type)) {
        memberType.setType(JavaTypeUtils.getPrimitiveName(type));
      } else {
        memberType.setType(PREFIX + type.getSimpleName());
        analyze(type);
      }
      if (type.getSimpleName().equals("Object")) {
        memberType.setType("String");
      }
      classType.getMember().add(memberType);
    }
    classTypeMap.put(classType.getName(), classType);
  }

  private static ClassType findOrCreate(String className) {
    ClassType classType = classTypeMap.get(className);
    if (classType == null) {
      classType = new ClassType();
      classType.setName(className);
      classTypeMap.put(className, classType);
    }
    return classType;
  }

}
