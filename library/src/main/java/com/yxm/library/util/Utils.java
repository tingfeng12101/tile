package com.yxm.library.util;


import com.yxm.library.Board;
import com.yxm.library.Coordinate;

public class Utils {

	public static int boardSize=19;

	//创建棋盘的星
	public static Coordinate[] createStar(){
		Coordinate[] cs=new Coordinate[9];
		
		
		int star=4;
		int dao3=boardSize-3;
		if(boardSize<=9)
		{
			star=3;
			dao3=boardSize-2;
		}
		

		cs[0]=new Coordinate(star,star);
		cs[1]=new Coordinate(dao3,star);
		cs[2]=new Coordinate(star,dao3);
		cs[3]=new Coordinate(dao3,dao3);
		
		int zhong=(boardSize+1)/2;
		
		cs[4]=new Coordinate(star,zhong);
		cs[5]=new Coordinate(zhong,star);
		cs[6]=new Coordinate(zhong,dao3);
		cs[7]=new Coordinate(dao3,zhong);
		
		cs[8]=new Coordinate(zhong,zhong);
		
		return cs;
	}
	
	// 翻转颜色
	public static int getReBW(int bw) {
		if (bw == Board.White)
			return Board.Black;
		if (bw == Board.Black)
			return Board.White;
		/**
		 * 此行为改动代码  李朦利2016-3-8 为实现题库标记功能。
		 */
		return Board.FLAG;
	}
}
