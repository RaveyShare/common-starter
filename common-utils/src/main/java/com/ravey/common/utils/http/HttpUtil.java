package com.ravey.common.utils.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.ContentType;
import com.ravey.common.api.model.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * http请求工具类
 *
 * @author Ravey
 */
@Slf4j
public class HttpUtil {

    private static Integer RESP_LENGTH = 3000;

    /**
     * 通用请求头
     */
    public static final Map<String, String> HEADERS = new HashMap<String, String>() {
        {
            put("Content-Type", "application/json");
            put("Accept", "application/json");
        }
    };

    public static final Map<String, String> TEXT_XML_HEADER = new HashMap<String, String>() {
        {
            put("Content-Type", "text/xml");
        }
    };

    /**
     * 表单格式通用请求头
     */
    public static final Map<String, String> FORM_HEADERS = new HashMap<String, String>() {
        {
            put("Content-Type", ContentType.FORM_URLENCODED.toString());
        }
    };


    /**
     * 默认编码
     */
    private static final String ENCODING = "UTF-8";

    /**
     * 带超时时间的 postBody 请求
     * @param url
     * @param body
     * @param headers
     * @param timeout 超时时间
     * @return
     */
    public static HttpRespons postBody(final String url, final String body, final Map<String, String> headers, int timeout) {
        if (timeout > 0) {
            final RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout) // 设置连接超时时间
                    .setSocketTimeout(timeout) // 设置等待数据超时时间
                    .setConnectionRequestTimeout(2000) // 设置从连接池获取连接的等待超时时间
                    .build();
            return postBody(url, body, headers, config);
        } else {
            return postBody(url, body, headers, null);
        }
    }

    /**
     * postBody 请求
     * @param url
     * @param body
     * @param headers
     * @return
     */
    public static HttpRespons postBody(final String url, final String body, final Map<String, String> headers) {
        return postBody(url, body, headers, null);
    }

    /**
     * 最底层的 postBody 请求
     * @param url
     * @param body
     * @param headers
     * @param config
     * @return
     */
    public static HttpRespons postBody(final String url, final String body, final Map<String, String> headers, RequestConfig config) {
        final HttpRespons httpRespons = new HttpRespons();
        HttpResponse response = null;
        try {
            log.info("http req -> url:[{}],body:[{}]，headers:[{}]", url, body, headers);
            HttpPost post = new HttpPost(url);
            // 设置body
            final StringEntity stringEntity = new StringEntity(body.toString(), ENCODING);
            post.setEntity(stringEntity);
            // 设置header
            if (headers != null && headers.size() > 0) {
                for (final String key : headers.keySet()) {
                    post.addHeader(key, headers.get(key));
                }
            }
            if (config != null) {
                post.setConfig(config);
            }
            response = HttpClientManager.getHttpClient().execute(post);
            // 获得响应
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), ENCODING);
            if (code != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            log.info("http resp -> code:[{}],content:[{}]", code, StringUtils.substring(content, 0, RESP_LENGTH));
            httpRespons.setCode(code);
            httpRespons.setContent(content);
            return httpRespons;
        } catch (Exception e) {
            log.error("HTTP访问出错：" + e.getMessage(), e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            if (isTimeOut(e)) {
                throw new ServiceException(-1, "HTTP访问超时：" + e.getMessage());
            }
            throw new ServiceException(-1, "HTTP访问出错：" + e.getMessage());
        }
    }

    /**
     * post from 表单请求
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static HttpRespons postForm(final String url, final Map<String, String> params, final Map<String, String> headers) {
        return postForm(url, params, headers, null);
    }

    /**
     * post表单请求
     * @param url
     * @param params
     * @param headers
     * @param charset 部分三方接口需要指定编码，postForm(String url, Map<String, String> params, Map<String, String> headers) 已经在使用，于是增加重载方法
     * @return
     */
    public static HttpRespons postForm(final String url, final Map<String, String> params, final Map<String, String> headers, Charset charset) {
        final HttpRespons httpRespons = new HttpRespons();
        HttpResponse response = null;
        try {
            log.info("http req -> url:[{}],params:[{}]，headers:[{}]", url, params, headers);
            HttpPost post = new HttpPost(url);
            ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();//用于存放表单数据.

            //遍历map 将其中的数据转化为表单数据
            for (Map.Entry<String, String> entry :
                    params.entrySet()) {
                pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            //对表单数据进行url编码
            UrlEncodedFormEntity urlEncodedFormEntity;
            if (null == charset) {
                urlEncodedFormEntity = new UrlEncodedFormEntity(pairs);
            } else {
                urlEncodedFormEntity = new UrlEncodedFormEntity(pairs, charset);
            }
            post.setEntity(urlEncodedFormEntity);

            // 设置header
            if (headers != null && headers.size() > 0) {
                for (final String key : headers.keySet()) {
                    post.addHeader(key, headers.get(key));

                }
            }
            response = HttpClientManager.getHttpClient().execute(post);
            // 获得响应
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), ENCODING);
            if (code != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            log.info("http resp -> code:[{}],content:[{}]", code, StringUtils.substring(content, 0, RESP_LENGTH));
            httpRespons.setCode(code);
            httpRespons.setContent(content);
            return httpRespons;
        } catch (Exception e) {
            log.error("HTTP访问出错：" + e.getMessage(), e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            if (isTimeOut(e)) {
                throw new ServiceException(-1, "HTTP访问超时：" + e.getMessage());
            }
            throw new ServiceException(-1, "HTTP访问出错：" + e.getMessage());
        }
    }

    /**
     * multipartPost请求
     * @param url 请求路径
     * @param textParams 文本参数
     * @param binaryParams 二进制参数
     * @param headers 请求头
     * @return
     */
    public static HttpRespons multipartPost(final String url, final Map<String, String> textParams,
                                            final Map<String, List<MultipartFile>> binaryParams, final Map<String, String> headers) {
        final HttpRespons httpRespons = new HttpRespons();
        HttpResponse response = null;
        try {
            log.info("http req -> url:[{}], textParams:[{}]", url, textParams);
            HttpPost httpPost = new HttpPost(url);
            if (headers != null && headers.size() > 0) {
                for (final String key : headers.keySet()) {
                    httpPost.addHeader(key, headers.get(key));
                }
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Map.Entry<String, String> entry : textParams.entrySet()) {
                builder.addTextBody(entry.getKey(), entry.getValue(), org.apache.http.entity.ContentType.create("text/plain", Charset.forName("UTF-8")));
            }
            if (CollUtil.isNotEmpty(binaryParams)) {
                for (Map.Entry<String, List<MultipartFile>> entry : binaryParams.entrySet()) {
                    for (MultipartFile file : entry.getValue()) {
                        builder.addBinaryBody(entry.getKey(), file.getBytes(), org.apache.http.entity.ContentType.DEFAULT_BINARY, file.getName());
                    }
                }
            }
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            response = HttpClientManager.getHttpClient().execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), ENCODING);
            if (code != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            log.info("http resp -> code:[{}],content:[{}]", code, StringUtils.substring(content, 0, RESP_LENGTH));
            httpRespons.setCode(code);
            httpRespons.setContent(content);
            return httpRespons;
        } catch (Exception e) {
            log.error("HTTP访问出错：" + e.getMessage(), e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            if (isTimeOut(e)) {
                throw new ServiceException(-1, "HTTP访问超时：" + e.getMessage());
            }
            throw new ServiceException(-1, "HTTP访问出错：" + e.getMessage());
        }
    }

    /**
     * 带超时时间的 get 请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static HttpRespons getReq(String url, final Map<String, String> params, final Map<String, String> headers, int timeout) {
        if (timeout > 0) {
            final RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout) // 设置连接超时时间
                    .setSocketTimeout(timeout) // 设置等待数据超时时间
                    .setConnectionRequestTimeout(2000) // 设置从连接池获取连接的等待超时时间
                    .build();
            return getReq(url, params, headers, config);
        } else {
            return getReq(url, params, headers, null);
        }
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static HttpRespons getReq(String url, final Map<String, String> params, final Map<String, String> headers) {
        return getReq(url, params, headers, null);
    }

    /**
     * get请求
     *
     * @param url
     * @param params
     * @param headers
     * @return
     */
    public static HttpRespons getReq(String url, final Map<String, String> params, final Map<String, String> headers, RequestConfig config) {
        final HttpRespons httpRespons = new HttpRespons();
        HttpResponse response = null;
        try {
            if (CollectionUtil.isNotEmpty(params)) {
                url = url + buildQuery(params, ENCODING, true);
            }
            log.info("http req -> url:[{}],body:[{}]，headers:[{}]", url, params, headers);
            HttpGet get = new HttpGet(url);
            // 设置header
            if (headers != null && headers.size() > 0) {
                for (final String key : headers.keySet()) {
                    get.addHeader(key, headers.get(key));
                }
            }
            if (config != null) {
                get.setConfig(config);
            }
            response = HttpClientManager.getHttpClient().execute(get);
            // 获得响应
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), ENCODING);
            if (code != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            log.info("http resp -> code:[{}],content:[{}]", code, StringUtils.substring(content, 0, RESP_LENGTH));
            httpRespons.setCode(code);
            httpRespons.setContent(content);
            return httpRespons;
        } catch (Exception e) {
            log.error("HTTP访问出错：" + e.getMessage(), e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            if (isTimeOut(e)) {
                throw new ServiceException(-1, "HTTP访问超时：" + e.getMessage());
            }
            throw new ServiceException(-1, "HTTP访问出错：" + e.getMessage());
        }
    }


    /**
     * get请求
     * 对参数进行URLEncode
     *
     * @param url
     * @param params
     * @return
     */
    public static HttpRespons getReqUrlEncode(String url, final Map<String, Object> params) {
        final HttpRespons httpRespons = new HttpRespons();
        HttpResponse response = null;
        try {
            if (CollectionUtil.isNotEmpty(params)) {
                url = url + URLEncoder.encode(getUrlParamsByMap(params), "UTF-8");
            }
            log.info("http req -> url:[{}],body:[{}]", url, params);
            HttpGet get = new HttpGet(url);
            response = HttpClientManager.getHttpClient().execute(get);
            // 获得响应
            int code = response.getStatusLine().getStatusCode();
            String content = EntityUtils.toString(response.getEntity(), ENCODING);
            if (code != HttpStatus.SC_OK) {
                EntityUtils.consume(response.getEntity());
            }
            log.info("http resp -> code:[{}],content:[{}]", code, StringUtils.substring(content, 0, RESP_LENGTH));
            httpRespons.setCode(code);
            httpRespons.setContent(content);
            return httpRespons;
        } catch (Exception e) {
            log.error("HTTP访问出错：" + e.getMessage(), e);
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e1) {
                    log.error(e1.getMessage(), e1);
                }
            }
            if (isTimeOut(e)) {
                throw new ServiceException(-1, "HTTP访问超时：" + e.getMessage());
            }
            throw new ServiceException(-1, "HTTP访问出错：" + e.getMessage());
        }
    }

    /**
     * 判断异常是不是超时的异常
     * @param e
     * @return
     */
    private static boolean isTimeOut(Exception e) {
        if (e instanceof java.net.SocketTimeoutException ||
                e instanceof org.apache.http.conn.ConnectTimeoutException ||
                // 理论上下面这个异常不属于超时
                e instanceof org.apache.http.conn.HttpHostConnectException) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    private static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * get请求参数拼接
     *
     * @param params
     * @param encode
     * @param needQuestionMark
     * @return
     * @throws IOException
     */
    public static String buildQuery(Map<String, String> params, String encode, boolean needQuestionMark) throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        if (needQuestionMark) {
            query.append("?");
        }
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Map.Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=");
                if (StringUtils.isEmpty(encode)) {
                    query.append(value);
                } else {
                    query.append(URLEncoder.encode(value, encode));
                }
            }
        }

        return query.toString();
    }

}
