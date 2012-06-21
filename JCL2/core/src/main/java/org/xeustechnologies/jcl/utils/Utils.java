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

package org.xeustechnologies.jcl.utils;

/**
 * Class that contains utility methods
 * 
 * @author Kamran Zafar
 * 
 */
public class Utils {
    /**
     * Converts wildcard to regular expression
     * 
     * @param wildcard
     * @return regex
     */
    public static String wildcardToRegex(String wildcard) {
        StringBuffer s = new StringBuffer( wildcard.length() );
        s.append( '^' );
        for( int i = 0, is = wildcard.length(); i < is; i++ ) {
            char c = wildcard.charAt( i );
            switch (c) {
            case '*':
                s.append( ".*" );
                break;
            case '?':
                s.append( "." );
                break;
            case '(':
            case ')':
            case '[':
            case ']':
            case '$':
            case '^':
            case '.':
            case '{':
            case '}':
            case '|':
            case '\\':
                s.append( "\\" );
                s.append( c );
                break;
            default:
                s.append( c );
                break;
            }
        }
        s.append( '$' );
        return ( s.toString() );
    }
}
