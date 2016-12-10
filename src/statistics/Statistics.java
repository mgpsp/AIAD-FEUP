package statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import agents.BuildingAgent;
import agents.LiftAgent;

public class Statistics {
	private int numCallsTotal = 0;
	private double maxWaitTime = 0;
	private double totalWaitTime = 0;
	private ArrayList<Integer> numCalls;
	private ArrayList<Integer> useTime;
	private ArrayList<Integer> noUseTime;
	
	public Statistics(int numLifts) {
		numCalls = new ArrayList<Integer>(Collections.nCopies(numLifts, 0));
		useTime = new ArrayList<Integer>(Collections.nCopies(numLifts, 0));
		noUseTime = new ArrayList<Integer>(Collections.nCopies(numLifts, 0));
	}
	
	public void incrementCalls() {
		numCallsTotal++;
	}
	
	public void addCallToLift(int lift) {
		int calls = numCalls.get(lift);
		numCalls.set(lift, ++calls);
	}
	
	public void addUseTime(int lift, int time) {
		int use = useTime.get(lift);
		useTime.set(lift, use + time);
	}
	
	public void addNoUseTime(int lift, int time) {
		int noUse = noUseTime.get(lift );
		noUseTime.set(lift, noUse + time);
	}
	
	public void addWaitTime(double time) {
		if (time > maxWaitTime)
			maxWaitTime = time;
		totalWaitTime += time;
	}
	
	public void printStatistics() {
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println("Total calls: " + numCallsTotal);
		System.out.println("Number of calls per lift");
		for(int i = 0; i < numCalls.size(); i++)
			System.out.println("\tLift " + (i+1) + ": " + numCalls.get(i));
		System.out.println("Use rate per lift (use time / no use time)");
		DecimalFormat df = new DecimalFormat("#.##");
		for(int i = 0; i < useTime.size(); i++) {
			if (noUseTime.get(i) != 0)
				System.out.println("\tLift " + (i+1) + ": " + df.format((double) useTime.get(i)/(double) noUseTime.get(i)));
			else
				System.out.println("\tLift " + (i+1) + ": " + useTime.get(i));
		}
		System.out.println("Maximum wait time: " + maxWaitTime);
		System.out.println("Total wait time: " + totalWaitTime);
		if (numCallsTotal != 0)
			System.out.println("Average wait time: " + df.format(totalWaitTime/numCallsTotal));
		else
			System.out.println("Average wait time: 0");
		System.out.println("-------------------------------");
		System.out.println();
	}
	
	public void addStatisticsToBuilding(BuildingAgent building) {
		building.addStatistics(this);
	}
	
	public void addStatisticsToLift(LiftAgent lift) {
		lift.addStatistics(this);
	}
}
