package agents;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int originFloor;
	private ArrayList<Integer> destinationFloor;
	private ArrayList<Integer> numPeople;
	private Direction direction;
	boolean done;
	private int numCalls;
	
	private double callTime;
	
	public Task(int originFloor, ArrayList<Integer> destinationFloor, ArrayList<Integer> numPeople) {
		this.originFloor = originFloor;
		this.destinationFloor = new ArrayList<Integer>();
		this.numPeople = new ArrayList<Integer>();
		this.destinationFloor = destinationFloor;
		this.numPeople = numPeople;
		this.numCalls = 1;
		if (originFloor < (int) destinationFloor.get(0))
			direction = Direction.UP;
		else if (originFloor > (int) destinationFloor.get(0))
			direction = Direction.DOWN;
		else
			direction = Direction.STOPPED;
		done = false;
	}
	
	public String toString() {
		return originFloor + "\n" + destinationFloor + "\n" + numPeople +  "\n" + direction;
	}
	
	public String oneLine() {
		return originFloor + " " + destinationFloor + " " + numPeople +  " " + direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getOriginFloor() {
		return originFloor;
	}
	
	public int getDestinationFloor() {
		return destinationFloor.get(0);
	}
	
	public void removeDestinationFloor() {
		destinationFloor.remove(0);
	}
	
	public ArrayList<Integer> getDestinations() {
		return destinationFloor;
	}
	
	public int getNumPeopleSize() {
		return numPeople.size();
	}
	
	public void addDestinationFloor(int destinationFloor) {
		this.destinationFloor.add(destinationFloor);
		Collections.sort(this.destinationFloor);
		if (direction == Direction.DOWN)
			Collections.reverse(this.destinationFloor);
	}
	
	public void removeNumPeople() {
		numPeople.remove(0);
	}
	
	public void setNumPeople(int numPeople) {
		this.numPeople.set(0, numPeople);
	}
	
	public void addNumPeople(int numPeople) {
		this.numPeople.add(numPeople);
	}
	public int getNumPeople() {
		return numPeople.get(0);
	}
	
	public int getNumAllPeople() {
		int sum = 0;
		for (int i = 0; i < numPeople.size(); i++)
			sum += numPeople.get(i);
		return sum;
	}
	
	public void incrementNumCalls() {
		numCalls++;
	}
	
	public int getNumCalls() {
		return numCalls;
	}

	public double getCallTime() {
		return callTime;
	}

	public void setCallTime(double waitTime) {
		this.callTime = waitTime;
	}
}
