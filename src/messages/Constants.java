package messages;

import java.awt.Point;

public class Constants {
	
	public static final char LEFTMOUSEDOWN = 'a';  // Nhap chuot trai
	public static final char LEFTMOUSEUP = 'b';    // Tha chuot trai
	
	public static final char RIGHTMOUSEDOWN = 'c';
	public static final char RIGHTMOUSEUP = 'd';
	
	public static final char LEFTCLICK = 'e';     // click chuot trai
	
	public static final char SCROLLUP = 'h';      // cuon len
	public static final char SCROLLDOWN = 'i';
	
	public static final char KEYBOARD = 'k';
	public static final char KEYCODE = 'l';
	
	public static final char DELIMITER = '/';
	
	public static final char MOVEMOUSE = 'p';       // di chuyen chuot
	
	public static final char REQUESTIMAGE = 'I';
	
	public static final char BTN_NEXT = 't';    // nut next slide
	public static final char BTN_BACK = 'w';	// nut back slide
	
	/*
	 *  Tra ve mot chuoi de phan tich sau nay
	 *  format: MOVEMOUSEintxDELIMITERinty
	 *  ex: 	p5/6
	 */
	public static String createMoveMouseMessage(float x, float y){
		int intx = Math.round(x);
		int inty = Math.round(y);
		return ""+MOVEMOUSE + intx + DELIMITER + inty;
	}
	
	public static Point parseMoveMouseMessage(String message){
		String[] tokens = message.substring(1).split(""+Constants.DELIMITER );
		return new Point(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
	}
}
