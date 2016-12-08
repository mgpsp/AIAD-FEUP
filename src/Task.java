
public class Task {
	private int originFloor;
	private int destinationFloor;
	private Direction direction;
	
	public Task(int originFloor, int destinationFloor) {
		this.originFloor = originFloor;
		this.destinationFloor = destinationFloor;
		if (originFloor < destinationFloor)
			direction = Direction.UP;
		else if (originFloor > destinationFloor)
			direction = Direction.DOWN;
		else
			direction = Direction.STOPPED;
	}
	
	public String toString() {
		return originFloor + " " + destinationFloor + " " + direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getOriginFloor() {
		return originFloor;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
}
