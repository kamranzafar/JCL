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

package org.xeustechnologies.jcl.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.xeustechnologies.jcl.JarClassLoader;

/**
 * @author Kamran Zafar
 * 
 */
public class JclBeanDefinitionParser implements BeanDefinitionParser {

    private static Logger logger = Logger.getLogger( JclBeanDefinitionParser.class );

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionHolder holder = parserContext.getDelegate().parseBeanDefinitionElement( element );

        String beanName = holder.getBeanName();

        BeanDefinition bd = holder.getBeanDefinition();
        bd.setBeanClassName( JarClassLoader.class.getName() );

        logger.info( "Registering JarClassLoader bean: " + beanName );

        parserContext.getRegistry().registerBeanDefinition( beanName, bd );

        return bd;
    }
}