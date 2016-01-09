package team022;

import java.util.ArrayList;

import battlecode.common.*;

public class Scout extends BaseRobot {

	public boolean[][] scoutedLocations;
	public MapLocation initalLocation;
	
	public int mapTop = -100000;
	public int mapBottom = 100000;
	public int mapLeft = -1000000;
	public int mapRight = 1000000;

	public Scout(RobotController rcin) {
		super(rcin);
		scoutedLocations = new boolean[300][300];
		initalLocation = rc.getLocation();
	}

	@Override
	public void run() {
		
		while(true){
			try {
				DashAway(0.0);
			} catch (GameActionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			MapLocation nextTarget = findNearestUnexploredBlock();
			rc.setIndicatorString(2, "Bytecode Used:" + Clock.getBytecodeNum());
			if(nextTarget != null){
				try {
					moveAsCloseToDirection(rc.getLocation().directionTo(nextTarget), true);
					rc.setIndicatorString(1, String.format("Goto (%d, %d)", nextTarget.x, nextTarget.y));
				} catch (GameActionException e) {
					e.printStackTrace();
				}
			}
			try {
				recordMap();
			} catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rc.setIndicatorString(0, String.format("Top:%d Bottom:%d Left:%d Right:%d", mapTop, mapBottom, mapLeft, mapRight));
			if(rc.getRoundNum()%100 == 0){
				printScoutedArea();
			}
			Clock.yield();
		}

	}
	
	private void printScoutedArea() {
		for(int i = 0; i < scoutedLocations.length; i++){
			for(int j = 0; j < scoutedLocations[i].length; j++){
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
		MapLocation currentLocation = rc.getLocation();
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
	}
	
	public void recordMap() throws GameActionException{
		MapLocation[] visable = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(),rc.getType().sensorRadiusSquared);
		for(MapLocation square : visable){
			if(!rc.onTheMap(square)){
				edgeOTheWorld(square);
			}
			square = globalToRelative(square);
			if(!scoutedLocations[square.x][square.y]){
				scoutedLocations[square.x][square.y] = true;				
			}		
			
		}
	}
	
	private void edgeOTheWorld(MapLocation square) {
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
		if(x < mapLeft || x > mapRight || y < mapTop || y > mapRight){
			return false;
		}
		MapLocation loc = globalToRelative(new MapLocation(x, y));
		return !scoutedLocations[loc.x][loc.y];
	}

}
