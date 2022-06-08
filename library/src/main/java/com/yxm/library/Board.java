package com.yxm.library;

import android.os.Bundle;

import com.yxm.library.bean.PieceProcess;
import com.yxm.library.util.LogUtils;
import com.yxm.library.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Board {

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int boardSize = 19;
    public static final int None = 0;
    public static final int Black = 1;
    public static final int White = 2;
    public static final int FLAG = 3;

    public static boolean hasPickother = false;

    public boolean hasPickStone = false; // 当前是否提子了

    // 行棋记录
    private List<PieceProcess> list = new ArrayList<>();

    public Grid currentGrid = new Grid();// 当前盘面
    private int expBw = Black;// 轮到哪一方下
    private int bwtag = 0;// 连续颜色0黑1白
    private Function listener;
    public boolean bGobanging = false;
    // ------------------------------------------------------------------

    public boolean continueput(int x, int y, int bwtag) {
        Coordinate c = new Coordinate(x, y);
        PieceProcess p = new PieceProcess(bwtag, c,0);

        if (x == 0 && y == 0) // 执行虚步，停一手。
        {
            return false;
        }
        if (currentGrid.putPiece(p)) {
            if (!check(p)) {
                currentGrid.executePieceProcess(p, true); // 返回一步
                return false;
            }

            if (p.bw != 3) {

                list.add(p);
            }
            nochangefinishPut();
            return true;
        }
        return false;
    }

    public boolean continueputNoPick(int x, int y, int bwtag) {
        Coordinate c = new Coordinate(x, y);
        // PieceProcess p = new PieceProcess(expBw, c);
        // int bwtag =0;
        PieceProcess p = new PieceProcess(bwtag, c,0);

        if (x == 0 && y == 0) // 执行虚步，停一手。
        {
            return false;
        }

        if (currentGrid.putPieceNoPick(p)) {
            if (!check(p)) {
                currentGrid.executePieceProcess(p, true); // 返回一步
                return false;
            }

            if (p.bw != 3) {

                list.add(p);
            }
            nochangefinishPut();
            return true;
        }
        return false;
    }

    public List<PieceProcess> getList() {
        return list;
    }

    public void setList(List<PieceProcess> list) {
        this.list = list;
    }

    public void setCurBW(int bw) {
        expBw = bw;
    }

    public boolean put(int bw,int x, int y) {
        Coordinate c = new Coordinate(x, y);

        // PieceProcess p = new PieceProcess(expBw, c);
        // int bwtag =0;

        PieceProcess p = new PieceProcess(bw, c,0);

        if (x == 0 && y == 0) // 执行虚步，停一手。
        {

            if (p.bw != 3) {

                list.add(p);
            }
            finishedPut();
            return true;
            // return false;
        }

        if (currentGrid.putPiece(p)) {
            if (!check(p)) {
                currentGrid.executePieceProcess(p, true); // 返回一步
                return false;
            }


            if (p.bw != 3) {

                list.add(p);
            }
            finishedPut();
            return true;
        }
        return false;
    }

    public boolean put(int x, int y) {
        Coordinate c = new Coordinate(x, y);
        // PieceProcess p = new PieceProcess(expBw, c);
        // int bwtag =0;
        PieceProcess p = new PieceProcess(expBw, c,0);

        if (x == 0 && y == 0) // 执行虚步，停一手。
        {

            if (p.bw != 3) {

                list.add(p);
            }
            finishedPut();
            return true;
            // return false;
        }

        if (currentGrid.putPiece(p)) {
            if (!check(p)) {
                currentGrid.executePieceProcess(p, true); // 返回一步
                return false;
            }


            if (p.bw != 3) {

                list.add(p);
            }
            finishedPut();
            return true;
        }
        return false;
    }

    // 预落子，检验是否合法
    public boolean prePut(int x, int y) {
        Coordinate c = new Coordinate(x, y);
        PieceProcess p = new PieceProcess(expBw, c,1);

        if (currentGrid.putPiece(p)) {
            if (!check(p)) {
                currentGrid.executePieceProcess(p, true); // 返回一步
                return false;
            }

            currentGrid.executePieceProcess(p, true); // 返回一步
            return true;
        }
        return false;
    }

    private void finishedPut() {
        expBw = Utils.getReBW(expBw);
        postEnvet();
    }

    private void nochangefinishPut() {
        // expBw = Utils.nogetReBW(expBw);
        postEnvet();
    }

    // 打劫检测
    public boolean check(PieceProcess p) {

        // 后面按照中国惯例规则，改成，不要和上上一步通型。刚提子的地方下棋反提子。
        if (list.size() < 3)
            return true;
        if (isOverEqualse(list.size() - 2, p))
            return false; // 盘面与上上一手比较，看是否同型

        return true;

    }

    private boolean isOverEqualse(int position, PieceProcess p) {
        Board sb = getSubBoard(position + 1);
        return sb.currentGrid.equals(this.currentGrid);
    }

    public String toShortString() {
        String a = "";
        int[][] nn = this.currentGrid.getA();

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                a += nn[j][i];
            }
        }

        System.out.println("===========自动生成的盘面==========" + a);
        return a;
    }

    public String toShortString(int rotate,int boardSize) {
        String a = "";
        int[][] nn = this.currentGrid.getA();
        LogUtils.d("===currentGrid", this.currentGrid.getA());
        switch (rotate) {
            case 0:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[j][i];
                    }
                }
                break;
            case 90:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[i][boardSize-1-j];
                    }
                }
                break;
            case 180:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[boardSize-1-j][boardSize-1-i];
                    }
                }
                break;
            case 270:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[boardSize-1-i][j];
                    }
                }
                break;
        }


        System.out.println("===========自动生成的盘面==========" + a);
        return a;
    }

    public String toShortString(int[][] nn,int rotate) {
        String a = "";
        switch (rotate) {
            case 0:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[j][i];
                    }
                }
                break;
            case 90:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[i][boardSize-1-j];
                    }
                }
                break;
            case 180:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[boardSize-1-j][boardSize-1-i];
                    }
                }
                break;
            case 270:
                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        a += nn[boardSize-1-i][j];
                    }
                }
                break;
        }

        System.out.println("===========自动生成的盘面==========" + a);
        return a;
    }


    // ------------------------------------------------------------------rebuilt

    public SubBoard getSubBoard(int index) {
        SubBoard board = new SubBoard(this);
        board.gotoIt(index);
        return board;
    }

    public void cleanGrid() {
        this.currentGrid = new Grid();
    }

    protected void addPieceProcess(PieceProcess p) {
        currentGrid.executePieceProcess(p, false);

        if (p.bw != 3) {

            list.add(p);
        }
        finishedPut();
    }

    protected void removePieceProcess() {
        if (list.size() == 0)
            return;
        PieceProcess p = list.remove(getCount() - 1);
        currentGrid.executePieceProcess(p, true);
        finishedPut();
    }

    // ------------------------------------------------------------------getter

    public int getValue(int x, int y) {
        return currentGrid.getValue(new Coordinate(x, y));
    }

    private void postEnvet() {

        this.hasPickStone = Board.hasPickother;    // 值引用，全局静态值Board.hasPickother会被改变。
        if (listener == null)
            return;
        listener.apply(getCount(), expBw, bwtag);
    }

    public void setListener(Function listener) {
        this.listener = listener;
    }

    public Coordinate getLastPosition() {
        if (getCount() == 0)
            return null;
        return list.get(getCount() - 1).c;
    }

    public int getCount() {
        return list.size();
    }

    public PieceProcess getPieceProcess(int i) {
        if (i >= getCount() || i < 0)
            return null;
        return list.get(i);
    }

    public String getCurBW() {
        if (expBw == Black)
            return "+";
        return "-";

    }


    // ------------------------------------------------------------------status

    public Bundle saveState() {
        Bundle map = new Bundle();
        map.putInt("count", getCount());
        int i = 0;
        for (PieceProcess p : list) {
            map.putInt("x" + i, p.c.x);
            map.putInt("y" + i, p.c.y);
            i++;
        }
        return map;
    }

    public void restoreState(Bundle map) {
        int n = map.getInt("count");
        for (int i = 0; i < n; i++) {
            int x = map.getInt("x" + i);
            int y = map.getInt("y" + i);

            this.put(x, y);
        }
    }
}
