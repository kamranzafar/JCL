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
import java.net.URISyntaxException;
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

	private JarClassLoader() {
		classpathResources = new ClasspathResources();
	}

	/**
	 * Loads a single resource
	 * 
	 * @param resourceName
	 * @throws IOException
	 * @throws JclException
	 */
	@Deprecated
	public JarClassLoader(String resourceName) throws IOException, JclException {
		this();
		classpathResources.loadResource(resourceName);
	}

	/**
	 * Loads multiple resources
	 * 
	 * @param resourceNames
	 * @throws IOException
	 * @throws JclException
	 */
	public JarClassLoader(String[] resourceNames) throws IOException,
			JclException {
		this();

		for (String resource : resourceNames)
			classpathResources.loadResource(resource);
	}

	/**
	 * Loads a single resource
	 * 
	 * @param jarStream
	 * @throws IOException
	 * @throws JclException
	 */
	@Deprecated
	public JarClassLoader(InputStream jarStream) throws IOException,
			JclException {
		this();
		classpathResources.loadJar(jarStream);
	}

	/**
	 * Loads multiple resources
	 * 
	 * @param jarStreams
	 * @throws IOException
	 * @throws JclException
	 */
	public JarClassLoader(InputStream[] jarStreams) throws IOException,
			JclException {
		this();

		for (InputStream stream : jarStreams)
			classpathResources.loadJar(stream);
	}

	/**
	 * Loads a single resource
	 * 
	 * @param url
	 * @throws IOException
	 * @throws JclException
	 * @throws URISyntaxException
	 */
	@Deprecated
	public JarClassLoader(URL url) throws IOException, JclException,
			URISyntaxException {
		classpathResources = new ClasspathResources();
		classpathResources.loadResource(url);
	}

	/**
	 * Loads multiple resources
	 * 
	 * @param urls
	 * @throws IOException
	 * @throws JclException
	 * @throws URISyntaxException
	 */
	public JarClassLoader(URL[] urls) throws IOException, JclException,
			URISyntaxException {
		classpathResources = new ClasspathResources();

		for (URL url : urls)
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

		return (classpathResources.getResource(className));
	}

	/**
	 * Attempts to unload class, it only unloads the locally loaded classes by
	 * JCL
	 * 
	 * @param className
	 * @throws JclException 
	 */
	public void unloadClass(String className) throws JclException {
		logger.debug("Unloading class " + className);

		if (classes.containsKey(className)) {
			logger.debug("Removing loaded class " + className);
			classes.remove(className);
			try {
				classpathResources.unload(formatClassName(className));
			} catch (ResourceNotFoundException e) {
				throw new JclException("Something is very wrong!!!" +
								"The locally loaded classes must be in synch with ClasspathResources", e);
			}
		} else {
			try {
				classpathResources.unload(formatClassName(className));
			} catch (ResourceNotFoundException e) {
				new JclException(
						"Class could not be unloaded " +
						"[Possible reason: Class belongs to the system]", e);
			}
		}
	}
}
