/**
 * 
 */
package com.yxm.library.bean;

import com.yxm.library.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * 每一步棋的记录
 *
 */
public class PieceProcess {
	public int bw;
	public Coordinate c;
	public List<PieceProcess> removedList;

	public int putType=0; //0-真实落子 1-预落子  //2019-01-16 修改，处理预落子模式时，引起的提子数加倍的问题

	public int resultBlackCount;
	public int resultWhiteCount;

	public PieceProcess(int bw, Coordinate c, List<PieceProcess> removedList) {
		this.bw = bw;
		this.c = c;
		this.removedList=removedList;
	}
	
	public PieceProcess(int bw, Coordinate c , int putType) {
		this.bw = bw;
		this.c = c;
		this.removedList= new ArrayList<>();
		this.putType=putType;
	}

	public int getPutType() {
		return putType;
	}

	public void setPutType(int putType) {
		this.putType = putType;
	}

}