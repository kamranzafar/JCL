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
