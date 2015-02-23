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
