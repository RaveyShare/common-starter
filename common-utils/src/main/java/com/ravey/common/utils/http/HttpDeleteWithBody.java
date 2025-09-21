package com.ravey.common.utils.http;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    public String getMethod() {
        return "DELETE";
    }

    public HttpDeleteWithBody(String uri) {
        this.setURI(URI.create(uri));
    }

    public HttpDeleteWithBody(URI uri) {
        this.setURI(uri);
    }

    public HttpDeleteWithBody() {
    }
}