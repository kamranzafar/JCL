/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2009  Xeus Technologies
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

package xeus.jcl;

/**
 * General configuration
 * 
 * @author Kamran Zafar
 * 
 */
public class Configuration {

    private static final String JCL_SUPPRESS_COLLISION_EXCEPTION = "jcl.suppressCollisionException";

    public static boolean supressCollisionException() {
        if( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) == null )
            return true;

        return Boolean.parseBoolean( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) );
    }

    /**
     * Check to see if Loader is enabled/disabled The Loader can be
     * enabled/disabled programmatically or by passing the class names as
     * argument e.g. <br>
     * -Dxeus.jcl.AbstractClassLoader.ParentLoader=false
     * 
     * @param clazz
     * @return
     */
    public static boolean isLoaderEnabled(Class clazz) {
        if( System.getProperty( clazz.getName() ) == null )
            return true;

        return Boolean.parseBoolean( System.getProperty( clazz.getName() ) );
    }

    public static boolean isLocalLoaderEnabled() {
        return isLoaderEnabled( JarClassLoader.LocalLoader.class );
    }

    public static boolean isCurrentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.CurrentLoader.class );
    }

    public static boolean isParentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.ParentLoader.class );
    }

    public static boolean isSystemLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.SystemLoader.class );
    }
}
