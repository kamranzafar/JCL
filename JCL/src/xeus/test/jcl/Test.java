package xeus.test.jcl;

public class Test implements TestInterface {

	private String firstName;
	private String lastName;

	public Test() {
		firstName = "World";
		lastName="";
	}

	public Test(String firstName) {
		this.firstName = firstName;
	}

	public String sayHello() {
		return "Hello " + firstName + " " + lastName;
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
