import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;

public class BuildingSpace {
	private Object2DGrid building;
	private Object2DGrid lifts;
	
	public BuildingSpace(int xSize, int ySize){
		building = new Object2DGrid(xSize, ySize);
		lifts = new Object2DGrid(xSize, ySize);

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
	
	public void putCall(int floor) {
		int width = building.getSizeX();
		for (int i = 0; i < width; i++) {
			building.putObjectAt(i,floor,1);
		}
	}
	
	public boolean isCellOccupied(int x, int y){
		boolean retVal = false;
		if (lifts.getObjectAt(x, y)!=null)
			retVal = true;
		return retVal;
	}
	
	public boolean addLift(LiftAgent lift){
		lifts.putObjectAt(lift.getX(),lift.getY(),lift);
		//lift.setBuilding(this);
		return true;
	}
	
	public void addBuildingSpaceToAgent(BuildingAgent buildingAgent) {
		buildingAgent.setBuilding(this);
	}

}
