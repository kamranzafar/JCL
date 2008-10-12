/**
 *  JCL (Jar Class Loader)
 *
 *  Copyright (C) 2008  Xeus Technologies
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

package xeus.jcl.exception;

import xeus.jcl.ResourceType;

/**
 * @author Kamran Zafar
 *
 */
public class ResourceNotFoundException extends JclException {

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
		super(message);
	}

	/**
	 * @param resource
	 * @param message
	 */
	public ResourceNotFoundException(String resource, String message) {
		super(message);
		resourceName = resource;
		determineResourceType(resource);
	}
	
	/**
	 * @param e
	 * @param resource
	 * @param message
	 */
	public ResourceNotFoundException(Throwable e, String resource, String message){
		super(message, e);
		resourceName = resource;
		determineResourceType(resource);			
	}
	
	/**
	 * @param resourceName
	 */
	private void determineResourceType(String resourceName){
		if (resourceName.toLowerCase().endsWith(".class"))
			resourceType = ResourceType.CLASS;
		else if (resourceName.toLowerCase().endsWith(".properties"))
			resourceType = ResourceType.PROPERTIES;
		else if (resourceName.toLowerCase().endsWith(".xml"))
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
