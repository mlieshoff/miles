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
package org.mili.utils.sql.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class DaoException extends Exception {

    private List<Object> objects;

    public DaoException(Exception e) {
        super(e);
    }

    public DaoException(Exception e, Object... os) {
        this(e);
        if (os.length > 0) {
            objects = new ArrayList<>();
            for (Object o : os) {
                objects.add(o);
            }
        }
    }

    @Override
    public String getMessage() {
        return super.getMessage() + StringUtils.join(objects, ", ");
    }

}