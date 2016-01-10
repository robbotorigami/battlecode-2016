package team022;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import battlecode.common.*;

public class ComSystem {
	RobotController rc;
	ArrayList<Signal> AnsweringMachine;
    ArrayList<messageHandler> handlers;
	
	class messageHandler{
		byte flag;
		Function handler;
		
		public messageHandler(byte flag, Function handler){
			this.flag = flag;
			this.handler = handler;
		}
	}

	public ComSystem(RobotController rcin) {
		rc = rcin;
		AnsweringMachine = new ArrayList<Signal>();
	}
	
	public static int mapToInt(MapLocation toConvert){
		int x = toConvert.x;
		int y = toConvert.y & 0xFFFF;
		int newInt = ((int)x)<<16 | y;
		return newInt;
	}
	
	public static MapLocation intToMap(int toConvert){
		short x = (short) (toConvert >>> 16 & 0xFFFF);
		short y = (short) (toConvert & 0xFFFF);
		return new MapLocation(x, y);
	}
	
	public static boolean checkFlag(Signal message, byte flag){
		return (message.getMessage()[0] & 0xFF) == flag;
	}
	
	public void readAllMessages(){
		AnsweringMachine.addAll(Arrays.asList(rc.emptySignalQueue()));
	}
	
	public void addHandler(byte flag, Function handler){
		handlers.add(new messageHandler(flag, handler));
	}
	
	public static byte getFlag(Signal message){
		return (byte)( message.getMessage()[0] & 0xFF);
	}
	
	public void removeHandler(byte flag){
		for(messageHandler h : handlers){
			if(h.flag == flag){
				handlers.remove(h);
			}
		}
	}

}
