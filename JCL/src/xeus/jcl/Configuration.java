/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2009  Xeus Technologies
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
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

    private static final String JCL_CLASSLOADER_SYSTEM = "jcl.classloader.system";
    private static final String JCL_CLASSLOADER_PARENT = "jcl.classloader.parent";
    private static final String JCL_CLASSLOADER_CURRENT = "jcl.classloader.current";
    private static final String JCL_CLASSLOADER_LOCAL = "jcl.classloader.local";
    private static final String JCL_SUPPRESS_COLLISION_EXCEPTION = "jcl.suppressCollisionException";

    public static boolean supressCollisionException() {
        if( System.getProperty( JCL_SUPPRESS_COLLISION_EXCEPTION ) == null )
            return true;

        return Boolean.parseBoolean( JCL_SUPPRESS_COLLISION_EXCEPTION );
    }

    public static boolean isLocalLoaderEnabled() {
        if( System.getProperty( JCL_CLASSLOADER_LOCAL ) == null )
            return true;

        return Boolean.parseBoolean( System.getProperty( JCL_CLASSLOADER_LOCAL ) );
    }

    public static boolean isCurrentLoaderEnabled() {
        if( System.getProperty( JCL_CLASSLOADER_CURRENT ) == null )
            return true;

        return Boolean.parseBoolean( JCL_CLASSLOADER_CURRENT );
    }

    public static boolean isParentLoaderEnabled() {
        if( System.getProperty( JCL_CLASSLOADER_PARENT ) == null )
            return true;

        return Boolean.parseBoolean( JCL_CLASSLOADER_PARENT );
    }

    public static boolean isSystemLoaderEnabled() {
        if( System.getProperty( JCL_CLASSLOADER_SYSTEM ) == null )
            return true;

        return Boolean.parseBoolean( JCL_CLASSLOADER_SYSTEM );
    }
}
