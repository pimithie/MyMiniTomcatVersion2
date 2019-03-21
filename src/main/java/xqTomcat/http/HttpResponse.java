package xqTomcat.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author xiaqi
 * http response http响应封装类
 */
public class HttpResponse {

    private OutputStream outputStream;

    /**
     * the flag of setting headers
     * 指示是否可以设置响应行和头部字段
     */
    private boolean isAssignable = true;

    /**
     * statusCode: 200,by default
     * 响应状态码：默认为200
     */
    private String statusCode = "200 ok";

    /**
     * response Content-Type:"text/html;charset=UTF-8",by default
     * 响应报文的头部字段Content-Type，默认为"text/html;charset=UTF-8"
     */
    private String contentType = "text/html;charset=UTF-8";

    public HttpResponse(OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        if (isAssignable){
            this.statusCode =statusCode;
        } else {
            throw new RuntimeException("already setting the response headers!");
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        if (isAssignable){
            this.contentType = contentType;
        } else {
            throw new RuntimeException("already setting the response headers!");
        }
    }

    public OutputStream getOutputStream() throws IOException {
        isAssignable = false;
        buildHttpResponseDatagram();
        return outputStream;
    }

    /**
     * finally response to browser (client)
     * 最终返回给客户端的响应
     * @return http响应报文
     */
    public void buildHttpResponseDatagram() throws IOException {
        StringBuilder stringBuilder = new StringBuilder(200);
        stringBuilder.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        stringBuilder.append("Content-Type: ").append(contentType).append("\r\n");
        stringBuilder.append("\r\n");
        outputStream.write(stringBuilder.toString().getBytes("UTF-8"));
    }

    /**
     * @throws IOException
     */
    public void send() throws IOException {
        outputStream.flush();
        outputStream.close();
    }
}
