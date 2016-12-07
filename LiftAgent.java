import java.awt.Color;
import java.util.ArrayList;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import sajas.core.Agent;
import sajas.core.behaviours.*;

import jade.lang.acl.ACLMessage;

public class LiftAgent extends Agent implements Drawable {
	private int x;
	private int y;
	private int currentFloor;
	private int velocity;
	private ArrayList<Task> tasks;
	
	private Direction state;
	
	private static int IDNumber = 0;
	private int ID;
	
	public LiftAgent(int positionX, int liftVelocity, int positionY) {
		x = positionX;
		y = positionY;
		currentFloor = 0;
		velocity = liftVelocity;
		IDNumber++;
	    ID = IDNumber;
	    tasks = new ArrayList<Task>();
	    state = Direction.STOPPED;
	}
	
	@Override
	protected void setup() {
		System.out.println("Hi! I'm a Lift Agent. My AID is " + getAID().getName());

		addBehaviour(new AnswerRequest(this));
		addBehaviour(new ExecuteTasks(this));
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
					//System.out.println(getAID().getName()  + " received a request: " + msg.getContent());
					String[] splited = msg.getContent().split("\\s+");
					Direction direction;
					if (splited[1] == "UP")
						direction = Direction.UP;
					else
						direction = Direction.DOWN;
					task = new Task(Integer.parseInt(splited[0]), direction);
					int waitingTime = calculateWaitingTime(task);
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(Integer.toString(waitingTime));
					//System.out.println("Sending score");
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
					else
						System.out.println(getAID().getName() + " will ignore the request.");
					step = 0;
				}
				else
					block();
			}
		}
	}
	
	private class ExecuteTasks extends CyclicBehaviour {
		private static final long serialVersionUID = 1L;
		
		private Task currentTask;
		
		public ExecuteTasks(Agent agent) {
			super(agent);
			currentTask = new Task(0, Direction.STOPPED);
		}
		
		@Override
		public void action() {
			if (currentTask.getFloor() == currentFloor && tasks.size() > 0) {
				tasks.remove(0);
				currentTask = tasks.get(0);
				state = currentTask.getDirection();
			}
		}
	}
	
	public void setXY(int newX, int newY){
		x = newX;
		y = newY;
	}
	
	/*public void setBuilding(BuildingSpace building){
		this.building = building;
	}*/

	public String getID(){
		return "A-" + ID;
	}
	
	public void report(){
		System.out.println(getID() + " at " + x + ", " + currentFloor);
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

	/*public void step() {
		if (state == Direction.UP)
			currentFloor--;
		else if (state == Direction.DOWN)
			currentFloor++;
	}*/
	
	public int calculateWaitingTime(Task task) {
		int score = 0;
		int floor = currentFloor;
		for (Task t : tasks) {
			score += Math.abs(floor - t.getFloor()) * velocity;
			floor = t.getFloor();
		}
		score += Math.abs(floor - task.getFloor()) * velocity;
		return score;
	}
}
