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
package org.mili.gramma.german;

import org.mili.gramma.Parser;
import org.mili.gramma.Sentence;
import org.mili.gramma.Text;
import org.mili.gramma.Word;

/**
 * @author Michael Lieshoff
 */
public class GermanParser implements Parser {

    @Override
    public void parse(String s) {
        Text text = new Text();
        StringBuilder word = new StringBuilder();
        Sentence sentence = new Sentence();
        text.getSentences().add(sentence);
        for (int i = 0, n = s.length(); i < n; i ++) {
            boolean addWord = false;
            char c = s.charAt(i);
            if (' ' == c) {
                addWord = true;
            } else if (',' == c) {
                addWord = true;
            } else if ('.' == c) {
                addWord = true;
            } else {
                word.append(c);
            }
            if (addWord && word.length() > 0) {
                sentence.getElements().add(new Word(word.toString()));
                word.setLength(0);
            }
        }
        System.out.println(text);
    }
}
