package com.ravey.common.utils.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;

import java.io.IOException;

@Slf4j
public class HttpClientManager {

	private static PoolingHttpClientConnectionManager manager;
	private static CloseableHttpClient httpClient;

	public HttpClientManager() {
		// 注册访问协议相关的Socket工厂
		final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
				.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();
		// HttpConnection工厂：配置写请求/解析响应处理器
		final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
				DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
		// DNS解析器
		final DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
		// 创建池化链接管理器
		manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory, dnsResolver);
		// 默认为socket配置
		final SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
		manager.setDefaultSocketConfig(defaultSocketConfig);
		manager.setMaxTotal(300); // 设置整个连接池的最大连接数
		// 每个路由的默认最大连接，每个路由实际最大连接数默认为DefaultMaxPerRoute控制,而MaxTotal是控制整个池子的最大数
		// 设置过小无法支持大并发，(ConnectionPoolTimeoutException:Timeout waiting for
		// connection from pool),路由是对maxTotal的细分
		manager.setDefaultMaxPerRoute(200); // 每个路由的最大连接数
		// 再从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
		manager.setValidateAfterInactivity(5 * 1000);
		// 默认请求设置
		final RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(20 * 1000) // 设置连接超时时间
				.setSocketTimeout(20 * 1000) // 设置等待数据超时时间
				.setConnectionRequestTimeout(2000) // 设置从连接池获取连接的等待超时时间
				.build();
		// 创建HttpClient
		httpClient = HttpClients.custom().setConnectionManager(manager).build();// 指定HttpClient使用的连接池
		// JVM 停止或重启时，关闭连接池释放掉连接（跟数据库连接池类似）
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				httpClient.close();
			} catch (final IOException e) {
				log.error(e.getMessage(), e);
			}
		}));
	}

	public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		return manager;
	}

	public CloseableHttpClient getCloseableHttpClient() {
		return httpClient;
	}

	private static class SingletonInstance {
		private static final HttpClientManager INSTANCE = new HttpClientManager();
	}

	/**
	 * 获取HttpClient
	 * @return
	 */
	public static CloseableHttpClient getHttpClient() {
		return SingletonInstance.INSTANCE.getCloseableHttpClient();
	}

	public static PoolingHttpClientConnectionManager getManager() {
		return SingletonInstance.INSTANCE.getPoolingHttpClientConnectionManager();
	}

}
