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

package org.xeustechnologies.jcl.exception;

import org.xeustechnologies.jcl.ResourceType;

/**
 * @author Kamran Zafar
 * 
 */
public class ResourceNotFoundException extends JclException {
    /**
     * Default serial id
     */
    private static final long serialVersionUID = 1L;

    private String resourceName;
    private ResourceType resourceType;

    /**
     * Default constructor
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     * @param message
     */
    public ResourceNotFoundException(String message) {
        super( message );
    }

    /**
     * @param resource
     * @param message
     */
    public ResourceNotFoundException(String resource, String message) {
        super( message );
        resourceName = resource;
        determineResourceType( resource );
    }

    /**
     * @param e
     * @param resource
     * @param message
     */
    public ResourceNotFoundException(Throwable e, String resource, String message) {
        super( message, e );
        resourceName = resource;
        determineResourceType( resource );
    }

    /**
     * @param resourceName
     */
    private void determineResourceType(String resourceName) {
        if( resourceName.toLowerCase().endsWith( "." + ResourceType.CLASS.name().toLowerCase() ) )
            resourceType = ResourceType.CLASS;
        else if( resourceName.toLowerCase().endsWith( "." + ResourceType.PROPERTIES.name().toLowerCase() ) )
            resourceType = ResourceType.PROPERTIES;
        else if( resourceName.toLowerCase().endsWith( "." + ResourceType.XML.name().toLowerCase() ) )
            resourceType = ResourceType.XML;
        else
            resourceType = ResourceType.UNKNOWN;
    }

    /**
     * @return {@link ResourceType}
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * @return {@link ResourceType}
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * @param resourceType
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}
