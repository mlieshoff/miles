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

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.Range;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.mili.utils.Function;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Lieshoff
 */
public class FormPostRequest extends PostRequest<String> {

    private final Map<String, List<String>> formParameters = new HashMap<>();

    protected FormPostRequest(String uri, Map<String, List<String>> uriParameters, Map<String, String> headers,
                              Range<Integer> successStatusCodeRange, boolean throwExceptionOnError, boolean async,
                              RequestListener requestListener, int timeout,
                              Map<Class<?>, Function<HttpResponse, ?>> responseTransformers, boolean chunked,
                              String contentType, String contentEncoding, Map<String, List<String>> formParameters) {
        super(uri, uriParameters, headers, successStatusCodeRange, throwExceptionOnError, async, requestListener,
                timeout, responseTransformers, chunked, contentType, contentEncoding, null);
        if (MapUtils.isNotEmpty(formParameters)) {
            this.formParameters.putAll(formParameters);
        }
    }

    @Override
    public HttpEntity getHttpEntity() {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : formParameters.entrySet()) {
            String name = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                nameValuePairs.add(new BasicNameValuePair(name, value));
            }
        }
        try {
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairs);
            urlEncodedFormEntity.setChunked(isChunked());
            urlEncodedFormEntity.setContentEncoding(getContentEncoding());
            urlEncodedFormEntity.setContentType(getContentType());
            return urlEncodedFormEntity;
        } catch (UnsupportedEncodingException e) {
            throw new CrawlerException("entity creation", e);
        }
    }

    public abstract static class FormPostRequestBuilder<R extends FormPostRequest, B extends PostRequest.PostRequestBuilder> extends PostRequestBuilder<String, R, B> {

        protected Map<String, List<String>> formParameters = new HashMap<>();

        public B field(String name, String value) {
            List<String> list = formParameters.get(name);
            if (list == null) {
                list = new ArrayList<>();
                formParameters.put(name, list);
            }
            list.add(value);
            return getThis();
        }

        public B fields(Map<String, List<String>> formParameters) {
            this.formParameters.putAll(formParameters);
            return getThis();
        }

    }

}
