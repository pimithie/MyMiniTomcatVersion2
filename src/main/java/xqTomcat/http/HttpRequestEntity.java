package xqTomcat.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaqi
 * http request entity,including current http request information
 * http请求报文信息的封装
 */
public class HttpRequestEntity {

    /**
     * http request method
     */
    private HttpMethod httpMethod;

    /**
     * http request url
     */
    private String requestURL;

    /**
     * http request headers
     */
    private Map<String,String> requestHeaders = new HashMap<>();

    /**
     * http request entity
     * 目前请求报文只支持键值对参数
     */
    private Map<String,String> requestEntity = new HashMap<>();

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getRequestEntity() {
        return requestEntity;
    }

    public void setRequestEntity(Map<String, String> requestEntity) {
        this.requestEntity = requestEntity;
    }
}
