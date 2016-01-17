package team022;

import battlecode.common.*;

public class TTM extends BaseRobot {
	
	public MapLocation archonLoc = null;

	public TTM(RobotController rcin) {
		super(rcin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while(true){
			receiveAllMessages();
			if(archonLoc != null){
				boolean notMoved = true;
				while(notMoved){
					try {
						notMoved  = moveAsCloseToDirection(archonLoc.directionTo(rc.getLocation()), false);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Clock.yield();
				}
				try {
					rc.unpack();
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void run_ret(MapLocation archonLoc){
		while(archonLoc == null)
			receiveAllMessages();
		boolean notMoved = true;
		while(notMoved){
			try {
				notMoved  = !moveAsCloseToDirection(archonLoc.directionTo(rc.getLocation()), false);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Clock.yield();
		}
		try {
			rc.unpack();
			rc.emptySignalQueue();
			for(int i = 0; i < 10; i++)
				Clock.yield();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void receiveAllMessages(){
		for(Signal message : rc.emptySignalQueue()){
			if(ComSystem.getFlag(message) == 0x42){
				archonLoc = message.getLocation();
				break;
			}
		}
	}
	
	

}
