package xeus.jcl.test;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class Test implements Serializable {

	private String firstName;
	private String lastName;

	private static Logger logger = Logger.getLogger(Test.class);

	public Test() {
		firstName = "World";
		lastName = "";
	}

	public Test(String firstName) {
		this.firstName = firstName;
	}

	public void sayHello() {
		logger.debug("Hello " + firstName + " " + lastName);
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
