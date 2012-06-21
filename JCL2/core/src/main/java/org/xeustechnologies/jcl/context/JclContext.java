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

package org.xeustechnologies.jcl.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.exception.JclContextException;

/**
 * JclContext holds all the JarClassLoader instances so that they can be
 * accessed from anywhere in the application.
 * 
 * @author Kamran
 * 
 */
public class JclContext {
    private static final Map<String, JarClassLoader> loaders = Collections
            .synchronizedMap( new HashMap<String, JarClassLoader>() );
    public static final String DEFAULT_NAME = "jcl";

    public JclContext() {
        validate();
    }

    private void validate() {
        if( isLoaded() ) {
            throw new JclContextException( "Context already loaded. Destroy the existing context to create a new one." );
        }
    }

    public static boolean isLoaded() {
        return !loaders.isEmpty();
    }

    /**
     * Populates the context with JarClassLoader instances
     * 
     * @param name
     * @param jcl
     */
    public void addJcl(String name, JarClassLoader jcl) {
        if( loaders.containsKey( name ) )
            throw new JclContextException( "JarClassLoader[" + name + "] already exist. Name must be unique" );

        loaders.put( name, jcl );
    }

    /**
     * Clears the context
     */
    public static void destroy() {
        if( isLoaded() ) {
            loaders.clear();
        }
    }

    public static JarClassLoader get() {
        return loaders.get( DEFAULT_NAME );
    }

    public static JarClassLoader get(String name) {
        return loaders.get( name );
    }

    public static Map<String, JarClassLoader> getAll() {
        return Collections.unmodifiableMap( loaders );
    }
}
