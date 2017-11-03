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
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.mili.utils.Function;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Lieshoff
 */
public abstract class Request {

    private final int timeout;

    private final RequestListener requestListener;

    private final boolean async;
    private final boolean throwExceptionOnError;

    private final String uri;

    private final Range<Integer> successStatusCodeRange;

    private final Map<String, List<String>> uriParameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<Class<?>, Function<HttpResponse, ?>> responseTransformers = new HashMap<>();

    protected Request(String uri, Map<String, List<String>> uriParameters, Map<String, String> headers,
                      Range<Integer> successStatusCodeRange, boolean throwExceptionOnError, boolean async,
                      RequestListener requestListener, int timeout, Map<Class<?>, Function<HttpResponse, ?>> responseTransformers) {
        this.uri = uri;
        if (MapUtils.isNotEmpty(uriParameters)) {
            this.uriParameters.putAll(uriParameters);
        }
        if (MapUtils.isNotEmpty(headers)) {
            this.headers.putAll(headers);
        }
        this.successStatusCodeRange = successStatusCodeRange;
        this.throwExceptionOnError = throwExceptionOnError;
        this.async = async;
        this.requestListener = requestListener;
        this.timeout = timeout;
        if (MapUtils.isNotEmpty(this.responseTransformers)) {
            this.responseTransformers.putAll(responseTransformers);
        }
    }

    public URI createUri(String uri, Map<String, List<String>> urlParameters) {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            if (MapUtils.isNotEmpty(urlParameters)) {
                for (Map.Entry<String, List<String>> entry : urlParameters.entrySet()) {
                    String name = entry.getKey();
                    List<String> values = entry.getValue();
                    if (values == null) {
                        uriBuilder.addParameter(name, null);
                    } else {
                        for (String value : values) {
                            uriBuilder.addParameter(name, value);
                        }
                    }
                }
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new CrawlerException("url building", e);
        }
    }

    public abstract HttpUriRequest getHttpUriRequest();

    public String getUri() {
        return uri;
    }

    public Map<String, List<String>> getUriParameters() {
        return uriParameters;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Range<Integer> getSuccessStatusCodeRange() {
        return successStatusCodeRange;
    }

    public boolean isThrowExceptionOnError() {
        return throwExceptionOnError;
    }

    public boolean isAsync() {
        return async;
    }

    public RequestListener getRequestListener() {
        return requestListener;
    }

    public int getTimeout() {
        return timeout;
    }

    public <T> Function<HttpResponse, ?> getTransformer(Class<T> clazz) {
        return responseTransformers.get(clazz);
    }

    public static abstract class RequestBuilder<R extends Request, B> {

        protected int timeout = 5000;

        protected RequestListener requestListener;

        protected boolean async;
        protected boolean throwExceptionOnError;

        protected String uri;

        protected Map<String, List<String>> uriParameters = new HashMap<>();
        protected Map<String, String> headers = new HashMap<>();
        protected Map<Class<?>, Function<HttpResponse, ?>> responseTransformers = new HashMap<>();

        protected Range<Integer> successStatusCodeRange = Range.between(200, 399);

        public B timeout(int timeout) {
            this.timeout = timeout;
            return getThis();
        }

        public B requestListener(RequestListener requestListener) {
            this.requestListener = requestListener;
            return getThis();
        }

        public B async(boolean async) {
            this.async = async;
            return getThis();
        }

        public B uri(String uri) {
            this.uri = uri;
            return getThis();
        }

        public B header(String name, String value) {
            headers.put(name, value);
            return getThis();
        }

        public B headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return getThis();
        }

        public B param(String name, String value) {
            List<String> list = uriParameters.get(name);
            if (list == null) {
                list = new ArrayList<>();
                uriParameters.put(name, list);
            }
            list.add(value);
            return getThis();
        }

        public B params(Map<String, List<String>> uriParameters) {
            this.uriParameters.putAll(uriParameters);
            return getThis();
        }

        public B paramsUnique(Map<String, String> uriParameters) {
            for (Map.Entry<String, String> entry : uriParameters.entrySet()) {
                this.uriParameters.put(entry.getKey(), Collections.singletonList(entry.getValue()));
            }
            return getThis();
        }

        public B successStatusCode(int statusCode) {
            successStatusCodeRange = Range.between(statusCode, statusCode);
            return getThis();
        }

        public B successStatusCodeRange(Range<Integer> range) {
            successStatusCodeRange = Range.between(range.getMinimum(), range.getMaximum());
            return getThis();
        }

        public B throwExceptionOnError(boolean throwExceptionOnError) {
            this.throwExceptionOnError = throwExceptionOnError;
            return getThis();
        }

        public <T> B registerResponseTransformer(Class<T> clazz, Function<HttpResponse, ?> typedTransformer) {
            responseTransformers.put(clazz, typedTransformer);
            return getThis();
        }

        public abstract R build();

        public abstract B getThis();

    }

}
