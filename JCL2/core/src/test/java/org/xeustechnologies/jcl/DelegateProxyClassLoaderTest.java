package org.xeustechnologies.jcl;

import org.junit.Test;
import org.xeustechnologies.jcl.sample.Test1;

import static org.junit.Assert.*;

/**
 * 
 * DelegateProxyClassLoaderTest test with DelegateProxyClassLoader.
 * 
 * @author <a href="mailto:jguibert@intelligents-ia.com" >Jerome Guibert</a>
 * 
 */
public class DelegateProxyClassLoaderTest {

	@Test
	public void checkDelegateProxyClassLoader() throws ClassNotFoundException {
		/**
		 * First classLoader without Test1
		 */
		JarClassLoader classLoader = new JarClassLoader();
		doIsolated(classLoader);
		assertTrue(classLoader.getLocalLoader().isEnabled());
		try {
			classLoader.loadClass(Test1.class.getName());
			fail("Should obtain java.lang.ClassNotFoundException: org.xeustechnologies.jcl.sample.Test1");
		} catch (ClassNotFoundException e) {
		}
		/**
		 * target  classLoader with Test1
		 */
		JarClassLoader target = new JarClassLoader();
		doIsolated(classLoader);
		assertTrue(classLoader.getLocalLoader().isEnabled());
		target.add(Test1.class.getName());
		target.loadClass(Test1.class.getName());
		/**
		 * Add delegate
		 */
		classLoader.addLoader(new DelegateProxyClassLoader(target));
		classLoader.loadClass(Test1.class.getName());
	}

	/**
	 * Only local loader.
	 * 
	 * @param classLoader
	 */
	protected void doIsolated(JarClassLoader classLoader) {
		classLoader.getCurrentLoader().setEnabled(false);
		classLoader.getParentLoader().setEnabled(false);
		classLoader.getThreadLoader().setEnabled(false);
		classLoader.getSystemLoader().setEnabled(false);
		classLoader.getOsgiBootLoader().setEnabled(false);
	}
}
