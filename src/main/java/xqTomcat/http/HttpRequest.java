package xqTomcat.http;

import com.xqTomcat.utils.HttpRequestParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiaqi
 * http request 封装类
 */
public class HttpRequest {

    private InputStream inputStream;

    private HttpRequestEntity entity;

    public HttpRequest(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        try {
            this.entity = HttpRequestParser.parse(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public HttpRequestEntity getEntity() {
        return entity;
    }

    public void setEntity(HttpRequestEntity entity) {
        this.entity = entity;
    }

    public HttpMethod getRequestMethod(){
        return entity.getHttpMethod();
    }

    public String getRequestURL(){
        if (null == entity){
            return null;
        }
        return entity.getRequestURL();
    }

    public String getParameter(String key){
        return entity.getRequestEntity().get(key);
    }
}
