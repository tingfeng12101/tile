package com.yxm.library;

import com.yxm.library.bean.PieceProcess;
import com.yxm.library.util.Utils;

import java.util.Arrays;
import java.util.List;

public class Grid {
	int[][] a;
	public boolean needPick = true;

	public int getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}

	public int boardSize=19;

	public Grid() {
		a = new int[boardSize][boardSize];
	}

	public int getValue(Coordinate c) {
		return a[c.x - 1][c.y - 1];
	}

	private void setValue(Coordinate c, int value) {
		a[c.x - 1][c.y - 1] = value;
	}

	/*
	 * 执行棋子过程 p：行棋记录 reverse：是否反悔行棋
	 */
	public void executePieceProcess(PieceProcess p, boolean reverse) {

		if (p.c.x == 0 && p.c.y == 0) // 停一手时
		{
			return;
		}

		if (!reverse) {
			// 非悔棋，即正常行棋。落子后，对应位置标记为黑子或白子，同时，被迟掉的子置为NONE（空白）。
			setValue(p.c, p.bw);
			for (PieceProcess pp : p.removedList) {
				setValue(pp.c, Board.None);
			}
		} else {
			// 悔棋。当前子设置为NONE（空白），被踢掉的子恢复状态。
			for (PieceProcess pp : p.removedList) {
				setValue(pp.c, pp.bw);
			}
			setValue(p.c, Board.None);
		}
	}

	// 落子
	public boolean putPiece(PieceProcess piece) {

		// 先检查坐标是否有效，无效直接返回false
		if (!piece.c.isValid(boardSize))
			return false;

		// 检查档期坐标点是否已经有子，如果有子，则落子无效，直接返回false;
		if (getValue(piece.c) != Board.None)
			return false;

		setValue(piece.c, piece.bw);

		// 五子棋不需要提子
		if (!(boardSize == 15)) {
			//如果是标记字符则不需要提子！
			if (piece.bw != Board.FLAG) {
				startPick(piece.c, piece.bw, piece.removedList,piece.getPutType());
				// 如果落子后会自杀，则返回false
				if (isSuicide(piece.c, piece.bw)) {
					setValue(piece.c, Board.None); // 还原回来
					return false;
				}
			}
		}

		piece.resultBlackCount = getPieceCount(Board.Black);
		piece.resultWhiteCount = getPieceCount(Board.White);

		return true;
	}
	// 落子
	public boolean putPieceNoPick(PieceProcess piece) {

		// 先检查坐标是否有效，无效直接返回false
		if (!piece.c.isValid(boardSize))
			return false;

		// 检查档期坐标点是否已经有子，如果有子，则落子无效，直接返回false;
		if (getValue(piece.c) != Board.None)
			return false;

		setValue(piece.c, piece.bw);

		piece.resultBlackCount = getPieceCount(Board.Black);
		piece.resultWhiteCount = getPieceCount(Board.White);

		return true;
	}

	// 统计盘面棋子数量
	private int getPieceCount(int bw) {
		int c = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (a[i][j] == bw) {
					c++;
				}
			}
		}
		return c;
	}

	// 判断落子会不会直接杀死自己
	private boolean isSuicide(Coordinate c, int bw) {
		boolean[][] v = new boolean[boardSize][boardSize];
		Block block = new Block(bw);
		pick(c, v, bw, block);

		return !block.isLive();

	}

	// ------------------------------------------------------------------提子

	private void startPick(Coordinate c, int bw, List<PieceProcess> removedList,int putType) {
		int reBw = Utils.getReBW(bw);
		pickOther(c, reBw, removedList);

		if(putType==0){
			if (removedList.size() > 0) {
				Board.hasPickother = true;
			}else{
				Board.hasPickother = false;
			}

		}

		// pickSelf(c, bw, removedList);

	}

	private void pickOther(Coordinate c, int bw, List<PieceProcess> removedList) {
		boolean[][] v = new boolean[boardSize][boardSize];

		for (int i = 0; i < 4; i++) {
			Coordinate nc = c.getNear(i,boardSize);
			Block block = new Block(bw);
			pick(nc, v, bw, block);

			if (!block.isLive()) {
				deleteBlock(block, removedList);

			}
		}
	}

	private void pickSelf(Coordinate c, int bw, List<PieceProcess> removedList) {
		boolean[][] v = new boolean[boardSize][boardSize];
		Block block = new Block(bw);
		pick(c, v, bw, block);
		if (!block.isLive()) {
			deleteBlock(block, removedList);
		}
	}

	// 递归构造棋块
	private void pick(Coordinate c, boolean[][] v, int bw, Block block) {
		if (c == null)
			return;
		if (v[c.x - 1][c.y - 1] == true)
			return;

		if (getValue(c) == Board.None) {
			block.addAir(1);
			return;
		}else if(getValue(c) == Board.FLAG){
			block.addAir(1);
			return;
		} else if (getValue(c) != bw) {
			return;
		}

		v[c.x - 1][c.y - 1] = true;
		block.add(c);

		for (int i = 0; i < 4; i++) {
			Coordinate nc = c.getNear(i,boardSize);
			pick(nc, v, bw, block);
		}
	}

	private void deleteBlock(final Block block,
			final List<PieceProcess> removedList) {
		block.each(obj -> {
			Coordinate c = (Coordinate) obj[0];
			a[c.x - 1][c.y - 1] = Board.None;
			removedList.add(new PieceProcess(block.getBw(), c, null));

			return null;
		});
	}

	// ------------------------------------------------------------------

	@Override
	public String toString() {
		String s = "";
		for (int j = 0; j < boardSize; j++) {
			for (int i = 0; i < boardSize; i++) {
				if (a[i][j] == Board.None) {
					s += " +";
				} else if (a[i][j] == Board.White) {
					s += " o";
				} else if (a[i][j] == Board.Black) {
					s += " x";
				}
			}
			s += "\n";
		}
		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(a);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Grid other = (Grid) obj;
		if (!myEqualse(a, other.a, boardSize))
			return false;
		return true;
	}

	private boolean myEqualse(int[][] a, int b[][], int n) {
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < n; i++) {
				if (a[j][i] != b[j][i])
					return false;
			}
		}
		return true;
	}

	public int[][] getA() {
		return a;
	}

	public void setA(int[][] a) {
		this.a = a;
	}
}
