package team022;

import battlecode.common.*;

public class Archon extends BaseRobot {

	public Archon(RobotController rcin) {
		super(rcin);
	}

	@Override
	public void run() {
		sayHello();
		Clock.yield();
		judgeSociety();
		while(true){
			if(rc.isCoreReady()){
				Direction[] toTry = Direction.values();
				for(Direction dir : toTry){
					if(rc.canBuild(dir,  RobotType.SOLDIER)&& rc.isCoreReady()){
						try {
							rc.build(dir, RobotType.SOLDIER);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			try {
				springCleaning();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Clock.yield();
		}

	}

}
