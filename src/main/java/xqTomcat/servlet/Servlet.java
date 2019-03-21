package xqTomcat.servlet;

import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;

/**
 * @author xiaqi
 */
public interface Servlet {

    public void init();

    public void service(HttpRequest request, HttpResponse response) throws Exception;

}
