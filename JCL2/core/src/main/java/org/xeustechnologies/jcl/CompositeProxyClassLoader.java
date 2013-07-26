/**
 *        Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 *
 */
package org.xeustechnologies.jcl;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * CompositeProxyClassLoader implement a composite of delegate class loader.
 * 
 * @author <a href="mailto:jguibert@intelligents-ia.com" >Jerome Guibert</a>
 * 
 */
public class CompositeProxyClassLoader extends ProxyClassLoader {
	private final List<ProxyClassLoader> proxyClassLoaders = new ArrayList<ProxyClassLoader>();

	/**
	 * Build a new instance of CompositeProxyClassLoader.java.
	 */
	public CompositeProxyClassLoader() {
		super();
	}

	@SuppressWarnings("rawtypes") 
	public Class loadClass(String className, boolean resolveIt) {
		Class result = null;
		Iterator<ProxyClassLoader> iterator = proxyClassLoaders.iterator();
		while (result == null && iterator.hasNext()) {
			result = iterator.next().loadClass(className, resolveIt);
		}
		return result;
	}
 
	public InputStream loadResource(String name) {
		InputStream result = null;
		Iterator<ProxyClassLoader> iterator = proxyClassLoaders.iterator();
		while (result == null && iterator.hasNext()) {
			result = iterator.next().loadResource(name);
		}
		return result;
	}

	@Override
	public URL findResource(String name) {
		URL result = null;
		Iterator<ProxyClassLoader> iterator = proxyClassLoaders.iterator();
		while (result == null && iterator.hasNext()) {
			result = iterator.next().findResource(name);
		}
		return result;
	}

	/**
	 * @return
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return proxyClassLoaders.isEmpty();
	}

	public boolean contains(Object o) {
		return proxyClassLoaders.contains(o);
	}

	public boolean add(ProxyClassLoader e) {
		return proxyClassLoaders.add(e);
	}

	public boolean remove(ProxyClassLoader o) {
		return proxyClassLoaders.remove(o);
	}

	public boolean addAll(Collection<? extends ProxyClassLoader> c) {
		return proxyClassLoaders.addAll(c);
	}

	public List<ProxyClassLoader> getProxyClassLoaders() {
		return proxyClassLoaders;
	}
}
