/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2008  Xeus Technologies
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *  @author Kamran Zafar    
 *
 *  Contact Info:
 *  Email:  xeus.man@gmail.com
 *  Web:    http://xeustech.blogspot.com
 */

package xeus.jcl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import xeus.jcl.loader.Loader;

/**
 * Abstract class loader that can load classes from different resources
 * 
 * @author Kamran Zafar
 * 
 */
@SuppressWarnings("unchecked")
public abstract class AbstractClassLoader extends ClassLoader {

	protected Map<String, Class> classes;
	private char classNameReplacementChar;
	private final List<Loader> loaders = new ArrayList<Loader>();

	private Loader systemLoader = new SystemLoader();
	private Loader parentLoader = new ParentLoader();
	private Loader localLoader = new LocalLoader();

	/**
	 * No arguments constructor
	 */
	public AbstractClassLoader() {
		classes = Collections.synchronizedMap(new HashMap<String, Class>());
		loaders.add(systemLoader);
		loaders.add(parentLoader);
		loaders.add(localLoader);
	}

	public void addLoader(Loader loader) {
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
	 * Overrid the loadClass method to load classes from other resources,
	 * JarClassLoader is the only subclass in this project that loads classes
	 * from jar files
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	@Override
	public Class loadClass(String className, boolean resolveIt) throws ClassNotFoundException {
		Collections.sort(loaders);
		Class clazz = null;
		for (Loader l : loaders) {
			clazz = l.load(className, resolveIt);
			if (clazz != null)
				break;
		}

		if (clazz == null)
			throw new ClassNotFoundException(className);

		return clazz;
	}

	/**
	 * @param replacement
	 */
	public void setClassNameReplacementChar(char replacement) {
		classNameReplacementChar = replacement;
	}

	/**
	 * @return char
	 */
	public char getClassNameReplacementChar() {
		return classNameReplacementChar;
	}

	/**
	 * Abstarct method that allows class content to be loaded from other sources
	 * 
	 * @param className
	 * @return byte[]
	 */
	protected abstract byte[] loadClassBytes(String className);

	/**
	 * @param className
	 * @return String
	 */
	protected String formatClassName(String className) {
		if (classNameReplacementChar == '\u0000') {
			// '/' is used to map the package to the path
			return className.replace('.', '/') + ".class";
		} else {
			// Replace '.' with custom char, such as '_'
			return className.replace('.', classNameReplacementChar) + ".class";
		}
	}

	/**
	 * Local class loader
	 * 
	 */
	class LocalLoader extends Loader {

		private Logger logger = Logger.getLogger(LocalLoader.class);

		public LocalLoader() {
			order = 1;
		}

		public Class load(String className, boolean resolveIt) {
			Class result = null;
			byte[] classBytes;
			if (logger.isTraceEnabled())
				logger.trace("Loading class: " + className + ", " + resolveIt + "");

			result = classes.get(className);
			if (result != null) {
				if (logger.isTraceEnabled())
					logger.trace("Returning local loaded class " + className);
				return result;
			}

			classBytes = loadClassBytes(className);
			if (classBytes == null) {
				return null;
			}

			result = defineClass(className, classBytes, 0, classBytes.length);

			if (result == null) {
				return null;
			}

			if (resolveIt)
				resolveClass(result);

			classes.put(className, result);
			if (logger.isTraceEnabled())
				logger.trace("Return newly loaded class " + className);
			return result;
		}
	}

	/**
	 * System class loader
	 * 
	 */
	class SystemLoader extends Loader {

		private Logger logger = Logger.getLogger(SystemLoader.class);

		public SystemLoader() {
			order = 3;
		}

		public Class load(String className, boolean resolveIt) {
			Class result;
			try {
				result = findSystemClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isTraceEnabled())
				logger.trace("Returning system class " + className);

			return result;
		}

	}

	/**
	 * Parent class loader
	 * 
	 */
	class ParentLoader extends Loader {
		private Logger logger = Logger.getLogger(ParentLoader.class);

		public ParentLoader() {
			order = 2;
		}

		public Class load(String className, boolean resolveIt) {
			Class result;
			try {
				result = this.getClass().getClassLoader().getParent().loadClass(className);
			} catch (ClassNotFoundException e) {
				return null;
			}

			if (logger.isTraceEnabled())
				logger.trace("Returning class " + className + " loaded with parent classloader");

			return result;
		}

	}

	public Loader getSystemLoader() {
		return systemLoader;
	}

	public Loader getParentLoader() {
		return parentLoader;
	}

	public Loader getLocalLoader() {
		return localLoader;
	}
}
