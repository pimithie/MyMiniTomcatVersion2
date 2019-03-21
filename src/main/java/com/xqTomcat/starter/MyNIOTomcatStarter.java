package com.xqTomcat.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author xiaqi
 * NIO-based mini tomcat implementation
 * 基于NIO的mini版tomcat的实现
 */
public class MyTomcatStarter2 {

    private static final Logger logger = LoggerFactory.getLogger(MyTomcatStarter2.class);

    private static final int PORT = 8080;
    /**
     * start the tomcat server
     */
    public static void main(String[] args) throws Exception {
        // create selector 创建选择器
        Selector selector = Selector.open();
        // create ServerSocketChannel 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // bind the ip and port 绑定ip和端口
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), PORT));
        // non-blocking configuration 配置非阻塞
        serverSocketChannel.configureBlocking(false);
        // register the ServerSocketChannel and the accept event
        // 注册ServerSocketChannel到选择器上,并开启监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("my tomcat started,listening "+PORT+" port");
        while (true) {
            // start listening 开启监听
            selector.select();
            // retrieve the event set 获取事件集
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // traverse the event set 遍历事件集
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // process the accept event 处理接收事件
                if (key.isAcceptable()) {
                    // retrieve the ServerSocketChannel 取到对应的ServerSocketChannel
                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    // create the SocketChannel 创建对应的SocketChannel
                    SocketChannel socketChannel = ssChannel.accept();
                    // non-blocking configuration and register the SocketChannel
                    // 非阻塞配置并注册到选择器上
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // process the request 处理请求
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    processRequest(socketChannel);
                }
                iterator.remove();
            }
        }
    }

    private static void processRequest(SocketChannel socketChannel) throws IOException {
        // allocate the read buffer 分配读取缓冲区
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);

        String httpResponse = getHttpResponse();
        byte[] httpResponseBytes = httpResponse.toString().getBytes("UTF-8");
        // allocate the out buffer 分配输出缓冲区
        ByteBuffer outBuffer = ByteBuffer.allocate(httpResponseBytes.length);

        readBuffer.clear();
        int length = 0;
        // read the http request 读取http请求
        System.out.println("http request：");
        while ((length = socketChannel.read(readBuffer)) > 0){
            readBuffer.flip();
            System.out.print(new String(readBuffer.array()));
            readBuffer.clear();
        }
        // out the response 返回响应
        outBuffer.put(httpResponseBytes);
        outBuffer.flip();
        socketChannel.write(outBuffer);
        socketChannel.close();
    }

    private static String getHttpResponse(){
        // the http response http响应报文
        StringBuilder httpResponse = new StringBuilder(40);
        httpResponse.append("HTTP/1.1 200 OK\r\n");
        httpResponse.append("Content-Type: text/html;charset=UTF-8\r\n");
        httpResponse.append("\r\n");
        httpResponse.append("<! DOCTYPE html>").append("<html><head><title>my NIO tomcat</title>");
        httpResponse.append("</head><body>");
        httpResponse.append("<h3>你好，NIO tomcat</h3></body></html>");
        return httpResponse.toString();
    }

}
