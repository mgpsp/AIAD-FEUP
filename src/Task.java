import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;
	private int originFloor;
	private transient ArrayList<Integer> destinationFloor;
	private transient ArrayList<Integer> numPeople;
	private transient Direction direction;
	boolean done;
	private int numCalls;
	
	public Task(int originFloor, ArrayList<Integer> destinationFloor, ArrayList<Integer> numPeople) {
		this.originFloor = originFloor;
		this.destinationFloor = new ArrayList<Integer>();
		this.numPeople = new ArrayList<Integer>();
		this.destinationFloor = destinationFloor;
		this.numPeople = numPeople;
		this.numCalls = 1;
		if (originFloor < destinationFloor.get(0))
			direction = Direction.UP;
		else if (originFloor > destinationFloor.get(0))
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
	
	public void addDestinationFloor(int destinationFloor) {
		this.destinationFloor.add(destinationFloor);
		Collections.sort(this.destinationFloor);
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
	
	public void incrementNumCalls() {
		numCalls++;
	}
	
	public int getNumCalls() {
		return numCalls;
	}
	
	private synchronized void writeObject(java.io.ObjectOutputStream stream) throws java.io.IOException {
		stream.defaultWriteObject( );
		stream.writeInt(destinationFloor.size());
		for (int i=0; i<destinationFloor.size(); i++)
			stream.writeInt(destinationFloor.get(i));
		stream.writeInt(numPeople.size());
		for (int i=0; i<numPeople.size(); i++)
			stream.writeInt(numPeople.get(i));
	}
	
	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
		in.defaultReadObject();
		for (int i=0; i<in.readInt(); i++)
			in.readInt();
		for (int i=0; i<in.readInt(); i++)
			in.readInt();
	}
}
