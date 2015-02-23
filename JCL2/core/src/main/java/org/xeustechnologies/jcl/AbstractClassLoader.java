/**
 *
 * Copyright 2015 Kamran Zafar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.xeustechnologies.jcl;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;
import org.xeustechnologies.jcl.utils.Utils;

/**
 * Abstract class loader that can load classes from different resources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

	protected final List<ProxyClassLoader> loaders = new ArrayList<ProxyClassLoader>();

	private final ProxyClassLoader systemLoader = new SystemLoader();
	private final ProxyClassLoader parentLoader = new ParentLoader();
	private final ProxyClassLoader currentLoader = new CurrentLoader();
	private final ProxyClassLoader threadLoader = new ThreadContextLoader();
	private final ProxyClassLoader osgiBootLoader = new OsgiBootLoader();

	/**
	 * Build a new instance of AbstractClassLoader.java.
	 * @param parent parent class loader
	 */
	public AbstractClassLoader(ClassLoader parent) {
		super(parent);
		addDefaultLoader();
	}
	
	/**
	 * No arguments constructor
	 */
	public AbstractClassLoader() {
		super();
		addDefaultLoader();
	}

	protected void addDefaultLoader() {
		loaders.add(systemLoader);
		loaders.add(parentLoader);
		loaders.add(currentLoader);
		loaders.add(threadLoader);
	}

	public void addLoader(ProxyClassLoader loader) {
		loaders.add(loader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	/**
	 * Overrides the loadClass method to load classes from other resources,
	 * JarClassLoader is the only subclass in this project that loads classes
	 * from jar files
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	public Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
		if (className == null || className.trim().equals(""))
			return null;

		Collections.sort(loaders);

		Class clazz = null;

		// Check osgi boot delegation
		if (osgiBootLoader.isEnabled()) {
			clazz = osgiBootLoader.loadClass(className, resolveIt);
		}

		if (clazz == null) {
			for (ProxyClassLoader l : loaders) {
				if (l.isEnabled()) {
					clazz = l.loadClass(className, resolveIt);
					if (clazz != null)
						break;
				}
			}
		}

		if (clazz == null)
			throw new ClassNotFoundException(className);

		return clazz;
	}

	/**
	 * Overrides the getResource method to load non-class resources from other
	 * sources, JarClassLoader is the only subclass in this project that loads
	 * non-class resources from jar files
	 *
	 * @see java.lang.ClassLoader#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		if (name == null || name.trim().equals(""))
			return null;

		Collections.sort(loaders);

		URL url = null;

		// Check osgi boot delegation
		if (osgiBootLoader.isEnabled()) {
			url = osgiBootLoader.findResource(name);
		}

		if (url == null) {
			for (ProxyClassLoader l : loaders) {
				if (l.isEnabled()) {
					url = l.findResource(name);
					if (url != null)
						break;
				}
			}
		}

		return url;

	}

	/**
	 * Overrides the getResourceAsStream method to load non-class resources from
	 * other sources, JarClassLoader is the only subclass in this project that
	 * loads non-class resources from jar files
	 * 
	 * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String name) {
		if (name == null || name.trim().equals(""))
			return null;

		Collections.sort(loaders);

		InputStream is = null;

		// Check osgi boot delegation
		if (osgiBootLoader.isEnabled()) {
			is = osgiBootLoader.loadResource(name);
		}

		if (is == null) {
			for (ProxyClassLoader l : loaders) {
				if (l.isEnabled()) {
					is = l.loadResource(name);
					if (is != null)
						break;
				}
			}
		}

		return is;

	}

	/**
	 * System class loader
	 * 
	 */
	class SystemLoader extends ProxyClassLoader {

		private final Logger logger = Logger.getLogger(SystemLoader.class.getName());

		public SystemLoader() {
			order = 50;
			enabled = Configuration.isSystemLoaderEnabled();
		}

		@Override
		public Class loadClass(String className, boolean resolveIt) {
			Class result;

			try {
				result = findSystemClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isLoggable(Level.FINEST))
				logger.finest("Returning system class " + className);

			return result;
		}

		@Override
		public InputStream loadResource(String name) {
			InputStream is = getSystemResourceAsStream(name);

			if (is != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning system resource " + name);

				return is;
			}

			return null;
		}

		@Override
		public URL findResource(String name) {
			URL url = getSystemResource(name);

			if (url != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning system resource " + name);

				return url;
			}

			return null;
		}
	}

	/**
	 * Parent class loader
	 * 
	 */
	class ParentLoader extends ProxyClassLoader {
		private final Logger logger = Logger.getLogger(ParentLoader.class.getName());

		public ParentLoader() {
			order = 30;
			enabled = Configuration.isParentLoaderEnabled();
		}

		@Override
		public Class loadClass(String className, boolean resolveIt) {
			Class result;

			try {
				result = getParent().loadClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isLoggable(Level.FINEST))
				logger.finest("Returning class " + className + " loaded with parent classloader");

			return result;
		}

		@Override
		public InputStream loadResource(String name) {
			InputStream is = getParent().getResourceAsStream(name);

			if (is != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with parent classloader");

				return is;
			}
			return null;
		}


		@Override
		public URL findResource(String name) {
			URL url = getParent().getResource(name);

			if (url != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with parent classloader");

				return url;
			}
			return null;
		}
	}

	/**
	 * Current class loader
	 * 
	 */
	class CurrentLoader extends ProxyClassLoader {
		private final Logger logger = Logger.getLogger(CurrentLoader.class.getName());

		public CurrentLoader() {
			order = 20;
			enabled = Configuration.isCurrentLoaderEnabled();
		}

		@Override
		public Class loadClass(String className, boolean resolveIt) {
			Class result;

			try {
				result = getClass().getClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isLoggable(Level.FINEST))
				logger.finest("Returning class " + className + " loaded with current classloader");

			return result;
		}

		@Override
		public InputStream loadResource(String name) {
			InputStream is = getClass().getClassLoader().getResourceAsStream(name);

			if (is != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with current classloader");

				return is;
			}

			return null;
		}


		@Override
		public URL findResource(String name) {
			URL url = getClass().getClassLoader().getResource(name);

			if (url != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with current classloader");

				return url;
			}

			return null;
		}
	}

	/**
	 * Current class loader
	 * 
	 */
	class ThreadContextLoader extends ProxyClassLoader {
		private final Logger logger = Logger.getLogger(ThreadContextLoader.class.getName());

		public ThreadContextLoader() {
			order = 40;
			enabled = Configuration.isThreadContextLoaderEnabled();
		}

		@Override
		public Class loadClass(String className, boolean resolveIt) {
			Class result;
			try {
				result = Thread.currentThread().getContextClassLoader().loadClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isLoggable(Level.FINEST))
				logger.finest("Returning class " + className + " loaded with thread context classloader");

			return result;
		}

		@Override
		public InputStream loadResource(String name) {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);

			if (is != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with thread context classloader");

				return is;
			}

			return null;
		}

		@Override
		public URL findResource(String name) {
			URL url = Thread.currentThread().getContextClassLoader().getResource(name);

			if (url != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest("Returning resource " + name + " loaded with thread context classloader");

				return url;
			}

			return null;
		}

	}

	/**
	 * Osgi boot loader
	 * 
	 */
	public final class OsgiBootLoader extends ProxyClassLoader {
		private final Logger logger = Logger.getLogger(OsgiBootLoader.class.getName());
		private boolean strictLoading;
		private String[] bootDelagation;

		private static final String JAVA_PACKAGE = "java.";

		public OsgiBootLoader() {
			enabled = Configuration.isOsgiBootDelegationEnabled();
			strictLoading = Configuration.isOsgiBootDelegationStrict();
			bootDelagation = Configuration.getOsgiBootDelegation();
			order = 0;
		}

		@Override
		public Class loadClass(String className, boolean resolveIt) {
			Class clazz = null;

			if (enabled && isPartOfOsgiBootDelegation(className)) {
				clazz = getParentLoader().loadClass(className, resolveIt);

				if (clazz == null && strictLoading) {
					throw new JclException(new ClassNotFoundException("JCL OSGi Boot Delegation: Class " + className + " not found."));
				}

				if (logger.isLoggable(Level.FINEST))
					logger.finest("Class " + className + " loaded via OSGi boot delegation.");
			}

			return clazz;
		}

		@Override
		public InputStream loadResource(String name) {
			InputStream is = null;

			if (enabled && isPartOfOsgiBootDelegation(name)) {
				is = getParentLoader().loadResource(name);

				if (is == null && strictLoading) {
					throw new ResourceNotFoundException("JCL OSGi Boot Delegation: Resource " + name + " not found.");
				}

				if (logger.isLoggable(Level.FINEST))
					logger.finest("Resource " + name + " loaded via OSGi boot delegation.");
			}

			return is;
		}

		@Override
		public URL findResource(String name) {
			URL url = null;

			if (enabled && isPartOfOsgiBootDelegation(name)) {
				url = getParentLoader().findResource(name);

				if (url == null && strictLoading) {
					throw new ResourceNotFoundException("JCL OSGi Boot Delegation: Resource " + name + " not found.");
				}

				if (logger.isLoggable(Level.FINEST))
					logger.finest("Resource " + name + " loaded via OSGi boot delegation.");
			}

			return url;
		}

		/**
		 * Check if the class/resource is part of OSGi boot delegation
		 * 
		 * @param resourceName
		 * @return
		 */
		private boolean isPartOfOsgiBootDelegation(String resourceName) {
			if (resourceName.startsWith(JAVA_PACKAGE))
				return true;

			String[] bootPkgs = bootDelagation;

			if (bootPkgs != null) {
				for (String bc : bootPkgs) {
					Pattern pat = Pattern.compile(Utils.wildcardToRegex(bc), Pattern.CASE_INSENSITIVE);

					Matcher matcher = pat.matcher(resourceName);
					if (matcher.find()) {
						return true;
					}
				}
			}

			return false;
		}

		public boolean isStrictLoading() {
			return strictLoading;
		}

		public void setStrictLoading(boolean strictLoading) {
			this.strictLoading = strictLoading;
		}

		public String[] getBootDelagation() {
			return bootDelagation;
		}

		public void setBootDelagation(String[] bootDelagation) {
			this.bootDelagation = bootDelagation;
		}
	}

	public ProxyClassLoader getSystemLoader() {
		return systemLoader;
	}

	public ProxyClassLoader getParentLoader() {
		return parentLoader;
	}

	public ProxyClassLoader getCurrentLoader() {
		return currentLoader;
	}

	public ProxyClassLoader getThreadLoader() {
		return threadLoader;
	}

	public ProxyClassLoader getOsgiBootLoader() {
		return osgiBootLoader;
	}
}
