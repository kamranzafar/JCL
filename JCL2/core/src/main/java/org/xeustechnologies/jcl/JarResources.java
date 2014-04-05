/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2011  Kamran Zafar
 *
 *  This file is part of Jar Class Loader (JCL).
 *  Jar Class Loader (JCL) is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JarClassLoader is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with JCL.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  @author Kamran Zafar
 *
 *  Contact Info:
 *  Email:  xeus.man@gmail.com
 *  Web:    http://xeustech.blogspot.com
 */

package org.xeustechnologies.jcl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xeustechnologies.jcl.exception.JclException;

/**
 * JarResources reads jar files and loads the class content/bytes in a HashMap
 * 
 * @author Kamran Zafar
 * 
 */
public class JarResources {

	protected String baseUrl;
	protected Map<String, JarResource> jarEntryContents;
	protected boolean collisionAllowed;

	private static Logger logger = Logger.getLogger(JarResources.class.getName());

	/**
	 * Default constructor
	 */
	public JarResources() {
		jarEntryContents = new HashMap<String, JarResource>();
		collisionAllowed = Configuration.suppressCollisionException();
	}

	/**
	 * @param name
	 * @return URL
	 */
	public URL getResourceURL(String name) {
		if (baseUrl == null) {
			throw new JclException("non-URL accessible resource");
		}
		if (jarEntryContents.get(name) != null) {
			try {
				return new URL(baseUrl.toString() + name);
			} catch (MalformedURLException e) {
				throw new JclException(e);
			}
		}

		return null;
	}

	/**
	 * @param name
	 * @return byte[]
	 */
	public JarResource getResource(String name) {
		return jarEntryContents.get(name);
	}

	/**
	 * Returns an immutable Map of all jar resources
	 * 
	 * @return Map
	 */
	public Map<String, JarResource> getResources() {
		return Collections.unmodifiableMap(jarEntryContents);
	}

	/**
	 * Reads the specified jar file
	 * 
	 * @param jarFile
	 */
	public void loadJar(String jarFile) {
		if (logger.isLoggable(Level.FINEST))
			logger.finest("Loading jar: " + jarFile);

		FileInputStream fis = null;
		try {
			File file = new File(jarFile);
			baseUrl = "jar:" + file.toURI().toString() + "!/";
			fis = new FileInputStream(file);
			loadJar(fis, getProtectionDomain(file.toURI().toURL()));
		} catch (IOException e) {
			baseUrl = null;
			throw new JclException(e);
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					throw new JclException(e);
				}
		}
	}

	/**
	 * Reads the jar file from a specified URL
	 * 
	 * @param url
	 */
	public void loadJar(URL url) {
		if (logger.isLoggable(Level.FINEST))
			logger.finest("Loading jar: " + url.toString());

		InputStream in = null;
		try {
			baseUrl = "jar:" + url.toString() + "!/";
			in = url.openStream();
			loadJar(in, getProtectionDomain(url));
		} catch (IOException e) {
			baseUrl = null;
			throw new JclException(e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					throw new JclException(e);
				}
		}
	}

	/**
	 * Loads protection domain for specified jar. The domain is not loaded from the jar itself but it is derived from parent protection domain.
	 * 
	 * @param aURL jar url
	 * @return
	 */
	protected ProtectionDomain getProtectionDomain(URL aURL) {
		ProtectionDomain parentDomain = getClass().getProtectionDomain();
		CodeSource csParent = parentDomain.getCodeSource();
		Certificate[] certParent = csParent.getCertificates();
		CodeSource csChild = (certParent == null ? new CodeSource(aURL, csParent.getCodeSigners()) : new CodeSource(aURL, certParent));
		ProtectionDomain pdChild = new ProtectionDomain(csChild, parentDomain.getPermissions(), parentDomain.getClassLoader(), parentDomain.getPrincipals());
		return pdChild;
	}

	public void loadJar(InputStream jarStream) {
		loadJar(jarStream, null);
	}

	/**
	 * Load the jar contents from InputStream
	 * 
	 */
	private void loadJar(InputStream jarStream, ProtectionDomain protectionDomain) {

		BufferedInputStream bis = null;
		JarInputStream jis = null;

		try {
			bis = new BufferedInputStream(jarStream);
			jis = new JarInputStream(bis);

			JarEntry jarEntry = null;
			while ((jarEntry = jis.getNextJarEntry()) != null) {
				if (logger.isLoggable(Level.FINEST))
					logger.finest(dump(jarEntry));

				if (jarEntry.isDirectory()) {
					continue;
				}

				if (jarEntryContents.containsKey(jarEntry.getName())) {
					if (!collisionAllowed)
						throw new JclException("Class/Resource " + jarEntry.getName() + " already loaded");
					else {
						if (logger.isLoggable(Level.FINEST))
							logger.finest("Class/Resource " + jarEntry.getName() + " already loaded; ignoring entry...");
						continue;
					}
				}

				if (logger.isLoggable(Level.FINEST))
					logger.finest("Entry Name: " + jarEntry.getName() + ", " + "Entry Size: " + jarEntry.getSize());

				byte[] b = new byte[2048];
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int len = 0;
				while ((len = jis.read(b)) > 0) {
					out.write(b, 0, len);
				}

				// add to internal resource HashMap
				jarEntryContents.put(jarEntry.getName(), new JarResource(out.toByteArray(), protectionDomain));

				if (logger.isLoggable(Level.FINEST))
					logger.finest(jarEntry.getName() + ": size=" + out.size() + " ,csize=" + jarEntry.getCompressedSize());

				out.close();
			}
		} catch (IOException e) {
			throw new JclException(e);
		} catch (NullPointerException e) {
			if (logger.isLoggable(Level.FINEST))
				logger.finest("Done loading.");
		} finally {
			if (jis != null)
				try {
					jis.close();
				} catch (IOException e) {
					throw new JclException(e);
				}

			if (bis != null)
				try {
					bis.close();
				} catch (IOException e) {
					throw new JclException(e);
				}
		}
	}

	/**
	 * For debugging
	 * 
	 * @param je
	 * @return String
	 */
	private String dump(JarEntry je) {
		StringBuffer sb = new StringBuffer();
		if (je.isDirectory()) {
			sb.append("d ");
		} else {
			sb.append("f ");
		}

		if (je.getMethod() == JarEntry.STORED) {
			sb.append("stored   ");
		} else {
			sb.append("defalted ");
		}

		sb.append(je.getName());
		sb.append("\t");
		sb.append("" + je.getSize());
		if (je.getMethod() == JarEntry.DEFLATED) {
			sb.append("/" + je.getCompressedSize());
		}

		return (sb.toString());
	}

}
