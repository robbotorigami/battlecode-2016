package team022;

import java.util.ArrayList;

import battlecode.common.*;

public class Scout extends BaseRobot {

	public boolean[][] scoutedLocations;
	public MapLocation initalLocation;
	
	public scoutingStyle ourStyle = scoutingStyle.EXPLORATORY;
	
	public int mapTop = -100000;
	public int mapBottom = 100000;
	public int mapLeft = -1000000;
	public int mapRight = 1000000;

	public Scout(RobotController rcin) {
		super(rcin);
		scoutedLocations = new boolean[300][300];
		initalLocation = rc.getLocation();
		try {
			recordMapInit();
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public enum scoutingStyle{
		EXPLORATORY, CIRCLE_FROM_SPAWN, SIMPLE;
	}

	@Override
	public void run() {
		double randomAngle = rand.nextDouble()* 2 * Math.PI;
		int radius = 5;
		
		while(true){
			//MapLocation target = calculateTarget(randomAngle, radius);
			
			
			int prof0 = Clock.getBytecodeNum();
			int rold0 = rc.getRoundNum();
			try {
				DashAway(0.0);
			} catch (GameActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			int prof1 = Clock.getBytecodeNum();
			int rold1 = rc.getRoundNum();
			if(rc.isCoreReady()){
				MapLocation nextTarget = findNearestUnexploredBlock();
				if(nextTarget != null){
					rc.setIndicatorString(1, String.format("Goto: (%d, %d)", nextTarget.x, nextTarget.y));
					slugPathing(nextTarget);
				}
			}
			int prof2 = Clock.getBytecodeNum();
			int rold2 = rc.getRoundNum();
			try {
				recordMapInit();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int prof3 = Clock.getBytecodeNum();
			int rold3 = rc.getRoundNum();

			rc.setIndicatorString(2, String.format("Dash: %d, nextTarget+move: %d, Record: %d", prof1-prof0 + 20000*(rold1-rold0), prof2-prof1+ 20000*(rold2-rold1), prof3 - prof2+ 20000*(rold3-rold2)));
			rc.setIndicatorString(0, String.format("Top:%d Bottom:%d Left:%d Right:%d", mapTop, mapBottom, mapLeft, mapRight));
//			if(rc.getRoundNum()%100 == 0){
//				printScoutedArea();
//			}
			scan();
			Clock.yield();
		}

	}
	
	private MapLocation calculateTarget(double randomAngle, int radius) {
		int x = (int) (Math.cos(randomAngle) * radius);
		return null;
	}

	private void printScoutedArea() {
		for(int i = 130; i < 170; i++){
			for(int j = 130; j < 170; j++){
				if(scoutedLocations[i][j]){
					System.out.print('.');
				}else{
					System.out.print('X');
				}
			}
			System.out.println();
		}
		
	}

	public MapLocation findNearestUnexploredBlock(){
		switch(ourStyle){
		case EXPLORATORY:
			MapLocation currentLocation = rc.getLocation();
			for(int size = 7; size < 50; size+=1){
				int i, j;
				//Upper row
				i = size;
				for(j = -size; j <=size; j++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}
				//Lower row
				i = -size;
				for(j = -size; j <=size; j++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}

				//Left row
				j = -size;
				for(i = -size; i <=size; i++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}

				//Right row
				j = size;
				for(i = -size; i <=size; i++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}
			}

			return null;
		case CIRCLE_FROM_SPAWN:
			currentLocation = initalLocation;
			for(int size = 6; size < 50; size++){
				int i, j;
				//Upper row
				i = size;
				for(j = -size; j <=size; j++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}
				//Lower row
				i = -size;
				for(j = -size; j <=size; j++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}

				//Left row
				j = -size;
				for(i = -size; i <=size; i++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}

				//Right row
				j = size;
				for(i = -size; i <=size; i++){
					if(needsFreedom(currentLocation.x+i, currentLocation.y+j)){
						return new MapLocation(currentLocation.x +i, currentLocation.y +j);
					}
				}
			}

			return null;
			
		case SIMPLE:
			int[] distanceToNextBlock = new int[8];
			for(int i = 0; i < 8; i++){
				MapLocation block = rc.getLocation();
				for(int j = 0; j < 8; j++){
					if(block.x < mapLeft || block.x > mapRight || block.y < mapTop || block.y > mapBottom){
						block = block.add(directionValues[i], 100);
						break;
					}
					if(!needsFreedom(block.x, block.y)){
						block = block.add(directionValues[i], 6);
					}else{
						break;
					}
				}
				distanceToNextBlock[i] = block.distanceSquaredTo(rc.getLocation());
			}
			int smallestDistance = 1000000;
			int index = 0;
			for(int i = 0; i < 8; i++){
				if(smallestDistance > distanceToNextBlock[i]){
					index = i;
					smallestDistance = distanceToNextBlock[i];
				}
				System.out.println(""+i + " " + distanceToNextBlock[i]);
			}
			MapLocation toReturn = rc.getLocation().add(directionValues[index], smallestDistance);
			rc.setIndicatorDot(toReturn, 0, 255, 255);
			return toReturn;
		}
		return null;
	}
	
	public void recordMap() throws GameActionException{
		MapLocation[] visable = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(),rc.getType().sensorRadiusSquared);
		for(MapLocation square : visable){
			if(rc.getLocation().distanceSquaredTo(square) > 40){
				if(!rc.onTheMap(square)){
					edgeOTheWorld(square);
				}
				square = globalToRelative(square);
				scoutedLocations[square.x][square.y] = true;	
			}
		}
	}

	public void recordMapInit() throws GameActionException{
		MapLocation[] visable = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(),rc.getType().sensorRadiusSquared);
		for(MapLocation square : visable){
			if(!rc.onTheMap(square)){
				edgeOTheWorld(square);
			}
			square = globalToRelative(square);
			scoutedLocations[square.x][square.y] = true;	
		}
	}
	
	public void scan(){
		RobotInfo[] zombles = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, Team.ZOMBIE);
		for(RobotInfo zomble : zombles){
			if(zomble.type == RobotType.ZOMBIEDEN){
				weGotOne(zomble.location);
			}
		}
	}
	
	public void weGotOne(MapLocation denLoc){
		int loc = ComSystem.mapToInt(denLoc);
		try {
			rc.broadcastMessageSignal(0x01, loc, rc.getLocation().distanceSquaredTo(initalLocation)+ 100);
		} catch (GameActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void edgeOTheWorld(MapLocation square) {
		if(!(rc.getLocation().x == square.x) && !(rc.getLocation().y == square.y))return;
		
		switch(rc.getLocation().directionTo(square)){
		case NORTH:
			if(mapTop < square.y)
				mapTop = square.y;
			break;
		case SOUTH:
			if(mapBottom > square.y)
				mapBottom = square.y;
			break;
		case EAST:
			if(mapRight > square.x)
				mapRight = square.x;
			break;
		case WEST:
			if(mapLeft < square.x)
				mapLeft = square.x;
			break;
		default:
		}
		
	}

	public MapLocation globalToRelative(MapLocation toConvert){
		return new MapLocation(toConvert.x - initalLocation.x +150, toConvert.y - initalLocation.y +150);
	}
	
	public MapLocation relativeToGlobal(MapLocation toConvert){
		return new MapLocation(toConvert.x + initalLocation.x - 150, toConvert.y + initalLocation.y - 150);
	}
	
	public boolean needsFreedom(int x, int y){
		if(x < mapLeft || x > mapRight || y < mapTop || y > mapBottom){
			return false;
		}
		MapLocation loc = globalToRelative(new MapLocation(x, y));
		return !scoutedLocations[loc.x][loc.y];
	}

}
