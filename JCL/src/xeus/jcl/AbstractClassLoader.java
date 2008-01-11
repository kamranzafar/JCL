/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2006  Xeus Technologies
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Abstract class loader that can load classes from different resources 
 * 
 * @author Kamran Zafar
 * 
 */
public abstract class AbstractClassLoader extends ClassLoader {

	private Map classes;
	private char classNameReplacementChar;
	static Logger logger = Logger.getLogger(AbstractClassLoader.class);

	/**
	 * No arguments constructor
	 */
	public AbstractClassLoader() {
		classes = Collections.synchronizedMap(new HashMap());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String)
	 */
	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	/**
	 * Overrid the loadClass method to load classes from other resources, 
	 * JarClassLoader is the only subclass in this project that loads classes from jar files
	 * 
	 * @see java.lang.ClassLoader#loadClass(java.lang.String, boolean)
	 */
	public synchronized Class loadClass(String className, boolean resolveIt)
			throws ClassNotFoundException {

		Class result;
		byte[] classBytes;
		logger.debug("Loading class: " + className + ", " + resolveIt + "");

		result = (Class) classes.get(className);
		if (result != null) {
			logger.debug("Returning local loaded class");
			return result;
		}

		try {
			result = findSystemClass(className);
			logger.debug("Returning system class");
			return result;
		} catch (ClassNotFoundException e) {
			logger.debug(e);
		}

		classBytes = loadClassBytes(className);
		if (classBytes == null) {
			throw new ClassNotFoundException();
		}

		result = defineClass(className, classBytes, 0, classBytes.length);
		if (result == null) {
			throw new ClassFormatError();
		}

		if (resolveIt)
			resolveClass(result);

		classes.put(className, result);
		logger.debug("Return newly loaded class");
		return result;
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
	 * Abstarct method that allows class content to be loaded from other
	 * sources
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
}
