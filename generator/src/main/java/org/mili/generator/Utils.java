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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author Michael Lieshoff
 */
public class Utils {

    public static String getJavaType(String value) {
        String computedType = JavaTypeUtils.translateDslToJavaTypeSpec(value);
        System.out.println("*** " + value + " - ");
        return computedType;
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

}
