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
