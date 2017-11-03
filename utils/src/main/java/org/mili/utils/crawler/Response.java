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
package org.mili.utils.crawler;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.mili.utils.Function;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author Michael Lieshoff
 */
public class Response {

    private CloseableHttpResponse response;

    private Request request;

    public Response(Request request) {
        this.request = request;
    }

    public byte[] getBytes() {
        try {
            return EntityUtils.toByteArray(response.getEntity());
        } catch (IOException e) {
            throw new CrawlerException("read from response", e);
        }
    }

    public String getString(String encoding) {
        try {
            return new String(getBytes(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new CrawlerException("read from response", e);
        }
    }

    public InputStream getInputStream() {
        try {
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new CrawlerException("read from response", e);
        }
    }

    public <T> T getObject(Class<T> clazz) {
        Function<HttpResponse, ?> typedTransformer = request.getTransformer(clazz);
        if (typedTransformer == null) {
            throw new UnsupportedOperationException("object transformation not supported: " + clazz);
        }
        return (T) typedTransformer.execute(response);
    }

    public int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    public boolean isError() {
        return !request.getSuccessStatusCodeRange().contains(getStatusCode());
    }

    public void throwExceptionOnError() throws  CrawlerException {
        if (isError() && request.isThrowExceptionOnError()) {
            try {
                throw new CrawlerException(
                        response.getStatusLine().getReasonPhrase()
                                + ": "
                                + response.getStatusLine().getStatusCode()
                                + ">>> "
                                + IOUtils.toString(response.getEntity().getContent(), Charsets.UTF_8),
                        null
                );
            } catch (IOException e) {
                throw new CrawlerException("while error handling", e);
            }
        }
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

}
