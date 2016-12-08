import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import sajas.core.Agent;
import sajas.core.behaviours.*;

import jade.lang.acl.ACLMessage;

public class LiftAgent extends Agent implements Drawable {
	private BuildingSpace buildingSpace;
	
	private int x;
	private int y;
	private int currentFloor;
	private int velocity;
	private ArrayList<Task> tasks;
	
	private Direction state;
	private Task currentTask;
	private boolean goingToOrigin = false;
	
	private static int IDNumber = 0;
	private int ID;
	
	BufferedImage image;
	
	public LiftAgent(int positionX, int liftVelocity, int positionY) {
		x = positionX;
		y = positionY;
		currentFloor = 0;
		velocity = liftVelocity;
		IDNumber++;
	    ID = IDNumber;
	    tasks = new ArrayList<Task>();
	    state = Direction.STOPPED;
	    currentTask = null;
	    
	    image = null;
	    try {
	        image = ImageIO.read(new File("door.jpg"));
	    } catch (IOException e) {
	    	System.out.println("ERRO");
	    }
	}
	
	@Override
	protected void setup() {
		System.out.println("Hi! I'm a Lift Agent. My AID is " + getAID().getName());

		addBehaviour(new AnswerRequest(this));
	}
	
	private class AnswerRequest extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		
		int step = 0;
		Task task;
		
		public AnswerRequest(Agent agent) {
			super(agent);
		}

		@Override
		public void action() {
			switch(step) {
			case 0:
				ACLMessage msg = myAgent.receive();
				if (msg != null && msg.getPerformative() == ACLMessage.CFP) {
					String[] splited = msg.getContent().split("\\s+");
					task = new Task(Integer.parseInt(splited[0]), Integer.parseInt(splited[1]));
					int waitingTime = calculateWaitingTime(task);
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(Integer.toString(waitingTime));
					myAgent.send(reply);
					step++;
				}
				else
					block();
				break;
			case 1:
				ACLMessage reply = myAgent.receive();
				if (reply != null) {
					if (reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
						System.out.println(getAID().getName() + " will answer the request.");
						tasks.add(task);
					}	
					step = 0;
				}
				else
					block();
			}
		}
	}
	
	public void executeTasks() {
		if (currentTask == null && tasks.size() > 0) {
			currentTask = tasks.get(0);
			goingToOrigin = true;
			if (currentFloor < currentTask.getOriginFloor())
				state = Direction.UP;
			else if (currentFloor > currentTask.getOriginFloor())
				state = Direction.DOWN;
			else {
				goingToOrigin = false;
				buildingSpace.updateCalls(currentTask, true);
				state = currentTask.getDirection();
			}
		}
		else if (currentTask != null) {
			if (currentTask.getOriginFloor() == currentFloor && goingToOrigin) {
				state = currentTask.getDirection();
				buildingSpace.updateCalls(currentTask, true);
				goingToOrigin = false;
			}
			else if (currentTask.getDestinationFloor() == currentFloor && !goingToOrigin && state != Direction.STOPPED) {
				tasks.remove(0);
				System.out.println(getID() + " answered " + currentTask);
				if (tasks.size() > 0) {
					currentTask = tasks.get(0);
					goingToOrigin = true;
					if (currentFloor < currentTask.getOriginFloor())
						state = Direction.UP;
					else if (currentFloor > currentTask.getOriginFloor())
						state = Direction.DOWN;
					else {
						goingToOrigin = false;
						buildingSpace.updateCalls(currentTask, true);
						state = currentTask.getDirection();
					}
				}
				else {
					state = Direction.STOPPED;
					currentTask = null;
				}
			}
		}
	}
	
	public void setXY(int newX, int newY){
		x = newX;
		y = newY;
	}

	public String getID(){
		return "A-" + ID;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int getCurrentFloor(){
		return currentFloor;
	}

	public void draw(SimGraphics G){
		G.drawFastRoundRect(Color.blue);
		//G.drawImageToFit(image);
	}
	
	public void move() {
		if (state == Direction.UP) {
			currentFloor++;
			y--;
		}	
		else if (state == Direction.DOWN) {
			currentFloor--;
			y++;
		}
	}
	
	public int calculateWaitingTime(Task task) {
		int score = 0;
		int floor = currentFloor;
		for (Task t : tasks) {
			if (t == currentTask && !goingToOrigin)
				score += Math.abs(floor - t.getDestinationFloor()) * velocity;
			else
				score += Math.abs(floor - t.getOriginFloor()) * velocity;
			if (t != currentTask)
				floor = t.getOriginFloor();
		}
		score += Math.abs(floor - task.getOriginFloor()) * velocity;
		return score;
	}
	
	public void setBuildingSpace(BuildingSpace buildingSpace){
		this.buildingSpace = buildingSpace;
	}
}
