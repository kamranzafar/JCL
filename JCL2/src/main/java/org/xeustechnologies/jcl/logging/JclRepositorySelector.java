/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2010  Xeus Technologies
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

package org.xeustechnologies.jcl.logging;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Kamran Zafar
 * 
 */
public class JclRepositorySelector implements RepositorySelector {
    private static boolean initialized = false;
    private static Object guard = LogManager.getRootLogger();
    private static Map<ClassLoader, LoggerRepository> repositories = new HashMap<ClassLoader, LoggerRepository>();
    private static LoggerRepository defaultRepository;
    private static final String LOG4JXML = "org/xeustechnologies/jcl/logging/log4j.xml";

    public static synchronized void init() {
        if( !initialized ) {
            defaultRepository = LogManager.getLoggerRepository();
            RepositorySelector theSelector = new JclRepositorySelector();
            LogManager.setRepositorySelector( theSelector, guard );
            initialized = true;
        }

        Hierarchy hierarchy = new Hierarchy( new RootLogger( Level.DEBUG ) );
        loadLog4JConfig( hierarchy );
        repositories.put( JclRepositorySelector.class.getClassLoader(), hierarchy );
    }

    public static synchronized void removeFromRepository() {
        repositories.remove( JclRepositorySelector.class.getClassLoader() );
    }

    private static void loadLog4JConfig(Hierarchy hierarchy) {
        InputStream log4JConfig = JclRepositorySelector.class.getClassLoader().getResourceAsStream( LOG4JXML );
        DOMConfigurator conf = new DOMConfigurator();
        conf.doConfigure( log4JConfig, hierarchy );
    }

    private JclRepositorySelector() {
    }

    public LoggerRepository getLoggerRepository() {
        LoggerRepository repository = repositories.get( JclRepositorySelector.class.getClassLoader() );
        if( repository == null ) {
            return defaultRepository;
        } else {
            return repository;
        }
    }
}