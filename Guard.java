package team022;

import battlecode.common.*;

public class Guard extends BaseRobot {

	MapLocation initialLocation;
	GuardState state = GuardState.DEFENDING;
	MapLocation den;

	public enum GuardState{
		DEFENDING, ATTACKING, RETURNING;
	}

	public Guard(RobotController rcin) {
		super(rcin);
		// TODO Auto-generated constructor stub
		initialLocation = rc.getLocation();
	}

	@Override
	public void run() {
		while(true){
			switch(state){
			case DEFENDING:
				recieveTargets();
				MapLocation target = closestTarget();
				if(target != null){
					den = target;
					state = GuardState.ATTACKING;
					initialLocation = rc.getLocation();
					break;
				}
				if(rc.isCoreReady() && rand.nextDouble() < 0.5){
					try {
						springCleaning();
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
				break;
			case ATTACKING:
				rc.setIndicatorString(2, den.toString());
				try {
					if(denGone()){
						state = GuardState.RETURNING;
						destroyedTargets.add(den);
						targets.remove(den);
						break;
					}
				} catch (GameActionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(rc.isCoreReady() && rand.nextDouble() < 0.5){
					try {
						springCleaning();
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				attack(den);

				Clock.yield();
				break;
			case RETURNING:
				RobotInfo[] ourBots = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
				boolean archon = false;
				for(RobotInfo bot : ourBots){
					if(bot.type == RobotType.ARCHON){
						archon = true;
					}
				}
				if(archon){
					state = GuardState.DEFENDING;
					break;
				}else{
					slugPathing(initialLocation);
					Clock.yield();
				}
				break;
				
			}
			rc.setIndicatorString(0, state.toString());
		}
	}

	private boolean denGone() throws GameActionException {
		if(rc.canSense(den)){
			RobotInfo checker = rc.senseRobotAtLocation(den);
			if(checker == null || checker.type != RobotType.ZOMBIEDEN){
				return true;
			}
		}
		return false;
	}

}
