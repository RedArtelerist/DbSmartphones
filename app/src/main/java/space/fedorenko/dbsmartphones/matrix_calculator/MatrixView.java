package space.fedorenko.matrixcalculator;

import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MatrixView {
    private GridLayout grid;
    private ArrayList<TextView> cells;
    private int rows;
    private int cols;

    public MatrixView(GridLayout grid, ArrayList<TextView> textViews, int rows, int cols){
        this.grid = grid;
        this.cells = textViews;
        this.rows = rows;
        this.cols = cols;
    }

    public GridLayout getGridLayout() { return grid; }

    public ArrayList<TextView> getCells() { return cells; }

    public int getRows() { return rows; }

    public int getCols() { return cols; }

    public void setRows(int r){ rows = r; }

    public void setCols(int c) { cols = c; }

    public void setCells(ArrayList<TextView> c) { cells = c; }
}