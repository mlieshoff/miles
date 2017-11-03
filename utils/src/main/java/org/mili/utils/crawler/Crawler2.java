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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author Michael Lieshoff
 */
public class Crawler2 {

    private static final ListeningExecutorService EXECUTOR_SERVICE = MoreExecutors.listeningDecorator(
            Executors.newFixedThreadPool(5, new BasicThreadFactory.Builder()
                    .daemon(true)
                    .namingPattern("org.mili.AsyncCrawler")
                    .priority(Thread.NORM_PRIORITY)
                    .build())
    );

    public Response get(GetRequest getRequest) {
        if (getRequest.isAsync()) {
            async(getRequest);
            return null;
        }
        return exec(getRequest);
    }

    public Response post(PostRequest postRequest) {
        if (postRequest.isAsync()) {
            async(postRequest);
            return null;
        }
        return exec(postRequest);
    }

    public Response patch(PatchRequest patchRequest) {
        if (patchRequest.isAsync()) {
            async(patchRequest);
            return null;
        }
        return exec(patchRequest);
    }

    public Response delete(DeleteRequest deleteRequest) {
        if (deleteRequest.isAsync()) {
            async(deleteRequest);
            return null;
        }
        return exec(deleteRequest);
    }

    public static void main(String[] args) {
        Response response = new Crawler2().get(GetRequest.builder().uri("http://www.google.de").build());
        System.out.println(response);
    }

    private void async(final Request request) {
        EXECUTOR_SERVICE.submit(new Runnable() {
            public void run() {
                try {
                    request.getRequestListener().onResponse(new Crawler2().exec(request));
                } catch (Exception e) {
                    request.getRequestListener().onException(e);
                }
            }
        });
    }

    private Response exec(Request request) {
        Response response = new Response(request);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(request.getTimeout())
                        .setConnectionRequestTimeout(request.getTimeout())
                        .setSocketTimeout(request.getTimeout())
                        .build())
                .setDefaultCookieStore(cookieStore)
                .build();
        HttpUriRequest httpUriRequest = request.getHttpUriRequest();
        setHeaders(request, httpUriRequest);
        try {
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpUriRequest);
            response.setResponse(closeableHttpResponse);
        } catch (IOException e) {
            throw new CrawlerException("execute", e);
        }
        return response;
    }

    private void setHeaders(Request request, HttpRequest httpRequest) {
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }

}
