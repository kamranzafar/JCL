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

/**
 * General configuration using System properties
 * 
 * @author Kamran Zafar
 * 
 */
public class Configuration {

    private static final String JCL_SUPPRESS_COLLISION_EXCEPTION = "jcl.suppressCollisionException";
    private static final String JCL_SUPPRESS_MISSING_RESOURCE_EXCEPTION = "jcl.suppressMissingResourceException";
    private static final String AUTO_PROXY = "jcl.autoProxy";

    /**
     * OSGi boot delegation
     */
    private static final String OSGI_BOOT_DELEGATION = "osgi.bootdelegation";
    private static final String OSGI_BOOT_DELEGATION_STRICT = "osgi.bootdelegation.strict";
    private static final String OSGI_BOOT_DELEGATION_CLASSES = "org.osgi.framework.bootdelegation";

    public static boolean suppressCollisionException() {
        if (System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) == null)
            return true;

        return Boolean.parseBoolean( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) );
    }

    public static boolean suppressMissingResourceException() {
        if (System.getProperty( JCL_SUPPRESS_MISSING_RESOURCE_EXCEPTION ) == null)
            return true;

        return Boolean.parseBoolean( System.getProperty( JCL_SUPPRESS_MISSING_RESOURCE_EXCEPTION ) );
    }

    public static boolean autoProxy() {
        if (System.getProperty( AUTO_PROXY ) == null) {
            return false;
        }

        return Boolean.parseBoolean( System.getProperty( AUTO_PROXY ) );
    }

    @SuppressWarnings("unchecked")
    public static boolean isLoaderEnabled(Class cls) {
        if (System.getProperty( cls.getName() ) == null)
            return true;

        return Boolean.parseBoolean( System.getProperty( cls.getName() ) );
    }

    public static boolean isSystemLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.SystemLoader.class );
    }

    public static boolean isParentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.ParentLoader.class );
    }

    public static boolean isCurrentLoaderEnabled() {
        return isLoaderEnabled( AbstractClassLoader.CurrentLoader.class );
    }

    public static boolean isLocalLoaderEnabled() {
        return isLoaderEnabled( JarClassLoader.LocalLoader.class );
    }

    public static boolean isThreadContextLoaderEnabled() {
        if (System.getProperty( AbstractClassLoader.ThreadContextLoader.class.getName() ) == null)
            return false;

        return isLoaderEnabled( AbstractClassLoader.ThreadContextLoader.class );
    }

    public static boolean isOsgiBootDelegationEnabled() {
        if (System.getProperty( OSGI_BOOT_DELEGATION ) == null)
            return false;

        return Boolean.parseBoolean( System.getProperty( OSGI_BOOT_DELEGATION ) );
    }

    public static boolean isOsgiBootDelegationStrict() {
        if (System.getProperty( OSGI_BOOT_DELEGATION_STRICT ) == null)
            return true;

        return Boolean.parseBoolean( System.getProperty( OSGI_BOOT_DELEGATION_STRICT ) );
    }

    public static String[] getOsgiBootDelegation() {
        if (System.getProperty( OSGI_BOOT_DELEGATION_CLASSES ) == null)
            return null;

        return System.getProperty( OSGI_BOOT_DELEGATION_CLASSES ).split( "," );
    }
}
