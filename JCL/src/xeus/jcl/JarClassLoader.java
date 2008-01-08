/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2006  Xeus Technologies
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Reads the class bytes from jar files using JarResources
 * 
 * @author Kamran Zafar
 * 
 */
public class JarClassLoader extends AbstractClassLoader {
	private JarResources jarResources;

	/**
	 * @param jarName
	 * @throws IOException
	 */
	public JarClassLoader(String jarName) throws IOException {
		jarResources = new JarResources();
		jarResources.loadJar(jarName);
	}
	
	/**
	 * @param jarStream
	 * @throws IOException
	 */
	public JarClassLoader(InputStream jarStream) throws IOException{
        jarResources = new JarResources();
        jarResources.loadJar(jarStream);	    
	}

	/**
     * @param url
     * @throws IOException
     */
    public JarClassLoader(URL url) throws IOException{
        jarResources = new JarResources();
        jarResources.loadJar(url);        
    }
	
	/**
	 * Reads the class bytes from jar files using JarResources
	 * 
	 * @see xeus.jcl.AbstractClassLoader#loadClassBytes(java.lang.String)
	 */
	protected byte[] loadClassBytes(String className) {
		className = formatClassName(className);

		return (jarResources.getResource(className));
	}
}
