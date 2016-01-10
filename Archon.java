package team022;

import java.util.ArrayList;
import java.util.Collections;

import battlecode.common.*;

public class Archon extends BaseRobot {

	public int myRank = 0;
	
	public double SoldierSpawnProp = 0.2;
	public double GuardSpawnProp = 0.6;
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
			try {
				DashAway(4.0);
			} catch (GameActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(rc.isCoreReady()){
				spawnUnitsProp();
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
	
	public void handleMessages(){
		
	}

}
