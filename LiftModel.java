import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.ColorMap;
import uchicago.src.sim.gui.Object2DDisplay;
import uchicago.src.sim.gui.Value2DDisplay;

import sajas.sim.repast3.Repast3Launcher;
import sajas.wrapper.ContainerController;
import sajas.core.Runtime;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.StaleProxyException;

public class LiftModel extends Repast3Launcher {
	
	private static final int NUMLIFTS = 4;
	private static final int NUMFLOORS = 10;
	private static final int CALLFREQUENCY = 50;
	private static final int LIFTVELOCITY = 50;
	
	private int numLifts = NUMLIFTS;
	private int numFloors = NUMFLOORS;
	private int callFrequency = CALLFREQUENCY;
	private int liftVelocity = LIFTVELOCITY;
	
	private ContainerController mainContainer;
	
	private BuildingSpace buildingSpace;
	
	private ArrayList<LiftAgent> lifts;
	private BuildingAgent buildingAgent;
	
	private DisplaySurface displaySurf;
	
	public LiftModel() {
		super();
	}
	
	public String getName() {
		return "Lifts";
	}
	
	public void setup(){
		System.out.println("Running setup");
		super.setup();
		buildingSpace = null;
		lifts = new ArrayList<LiftAgent>();
		buildingAgent = new BuildingAgent();
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
		
		buildingSpace = new BuildingSpace(numLifts, numFloors);
		buildingSpace.addBuildingSpaceToAgent(buildingAgent);
	}

	public void buildSchedule(){
		System.out.println("Running BuildSchedule");
		
		/*class LiftMove extends BasicAction {
			public void execute() {
				LiftAgent la = (LiftAgent)lifts.get(0);
				la.move();
				displaySurf.updateDisplay();
			}
		}*/
		
		class CallLift extends BasicAction {
			public void execute() {
				buildingAgent.generateCall(numFloors);
			}
		}
		
		//getSchedule().scheduleActionAtInterval(100, new LiftMove());
		getSchedule().scheduleActionAtInterval(1, displaySurf, "updateDisplay", Schedule.LAST);
		getSchedule().scheduleActionAtInterval(200, new CallLift());
	}

	public void buildDisplay(){
		System.out.println("Running BuildDisplay");
		
		ColorMap map = new ColorMap();
		map.mapColor(0, Color.WHITE);
		map.mapColor(1, Color.RED);
		
		Value2DDisplay displayBuilding = new Value2DDisplay(buildingSpace.getCurrentBuilding(), map);
		
		Object2DDisplay displayAgents = new Object2DDisplay(buildingSpace.getCurrentLifts());
	    displayAgents.setObjectList(lifts);
		
		displaySurf.addDisplayable(displayBuilding, "Building");
		displaySurf.addDisplayable(displayAgents, "Lifts");
		
		addSimEventListener(displaySurf);
		displaySurf.display();
	}

	private void addNewLift(int position){
		LiftAgent a = new LiftAgent(position, liftVelocity, numFloors - 1);
		lifts.add(a);
		
		try {
			mainContainer.acceptNewAgent("S-" + a.getID(), a).start();
			buildingAgent.addAID(a.getAID());
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	} 
	
	public String[] getInitParam(){
		String[] initParams = { "NumLifts", "NumFloors", "CallFrequency", "LiftVelocity" };
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
	
	@Override
	protected void launchJADE() {
		Runtime rt = Runtime.instance();
		Profile p1 = new ProfileImpl();
		mainContainer = rt.createMainContainer(p1);
		
		launchAgents();
	}
	
	private void launchAgents() {
		for(int i = 0; i < numLifts; i++){
			addNewLift(i);
		}
		try {
			mainContainer.acceptNewAgent("Building Agent", buildingAgent).start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < lifts.size(); i++){
			LiftAgent la = (LiftAgent)lifts.get(i);
			la.report();
		}
	}
	
	public static void main(String[] args) {
		SimInit init = new SimInit();
	    LiftModel model = new LiftModel();
	    init.loadModel(model, null, false);
	}
}
