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
package org.mili.utils.sql.service;

import org.mili.utils.Lambda;
import org.mili.utils.sql.migration.Migrator;
import org.mili.utils.sql.transaction.TransactionalWrapper;

import java.io.File;

public class MigrationService extends Service {

    public void migrate(final boolean dropAndCreate) throws ServiceException {
        new TransactionalWrapper().doTransactional(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws Exception {
                doInService(new Lambda<Void>() {
                    @Override
                    public Void exec(Object... params) throws Exception {
                        new Migrator().migrate(dropAndCreate);
                        return null;
                    }
                });
                return null;
            }
        });
    }

    public void migrate(final boolean dropAndCreate, final Class<?> clazz) throws ServiceException {
        new TransactionalWrapper().doTransactional(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws Exception {
                doInService(new Lambda<Void>() {
                    @Override
                    public Void exec(Object... params) throws Exception {
                        new Migrator().migrate(dropAndCreate, clazz);
                        return null;
                    }
                });
                return null;
            }
        });
    }

    public void migrate(final boolean dropAndCreate, final File directory) throws ServiceException {
        new TransactionalWrapper().doTransactional(new Lambda<Void>() {
            @Override
            public Void exec(Object... params) throws Exception {
                doInService(new Lambda<Void>() {
                    @Override
                    public Void exec(Object... params) throws Exception {
                        new Migrator().migrate(dropAndCreate, directory);
                        return null;
                    }
                });
                return null;
            }
        });
    }

}