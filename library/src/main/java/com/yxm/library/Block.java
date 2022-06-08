package com.yxm.library;

import java.util.ArrayList;
import java.util.List;

/**
 * 棋块
 *
 */
public class Block {
	private List<Coordinate> block= new ArrayList<>();
	private int airCount=0;//气数
	private int bw;//颜色
	
	public Block(int bw){
		this.bw=bw;
	}
	
	public int getBw(){
		return bw;
	}
	
	public void add(Coordinate c){
		block.add(c);
	}
	
	public void addAir(int air){
		airCount+=air;
	}
	
	public boolean isLive(){
		if(airCount>0 && block.size()>0)return true;
		return false;
	}
	
	public void each(Function f){
		for(Coordinate c:block){
			f.apply(c);
		}
	}
}
