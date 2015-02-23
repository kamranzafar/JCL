package org.xeustechnologies.jcl.test;

import java.io.Serializable;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.xeustechnologies.jcl.test.TestInterface;

public class Test implements Serializable, TestInterface {

    /**
	 * serialVersionUID:long
	 */
	private static final long serialVersionUID = 7683330206220877077L;
	private String firstName;
    private String lastName;

    private static Logger logger = Logger.getLogger( Test.class.getName() );

    public Test() {
        firstName = "World";
        lastName = "";
    }

    public Test(String firstName) {
        this.firstName = firstName;
    }

    public String sayHello() {
        String hello = "Hello " + firstName + " " + lastName;

        if( logger.isLoggable( Level.FINER ) )
            logger.finer( "Hello " + firstName + " " + lastName );

        return hello;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
