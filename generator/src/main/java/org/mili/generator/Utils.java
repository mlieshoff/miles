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

package org.mili.generator;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.mili.generator.model.ClassType;
import org.mili.generator.model.MemberType;
import org.mili.generator.model.ModelType;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author Michael Lieshoff
 */
public class Utils {

    public static String getJavaType(String value) {
        return JavaTypeUtils.translateDslToJavaTypeSpec(value);
    }

    public static String getJavaImplType(String value) {
        return JavaTypeUtils.translateDslToJavaTypeImpl(value);
    }

    public static String getExampleValue(String value) {
        return JavaTypeUtils.getExampleValue(value);
    }

    public static String getExampleValueGeneric(String value) {
        return JavaTypeUtils.getExampleValue(value);
    }

    public static String getExampleToStringValue(String value) {
        return JavaTypeUtils.getExampleToStringValue(value);
    }

    public static String getExampleFloatingDeltaIfNeed(String value) {
        String type = getJavaType(value);
        if ("float".equals(type)) {
            return ", 0.0F";
        } else if ("double".equals(type)) {
            return ", 0.0";
        }
        return "";
    }

    public static String getJavaName(String value) {
        if (value.contains("_")) {
            return StringUtils.uncapitalize(WordUtils.capitalizeFully(value, '_').replace("_", ""));
        }
        if (value.startsWith("is")) {
            return value.substring(2);
        }
        return value;
    }

    public static String getFieldAccessorName(String value, String fieldName) {
        if ("boolean".equals(value)) {
            return "is";
        }
        return "get";
    }

    public static String getNameOrAlias(MemberType memberType) {
        if (isNotBlank(memberType.getAlias())) {
            return memberType.getAlias();
        }
        return memberType.getName();
    }

    public static String getNameOrAlias(ClassType classType) {
        if (isNotBlank(classType.getAlias())) {
            return classType.getAlias();
        }
        return classType.getName();
    }

    public static String generateXmlElementDefinition(ModelType modelType, MemberType memberType) {
        if (memberType.getType().startsWith("list")) {
            StringBuilder s = new StringBuilder();
            String superType = memberType.getType().replace("list(", "").replace(")", "");
            Set<ClassType> classTypes = new HashSet();
            for (ClassType classType : modelType.getClazz()) {
                if (classType.getExtends() != null && classType.getExtends().equals(superType)) {
                    classTypes.add(classType);
                }
            }
            if (classTypes.size() > 0) {
                s.append(String.format("@XmlElementWrapper(name = \"%s\")\n", getNameOrAlias(memberType)));
                s.append("    @XmlElements({\n");
                for (Iterator<ClassType> iterator = classTypes.iterator(); iterator.hasNext(); ) {
                    ClassType classType = iterator.next();
                    s.append(String.format("        @XmlElement(name = \"%s\", type = %s.class)", classType.getName(), classType.getName()));
                    if (iterator.hasNext()) {
                        s.append(",\n");
                    }
                }
                s.append("\n    })");
                return s.toString();
            } else {
                return String.format("@XmlElement(name = \"%s\")", getNameOrAlias(memberType));
            }
        } else {
            return String.format("@XmlElement(name = \"%s\")", getNameOrAlias(memberType));
        }
    }

    public static boolean isCollection(String value) {
        if (value.startsWith("list(")) {
            return true;
        } else if (value.startsWith("set(")) {
            return true;
        }
        return false;
    }

    public static boolean isMap(String value) {
        if (value.startsWith("map(")) {
            return true;
        }
        return false;
    }

}
