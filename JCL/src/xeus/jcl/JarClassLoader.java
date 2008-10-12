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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import xeus.jcl.exception.JclException;
import xeus.jcl.exception.ResourceNotFoundException;

/**
 * Reads the class bytes from jar files and other resources using
 * ClasspathResources
 * 
 * @author Kamran Zafar
 * 
 */
public class JarClassLoader extends AbstractClassLoader {
	private ClasspathResources classpathResources;
	private static Logger logger = Logger.getLogger(JarClassLoader.class);

	public JarClassLoader() {
		classpathResources = new ClasspathResources();
	}

	/**
	 * Loads classes from different sources
	 * 
	 * @param resources
	 * @throws IOException
	 * @throws JclException
	 */
	public JarClassLoader(Object[] resources) throws IOException, JclException {
		this();

		for (Object resource : resources) {
			if (resource instanceof InputStream)
				add((InputStream) resource);
			else if (resource instanceof URL)
				add((URL) resource);
			else if (resource instanceof String)
				add((String) resource);
			else
				throw new JclException("Unknow Resource type");
		}
	}

	/**
	 * Loads local/remote resource
	 * 
	 * @param resourceName
	 * @throws IOException
	 * @throws JclException
	 */
	public void add(String resourceName) throws IOException, JclException {
		classpathResources.loadResource(resourceName);
	}

	/**
	 * Loads resource from InputStream
	 * 
	 * @param jarStream
	 * @throws IOException
	 * @throws JclException
	 */
	public void add(InputStream jarStream) throws IOException, JclException {
		classpathResources.loadJar(jarStream);
	}

	/**
	 * Loads local/remote resource
	 * 
	 * @param url
	 * @throws IOException
	 * @throws JclException
	 */
	public void add(URL url) throws IOException, JclException {
		classpathResources.loadResource(url);
	}

	/**
	 * Reads the class bytes from different local and remote resources using
	 * ClasspathResources
	 * 
	 * @see xeus.jcl.AbstractClassLoader#loadClassBytes(java.lang.String)
	 */
	@Override
	protected byte[] loadClassBytes(String className) {
		className = formatClassName(className);

		return classpathResources.getResource(className);
	}

	/**
	 * Attempts to unload class, it only unloads the locally loaded classes by
	 * JCL
	 * 
	 * @param className
	 * @throws JclException
	 */
	public void unloadClass(String className) throws JclException {
		if (logger.isTraceEnabled())
			logger.trace("Unloading class " + className);

		if (classes.containsKey(className)) {
			if (logger.isTraceEnabled())
				logger.trace("Removing loaded class " + className);
			classes.remove(className);
			try {
				classpathResources.unload(formatClassName(className));
			} catch (ResourceNotFoundException e) {
				throw new JclException("Something is very wrong!!!"
						+ "The locally loaded classes must be in synch with ClasspathResources", e);
			}
		} else {
			try {
				classpathResources.unload(formatClassName(className));
			} catch (ResourceNotFoundException e) {
				throw new JclException("Class could not be unloaded "
						+ "[Possible reason: Class belongs to the system]", e);
			}
		}
	}
}
