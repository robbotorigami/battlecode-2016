package team022;

import battlecode.common.*;

public class Turret extends BaseRobot {

	public Turret(RobotController rcin) {
		super(rcin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while(true){
			rc.setIndicatorString(0,  "");
			for(Signal Message: rc.emptySignalQueue()){
				if(Message.getMessage() != null){
					if(ComSystem.getFlag(Message) == 0x42){
						rc.setIndicatorString(0, "Pack it up boys");
						try {
							rc.pack();
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TTM Temp = new TTM(rc);
						Temp.run_ret(Message.getLocation());
					}
					break;
				}
			}
			/*
			for(RobotInfo stuff : rc.senseNearbyRobots(2, rc.getTeam())){
				if(stuff.type == RobotType.TTM){
					TTM Temp = new TTM(rc);
					Temp.run_ret();
					break;
				}
			}*/
			try {
				destroy();
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			Clock.yield();
		}

	}

}
