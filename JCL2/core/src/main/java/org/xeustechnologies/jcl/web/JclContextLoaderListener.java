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
