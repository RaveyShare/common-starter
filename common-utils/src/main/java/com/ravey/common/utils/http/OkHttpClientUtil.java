package com.ravey.common.utils.http;

import com.ravey.common.utils.json.JsonUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OkHttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpClientUtil.class);
    private static volatile OkHttpClient okHttpClient = null;

    public OkHttpClientUtil() {
    }

    private static OkHttpClient buildClient() {
        if (okHttpClient == null) {
            Class var0 = OkHttpClientUtil.class;
            synchronized(OkHttpClientUtil.class) {
                if (okHttpClient == null) {
                    TrustManager[] trustManagers = buildTrustManagers();
                    okHttpClient = (new OkHttpClient.Builder()).connectTimeout(10L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).readTimeout(20L, TimeUnit.SECONDS).sslSocketFactory(createSSLSocketFactory(trustManagers), (X509TrustManager)trustManagers[0]).hostnameVerifier((hostName, session) -> {
                        return true;
                    }).retryOnConnectionFailure(true).build();
                }
            }
        }

        return okHttpClient;
    }

    private static TrustManager[] buildTrustManagers() {
        return new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
    }

    private static SSLSocketFactory createSSLSocketFactory(TrustManager[] trustAllCerts) {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init((KeyManager[])null, trustAllCerts, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
        }

        return ssfFactory;
    }

    public static String postFormData(String requestUri, Map<String, Object> requestParam, Map<String, String> headerParam) {
        String ret = null;

        try {
            MultipartBody.Builder bodyBuilder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
            if (requestParam != null) {
                requestParam.forEach((k, v) -> {
                    if (v instanceof File) {
                        File file = (File)v;
                        bodyBuilder.addFormDataPart(k, file.getName(), RequestBody.create(file, MediaType.parse("application/octet-stream")));
                    } else {
                        bodyBuilder.addFormDataPart(k, (String)v);
                    }

                });
            }

            RequestBody body = bodyBuilder.build();
            Request.Builder requestBuilder = (new Request.Builder()).url(requestUri).method("POST", body);
            if (headerParam != null) {
                Objects.requireNonNull(requestBuilder);
                headerParam.forEach(requestBuilder::addHeader);
            }

            Request request = requestBuilder.build();
            ret = execute(request);
        } catch (Exception var8) {
            Exception e = var8;
            logger.error("请求[url={}, header={}, requestParam={}, errMsg={}]", new Object[]{requestUri, JsonUtil.bean2Json(headerParam), JsonUtil.bean2Json(requestParam), e.getMessage()});
        }

        return ret;
    }

    private static String execute(Request request) throws IOException {
        Response response = buildClient().newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            }
        }

        return null;
    }
}