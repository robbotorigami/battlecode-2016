package team022;

import battlecode.common.*;

public class Soldier extends BaseRobot {

	public Soldier(RobotController rcin) {
		super(rcin);
	}

	@Override
	public void run() {
		while(true){
			RobotInfo[] bots = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
			boolean neededToMove = false;
			for(RobotInfo bot : bots){
				if(bot.type == RobotType.ARCHON){
					if(rc.getLocation().distanceSquaredTo(bot.location) < 3){
						if(rc.canMove(rc.getLocation().directionTo(bot.location).opposite())){
							neededToMove = true;
							if(rc.isCoreReady()){
								try {
									rc.move(rc.getLocation().directionTo(bot.location).opposite());
								} catch (GameActionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			if(!neededToMove){
				rc.setIndicatorString(0, "In Position, TIME TO KILL!!!");
				try {
					destroy();
				} catch (GameActionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				rc.setIndicatorString(0, "Not in position");
			}
			Clock.yield();
		}

	}

}
