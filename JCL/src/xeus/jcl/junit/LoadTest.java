package xeus.jcl.junit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import xeus.jcl.JarClassLoader;
import xeus.jcl.exception.JclException;

public class LoadTest extends TestCase {

	private static Logger logger = Logger.getLogger(LoadTest.class);

	public void testWithReflection() throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException,
			SecurityException, InvocationTargetException,
			NoSuchMethodException, JclException {
		JarClassLoader jc2 = new JarClassLoader(new String[] { "test-jcl.jar" });

		Object testObj = jc2.loadClass("xeus.jcl.test.Test").newInstance();
		assertNotNull(testObj);

		logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
				.invoke(testObj, null));
	}

	public void testWithUrl() throws IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, SecurityException,
			InvocationTargetException, NoSuchMethodException, JclException,
			URISyntaxException {
		// URL url=new URL("http://localhost:8080/blank/test-jcl.jar");
		URL url = new URL("file:/c:\\Kamran\\work\\new\\test.jar");
		JarClassLoader jc = new JarClassLoader(new URL[] { url });
		Object testObj = jc.loadClass("Test").newInstance();
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
		JarClassLoader jc = new JarClassLoader(new FileInputStream[] { fis });
		Object testObj = jc.loadClass("xeus.jcl.test.Test").newInstance();
		assertNotNull(testObj);

		logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
				.invoke(testObj, null));
		fis.close();
	}

	public void testWithSpring() throws FileNotFoundException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException,
			SecurityException, InvocationTargetException, NoSuchMethodException {
		XmlBeanFactory bf = new XmlBeanFactory(new FileSystemResource(
				"spring-test.xml"));
		Object testObj = bf.getBean("test");
		assertNotNull(testObj);

		logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
				.invoke(testObj, null));
	}
}
