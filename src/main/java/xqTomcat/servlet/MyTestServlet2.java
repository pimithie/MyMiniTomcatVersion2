package xqTomcat.servlet;

import com.xqTomcat.annotation.MyServlet;
import com.xqTomcat.http.HttpRequest;
import com.xqTomcat.http.HttpResponse;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;

/**
 * @author xiaqi
 */
@MyServlet("/test2")
public class MyTestServlet2 extends HttpServlet {


    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setContentType("image/jpeg");
        String resourcePath = "C:\\Users\\xy\\Pictures\\Saved Pictures\\a.jpg";
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(resourcePath));
        OutputStream outputStream = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,length);
        }
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        doGet(request,response);
    }
}
