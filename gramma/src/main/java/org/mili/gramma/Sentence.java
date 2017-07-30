package org.mili.gramma;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class Sentence extends Element {

    private final List<Element> elements = new ArrayList<>();

    public List<Element> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Element element : elements) {
            if (element instanceof Word) {
                s.append(((Word) element).getChars());
            } else if (element instanceof Sentence) {
                s.append("    ");
                s.append(element);
            }
            s.append("\n");
        }
        return s.toString();
    }

}
