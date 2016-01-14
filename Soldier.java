package team022;

import battlecode.common.*;

public class Soldier extends BaseRobot {
	
	public MapLocation den;
	public MapLocation initialLocation;
	public GuardState state = GuardState.DEFENDING;

	public Soldier(RobotController rcin) {
		super(rcin);
	}
	
	public enum GuardState{
		DEFENDING, ATTACKING, RETURNING;
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
				iNeedMySpaceMom();
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
		/*
		while(true){
			RobotInfo[] bots = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
			boolean neededToMove = false;
			for(RobotInfo bot : bots){
				if(bot.type == RobotType.ARCHON){
					if(rc.getLocation().distanceSquaredTo(bot.location) < (3 + 0.005 * rc.getRoundNum())){
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
				defense();
			}
			try {
				springCleaning();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Clock.yield();
		}
		*/

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
	
	public MapLocation findArchon(){
		for( RobotInfo bot: rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam())){
			if(bot.type == RobotType.ARCHON){
				return bot.location;
			}
		}
		return null;
	}
	
	public void iNeedMySpaceMom(){
		MapLocation mommy = findArchon();
		if(mommy != null){
			if(rc.getLocation().distanceSquaredTo(mommy) < 10){
				if(rand.nextDouble() < 0.4){
					try {
						moveAsCloseToDirection(mommy.directionTo(rc.getLocation()), false);
					} catch (GameActionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
