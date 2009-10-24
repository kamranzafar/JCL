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

package org.xeustechnologies.jcl.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Kamran Zafar
 * 
 */
public class JclNamespaceHandler extends NamespaceHandlerSupport {
    private static final String JCL_ELEMENT = "jcl";
    private static final String JCL_REF_ELEMENT = "jcl-ref";

    public void init() {
        registerBeanDefinitionParser( JCL_ELEMENT, new JclBeanDefinitionParser() );
        registerBeanDefinitionParser( "jcl-bean", new JclBeanDefinitionParser() );
        registerBeanDefinitionDecorator( JCL_REF_ELEMENT, new JclBeanDefinitionDecorator() );
    }

}
