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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.mili.utils.Function;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Lieshoff
 */
public class StringPatchRequest extends PatchRequest<String> {

    protected StringPatchRequest(String uri, Map<String, List<String>> uriParameters, Map<String, String> headers,
                                 Range<Integer> successStatusCodeRange, boolean throwExceptionOnError, boolean async,
                                 RequestListener requestListener, int timeout,
                                 Map<Class<?>, Function<HttpResponse, ?>> responseTransformers, boolean chunked,
                                 String contentType, String contentEncoding, String body) {
        super(uri, uriParameters, headers, successStatusCodeRange, throwExceptionOnError, async, requestListener,
                timeout, responseTransformers, chunked, contentType, contentEncoding, body);
    }

    public static StringPatchRequest.StringPatchRequestBuilder builder() {
        return new StringPatchRequest.StringPatchRequestBuilder();
    }

    @Override
    public HttpEntity getHttpEntity() {
        try {
            StringEntity entity = new StringEntity(getBody());
            entity.setContentEncoding(getContentEncoding());
            entity.setChunked(isChunked());
            entity.setContentType(getContentType());
            return entity;
        } catch (UnsupportedEncodingException e) {
            throw new CrawlerException("entity creation", e);
        }
    }

    public static class StringPatchRequestBuilder extends PostRequest.PostRequestBuilder<String, StringPatchRequest, StringPatchRequestBuilder> {

        @Override
        public StringPatchRequest build() {
            return new StringPatchRequest(uri, uriParameters, headers, successStatusCodeRange,
                    throwExceptionOnError, async, requestListener, timeout, responseTransformers, chunked,
                    contentType, contentEncoding, body);
        }

        @Override
        public StringPatchRequestBuilder getThis() {
            return this;
        }

    }

}
