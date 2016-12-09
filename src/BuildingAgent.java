import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import sajas.core.Agent;
import sajas.core.behaviours.*;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class BuildingAgent extends Agent {
	private ArrayList<AID> liftsAID;
	private int numFloors;
	
	public BuildingAgent(int numFloors) {
		liftsAID = new ArrayList<AID>();
		this.numFloors = numFloors;
	}
	
	@Override
	protected void setup() {
		System.out.println("Hi! I'm the Building Agent. My AID is " + getAID().getName());
	}

	
	private class RequestLift extends Behaviour {
		private static final long serialVersionUID = 1L;
		
		Task request;
		int step = 0;
		int replysCnt = 0;
		int bestWaitingTime = Integer.MAX_VALUE;
		AID bestLift;
		
		public RequestLift(Task request) {
			this.request = request;
		}
		
		@Override
		public void action() {
			switch(step) {
			case 0:
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				for (AID aid : liftsAID)
					msg.addReceiver(aid);
				try {
					msg.setContentObject(new Test(1));
				} catch (IOException e) {
					e.printStackTrace();
				}
				msg.setContent(request.toString());
				send(msg);
				step++;
				break;
			case 1:
				ACLMessage reply = myAgent.receive();
				if (reply != null && reply.getPerformative() == ACLMessage.PROPOSE) {
					//System.out.println(reply.getSender().getName() + " waiting time: " + reply.getContent());
					int waitingTime = Integer.parseInt(reply.getContent());
					if (waitingTime < bestWaitingTime) {
						bestWaitingTime = waitingTime;
						bestLift = reply.getSender();
					}
					replysCnt++;
					if (replysCnt >= liftsAID.size())
						step++;
				}
				else
					block();
				break;
			case 2:
				ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				accept.addReceiver(bestLift);
				accept.setContent("I chose you!");
				send(accept);
				ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				for (AID aid : liftsAID) {
					if (aid != bestLift)
						reject.addReceiver(aid);
				}
				reject.setContent("I don't chose you.");
				send(reject);
				step++;
				break;
			}
		}

		@Override
		public boolean done() {
			return step == 3;
		}
		
	}
	
	public Task generateCall(int maxCapacity) {
		Random generator = new Random();
		int origFloor = generator.nextInt(numFloors);
		int destFloor = -1;
		do {
			destFloor = generator.nextInt(numFloors);
		} while(destFloor == origFloor);
		int numPeople = generator.nextInt(maxCapacity - 1) + 1;
		ArrayList<Integer> dests = new ArrayList<Integer>();
		dests.add(destFloor);
		ArrayList<Integer> people = new ArrayList<Integer>();
		people.add(numPeople);
		Task task = new Task(origFloor, dests, people);
		System.out.println("Request: " + task.oneLine());
		requestLift(task);
		return task;
	}
	
	public void addAID(AID liftAID) {
		liftsAID.add(liftAID);
	}
	
	public void requestLift(Task request) {
		addBehaviour(new RequestLift(request));
	}
	
}
