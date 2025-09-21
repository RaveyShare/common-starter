package com.ravey.common.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
    private static CloseableHttpClient apiHttpClient = null;
    private static final PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
    private static final Object SYNC_LOCK;
    private static final int SECONDS = 1000;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 60000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 15000;

    public HttpClientUtil() {
    }

    private static CloseableHttpClient getApiHttpClient() {
        if (apiHttpClient == null) {
            synchronized(SYNC_LOCK) {
                if (apiHttpClient == null) {
                    apiHttpClient = createDefaultHttpClient(createDefaultHttpClientConfig());
                }
            }
        }

        return apiHttpClient;
    }

    private static RequestConfig createDefaultHttpClientConfig() {
        return RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).setConnectionRequestTimeout(15000).setRedirectsEnabled(false).build();
    }

    private static CloseableHttpClient createDefaultHttpClient(RequestConfig config) {
        return HttpClients.custom().setConnectionManager(pcm).setDefaultRequestConfig(config).build();
    }

    private static String execute(HttpUriRequest request, String resultEncode) throws IOException {
        HttpResponse response = null;

        String var5;
        try {
            response = getApiHttpClient().execute(request);
            HttpEntity resEntity = response.getEntity();
            int status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException(EntityUtils.toString(resEntity, resultEncode));
            }

            var5 = EntityUtils.toString(resEntity, resultEncode);
        } finally {
            HttpClientUtils.closeQuietly(response);
        }

        return var5;
    }

    public static String get(String requestUri) {
        return get(requestUri, (Map)null, (String)null);
    }

    public static String get(String requestUri, Map<String, String> headerParam, String resultEncode) {
        String ret = null;

        try {
            HttpGet httpGet = new HttpGet(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpGet);
                headerParam.forEach(httpGet::setHeader);
            }

            ret = execute(httpGet, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=get, header={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), ret});
            }
        } catch (Exception var5) {
            Exception e = var5;
            logger.error("请求[url={}, method=get, header={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), ret, e});
        }

        return ret;
    }

    public static String post(String requestUri, Map<String, String> headerParam, Map<String, Object> requestParam) {
        return post(requestUri, headerParam, requestParam, (String)null, (String)null);
    }

    public static String post(String requestUri, Map<String, Object> requestParam) {
        return post(requestUri, (Map)null, requestParam, (String)null, (String)null);
    }

    public static String post(String requestUri, Map<String, String> headerParam, Map<String, Object> requestParam, String requestEncode, String resultEncode) {
        String ret = null;

        try {
            HttpPost httpPost = new HttpPost(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpPost);
                headerParam.forEach(httpPost::setHeader);
            }

            List<NameValuePair> nameValuePairList = new ArrayList();
            if (requestParam != null) {
                requestParam.forEach((k, v) -> {
                    nameValuePairList.add(new BasicNameValuePair(k, v.toString()));
                });
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, requestEncode));
            ret = execute(httpPost, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=post, header={}, requestParam={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), JSON.toJSONString(requestParam), ret});
            }
        } catch (Exception var8) {
            Exception e = var8;
            logger.error("请求[url={}, method=post, header={}, requestParam={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), JSON.toJSONString(requestParam), ret, e});
        }

        return ret;
    }

    public static String postJson(String requestUri, String json) {
        return postJson(requestUri, (Map)null, json, ContentType.APPLICATION_JSON, (String)null);
    }

    public static String postJson(String requestUri, Map<String, String> headerParam, String json) {
        return postJson(requestUri, headerParam, json, ContentType.APPLICATION_JSON, (String)null);
    }

    public static String postJson(String requestUri, Map<String, String> headerParam, String json, ContentType contentType, String resultEncode) {
        String ret = null;

        try {
            HttpPost httpPost = new HttpPost(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpPost);
                headerParam.forEach(httpPost::setHeader);
            }

            HttpEntity httpEntity = new StringEntity(json, contentType);
            httpPost.setEntity(httpEntity);
            ret = execute(httpPost, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=postJson, header={}, body={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret});
            }
        } catch (Exception var8) {
            Exception e = var8;
            logger.error("请求[url={}, method=postJson, header={}, body={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret, e});
        }

        return ret;
    }

    public static String postFormData(String requestUri, Map<String, Object> requestParam) {
        return postFormData(requestUri, (Map)null, requestParam);
    }

    public static String postFormData(String requestUri, Map<String, String> headerParam, Map<String, Object> requestParam) {
        return postFormData(requestUri, headerParam, requestParam, (String)null, (String)null, (Object)null);
    }

    public static String postFormData(String requestUri, Map<String, String> headerParam, Map<String, Object> requestParam, String fileKeyName, String fileName, Object obj) {
        return postFormData(requestUri, headerParam, requestParam, fileKeyName, fileName, obj, (String)null, (String)null);
    }

    public static String postFormData(String requestUri, Map<String, String> headerParam, Map<String, Object> requestParam, String fileKeyName, String fileName, Object obj, String requestEncode, String resultEncode) {
        String ret = null;

        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (obj != null) {
                if (obj instanceof File) {
                    multipartEntityBuilder.addBinaryBody(fileKeyName, (File)obj, ContentType.create("multipart/form-data"), fileName).setMode(HttpMultipartMode.RFC6532);
                } else if (obj instanceof InputStream) {
                    multipartEntityBuilder.addBinaryBody(fileKeyName, (InputStream)obj, ContentType.create("multipart/form-data"), fileName).setMode(HttpMultipartMode.RFC6532);
                } else {
                    if (!(obj instanceof byte[])) {
                        throw new IllegalArgumentException("param[obj]仅支持File ,byte[] ,InputStream类型!");
                    }

                    multipartEntityBuilder.addBinaryBody(fileKeyName, (byte[])obj, ContentType.create("multipart/form-data"), fileName).setMode(HttpMultipartMode.RFC6532);
                }
            }

            if (requestParam != null) {
                ContentType contentType = requestEncode == null ? ContentType.MULTIPART_FORM_DATA : ContentType.create("multipart/form-data", Charset.forName(requestEncode));
                requestParam.forEach((k, v) -> {
                    multipartEntityBuilder.addPart(k, new StringBody(v.toString(), contentType));
                });
            }

            HttpPost httpPost = new HttpPost(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpPost);
                headerParam.forEach(httpPost::setHeader);
            }

            httpPost.setEntity(multipartEntityBuilder.build());
            ret = execute(httpPost, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=postFormData, header={}, requestParam={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), JSON.toJSONString(requestParam), ret});
            }
        } catch (Exception var11) {
            Exception e = var11;
            logger.error("请求[url={}, method=postFormData, header={}, requestParam={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), JSON.toJSONString(requestParam), ret, e});
        }

        return ret;
    }

    public static String putJson(String requestUri, Map<String, String> headerParam, String json) {
        return putJson(requestUri, headerParam, json, ContentType.APPLICATION_JSON, (String)null);
    }

    public static String putJson(String requestUri, Map<String, String> headerParam, String json, ContentType contentType, String resultEncode) {
        String ret = null;

        try {
            HttpPut httpPut = new HttpPut(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpPut);
                headerParam.forEach(httpPut::setHeader);
            }

            HttpEntity httpEntity = new StringEntity(json, contentType);
            httpPut.setEntity(httpEntity);
            ret = execute(httpPut, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=putJson, header={}, body={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret});
            }
        } catch (Exception var8) {
            Exception e = var8;
            logger.error("请求[url={}, method=putJson, header={}, body={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret, e});
        }

        return ret;
    }

    public static String deleteJson(String requestUri, Map<String, String> headerParam, String json) {
        return deleteJson(requestUri, headerParam, json, ContentType.APPLICATION_JSON, (String)null);
    }

    public static String deleteJson(String requestUri, Map<String, String> headerParam, String json, ContentType contentType, String resultEncode) {
        String ret = null;

        try {
            HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(requestUri);
            if (headerParam != null) {
                Objects.requireNonNull(httpDelete);
                headerParam.forEach(httpDelete::setHeader);
            }

            HttpEntity httpEntity = new StringEntity(json, contentType);
            httpDelete.setEntity(httpEntity);
            ret = execute(httpDelete, resultEncode);
            if (logger.isDebugEnabled()) {
                logger.debug("请求[url={}, method=deleteJson, header={}, body={}]返回[ret={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret});
            }
        } catch (Exception var8) {
            Exception e = var8;
            logger.error("请求[url={}, method=deleteJson, header={}, body={}]返回[ret={}, errMsg={}]", new Object[]{requestUri, JSON.toJSONString(headerParam), json, ret, e.getMessage()});
        }

        return ret;
    }

    public static byte[] getByteFromUrl(String url) {
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse res = getApiHttpClient().execute(get);
            if (res.getStatusLine().getStatusCode() == 200) {
                byte[] data = EntityUtils.toByteArray(res.getEntity());
                return data;
            }
        } catch (IOException var4) {
            IOException e = var4;
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] getByteFromUrlPost(String url, String json) {
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpEntity httpEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(httpEntity);
            HttpResponse res = getApiHttpClient().execute(httpPost);
            if (res.getStatusLine().getStatusCode() == 200) {
                byte[] data = EntityUtils.toByteArray(res.getEntity());
                return data;
            }
        } catch (IOException var6) {
            IOException e = var6;
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        String token = "Bearer t-9f48c53d04876da5f6cf3100048ecbe8e11c312c";
        String url = "https://open.feishu.cn/open-apis/im/v1/images";
        File file = new File("/Users/lwb/I Had A Sausage.png");
        Map<String, String> header = new HashMap();
        header.put("Authorization", token);
        header.put("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
        Map<String, Object> map = new HashMap();
        map.put("image_type", "message");
        String s = postFormData(url, header, map, "image", "12345.jpg", file, "utf-8", "utf-8");
        System.out.println(s);
    }

    public static byte[] toByte(File file) throws Exception {
        FileInputStream inputStream = null;

        byte[] bytes;
        try {
            long len = file.length();
            bytes = new byte[(int)len];
            inputStream = new FileInputStream(file);
            inputStream.read(bytes);
        } finally {
            if (ObjectUtils.isNotEmpty(inputStream)) {
                inputStream.close();
            }

        }

        return bytes;
    }

    static {
        pcm.setMaxTotal(1000);
        pcm.setDefaultMaxPerRoute(100);
        SYNC_LOCK = new Object();
    }
}
