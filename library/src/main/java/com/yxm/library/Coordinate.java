package com.yxm.library;

public class Coordinate {
	
	public final int x;
	public final int y;
	public int bw;

	private Coordinate[] near;
	public static final int up = 0;
	public static final int down = 1;
	public static final int right = 2;
	public static final int left = 3;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(int x, int y, int bw) {
		this.x = x;
		this.y = y;
		this.bw =bw;
	}

	private Coordinate up(int boardSize ) {
		Coordinate c = new Coordinate(x, y - 1);
		return c.isValid(boardSize) ? c : null;
	}

	private Coordinate down(int boardSize) {
		Coordinate c = new Coordinate(x, y + 1);
		return c.isValid(boardSize) ? c : null;
	}

	private Coordinate right(int boardSize) {
		Coordinate c = new Coordinate(x + 1, y);
		return c.isValid(boardSize) ? c : null;
	}

	private Coordinate left(int boardSize) {
		Coordinate c = new Coordinate(x - 1, y);
		return c.isValid(boardSize) ? c : null;
	}

	private void initNear(int boardSize) {
		if (near == null) {
			near = new Coordinate[4];
			near[up] = up(boardSize);
			near[down] = down(boardSize);
			near[right] = right(boardSize);
			near[left] = left(boardSize);
		}
	}

	public Coordinate getNear(int direction,int boardSize) {
		initNear(boardSize);
		return near[direction];
	}

	//------------------------------------------------------------------------
	
	public boolean isValid(int boardSize) {
		if (x == 0 && y == 0)
			return true;
		
		if (x < 1)
			return false;
		if (y < 1)
			return false;
		if (x > boardSize)
			return false;
		if (y > boardSize)
			return false;
		return true;
	}

	public boolean isValid_old(int boardSize) {
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		if (x > boardSize - 1)
			return false;
		if (y > boardSize - 1)
			return false;
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
}
