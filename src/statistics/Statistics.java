package statistics;

import agents.BuildingAgent;
import agents.LiftAgent;

public class Statistics {
	private int numCalls = 0;
	
	public void incrementCalls() {
		numCalls++;
	}
	
	public void printStatistics() {
		System.out.println(numCalls);
	}
	
	public void addStatisticsToBuilding(BuildingAgent building) {
		building.addStatistics(this);
	}
	
	public void addStatisticsToLift(LiftAgent lift) {
		lift.addStatistics(this);
	}
}
