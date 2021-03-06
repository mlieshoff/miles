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

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class JavaTypeUtils {

    public static String translateDslToJavaTypeSpec(String value) {
        if (StringUtils.isBlank(value)) {
            return "Object";
        } else if (value.startsWith("map(")) {
            String[] genericTypes = value.replace("map(", "").replace(")", "").split(",");
            String genericType1 = translateDslToJavaWrapperType(genericTypes[0]);
            String genericType2 = translateDslToJavaWrapperType(genericTypes[1]);
            value = "Map<" + genericType1 + ", " + genericType2 + ">";
        } else if (value.startsWith("list(")) {
            String genericType = value.replace("list(", "").replace(")", "");
            value = "List<" + translateDslToJavaWrapperType(genericType) + ">";
        } else if (value.startsWith("set(")) {
            String genericType = value.replace("set(", "").replace(")", "");
            value = "Set<" + translateDslToJavaWrapperType(genericType) + ">";
        }
        return value;
    }

    public static List<String> getDslGenericType(String value) {
        List<String> genericTypes = new ArrayList<>();
        if (isNotBlank(value)) {
            String[] types = value
                .replace(" ", "")
                .replace("map(", "")
                .replace("list(", "")
                .replace("set(", "")
                .replace(")", "")
                .split("[,]");
            for (String type : types) {
                if (isNotBlank(type)) {
                    genericTypes.add(type);
                }
            }
        }
        return genericTypes;
    }

    public static String translateDslToJavaWrapperType(String value) {
        switch (value) {
            case "boolean": return "Boolean";
            case "boolean[]": return "Boolean[]";
            case "byte": return "Byte";
            case "byte[]": return "Byte[]";
            case "short": return "Short";
            case "short[]": return "Short[]";
            case "int": return "Integer";
            case "int[]": return "Integer[]";
            case "char": return "Character";
            case "char[]": return "Character[]";
            case "long": return "Long";
            case "long[]": return "Long[]";
            case "float": return "Float";
            case "float[]": return "Float[]";
            case "double": return "Double";
            case "double[]": return "Double[]";
        }
        return value;
    }

    public static String translateDslToJavaTypeImpl(String value) {
        String specType = translateDslToJavaTypeSpec(value);
        if (specType.startsWith("Map")) {
            return specType.replace("Map", "LinkedHashMap");
        } else if (specType.startsWith("List")) {
            return specType.replace("List", "ArrayList");
        } else if (specType.startsWith("Set")) {
            return specType.replace("Set", "LinkedHashSet");
        }
        return specType;
    }

    public static String getExampleValue(String value) {
        String type = translateDslToJavaTypeImpl(value);
        switch (value) {
            case "boolean": return "false";
            case "Boolean": return "Boolean.valueOf(false)";
            case "boolean[]": return "boolean[]{}";
            case "byte": return "7";
            case "Byte": return "Byte.valueOf(7)";
            case "byte[]": return "byte[]{}";
            case "short": return "256";
            case "Short": return "Short.valueOf(256)";
            case "short[]": return "short[]{}";
            case "int": return "815";
            case "Integer": return "Integer.valueOf(815)";
            case "int[]": return "int[]{}";
            case "Character":
            case "char": return "'F'";
            case "char[]": return "char[]{}";
            case "long": return "4711L";
            case "Long": return "Long.valueOf(4711L)";
            case "long[]": return "long[]{}";
            case "float": return "3.14F";
            case "Float": return "Float.valueOf(3.14F)";
            case "float[]": return "float[]{}";
            case "double": return "1.41";
            case "Double": return "Double.valueOf(1.41)";
            case "double[]": return "double[]{}";
            case "String": return "\"astring\"";
        }
        if (type.contains("Enum")) {
            return type + ".values()[0]";
        }
        return "new " + type + "()";
    }

    public static String getExampleToStringValue(String value) {
        String exampleValue = getExampleValue(value);
        String type = translateDslToJavaTypeImpl(value);
        if (exampleValue.startsWith("new ")) {
            exampleValue = "\" + " + exampleValue + " + \"";
        } else if (exampleValue.startsWith("\"")) {
            exampleValue = exampleValue.replace("\"", "");
        } else if (type.equals("Integer")) {
            exampleValue = exampleValue.replace("Integer.valueOf(", "").replace(")", "");
        } else if (type.equals("long")) {
            exampleValue = exampleValue.replace("L", "");
        } else if (type.equals("Long")) {
            exampleValue = exampleValue.replace("Long.valueOf(", "").replace(")", "");
        } else if (type.equals("Double")) {
            exampleValue = exampleValue.replace("Double.valueOf(", "").replace(")", "");
        } else if (type.equals("float")) {
            exampleValue = exampleValue.replace("F", "");
        }
        return exampleValue;
    }

    public static boolean isWrapper(Class<?> type) {
        return Boolean.class.isAssignableFrom(type)
            || String.class.isAssignableFrom(type)
            || Byte.class.isAssignableFrom(type)
            || Short.class.isAssignableFrom(type)
            || Float.class.isAssignableFrom(type)
            || Integer.class.isAssignableFrom(type)
            || Double.class.isAssignableFrom(type)
            || Long.class.isAssignableFrom(type);
    }

    public static String getPrimitiveName(Class<?> type) {
        if (Boolean.class.isAssignableFrom(type)) {
            return "boolean";
        } else if (String.class.isAssignableFrom(type)) {
            return "String";
        } else if (Byte.class.isAssignableFrom(type)) {
            return "byte";
        } else if (Short.class.isAssignableFrom(type)) {
            return "short";
        } else if (Float.class.isAssignableFrom(type)) {
            return "float";
        } else if (Integer.class.isAssignableFrom(type)) {
            return "int";
        } else if (Double.class.isAssignableFrom(type)) {
            return "double";
        } else if (Long.class.isAssignableFrom(type)) {
            return "long";
        }
        return type.getSimpleName();
    }

    public static String getPrimitiveName(String type) {
        if ("Boolean".equals(type)) {
            return "boolean";
        } else if ("Byte".equals(type)) {
            return "byte";
        } else if ("Short".equals(type)) {
            return "short";
        } else if ("Float".equals(type)) {
            return "float";
        } else if ("Integer".equals(type)) {
            return "int";
        } else if ("Double".equals(type)) {
            return "double";
        } else if ("Long".equals(type)) {
            return "long";
        }
        return type;
    }

    public static String getWrapperName(String type) {
        if ("boolean".equals(type)) {
            return "Boolean";
        } else if ("byte".equals(type)) {
            return "Byte";
        } else if ("short".equals(type)) {
            return "Short";
        } else if ("float".equals(type)) {
            return "Float";
        } else if ("int".equals(type)) {
            return "Integer";
        } else if ("double".equals(type)) {
            return "Double";
        } else if ("long".equals(type)) {
            return "Long";
        }
        return type;
    }

}
