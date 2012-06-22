package org.xeustechnologies.jcl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * AbstractClassLoaderTest test case on AbstractClassLoader class.
 * 
 * @author <a href="mailto:jguibert@intelligents-ia.com" >Jerome Guibert</a>
 * 
 */
public class AbstractClassLoaderTest {

	@Test
	public void checkInitializationOfDefaultProxyClassLoader() {
		AbstractClassLoader classLoader = new AbstractClassLoader() {
		};

		assertNotNull(classLoader.getSystemLoader());
		assertNotNull(classLoader.getThreadLoader());
		assertNotNull(classLoader.getParentLoader());
		assertNotNull(classLoader.getCurrentLoader());
		assertNotNull(classLoader.getOsgiBootLoader());

		assertEquals(50, classLoader.getSystemLoader().getOrder());
		assertEquals(40, classLoader.getThreadLoader().getOrder());
		assertEquals(30, classLoader.getParentLoader().getOrder());
		assertEquals(10, classLoader.getCurrentLoader().getOrder());
		assertEquals(0, classLoader.getOsgiBootLoader().getOrder());

	}
}
