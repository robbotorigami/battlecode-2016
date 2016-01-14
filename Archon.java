package team022;

import java.util.ArrayList;
import java.util.Collections;

import battlecode.common.*;

public class Archon extends BaseRobot {

	public int myRank = 0;
	
	public double SoldierSpawnProp = 0.5;
	public double GuardSpawnProp = 0.3;
	public double TurretSpawnProp = 0.2;
	
	
	

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
		rc.setIndicatorString(1, ""+archonCount+myRank);

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

}
