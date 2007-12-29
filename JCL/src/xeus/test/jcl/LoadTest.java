package xeus.test.jcl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import xeus.jcl.JarClassLoader;

public class LoadTest extends TestCase {

	private static Logger logger = Logger.getLogger(LoadTest.class);

	public void testWithInterface() throws IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		JarClassLoader jcl = new JarClassLoader("test-jcl.jar");

		TestInterface ti = (TestInterface) jcl.loadClass("xeus.test.jcl.Test")
				.newInstance();
		assertNotNull(ti);
		
		logger.debug(ti.sayHello());

	}

	public void testWithReflection() throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException,
			SecurityException, InvocationTargetException, NoSuchMethodException {
		JarClassLoader jc2 = new JarClassLoader("test-jcl.jar");

		Object testObj = jc2.loadClass("xeus.test.jcl.Test").newInstance();
		assertNotNull(testObj);
		
		logger.debug(testObj.getClass().getDeclaredMethod("sayHello", null)
				.invoke(testObj, null));
	}

	public void testWithInputStream() throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IllegalArgumentException,
			SecurityException, InvocationTargetException, NoSuchMethodException {
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
