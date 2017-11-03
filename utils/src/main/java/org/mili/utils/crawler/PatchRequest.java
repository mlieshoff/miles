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

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpUriRequest;
import org.mili.utils.Function;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Lieshoff
 */
public abstract class PatchRequest<T> extends Request {

    private final boolean chunked;

    private final String contentType;
    private final String contentEncoding;

    private final T body;

    protected PatchRequest(String uri, Map<String, List<String>> uriParameters, Map<String, String> headers,
                           Range<Integer> successStatusCodeRange, boolean throwExceptionOnError, boolean async,
                           RequestListener requestListener, int timeout,
                           Map<Class<?>, Function<HttpResponse, ?>> responseTransformers, boolean chunked,
                           String contentType, String contentEncoding, T body
            ) {
        super(uri, uriParameters, headers, successStatusCodeRange, throwExceptionOnError, async, requestListener,
                timeout, responseTransformers);
        this.chunked = chunked;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.body = body;
    }

    public abstract HttpEntity getHttpEntity();

    @Override
    public HttpUriRequest getHttpUriRequest() {
        HttpPatch httpPatch = new HttpPatch(createUri(getUri(), getUriParameters()));
        if (body != null && StringUtils.isNotBlank(body.toString())) {
            httpPatch.setEntity(getHttpEntity());
        }
        return httpPatch;
    }

    public boolean isChunked() {
        return chunked;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public T getBody() {
        return body;
    }

    public abstract static class PatchRequestBuilder<T, R extends Request, B extends PatchRequest.PatchRequestBuilder> extends RequestBuilder<R, B> {

        protected boolean chunked = false;

        protected String contentType;
        protected String contentEncoding;

        protected T body;

        public B chunked(boolean chunked) {
            this.chunked = chunked;
            return getThis();
        }

        public B contentType(String contentType) {
            this.contentType = contentType;
            return getThis();
        }

        public B contentEncoding(String contentEncoding) {
            this.contentEncoding = contentEncoding;
            return getThis();
        }

        public B body(T body) {
            this.body = body;
            return getThis();
        }

    }

}
