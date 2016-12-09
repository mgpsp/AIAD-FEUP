import java.io.Serializable;

public class Test implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int test;
	
	public Test(int i) {
		setTest(i);
	}

	public int getTest() {
		return test;
	}

	public void setTest(int test) {
		this.test = test;
	}
}
