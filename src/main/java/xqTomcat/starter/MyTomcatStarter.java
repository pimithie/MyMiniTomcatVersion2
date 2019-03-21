package xqTomcat.starter;

import com.xqTomcat.annotation.MyServlet;
import com.xqTomcat.dispatcher.Dispatcher;
import com.xqTomcat.servlet.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author xiaqi
 * tomcat starter.
 */
public class MyTomcatStarter {

    private final static Logger logger = LoggerFactory.getLogger(MyTomcatStarter.class);

    private static final Dispatcher DISPATCHER = new Dispatcher();

    private static final ExecutorService excutors = Executors.newFixedThreadPool(10);

    private static String basePackage;

    private static int port;

    //read the configuration file 读取配置文件
    static {
        InputStream resource = MyTomcatStarter.class.getClassLoader().getResourceAsStream("MyServer.properties");
        Properties properties = new Properties();
        try {
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("read configuration file fail!");
        }
        basePackage = properties.getProperty("basePackage");
        port = Integer.parseInt(properties.getProperty("port"));
    }

    /**
     * start the tomcat server
     */
    public static void main(String[] args) throws Exception {
        // record the start timestamp 记录tomcat启动时间戳
        long start = System.currentTimeMillis();

        // initialize servlet mapping
        // 初始化servlet mapping
        initServletMapping();

        // instantiation all servlet whose @MyServlet Annotation attribute load-on-startup is true
        // 实例化所有在容器启动的加载的servlet
        instantiationEarlyServlet();

        // create the serversocket to listening the specific port
        ServerSocket serverSocket = new ServerSocket(8080);
        logger.info("my tomcat started in ["+(System.currentTimeMillis()-start)+"] ms,listening "+8080+" port");
        while (true){
            // start listening 开启监听对应端口
            Socket socket = serverSocket.accept();
            logger.info("receive http request,create the socket:"+socket);
            logger.info("submit the request to a thread.");
            excutors.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        DISPATCHER.dispatch(socket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private static void instantiationEarlyServlet() throws Exception {
        // traverse the servlet mapping to search for all early servlet
        // 寻找需要提前实例化的servlet
        for (Map.Entry<String,Class<?>> entry : DISPATCHER.getServletMapping().entrySet()){
            Class<?> clazz = entry.getValue();
            MyServlet annotation = clazz.getAnnotation(MyServlet.class);
            if (annotation.load_on_startup()){
                Servlet instance = (Servlet) clazz.newInstance();
                // invoke servlet init method 调用servlet的init方法
                instance.init();
                logger.info("instantiation early servlet:"+clazz.getName());
                DISPATCHER.getAllAlreadyExistsServlet().put(entry.getKey(),instance);
            }
        }
    }

    /**
     * Scanning component,and initialize the ServletMapping
     * 扫描servlet组件，并初始化ServletMapping
     */
    private static void initServletMapping() throws Exception {
        logger.info("starting scanning package.");
        scanningPackage(basePackage);
    }

    private static void scanningPackage(String basePackage) throws Exception{
        // replace all "." with "/"  将所有的"."替换为"/"
        String basePackageStr = basePackage.replaceAll("\\.","/");

        // retrieve the root directory's url 获取根目录的url
        URL rootDirectoryUrl = MyTomcatStarter.class.getClassLoader().getResource(basePackageStr);
        String rootDirectoryStr = rootDirectoryUrl.getFile();

        File rootDirectory = new File(rootDirectoryStr);
        // retrieve all directory and file of rootDirectory
        // 获取根目录下所有的目录和文件
        String[] fileStrs = rootDirectory.list();
        for (String fileStr : fileStrs){
            File file = new File(rootDirectoryStr+"/"+fileStr);
            if (file.isDirectory()){
                scanningPackage(basePackage+"."+fileStr);
            } else {
                // com.xqTomcat.annotation.MyServlet.class
                String className = basePackage+"."+fileStr;
                // com.xqTomcat.annotation.MyServlet
                className = className.replace(".class","");
                // put into the servlet mapping 放入servlet mapping的map中
                Class<?> clazz = Class.forName(className);
                // exist @MyServlet annotation? 判断是否存在@MyServlet注解
                if (clazz.isAnnotationPresent(MyServlet.class)){
                    logger.info("find a servlet:"+clazz.getName());
                    MyServlet annotation = clazz.getAnnotation(MyServlet.class);
                    String path = annotation.value();
                    DISPATCHER.getServletMapping().put(path,clazz);
                }
            }
        }
    }

}
