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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.xeustechnologies.jcl.JarClassLoader;

/**
 * This class is builds the context from a single JCL instance. This should be
 * used if a single JarClassLoader is instantiated programmatically.
 * 
 * @author Kamran
 * 
 */
public class DefaultContextLoader implements JclContextLoader {
    private final JclContext jclContext;
    private final JarClassLoader jcl;

    private static Logger logger = Logger.getLogger( DefaultContextLoader.class.getName() );

    public DefaultContextLoader(JarClassLoader jcl) {
        jclContext = new JclContext();
        this.jcl = jcl;
    }

    /**
     * Loads a single JCL instance in context
     * 
     * @see org.xeustechnologies.jcl.context.JclContextLoader#loadContext()
     */ 
    public void loadContext() {
        jclContext.addJcl( JclContext.DEFAULT_NAME, jcl );

        if (logger.isLoggable( Level.FINER ))
            logger.finer( "Default JarClassLoader loaded into context." );
    }

    public void unloadContext() {
        JclContext.destroy();
    }
}
