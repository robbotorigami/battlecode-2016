package team022;

import java.util.ArrayList;
import java.util.Collections;

import battlecode.common.*;

public class Archon extends BaseRobot {

	public int myRank = 0;

	public Archon(RobotController rcin) {
		super(rcin);
	}

	@Override
	public void run() {
		sayHello();
		Clock.yield();
		judgeSociety();
		if(myRank == 0){
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
		while(true){
			if(rc.isCoreReady()){
				Direction[] toTry = directionValues;
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

	public void sayHello() {

		try {
			rc.broadcastSignal(10000);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rc.setIndicatorString(0, "said hello");
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

}
