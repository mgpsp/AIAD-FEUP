import java.util.ArrayList;
import java.util.Random;

import sajas.core.Agent;
import sajas.core.behaviours.*;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class BuildingAgent extends Agent {
	private BuildingSpace buildingSpace;
	private ArrayList<AID> liftsAID;
	
	public BuildingAgent() {
		liftsAID = new ArrayList<AID>();
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
				//System.out.println("Sending request");
				ACLMessage msg = new ACLMessage(ACLMessage.CFP);
				for (AID aid : liftsAID)
					msg.addReceiver(aid);
				msg.setContent(request.toString());
				send(msg);
				step++;
				break;
			case 1:
				ACLMessage reply = myAgent.receive();
				if (reply != null) {
					//System.out.println("Receiving proposals");
					System.out.println(getAID().getName()  + " received a message from " +  reply.getSender().getName() + ". The message is: " + reply.getContent());
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
				//System.out.println("Sending accept/reject");
				ACLMessage accept = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				accept.addReceiver(bestLift);
				accept.setContent("I chose you");
				send(accept);
				ACLMessage reject = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				for (AID aid : liftsAID) {
					if (aid != bestLift)
						reject.addReceiver(aid);
				}
				reject.setContent("I don't chose you");
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

	public void setBuilding(BuildingSpace buildingSpace){
		this.buildingSpace = buildingSpace;
	}
	
	public void generateCall(int numFloors) {
		Random generator = new Random();
		int floor = generator.nextInt(numFloors);
		Direction direction;
		if (generator.nextInt(2) == 0)
			direction = Direction.UP;
		else
			direction = Direction.DOWN;
		System.out.println("I'm on floor " + floor + " and I want to go " + direction);
		//buildingSpace.putCall(floor);
		requestLift(new Task(floor, direction));
		
		/*int rnd = -1;
		do {
			rnd = generator.nextInt(numFloors);
		} while(rnd == origFloor);
		int destFloor = rnd;*/
	}
	
	public void addAID(AID liftAID) {
		liftsAID.add(liftAID);
	}
	
	public void requestLift(Task request) {
		addBehaviour(new RequestLift(request));
	}
	
}
