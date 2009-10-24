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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Node;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.exception.JclException;

/**
 * @author Kamran Zafar
 * 
 */
public class JclBeanDefinitionDecorator implements BeanDefinitionDecorator {

    private static final String JCL_REF = "ref";
    private static final String JCL_FACTORY_METHOD = "create";
    private static final String JCL_FACTORY_PREFIX = "-factory";
    private static final String JCL_FACTORY_CONSTRUCTOR = "getInstance";

    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder holder, ParserContext parserContext) {
        String jclRef = node.getAttributes().getNamedItem( JCL_REF ).getNodeValue();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setFactoryBeanName( jclRef + JCL_FACTORY_PREFIX );
        bd.setFactoryMethodName( JCL_FACTORY_METHOD );
        bd.setConstructorArgumentValues( holder.getBeanDefinition().getConstructorArgumentValues() );
        bd.setPropertyValues( holder.getBeanDefinition().getPropertyValues() );
        bd.getConstructorArgumentValues().addIndexedArgumentValue( 0,
                new ConstructorArgumentValues.ValueHolder( parserContext.getRegistry().getBeanDefinition( jclRef ) ) );
        bd.getConstructorArgumentValues().addIndexedArgumentValue( 1,
                new ConstructorArgumentValues.ValueHolder( holder.getBeanDefinition().getBeanClassName() ) );

        BeanDefinitionHolder newHolder = new BeanDefinitionHolder( bd, holder.getBeanName() );

        createDependencyOnJcl( node, newHolder, parserContext );

        return newHolder;
    }

    @SuppressWarnings("unchecked")
    private void createDependencyOnJcl(Node node, BeanDefinitionHolder holder, ParserContext parserContext) {
        AbstractBeanDefinition definition = ( (AbstractBeanDefinition) holder.getBeanDefinition() );
        String jclRef = node.getAttributes().getNamedItem( JCL_REF ).getNodeValue();
        String jclFactory = jclRef + JCL_FACTORY_PREFIX;

        if( !parserContext.getRegistry().containsBeanDefinition( jclFactory ) ) {
            BeanDefinitionBuilder initializer = BeanDefinitionBuilder.rootBeanDefinition( JclObjectFactory.class,
                    JCL_FACTORY_CONSTRUCTOR );
            parserContext.getRegistry().registerBeanDefinition( jclFactory, initializer.getBeanDefinition() );
        }

        if( parserContext.getRegistry().containsBeanDefinition( jclRef ) ) {
            String[] dependsOn = definition.getDependsOn();
            if( dependsOn == null ) {
                dependsOn = new String[] { jclRef, jclFactory };
            } else {
                List dependencies = new ArrayList( Arrays.asList( dependsOn ) );
                dependencies.add( jclRef );
                dependencies.add( jclFactory );
                dependsOn = (String[]) dependencies.toArray( new String[0] );
            }
            definition.setDependsOn( dependsOn );
        } else
            throw new JclException( "JCL Bean definition " + jclRef + "not found" );
    }
}
