package xqTomcat.utils;

import com.xqTomcat.http.HttpMethod;
import com.xqTomcat.http.HttpRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author xiaqi
 * http parser
 * http请求解析器
 */
public class HttpRequestParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestParser.class);

    // can't create the instance of the class
    // 不能创建此对象的实例
    private HttpRequestParser(){}

    public static HttpRequestEntity parse(InputStream inputStream) throws IOException {
        // create the instance of HttpEntity 创建http请求实体对象
        HttpRequestEntity entity = new HttpRequestEntity();
        // read the request 读取请求
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        // start parse http request 开始解析http请求
        String httpRequest = new String(bytes);
        if (0 == httpRequest.length()){
            return null;
        }
        String[] requestLines = httpRequest.split("\r\n");
        // parse the request line 解析http请求行
        String requestLine = requestLines[0];
        LOGGER.info("requestLine:"+requestLine);
        String[] requestLineInfo = requestLine.split(" ");
        // only parse the get and post method 只解析get和post方法的请求
        if (requestLineInfo[0].equalsIgnoreCase("GET")){
            entity.setHttpMethod(HttpMethod.GET);
        } else {
            entity.setHttpMethod(HttpMethod.POST);
        }
        entity.setRequestURL(requestLineInfo[1]);
        // check get method parameter 检验为get方法时，读取参数
        if (HttpMethod.GET == entity.getHttpMethod()){
            int paramSplitorIndex = entity.getRequestURL().indexOf("?");
            if (-1 != paramSplitorIndex){
                String parameterPairs = entity.getRequestURL().substring(paramSplitorIndex+1);
                String[] paramPairs = parameterPairs.split("&");
                for (String params : paramPairs){
                    String[] strs = params.split("=");
                    entity.getRequestEntity().put(strs[0],strs[1]);
                }
            }
        }
        //parse the request headers and parameter 解析请求头部行和post提交参数
        for (int i = 1;i<requestLines.length;i++){
            if (!" ".equals(requestLines[i])){
                LOGGER.info("request header:"+requestLines[i]);
                String[] headers = requestLines[i].split(":");
                entity.getRequestHeaders().put(headers[0],headers[1]);
            } else {
                // 判断是否带有post参数
                if (i+1 < requestLines.length){
                    String[] paramPairs = requestLines[i + 1].split("&");
                    LOGGER.info("request parameter:"+requestLines[i + 1]);
                    for (String params : paramPairs){
                        String[] strs = params.split("=");
                        entity.getRequestEntity().put(strs[0],strs[1]);
                    }
                }
            }
        }

        return entity;

    }


}
