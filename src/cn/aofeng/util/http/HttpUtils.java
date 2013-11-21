/**
 * 建立时间：2010-5-12
 */
package cn.aofeng.util.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import cn.aofeng.util.Proxy;
import org.apache.log4j.Logger;

/**
 * HTTP操作实用类.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class HttpUtils {

    private HttpUtils() {
        
    }

    private final static Logger _logger = Logger.getLogger(HttpUtils.class);
    
    private static HttpClient httpClient = new HttpClient();

    /**
     * 获取HTTP URL的输入流.
     * 
     * @param httpUrl HTTP URL字符串.
     * @param httpContentCharset HTTP内容编码.
     * @return HTTP URL的输入流
     * @throws HttpException
     * @throws IOException
     */
    public static InputStream getInputStream(String httpUrl, String httpContentCharset) 
            throws HttpException, IOException {
        HttpMethod method = new GetMethod(httpUrl);
        return getInputStream(method, httpContentCharset);
    }

    public static InputStream getInputStream(String httpUrl, String httpContentCharset, String params) 
    	throws HttpException, IOException {
    	HttpMethod method = new PostMethod(httpUrl);
    	method.setRequestHeader("Referer", "http://api.blog.163.com/crossdomain.html");
    	method.setQueryString(params);
    	return getInputStream(method, httpContentCharset);
    }

    static InputStream getInputStream(HttpMethod httpMethod, String httpContentCharset) throws HttpException, IOException {
        Proxy proxy = Proxy.getInstance();
        if (proxy.isUseProxy()) {
            httpClient.getHostConfiguration().setProxy(proxy.getProxyHost(), proxy.getProxyPort());
        } else {
        	httpClient.getHostConfiguration().setProxyHost(null);
        }
        HttpClientParams params = httpClient.getParams();
        params.setParameter(HttpClientParams.HTTP_CONTENT_CHARSET, httpContentCharset);
        int responseStatu = httpClient.executeMethod(httpMethod);
        if (HttpStatus.SC_OK != responseStatu) {
            _logger.warn("http response statu not ok, url:" + httpMethod.getURI().toString());
        }
        
        return httpMethod.getResponseBodyAsStream();
    }

}
