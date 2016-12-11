package model;

import java.util.ArrayList;
import java.util.Random;

import agents.BuildingAgent;
import agents.LiftAgent;
import agents.Task;
import gui.BuildingSpace;
import gui.Door;
import statistics.Statistics;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimEvent;
import uchicago.src.sim.engine.SimEventListener;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Object2DDisplay;

import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import sajas.core.Runtime;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

public class LiftModel extends Repast3Launcher {
	private static final int NUMLIFTS = 4;
	private static final int NUMFLOORS = 10;
	private static final int CALLFREQUENCY = 70;
	private static final int LIFTVELOCITY = 25;
	private static final int MAXCAPACITY = 20;
	private static final int STRATEGY = 3;
	
	private int numLifts = NUMLIFTS;
	private int numFloors = NUMFLOORS;
	private int callFrequency = CALLFREQUENCY;
	private int liftVelocity = LIFTVELOCITY;
	private int maxCapacity = MAXCAPACITY;
	private int strategy = STRATEGY;
	
	private ContainerController mainContainer;
	
	private BuildingSpace buildingSpace;
	
	private ArrayList<LiftAgent> lifts;
	private ArrayList<Door> doors;
	private BuildingAgent buildingAgent;
	
	private DisplaySurface displaySurf;
	
	private ArrayList<Task> newCalls;
	
	private Statistics statistics;
	
	public LiftModel() {
		super();
		
		this.addSimEventListener(new SimEventListener() {
			public void simEventPerformed(SimEvent evt) {
				if (evt.getId() == SimEvent.PAUSE_EVENT || evt.getId() == SimEvent.STOP_EVENT)
						statistics.printStatistics();
			}
		});
	}
	
	public String getName() {
		return "Lifts";
	}
	
	public void setup(){
		System.out.println("Running setup");
		super.setup();
		buildingSpace = null;
		lifts = new ArrayList<LiftAgent>();
		doors = new ArrayList<Door>();
		newCalls = new ArrayList<Task>();
		statistics = new Statistics(numLifts);
		buildingAgent = new BuildingAgent(numFloors);
		statistics.addStatisticsToBuilding(buildingAgent);
		if (displaySurf != null){
			displaySurf.dispose();
		}
		displaySurf = null;

		displaySurf = new DisplaySurface(this, "Lift Model");
		registerDisplaySurface("Lift Model", displaySurf);
	}
	
	@Override
	public void begin(){
		super.begin();
		buildModel();
		buildDisplay();
		buildSchedule();
	}
	
	public void buildModel(){
		System.out.println("Running BuildModel");
		
		for (int i = 0; i < numLifts; i++) {
			for (int j = 0; j < numFloors; j++) {
				Door door = new Door(i, j, numFloors);
				doors.add(door);
				buildingSpace.addDoor(door);
			}
		}
	}

	public void buildSchedule(){
		System.out.println("Running BuildSchedule");
		
		class LiftMove extends BasicAction {
			@Override
			public void execute() {
				for (LiftAgent la : lifts) {
					la.move();
					Task doneTask = la.executeTasks();
					if (doneTask != null) {
						for (Door d : doors) {
							if (doneTask.getOriginFloor() == d.getFloor())
								d.setState(doneTask.getDirection(), true);
						}
					}
					if (doneTask != null && doneTask.getNumPeople() != 0)
						newCalls.add(doneTask);
				}
				
				displaySurf.updateDisplay();
			}
		}
		
		class CallLift extends BasicAction {
			@Override
			public void execute() {
				statistics.incrementCalls();
				Task task;
				if (newCalls.size() > 0) {
					task = newCalls.get(0);
					buildingAgent.requestLift(task);
					newCalls.remove(0);
				}
				else
					task = buildingAgent.generateCall(maxCapacity);
				
				for (Door d : doors) {
					if (task.getOriginFloor() == d.getFloor())
						d.setState(task.getDirection(), false);
				}
			}
		}
		
		getSchedule().scheduleActionAtInterval(liftVelocity, new LiftMove());
		getSchedule().scheduleActionAtInterval(1, displaySurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(callFrequency, new CallLift());
	}

	public void buildDisplay(){
		System.out.println("Running BuildDisplay");
		
		Object2DDisplay displayAgents = new Object2DDisplay(buildingSpace.getCurrentLifts());
	    displayAgents.setObjectList(lifts);
	    
	    Object2DDisplay displayDoors = new Object2DDisplay(buildingSpace.getCurrentDoors());
	    displayDoors.setObjectList(doors);
		
		displaySurf.addDisplayable(displayDoors, "Doors");
		displaySurf.addDisplayableProbeable(displayAgents, "Lifts");
		
		addSimEventListener(displaySurf);
		displaySurf.display();
	}

	private void addNewLift(int position, int capacity){
		LiftAgent a = new LiftAgent(position, liftVelocity, numFloors - 1, capacity, strategy);
		statistics.addStatisticsToLift(a);
		a.addSchedule(getSchedule());
		lifts.add(a);
		buildingSpace.addLift(a);
		
		try {
			mainContainer.acceptNewAgent(a.getID(), a).start();
			buildingAgent.addAID(a.getAID());
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	} 
	
	public String[] getInitParam(){
		String[] initParams = { "NumLifts", "NumFloors", "CallFrequency", "LiftVelocity", "MaxCapacity", "Strategy" };
		return initParams;
	}
	
	public int getNumLifts(){
		return numLifts;
	}

	public void setNumLifts(int numLifts){
		if (numLifts > 0)
			this.numLifts = numLifts;
		else
			System.out.println("The number of lifts has to be greater than 0.");
	}

	public int getNumFloors() {
		return numFloors;
	}

	public void setNumFloors(int numFloors) {
		if (numFloors > 1)
			this.numFloors = numFloors;
		else
			System.out.println("The number of floors has to be greater than 1.");
	}

	public int getCallFrequency() {
		return callFrequency;
	}

	public void setCallFrequency(int callFrequency) {
		this.callFrequency = callFrequency;
	}

	public int getLiftVelocity() {
		return liftVelocity;
	}

	public void setLiftVelocity(int liftVelocity) {
		this.liftVelocity = liftVelocity;
	}
	
	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}
	
	public int getStrategy() {
		return strategy;
	}

	public void setStrategy(int strategy) {
		this.strategy = strategy;
	}
	
	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		buildingSpace = new BuildingSpace(numLifts, numFloors);
		
		launchAgents();
	}
	
	private void launchAgents() {
		try {
			mainContainer.acceptNewAgent("Building Agent", buildingAgent).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		
		Random generator = new Random();
		int max = 0;
		for(int i = 0; i < numLifts; i++){
			int capacity = generator.nextInt(maxCapacity - 3) + 3;
			if (capacity > max)
				max = capacity;
			addNewLift(i, capacity);
		}
		setMaxCapacity(max);
	}
	
	public static void main(String[] args) {
		SimInit init = new SimInit();
	    LiftModel model = new LiftModel();
	    init.loadModel(model, null, false);
	}
}
