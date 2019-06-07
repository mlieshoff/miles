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
package org.mili.utils;

import static org.apache.commons.lang3.ObjectUtils.identityToString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class Log {

    public static void trace(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).trace(format(clazz, method, message, objects));
   }

    private static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    private static String format(Class cls, String method, String message, Object... objects) {
        return String.format("%s [%s]: %s", cls.getName(), method, String.format(message, objects));
    }

    public static void debug(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).debug(format(clazz, method, message, objects));
   }

    public static void debug(Object o, String method, String message, Object... objects) {
        getLogger(o.getClass()).debug(format(o, method, message, objects));
    }

    private static String format(Object o, String method, String message, Object... objects) {
        return String.format("%s [%s]: %s", identityToString(o), method, String.format(message, objects));
    }

    public static void info(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).info(format(clazz, method, message, objects));
    }

    public static void info(Object o, String method, String message, Object... objects) {
        getLogger(o.getClass()).info(format(o, method, message, objects));
    }

    public static void warn(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).warn(format(clazz, method, message, objects));
    }

    public static void warn(Object o, String method, String message, Object... objects) {
        getLogger(o.getClass()).warn(format(o, method, message, objects));
    }

    public static void error(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).error(format(clazz, method, message, objects));
    }

    public static void error(Object o, String method, String message, Object... objects) {
        getLogger(o.getClass()).error(format(o, method, message, objects));
    }

    public static void fatal(Class clazz, String method, String message, Object... objects) {
        getLogger(clazz).error(format(clazz, method, message, objects));
    }

    public static void fatal(Object o, String method, String message, Object... objects) {
        getLogger(o.getClass()).error(format(o, method, message, objects));
    }

}