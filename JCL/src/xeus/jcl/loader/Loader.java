package xeus.jcl.loader;

public abstract class Loader implements Comparable<Loader> {
	// Default order
	protected int order = 4;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public abstract Class load(String className, boolean resolveIt);

	public int compareTo(Loader o) {
		return order - o.getOrder();
	}
}
