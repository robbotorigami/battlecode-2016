package team022;

import java.util.ArrayList;
import java.util.Collections;

import battlecode.common.*;

public class Archon extends BaseRobot {

	public int myRank = 0;
	
	public double SoldierSpawnProp = 0.8;
	public double GuardSpawnProp = 0.0;
	public double TurretSpawnProp = 0.2;
	
	public MapLocation bossArchon;
	
	

	public Archon(RobotController rcin) {
		super(rcin);
	}

	@Override
	public void run() {
		sayHello();
		Clock.yield();
		judgeSociety();
		
		if(myRank == 0){
			makeScout();
		}
		
		switch(lifePlan){
		case MONGOLS:
			hoarders();
			break;
		case TURRTLE:
			turtle();
			break;
		}

	}
	
	public void turtle(){
		//If we aren't boss, move toward boss
		if(myRank != 0){
			int breakout = 0;
			while(rc.getLocation().distanceSquaredTo(bossArchon) > 2){
				if(rc.isCoreReady()){
					slugPathing(bossArchon);
					breakout ++;
					Clock.yield();
					if(breakout > 50) break;
				}
			}
		}
		while(true){
			if(myRank == 0){
				handleExpansion();
			}
			obamaCare();
			updateSpawnRates();
			spawnUnitsProp();
			Clock.yield();
		}
	}
	
	public void handleExpansion(){
		MapLocation[] nearby = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 2);
		boolean overCrowded = true;
		for(MapLocation toCheck : nearby){
			try {
				if(rc.senseRobotAtLocation(toCheck) == null){
					overCrowded = false;
				}
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(overCrowded){
			try {
				rc.broadcastMessageSignal(0x42, 0, 2);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateSpawnRates(){
		//this method only is run for turtling archons
		//We want to spawn ~ 10 soldiers to set up a perimiter, then start spawning more turrets and a few guards
		//So if we can only see <~10 of our units, we probably want to make some soldiers
		if(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()).length < 10){
			SoldierSpawnProp = 1.0;
			GuardSpawnProp = 0.0;
			TurretSpawnProp = 0.0;
		}else{
			SoldierSpawnProp = 0.0;
			GuardSpawnProp = 0.0;
			TurretSpawnProp = 1.0 - SoldierSpawnProp;
		}
	}
	
	public int soldiersInRange(){
		int count = 0;
		for(RobotInfo bot : rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam())){
			if(bot.type == RobotType.SOLDIER){
				count++;
			}
		}
		return count;
	}
	
	private void sendMessages(){
		for(MapLocation zombieDen : targets){
			try {
				rc.broadcastMessageSignal(0x00, ComSystem.mapToInt(zombieDen), 30);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void spawnUnitsProp(){
		RobotType spawnMeister = null;
		double diceRoll = rand.nextDouble();
		if(diceRoll < SoldierSpawnProp){
			spawnMeister = RobotType.SOLDIER;
		}else{
			diceRoll -= SoldierSpawnProp;
			if(diceRoll < GuardSpawnProp){
				spawnMeister = RobotType.GUARD;
			}else{
				diceRoll -= GuardSpawnProp;
				if(diceRoll < TurretSpawnProp){
					spawnMeister = RobotType.TURRET;
				}else{
					spawnMeister = RobotType.VIPER;
				}
			}
		}
		if(myRank < 2){
			if(rc.isCoreReady()){
				Direction[] toTry = directionValues;
				for(Direction dir : toTry){
					if(rc.canBuild(dir,  spawnMeister)&& rc.isCoreReady()){
						try {
							rc.build(dir, spawnMeister);
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}else{
			
		}
	}

	private void makeScout() {
		outer: while(true){
			if(rc.isCoreReady()){
				for(Direction dir : directionValues){
					if(rc.canBuild(dir,  RobotType.SCOUT)){
						try {
							rc.build(dir, RobotType.SCOUT);
							break outer;
						} catch (GameActionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
	}
	

	public void sayHello() {

		try {
			rc.broadcastSignal(10000);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void judgeSociety(){
		Signal[] archons = rc.emptySignalQueue();
		ArrayList<Integer> IDList= new ArrayList<Integer>();
		IDList.add(rc.getID());
		for(Signal archon : archons){
			if(archon.getTeam() == rc.getTeam()){
				IDList.add(archon.getID());
				archonCount++;
			}
		}

		Collections.sort(IDList);
		myRank = IDList.indexOf(rc.getID());
		
		bossArchon = rc.getLocation();
		
		for(Signal archon : archons){
			if(archon.getID() ==  IDList.get(0)){
				bossArchon = archon.getLocation();
				break;
			}
		}
		rc.setIndicatorString(1, ""+archonCount+myRank+" "+bossArchon.toString());

	}
	
	public void obamaCare(){
		RobotInfo[] patients = rc.senseNearbyRobots(rc.getType().attackRadiusSquared, rc.getTeam());
		double lowestHP = 1000000;
		RobotInfo deadyMcDeaderson = null;
		for(RobotInfo patient : patients){
			if(patient.health/patient.type.maxHealth < lowestHP && patient.type != RobotType.ARCHON){
				deadyMcDeaderson = patient;
				lowestHP = patient.health/patient.type.maxHealth;
			}
		}
		if(deadyMcDeaderson != null){
			try {
				rc.repair(deadyMcDeaderson.location);
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void handleMessages(){
		for(Signal message : rc.emptySignalQueue()){
			if(ComSystem.getFlag(message) == 0x01){
				if(notInTargets(ComSystem.intToMap(message.getMessage()[1])))
					targets.add(ComSystem.intToMap(message.getMessage()[1]));
			}
		}
	}
	
	private boolean denGone(MapLocation den) throws GameActionException {
		if(rc.canSense(den)){
			RobotInfo checker = rc.senseRobotAtLocation(den);
			if(checker == null || checker.type != RobotType.ZOMBIEDEN){
				return true;
			}
		}
		return false;
	}
	
	public void hoarders(){
		while(true){
			Clock.yield();
			try {
				DashAway(0.0);
			} catch (GameActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			obamaCare();
			
			if(threat > 0) continue;
			
			if(myRank > 0 && closestTarget() != null){
				if(rand.nextDouble() < 0.5){
					slugPathing(closestTarget());
				}
			}
			try {
				if(closestTarget() != null)
					if(denGone(closestTarget())){
						destroyedTargets.add(closestTarget());
						targets.remove(closestTarget());
					}
			} catch (GameActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if(rc.isCoreReady()){
				spawnUnitsProp();
			}
			if(rc.getRoundNum()%40 == 0){
				sendMessages();
			}
			
			try {
				springCleaning();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handleMessages();
		}
	}

}
