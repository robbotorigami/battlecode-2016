package team022;

import battlecode.common.*;

public class ComSystem {

	public ComSystem() {
		// TODO Auto-generated constructor stub
	}
	
	public int mapToInt(MapLocation toConvert){
		short x = (short) toConvert.x;
		short y = (short) toConvert.y;
		return (x<<16) | y;
	}
	
	public MapLocation intToMap(int toConvert){
		short x = (short) (toConvert >> 16);
		short y = (short) (toConvert & 0xFFFF);
		return new MapLocation(x, y);
	}

}
