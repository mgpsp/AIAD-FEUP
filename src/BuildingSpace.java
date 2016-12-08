import java.util.ArrayList;
import java.util.Collections;

import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;

public class BuildingSpace {	
	private Object2DGrid building;
	private Object2DGrid lifts;
	private Object2DGrid doors;
	
	private ArrayList<Direction> calls;
	
	public BuildingSpace(int xSize, int ySize){
		building = new Object2DGrid(xSize, ySize);
		lifts = new Object2DGrid(xSize, ySize);
		doors = new Object2DGrid(xSize, ySize);
		calls = new ArrayList<Direction>(Collections.nCopies(ySize, Direction.STOPPED));

		for(int i = 0; i < xSize; i++){
			for(int j = 0; j < ySize; j++){
				building.putObjectAt(i,j,new Integer(0));
			}
		}
	}

	public Discrete2DSpace getCurrentBuilding() {
		return building;
	}

	public Object2DGrid getCurrentLifts(){
		return lifts;
	}
	
	public Object2DGrid getCurrentDoors(){
		return doors;
	}
	
	public void updateCalls(Task call, Boolean remove) {
		int floor =  building.getSizeY() - call.getOriginFloor() - 1;
		Direction calledDirection = calls.get(floor);
		int width = building.getSizeX();
		boolean update = false;
		
		switch(calledDirection) {
		case UP:
			if (!remove && call.getDirection() == Direction.DOWN) {
				update = true;
				calls.set(floor, Direction.UPDOWN);
			} else if (remove) {
				update = true;
				calls.set(floor, Direction.STOPPED);
			}
			break;
		case DOWN:
			if (!remove && call.getDirection() == Direction.DOWN) {
				update = true;
				calls.set(floor, Direction.UPDOWN);
			} else if (remove) {
				update = true;
				calls.set(floor, Direction.STOPPED);
			}
			break;
		case UPDOWN:
			if (remove && call.getDirection() == Direction.UP) {
				update = true;
				calls.set(floor, Direction.DOWN);
			}
			else if (remove && call.getDirection() == Direction.DOWN) {
				update = true;
				calls.set(floor, Direction.UP);
			}
			break;
		case STOPPED:
			update = true;
			if (!remove && call.getDirection() == Direction.UP)
				calls.set(floor, Direction.UP);
			else if (!remove && call.getDirection() == Direction.DOWN)
				calls.set(floor, Direction.DOWN);
			break;
		}
		
		if (update) {
			for (int i = 0; i < width; i++)
				doors.getValueAt(floor, i);
				
		}
	}
	
	public boolean addLift(LiftAgent lift){
		lifts.putObjectAt(lift.getX(),lift.getY(),lift);
		lift.setBuildingSpace(this);
		return true;
	}
	
	public boolean addDoor(Door door){
		doors.putObjectAt(door.getX(),door.getY(),door);
		return true;
	}

}
