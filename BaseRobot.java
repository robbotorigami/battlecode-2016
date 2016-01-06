package team022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import battlecode.common.*;

public abstract class BaseRobot {
	public RobotController rc;
	public Random rand; //Random number generator
	public Direction[] directionValues;
	
	public BaseRobot(RobotController rcin){
		rc = rcin;
		rand = new Random(rc.getID());
		directionValues = new Direction[]{Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST,
                Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		
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
	public boolean moveAsCloseToDirection(Direction toMove, boolean allowBackwards) throws GameActionException{
		if(rc.isCoreReady()){
			Direction[] toTry;
			if(allowBackwards){
				toTry = new Direction[]{toMove,
						toMove.rotateLeft(),
						toMove.rotateRight(),
						toMove.rotateLeft().rotateLeft(),
						toMove.rotateRight().rotateRight(),
						toMove.rotateLeft().rotateLeft().rotateLeft(),
						toMove.rotateRight().rotateRight().rotateRight(),
						toMove.rotateLeft().rotateLeft().rotateLeft().rotateLeft()
				};
			}else{
				toTry = new Direction[]{toMove,
						toMove.rotateLeft(),
						toMove.rotateRight(),
						toMove.rotateLeft().rotateLeft(),
						toMove.rotateRight().rotateRight()
				};
			}
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
		return directionValues[rand.nextInt(8)];
	}
	
	/**
	 * preliminary basic combat micro
	 * @throws GameActionException 
	 */
	public void kiteingMicro() throws GameActionException{
		if(rc.isCoreReady()){
			DashAway(0.0);
		}
		if(rc.isWeaponReady()){
			destroy();
		}
	}
	
	/**
	 * If there is more than numRobots in radSquared tiles, move away from them
	 * Note: only uses robots from our team
	 * @throws GameActionException 
	 */
	public void spreadOut(int numRobots, int radSquared) throws GameActionException{
		RobotInfo[] ourBots = rc.senseNearbyRobots(radSquared, rc.getTeam());
		rc.setIndicatorString(0, ourBots.length + " Nearby");
		if(ourBots.length > numRobots){
			int avgx = 0;
			int avgy = 0;
			for(RobotInfo bot : ourBots){
				avgx += bot.location.x;
				avgy += bot.location.y;
			}
			avgx /= ourBots.length;
			avgy /= ourBots.length;
			moveAsCloseToDirection(new MapLocation(avgx, avgy).directionTo(rc.getLocation()), false);
		}
	}
	
	/**
	 * Code for moving away from enemy robots
	 */
	public boolean DashAway(double bravado) throws GameActionException{
		MapLocation[] adjacent = getAllAdjacent(rc.getLocation());
		RobotInfo[] enemies = rc.senseNearbyRobots(1000, rc.getTeam().opponent());
		RobotInfo[] zombles = rc.senseNearbyRobots(1000, Team.ZOMBIE);
		
		ArrayList<RobotInfo> danger = new ArrayList<RobotInfo>(Arrays.asList(enemies));
		danger.addAll(Arrays.asList(zombles));
		
		//Have danger score for all adjacent squares, plus one for our square
		
		double[] dangerscore = new double[adjacent.length];
		
		for(RobotInfo badGuy : danger){
			for(int i = 0; i < adjacent.length; i++){
				if(badGuy.type.canAttack() && badGuy.type.attackRadiusSquared >= adjacent[i].distanceSquaredTo(badGuy.location)
						&& badGuy.weaponDelay <= 1.0){
					dangerscore[i] += badGuy.attackPower;
				}
			}
		}

		
		if(dangerscore[0] > bravado){
			double best = dangerscore[0];
			MapLocation toMoveTo = null;
			for(int i = 1; i < dangerscore.length; i++){
				if(best > dangerscore[i] && rc.canMove(rc.getLocation().directionTo(adjacent[i]))){
					best = dangerscore[i];
					toMoveTo = adjacent[i];
				}
			}
			if(toMoveTo != null){
				if(rc.isCoreReady()){
					rc.move(rc.getLocation().directionTo(toMoveTo));
					return true;
				}
			}else{
				//We aren't in open terrain! So, we're going to try and run away from the average
				double vecx = 0;
				double vecy = 0;
				for(int i = 1; i < dangerscore.length; i++){
					vecx += dangerscore[i] * dirToCos(directionValues[i-1]);
					vecy += dangerscore[i] * dirToSin(directionValues[i-1]);
				}
				System.out.println(""+vecx+" "+vecy);
				if(vecx != 0 || vecy != 0){
					double angle = Math.atan2(vecy, vecx);
					Direction desired = angleToDir(angle).opposite();
					return moveAsCloseToDirection(desired, false);
				}
			}
		}
		return false;
	}
	
	/**
	 * Given an angle, return the closest possible direction
	 */
	Direction angleToDir(double angle){
		angle = Math.toDegrees(angle);
		double[] angles = {90, 45, 0, 315, 270, 225, 180, 135};
		double best = 10000;
		int index = 0;
		for(int i = 0; i < 8; i++){
			if(Math.abs(angles[i] - angle) < best){
				best = Math.abs(angles[i] - angle);
				index = i;
			}
		}
		return directionValues[index];
	}
	
	/**
	 * Converts given direction into cos of angle
	 */
	double dirToCos(Direction dir){
		switch(dir){
		case EAST:
			return 1;
		case NORTH_EAST:
			return 1/Math.sqrt(2);
		case NORTH:
			return 0;
		case NORTH_WEST:
			return -1/Math.sqrt(2);
		case WEST:
			return -1;
		case SOUTH_WEST:
			return -1/Math.sqrt(2);
		case SOUTH:
			return 0;
		case SOUTH_EAST:
			return 1/Math.sqrt(2);
		default:
			return 0;
		}
	}
	
	/**
	 * Converts given direction into sin of angle
	 */
	double dirToSin(Direction dir){
		switch(dir){
		case EAST:
			return 0;
		case NORTH_EAST:
			return 1/Math.sqrt(2);
		case NORTH:
			return 1;
		case NORTH_WEST:
			return 1/Math.sqrt(2);
		case WEST:
			return 0;
		case SOUTH_WEST:
			return -1/Math.sqrt(2);
		case SOUTH:
			return -1;
		case SOUTH_EAST:
			return -1/Math.sqrt(2);
		default:
			return 0;
		}
	}
	
	/**
	 * Gets all adjacent map squares. NOTE: the zero index is the square the robot is on
	 */
	public MapLocation[] getAllAdjacent(MapLocation center){
		MapLocation[] adjacent = new MapLocation[9];
		adjacent[0] = center;
		for(int i = 0; i<directionValues.length; i++){
			adjacent[i+1] = center.add(directionValues[i]);
		}
		return adjacent;
	}
	
	/**
	 * Cleans off the most rubble heavy square in range
	 * @throws GameActionException 
	 */
	public void springCleaning() throws GameActionException{
		if(rc.isCoreReady()){
			MapLocation[] squares = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 2);
			double mostRubble = 0;
			MapLocation toClean = null;
			for(MapLocation square : squares){
				if(rc.senseRubble(square) > mostRubble){
					mostRubble = rc.senseRubble(square);
					toClean = square;
				}
			}

			if(toClean != null){
				rc.clearRubble(rc.getLocation().directionTo(toClean));
			}
		}
	}
	
	/**
	 * Better shooting code
	 * @throws GameActionException
	 */
	public void destroy() throws GameActionException {
		ArrayList<RobotInfo> enemies = new ArrayList<RobotInfo>(Arrays.asList(rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam().opponent())));
		enemies.addAll(new ArrayList<RobotInfo>(Arrays.asList(rc.senseNearbyRobots(rc.getType().attackRadiusSquared, Team.ZOMBIE))));
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
