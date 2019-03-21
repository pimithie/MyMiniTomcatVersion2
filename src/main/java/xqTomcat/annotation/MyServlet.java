package xqTomcat.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiaqi
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyServlet {

    /**
     * url path
     */
    String value();

    /**
     * is early?
     * 是否提前初始化
     */
    boolean load_on_startup() default false;

}
