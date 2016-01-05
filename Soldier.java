package team022;

import battlecode.common.*;

public class Soldier extends BaseRobot {

	public Soldier(RobotController rcin) {
		super(rcin);
	}

	@Override
	public void run() {
		while(true){
			try {
				kiteingMicro();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Clock.yield();
		}

	}

}
