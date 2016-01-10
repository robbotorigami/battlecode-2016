package team022;

import battlecode.common.*;

public class Guard extends BaseRobot {

	public Guard(RobotController rcin) {
		super(rcin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while(true){
			defense();
			if(rc.isCoreReady()){
				try {
					springCleaning();
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Clock.yield();
		}

	}

}
