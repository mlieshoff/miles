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
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.mili.utils.Function;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Lieshoff
 */
public class GetRequest extends Request {

    private GetRequest(String uri, Map<String, List<String>> uriParameters, Map<String, String> headers,
                       Range<Integer> successStatusCodeRange, boolean throwExceptionOnError, boolean async,
                       RequestListener requestListener, int timeout, Map<Class<?>, Function<HttpResponse, ?>> responseTransformers) {
        super(uri, uriParameters, headers, successStatusCodeRange, throwExceptionOnError, async, requestListener,
                timeout, responseTransformers);
    }

    public static GetRequestBuilder builder() {
        return new GetRequestBuilder();
    }

    @Override
    public HttpUriRequest getHttpUriRequest() {
        return new HttpGet(createUri(getUri(), getUriParameters()));
    }

    public static class GetRequestBuilder extends RequestBuilder<GetRequest, GetRequestBuilder> {

        @Override
        public GetRequest build() {
            return new GetRequest(uri, uriParameters, headers, successStatusCodeRange, throwExceptionOnError,
                    async, requestListener, timeout, responseTransformers);
        }

        @Override
        public GetRequestBuilder getThis() {
            return this;
        }

    }

}
