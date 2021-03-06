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
package org.mili.gramma.german.partizip;

import org.mili.gramma.Element;
import org.mili.gramma.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff
 */
public class Partizipialgruppe extends Element {

    private final List<Word> elements = new ArrayList<>();

    public List<Word> getElements() {
        return elements;
    }

}
