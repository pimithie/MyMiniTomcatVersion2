package xqTomcat.dispatcher;

import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;
import com.xqTomcat.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xiaqi
 * process the http request and dispatch the request to servlet
 * 处理请求，查询handlerMapping找到对应的servlet并转发
 */
public class Dispatcher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * url对应的servlet
     * url ----> servlet
     */
    private final Map<String,Class<?>> servletMapping = new HashMap<>();

    /**
     * all exists servlet instance
     * 所有已经实例化过的servlet
     */
    private final Map<String,Object> allAlreadyExistsServlet = new ConcurrentHashMap<>();

    public Map<String, Class<?>> getServletMapping() {
        return servletMapping;
    }

    public Map<String,Object> getAllAlreadyExistsServlet(){
        return allAlreadyExistsServlet;
    }

    /**
     * parse the request to set the headers，and dispatch the instance of request and response to the servlet
     * 解析请求设置对应的响应头部行，并转发http请求和响应对象给对应的servlet
     * @param socket client 客户端对象的socket对象
     * @throws Exception
     */
    public void dispatch(Socket socket) throws Exception {
        HttpRequest request = new HttpRequest(socket.getInputStream());
        HttpResponse response = new HttpResponse(socket.getOutputStream());
        logger.info("create request instance:"+request);
        logger.info("create response instance:"+response);
        try {
            // retrieve the request url 获取http请求的url
            String requestURL = request.getRequestURL();
            if (null == requestURL){
                throw new RuntimeException("request url is null");
            }
            // search for the servlet 搜寻对应路径的servlet
            Servlet servlet = (Servlet) allAlreadyExistsServlet.get(requestURL);
            if (null == servlet){
                Class<?> clazz = servletMapping.get(requestURL);
                if (null == clazz){
                    throw new RuntimeException("no applicable servlet for this path:"+requestURL);
                }
                logger.info("create the servlet instance:"+clazz.getName());
                servlet = (Servlet) clazz.newInstance();
                servlet.init();
                logger.info("new servlet instance of "+clazz.getName()+" is put into the allAlreadyExistsServlet.");
                allAlreadyExistsServlet.put(requestURL,servlet);
            }
            // invoke the service method of the servlet 调用servlet的service方法
            servlet.service(request,response);
        } catch (Exception e){
            response.getOutputStream().write("404 not found for this url.".getBytes("UTF-8"));
        }finally {
            response.send();
            // close the socket 关闭socket连接
            socket.close();
        }
        logger.info("finishing request process.");
    }

}
