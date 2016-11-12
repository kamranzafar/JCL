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

package org.xeustechnologies.jcl.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final transient Logger logger = LoggerFactory.getLogger( JclBeanDefinitionParser.class );

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionHolder holder = parserContext.getDelegate().parseBeanDefinitionElement( element );

        String beanName = holder.getBeanName();

        BeanDefinition bd = holder.getBeanDefinition();
        bd.setBeanClassName( JarClassLoader.class.getName() );

        logger.info( "Registering JarClassLoader bean: {}", beanName );

        parserContext.getRegistry().registerBeanDefinition( beanName, bd );

        return bd;
    }
}
