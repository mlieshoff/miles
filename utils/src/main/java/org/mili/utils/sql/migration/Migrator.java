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
package org.mili.utils.sql.migration;

import org.apache.commons.io.FileUtils;
import org.mili.utils.Log;
import org.mili.utils.sql.dao.DaoException;
import org.mili.utils.sql.dao.DaoFactory;
import org.mili.utils.sql.dao.SchemaVersionDao;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Michael Lieshoff, 07.07.17
 */
public class Migrator {

    public void migrate(boolean dropAndCreate) throws DaoException, IOException {
        SchemaVersionDao schemaVersionDao = DaoFactory.getDao(SchemaVersionDao.class);
        schemaVersionDao.init(dropAndCreate);
        boolean fileExists = true;
        while (fileExists) {
            int newSchemaVersion = schemaVersionDao.readLastSchemaVersion() + 1;
            Log.info(this, "migrate", "search migration: " + String.format("migration_%s.sql", newSchemaVersion));
            // load script
            URL url = Migrator.class.getClassLoader().getResource(String.format("migration_%s.sql", newSchemaVersion));
            if (url == null) {
                File file = new File(String.format("migration_%s.sql", newSchemaVersion));
                if (file.exists()) {
                    url = file.toURL();
                }
            }
            fileExists = url != null;
            if (fileExists) {
                // execute script
                Log.info(this, "migrate", "execute migration: " + url.getFile());
                String script = FileUtils.readFileToString(new File(url.getFile()));
                schemaVersionDao.executeScript(script);
                // set new schema version
                schemaVersionDao.setLastSchemaVersion(newSchemaVersion);
            }
        }

        URL url = Migrator.class.getClassLoader().getResource("migration_dev.sql");
        if (url == null) {
            File file = new File(String.format("migration_dev.sql"));
            if (file.exists()) {
                url = file.toURL();
            }
        }
        fileExists = url != null;
        if (fileExists) {
            Log.info(this, "migrate", "execute migration: " + url.getFile());
            String script = FileUtils.readFileToString(new File(url.getFile()));
            schemaVersionDao.executeScript(script);
        }
    }

}