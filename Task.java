
public class Task {
	private int originFloor;
	private Direction direction;
	
	public Task(int originFloor, Direction direction) {
		this.originFloor = originFloor;
		this.direction = direction;
	}
	
	public String toString() {
		return originFloor + " " + direction;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getFloor() {
		return originFloor;
	}
}
