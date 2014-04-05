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

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Kamran Zafar
 * 
 */
public abstract class ProxyClassLoader implements Comparable<ProxyClassLoader> {
    // Default order
    protected int order = 5;
    // Enabled by default
    protected boolean enabled = true;

    public int getOrder() {
        return order;
    }

    /**
     * Set loading order
     * 
     * @param order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Loads the class
     * 
     * @param className
     * @param resolveIt
     * @return class
     */
    public abstract Class loadClass(String className, boolean resolveIt);

    /**
     * Loads the resource
     * 
     * @param name
     * @return InputStream
     */
    public abstract InputStream loadResource(String name);

    /**
     * Finds the resource
     *
     * @param name
     * @return InputStream
     */
    public abstract URL findResource(String name);

    public  Enumeration<URL> findResources(String name){
        Vector<URL> v = new Vector<URL>();
        URL r = findResource(name);
        if(r!=null){
             v.add(r);
        }
        return v.elements();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int compareTo(ProxyClassLoader o) {
        return order - o.getOrder();
    }
}
