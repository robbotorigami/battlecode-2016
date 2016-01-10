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
				for(Signal message : rc.emptySignalQueue()){
					if(ComSystem.getFlag( message ) == 0x00){
						den = ComSystem.intToMap(message.getMessage()[1]);
						state = GuardState.ATTACKING;
						initialLocation = rc.getLocation();
						break;
					}
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
				try {
					if(rc.canSense(den) && rc.senseRobotAtLocation(den) == null){
						state = GuardState.RETURNING;
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

}
