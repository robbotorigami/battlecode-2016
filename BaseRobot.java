package team022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import battlecode.common.*;

public abstract class BaseRobot {
	public RobotController rc;
	public Random rand; //Random number generator
	
	public BaseRobot(RobotController rcin){
		rc = rcin;
		rand = new Random(rc.getID());
		
	}

	//Abstract method for major functionality
	public abstract void run();
	
	//-------------------Base Methods for all Units------------------
	//shoot enemy with lowest HP within range
	public void shootWeakest() throws GameActionException{
		RobotInfo[] enemiesInRange = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent());
		double LowestHealth = 0;
		RobotInfo weakestLink = null;
		for(RobotInfo ri:enemiesInRange){
			if(ri.health>LowestHealth){
				weakestLink = ri;
				LowestHealth = ri.health;
			}
		}
		if(weakestLink != null){
			if(rc.isWeaponReady()&&rc.canAttackLocation(weakestLink.location)){
				rc.attackLocation(weakestLink.location);
			}
		}
	}
	
	//Move as close as possible to the provided direction
	public boolean moveAsCloseToDirection(Direction toMove) throws GameActionException{
		if(rc.isCoreReady()){
			Direction[] toTry = {toMove,
					toMove.rotateLeft(),
					toMove.rotateRight(),
					toMove.rotateLeft().rotateLeft(),
					toMove.rotateRight().rotateRight(),
					toMove.rotateLeft().rotateLeft().rotateLeft(),
					toMove.rotateRight().rotateRight().rotateRight(),
					toMove.rotateLeft().rotateLeft().rotateLeft().rotateLeft()
			};
			for(Direction dir:toTry){
				if(rc.canMove(dir)&&rc.isCoreReady()){
					rc.move(dir);
					return true;
				}
			}
		}	
		return false;
	}
	
	//Returns a random direction	
	public Direction getRandomDirection(){
		return Direction.values()[rand.nextInt(8)];
	}
	
	/**
	 * Better shooting code
	 * @throws GameActionException
	 */
	public void destroy() throws GameActionException {
		ArrayList<RobotInfo> enemies = new ArrayList<RobotInfo>(Arrays.asList(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam().opponent())));
		enemies.addAll(new ArrayList<RobotInfo>(Arrays.asList(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE))));
		rc.setIndicatorString(1, ""+enemies.size());
		RobotInfo toDestroy = null;
		int lowestHealth =100000;
		int lowestID = 1000000;
		for(RobotInfo enemy : enemies){
			boolean beatsBest = (enemy.health < lowestHealth)? true: enemy.ID < lowestID;
			if(beatsBest){
				lowestHealth = (int) enemy.health;
				lowestID = enemy.ID;
				toDestroy = enemy;
			}
		}
		if(toDestroy != null){
			rc.setIndicatorString(1, "Should be killing" + toDestroy.ID);
			if(rc.isWeaponReady()&&rc.canAttackLocation(toDestroy.location)){
				rc.attackLocation(toDestroy.location);
			}
		}
		
		
	}
}
