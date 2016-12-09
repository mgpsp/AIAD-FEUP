import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;

public class BuildingSpace {	
	private Object2DGrid building;
	private Object2DGrid lifts;
	private Object2DGrid doors;
	
	public BuildingSpace(int xSize, int ySize){
		building = new Object2DGrid(xSize, ySize);
		lifts = new Object2DGrid(xSize, ySize);
		doors = new Object2DGrid(xSize, ySize);

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
	
	public boolean addLift(LiftAgent lift){
		lifts.putObjectAt(lift.getX(),lift.getY(),lift);
		return true;
	}
	
	public boolean addDoor(Door door){
		doors.putObjectAt(door.getX(),door.getY(),door);
		return true;
	}

}
