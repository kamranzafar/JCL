package org.xeustechnologies.jcl.test;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xeustechnologies.jcl.test.TestInterface;

public class Test implements Serializable, TestInterface {

    /**
	 * serialVersionUID:long
	 */
	private static final long serialVersionUID = 7683330206220877077L;
	private String firstName;
    private String lastName;

    private final transient Logger logger = LoggerFactory.getLogger(Test.class);

    public Test() {
        firstName = "World";
        lastName = "";
    }

    public Test(String firstName) {
        this.firstName = firstName;
    }

    public String sayHello() {
        String hello = "Hello " + firstName + " " + lastName;

        logger.debug( "Hello {} {}", firstName, lastName );

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
