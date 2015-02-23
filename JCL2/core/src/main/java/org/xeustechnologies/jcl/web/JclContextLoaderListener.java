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
package org.xeustechnologies.jcl.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.xeustechnologies.jcl.context.XmlContextLoader;

/**
 * This class is used in web applications to load the JCL context from XML file.
 * 
 * @author Kamran
 * 
 */
public class JclContextLoaderListener implements ServletContextListener {
    private static final String JCL_CONTEXT = "jcl-context";
    protected XmlContextLoader contextLoader;

    /**
     * Destroys the context
     * 
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        contextLoader.unloadContext();
    }

    /**
     * The context is initialised from xml on web application's deploy-time
     * 
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        String jclConfig = sce.getServletContext().getInitParameter( JCL_CONTEXT );

        contextLoader = new XmlContextLoader( jclConfig );
        contextLoader.addPathResolver( new WebAppPathResolver( sce.getServletContext() ) );
        contextLoader.loadContext();
    }
}
