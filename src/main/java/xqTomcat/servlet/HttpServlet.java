package xqTomcat.servlet;

import com.xqTomcat.http.HttpMethod;
import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;


/**
 * @author xiaqi
 * simulation the HttpServlet
 * 模拟HttpServlet
 */
public abstract class HttpServlet implements Servlet{

    @Override
    public void init() {
        System.out.println("servlet inited!");
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (HttpMethod.GET == request.getRequestMethod()){
            doGet(request,response);
        } else {
            doPost(request,response);
        }
    }

    /**
     * Concrete subclass implementation to process get request
     * 具体子类实现的方法去处理get请求
     */
    protected abstract void doGet(HttpRequest request, HttpResponse response) throws Exception;

    /**
     * Concrete subclass implementation to process post request
     * 具体子类实现的方法去处理post请求
     */
    protected abstract void doPost(HttpRequest request, HttpResponse response) throws Exception;
}
