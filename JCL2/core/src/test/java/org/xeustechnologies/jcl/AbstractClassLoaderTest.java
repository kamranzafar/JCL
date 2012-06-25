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

		assertNotNull("SystemLoader should not be null", classLoader.getSystemLoader());
		assertNotNull("ThreadLoader should not be null", classLoader.getThreadLoader());
		assertNotNull("ParentLoader should not be null", classLoader.getParentLoader());
		assertNotNull("CurrentLoader should not be null", classLoader.getCurrentLoader());
		assertNotNull("OsgiBootLoader should not be null", classLoader.getOsgiBootLoader());

		assertEquals("SystemLoader order should be 50", 50, classLoader.getSystemLoader().getOrder());
		assertEquals("ThreadLoader order should be 40", 40, classLoader.getThreadLoader().getOrder());
		assertEquals("ParentLoader order should be 30", 30, classLoader.getParentLoader().getOrder());
		assertEquals("CurrentLoader order should be 20", 20, classLoader.getCurrentLoader().getOrder());
		assertEquals("OsgiBootLoader order should be 0", 0, classLoader.getOsgiBootLoader().getOrder());

	}

	@Test
	public void checkDefaultEnabledProxy() {
		AbstractClassLoader classLoader = new AbstractClassLoader() {
		};

		assertEquals(Configuration.isCurrentLoaderEnabled(), classLoader.getCurrentLoader().isEnabled());
		assertEquals(Configuration.isParentLoaderEnabled(), classLoader.getParentLoader().isEnabled());
		assertEquals(Configuration.isThreadContextLoaderEnabled(), classLoader.getThreadLoader().isEnabled());
		assertEquals(Configuration.isSystemLoaderEnabled(), classLoader.getSystemLoader().isEnabled());
		assertEquals(Configuration.isOsgiBootDelegationEnabled(), classLoader.getOsgiBootLoader().isEnabled());

	}
}
