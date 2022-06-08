package com.yxm.library;

import com.yxm.library.bean.PieceProcess;

public class SubBoard extends Board {
	private Board parent;
	private int position = -1;

	public SubBoard(Board parent) {
		super();
		this.parent = parent;
	}

	public void forward() {
		if (position + 1 < parent.getCount()) {
			position++;
			PieceProcess p = parent.getPieceProcess(position);
			this.addPieceProcess(p);
		}
	}

	public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public void back() {
		if (position < 0)return; 

		this.removePieceProcess();
		position--;
	}

	public void gotoIt(int n) {
		if (n > parent.getCount() || n < 0){
			return;
		}
		this.cleanGrid();
		for (int i = 0; i < n; i++) {
			forward();
		}
	}
	@Override
	public boolean put(int x,int y){
		boolean r=super.put(x, y);
		if(r==true){
			position++;
		}
		return r;
	}
}
