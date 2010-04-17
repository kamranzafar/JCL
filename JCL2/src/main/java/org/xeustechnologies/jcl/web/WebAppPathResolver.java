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

package org.xeustechnologies.jcl.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.xeustechnologies.jcl.utils.PathResolver;

/**
 * Resolves path to jar files and folder in a web application. The path must
 * starts with <b>webapp:</b>
 * 
 * @author Kamran
 * 
 */
public class WebAppPathResolver implements PathResolver {

    private static Logger logger = Logger.getLogger( WebAppPathResolver.class );

    private static final String JAR = ".jar";
    private static final String WEB_APP = "webapp:";
    private final ServletContext servletContext;

    public WebAppPathResolver(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * Resolves path to jar files and folder in a web application
     * 
     * @see org.xeustechnologies.jcl.utils.PathResolver#resolvePath(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Object[] resolvePath(String path) {
        if( path.startsWith( WEB_APP ) ) {
            String webpath = "/" + path.split( ":" )[1];

            if( isJar( webpath ) ) {
                if( logger.isTraceEnabled() ) {
                    logger.trace( "Found jar: " + webpath );
                }

                return new InputStream[] { servletContext.getResourceAsStream( webpath ) };
            }

            Set<String> paths = servletContext.getResourcePaths( webpath );

            if( paths.size() > 0 ) {
                Iterator<String> itr = paths.iterator();
                List<InputStream> streams = new ArrayList<InputStream>();

                while (itr.hasNext()) {
                    String source = itr.next();

                    if( isJar( source ) ) {
                        InputStream stream = servletContext.getResourceAsStream( source );

                        if( stream != null ) {
                            if( logger.isTraceEnabled() ) {
                                logger.trace( "Found jar: " + source );
                            }

                            streams.add( stream );
                        }
                    }
                }

                return streams.toArray( new InputStream[streams.size()] );
            }

        }

        return null;
    }

    private boolean isJar(String path) {
        return path.toLowerCase().endsWith( JAR );
    }
}