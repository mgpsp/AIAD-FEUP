import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import sajas.core.Agent;
import sajas.core.behaviours.*;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class LiftAgent extends Agent implements Drawable {	
	private static final String IMGSTOPPED = "carStopped.png";
	private static final String IMGUP = "carUp.png";
	private static final String IMGDOWN = "carDown.png";
	
	private static final int STOPTIME = 20;
	
	private int x;
	private int y;
	private int currentFloor;
	private int velocity;
	private int capacity;
	private ArrayList<Task> tasks;
	
	private Direction state;
	private Task currentTask;
	private boolean goingToOrigin = false;
	
	private static int IDNumber = 0;
	private int ID;
	
	BufferedImage up, down, stopped;
	
	private int numFloors;
	
	int strategy;
	
	public LiftAgent(int positionX, int liftVelocity, int positionY, int capacity, int strategy) {
		x = positionX;
		y = positionY;
		numFloors = positionY;
		currentFloor = 0;
		velocity = liftVelocity;
		this.capacity = capacity;
		IDNumber++;
	    ID = IDNumber;
	    tasks = new ArrayList<Task>();
	    state = Direction.STOPPED;
	    currentTask = null;
	    this.strategy = strategy;
	    
	    up = down = stopped = null;
	    try {
	        up = ImageIO.read(new File(IMGUP));
	        down = ImageIO.read(new File(IMGDOWN));
	        stopped = ImageIO.read(new File(IMGSTOPPED));
	    } catch (IOException e) {
	    	System.out.println("Couldn't load car images");
	    }
	}
	
	@Override
	protected void setup() {
		System.out.println("Hi! I'm a Lift Agent. My AID is " + getAID().getName() +  " and my capacity is " + capacity);

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
					/*String[] splited = msg.getContent().split("\\r?\\n");
					ArrayList<Integer> dests = new ArrayList<Integer>();
					ArrayList<Integer> people = new ArrayList<Integer>();
					String[] d = splited[1].replaceAll("\\[", "").replaceAll("\\]", "").split("\\s*,\\s*");
					String[] p = splited[2].replaceAll("\\[", "").replaceAll("\\]", "").split("\\s*,\\s*");
					for (int i = 0; i < d.length; i++) {
						dests.add(Integer.parseInt(d[i]));
						people.add(Integer.parseInt(p[i]));
					}*/
					Serializable test;
					try {
						test = msg.getContentObject();
						System.out.println(test);
					} catch (UnreadableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(task.oneLine());
					//task = new Task(Integer.parseInt(splited[0]), dests, people);
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
						System.out.println(getAID().getName() + " will answer the request " + task.oneLine());
						addTask(task);
					}	
					step = 0;
				}
				else
					block();
			}
		}
	}
	
	public Task executeTasks() {
		Task doneTask = null;
		if (currentTask == null && tasks.size() > 0) {
			currentTask = tasks.get(0);
			goingToOrigin = true;
			if (currentFloor < currentTask.getOriginFloor())
				state = Direction.UP;
			else if (currentFloor > currentTask.getOriginFloor())
				state = Direction.DOWN;
			else {
				if (currentTask.getNumPeople() > capacity) {
					System.out.println("Insufficient capacity for " + currentTask.oneLine() +  ". Making new request");
					currentTask.setNumPeople(currentTask.getNumPeople() - capacity);
				}
				else
					currentTask.setNumPeople(0);
				goingToOrigin = false;
				state = currentTask.getDirection();
				doneTask = currentTask;
			}
		}
		else if (currentTask != null) {
			if (currentTask.getOriginFloor() == currentFloor && goingToOrigin) {
				if (currentTask.getNumPeople() > capacity) {
					System.out.println("Insufficient capacity for " + currentTask.oneLine() +  ". Making new request");
					currentTask.setNumPeople(currentTask.getNumPeople() - capacity);
				}
				else {
					currentTask.setNumPeople(0);
				}
				state = currentTask.getDirection();
				goingToOrigin = false;
				doneTask = currentTask;
			}
			else if (currentTask.getDestinationFloor() == currentFloor && !goingToOrigin && state != Direction.STOPPED) {
				if (currentTask.getDestinations().size() > 1) {
					currentTask.removeDestinationFloor();
					currentTask.removeNumPeople();
				}
				else
					tasks.remove(0);
				System.out.println(getID() + " answered " + currentTask.oneLine());
				if (tasks.size() > 0) {
					currentTask = tasks.get(0);
					goingToOrigin = true;
					if (currentFloor < currentTask.getOriginFloor())
						state = Direction.UP;
					else if (currentFloor > currentTask.getOriginFloor())
						state = Direction.DOWN;
					else {
						goingToOrigin = false;
						state = currentTask.getDirection();
					}
				}
				else {
					goingToOrigin = false;
					state = Direction.STOPPED;
					currentTask = null;
				}
			}
		}
		return doneTask;
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
		switch (state) {
		case UP:
			G.drawImageToFit(up);
			break;
		case DOWN:
			G.drawImageToFit(down);
			break;
		case STOPPED:
			G.drawImageToFit(stopped);
			break;
		case UPDOWN:
			break;
		}
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
	
	public void addTask(Task task) {
		boolean addToTask = false;
		for (Task t : tasks) {
			if (t.getOriginFloor() == task.getOriginFloor() && t.getDirection() == task.getDirection()) {
				addToTask = true;
				if (t.getDestinationFloor() != task.getDestinationFloor()) {
					t.addNumPeople(task.getNumPeople());
					t.addDestinationFloor(task.getDestinationFloor());
					t.incrementNumCalls();
				}
				else
					t.setNumPeople(t.getNumPeople() + task.getNumPeople());
				break;	
			}
		}
		if (!addToTask)
			tasks.add(task);
	}
	
	public int estimateDestFloor(Task t) {
		int destFloor = 0;
		if (t.getOriginFloor() == 0)
			destFloor = (int) Math.floor((numFloors + 1)/2);
		else if (t.getDirection() == Direction.DOWN)
			destFloor = (int) Math.floor(t.getOriginFloor()/2);
		else if (t.getDirection() == Direction.UP)
			destFloor = (int) Math.floor(((numFloors + 1) - t.getOriginFloor())/2) + t.getOriginFloor();
		return destFloor;
	}
	
	public int bestFit(Task task, boolean numpad) {
		int score = 0;
		int floor = currentFloor, destFloor;
		boolean addToTask = false;
		for (Task t : tasks) {
			if (t == currentTask && !goingToOrigin) {
				int size = t.getDestinations().size() - 1;
				destFloor = t.getDestinations().get(size);
				score += Math.abs(floor - destFloor) * velocity + size * STOPTIME;
			}
			else if (t.getOriginFloor() == task.getOriginFloor() && t.getDirection() == task.getDirection()) {
				System.out.println(getID() + " joined tasks: " + t.oneLine() + " and " + task.oneLine());
				score += Math.abs(floor - t.getOriginFloor()) * velocity - 1;
				addToTask = true;
				break;
			}
			else {
				if (numpad)
					destFloor = task.getDestinationFloor();
				else
					destFloor = estimateDestFloor(t);
				score += Math.abs(floor - t.getOriginFloor()) * velocity + Math.abs(destFloor - t.getOriginFloor()) * velocity + t.getNumCalls() * STOPTIME;
			}
			
			floor = destFloor;
		}

		if (!addToTask)
			score += Math.abs(floor - task.getOriginFloor()) * velocity;
		return score;
	}
	
	public int closestLift(Task task) {
		return Math.abs(task.getOriginFloor() - currentFloor);
	}
	
	public int calculateWaitingTime(Task task) {
		int score = 0;
		
		switch(strategy) {
		case 1:
			score = bestFit(task, false);
			break;
		case 2:
			score = closestLift(task);
			break;
		case 3:
			score = bestFit(task, true);
			break;
		}
		
		return score;
	}
}