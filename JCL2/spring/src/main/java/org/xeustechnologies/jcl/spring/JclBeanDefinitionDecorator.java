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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    private static final String JCL_FACTORY = "jcl-factory-" + UUID.randomUUID();
    private static final String JCL_FACTORY_METHOD = "create";
    private static final String JCL_FACTORY_CONSTRUCTOR = "getInstance";

    public BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder holder, ParserContext parserContext) {
        String jclRef = node.getAttributes().getNamedItem( JCL_REF ).getNodeValue();

        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setFactoryBeanName( JCL_FACTORY );
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

        if( !parserContext.getRegistry().containsBeanDefinition( JCL_FACTORY ) ) {
            BeanDefinitionBuilder initializer = BeanDefinitionBuilder.rootBeanDefinition( JclObjectFactory.class,
                    JCL_FACTORY_CONSTRUCTOR );
            parserContext.getRegistry().registerBeanDefinition( JCL_FACTORY, initializer.getBeanDefinition() );
        }

        if( parserContext.getRegistry().containsBeanDefinition( jclRef ) ) {
            String[] dependsOn = definition.getDependsOn();
            if( dependsOn == null ) {
                dependsOn = new String[] { jclRef, JCL_FACTORY };
            } else {
                List dependencies = new ArrayList( Arrays.asList( dependsOn ) );
                dependencies.add( jclRef );
                dependencies.add( JCL_FACTORY );
                dependsOn = (String[]) dependencies.toArray( new String[0] );
            }
            definition.setDependsOn( dependsOn );
        } else
            throw new JclException( "JCL Bean definition " + jclRef + "not found" );
    }
}
