package team022;
//now with 500% more Adam
import battlecode.common.*;

import java.util.Random;

public class RobotPlayer {

    /**
     * Default Run method, handles directing execution to run instructions based on robottype
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) {
    	BaseRobot myself;
        switch(rc.getType()){
        case ARCHON:
        	myself = new Archon(rc);
        	break;
        case SOLDIER:
        	myself = new Soldier(rc);
        	break;
        case GUARD:
        	myself = new Guard(rc);
        	break;
        case SCOUT:
        	myself = new Scout(rc);
        	break;
        case TURRET:
        	myself = new Turret(rc);
        	break;
        case TTM:
        	myself = new TTM(rc);
        	break;
        default:
        	return;
        }
        myself.run();
    }
}
