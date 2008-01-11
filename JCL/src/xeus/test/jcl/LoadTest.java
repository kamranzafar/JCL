package xeus.test.jcl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import xeus.jcl.JarClassLoader;
import xeus.jcl.exception.JclException;

public class LoadTest extends TestCase {

    private static Logger logger = Logger.getLogger(LoadTest.class);

    public void testWithInterface() throws IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException, JclException {
        JarClassLoader jcl = new JarClassLoader("test-jcl.jar");

        TestInterface ti = (TestInterface) jcl.loadClass("xeus.test.jcl.Test")
                .newInstance();
        assertNotNull(ti);

        logger.debug(ti.sayHello());

    }

    public void testWithReflection() throws IOException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException,
            SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        JarClassLoader jc2 = new JarClassLoader("test-jcl.jar");

        Object testObj = jc2.loadClass("xeus.test.jcl.Test").newInstance();
        assertNotNull(testObj);

        logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
                .invoke(testObj, null));
    }

    public void testWithUrl() throws IOException, InstantiationException,
            IllegalAccessException, ClassNotFoundException,
            IllegalArgumentException, SecurityException,
            InvocationTargetException, NoSuchMethodException, JclException {
        // URL url=new URL("http://localhost:8080/blank/test-jcl.jar");
        URL url = new URL(
                "file:/C:/Kamran/Kamran/work/eclipse/JCL/test-jcl.jar");
        JarClassLoader jc = new JarClassLoader(url);
        Object testObj = jc.loadClass("xeus.test.jcl.Test").newInstance();
        assertNotNull(testObj);

        logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
                .invoke(testObj, null));
    }

    public void testWithInputStream() throws IOException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException, IllegalArgumentException,
            SecurityException, InvocationTargetException,
            NoSuchMethodException, JclException {
        FileInputStream fis = new FileInputStream("test-jcl.jar");
        JarClassLoader jc = new JarClassLoader(fis);
        Object testObj = jc.loadClass("xeus.test.jcl.Test").newInstance();
        assertNotNull(testObj);

        logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
                .invoke(testObj, null));
        fis.close();
    }

    public void testWithSpring() throws FileNotFoundException,
            InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        XmlBeanFactory bf = new XmlBeanFactory(new FileSystemResource(
                "spring-test.xml"));
        Test test = (Test) bf.getBean("test");
        assertNotNull(test);

        logger.debug(test.sayHello());
    }
}
