package space.fedorenko.dbsmartphones;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MatrixActivity extends AppCompatActivity {
    HashMap<Integer, space.fedorenko.matrixcalculator.MatrixView> matrixViews;

    private final int DEFAULT_ROWS = 3;
    private final int DEFAULT_COLS = 3;

    private int rowsA, colsA, rowsB, colsB;
    private static final String[] SIZE_SELECT = {"1", "2", "3", "4"};
    private Spinner numRowsMatrixA, numColumnsMatrixA, numRowsMatrixB, numColumnsMatrixB;
    space.fedorenko.matrixcalculator.MatrixView matrixA, matrixB;

    private ToggleButton tog;

    private PopupWindow resWindow;

    protected boolean enabled = true;

    public void enable(boolean b) {
        enabled = b;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !enabled || super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
        setTitle("Matrix Calculator");

        addValuesToSpinners();

        GridLayout matrixAView = findViewById(R.id.matrixA);
        GridLayout matrixBView = findViewById(R.id.matrixB);

        matrixA = new space.fedorenko.matrixcalculator.MatrixView(matrixAView, null, DEFAULT_ROWS, DEFAULT_COLS);
        matrixB = new space.fedorenko.matrixcalculator.MatrixView(matrixBView, null, DEFAULT_ROWS, DEFAULT_COLS);

        matrixViews = new HashMap<>();
        int MATRIX_A = 0;
        matrixViews.put(MATRIX_A, matrixA);
        int MATRIX_B = 1;
        matrixViews.put(MATRIX_B, matrixB);

        setMatrixSize(matrixA, DEFAULT_ROWS, DEFAULT_COLS);
        setMatrixSize(matrixB, DEFAULT_ROWS, DEFAULT_COLS);

        rowsA = DEFAULT_ROWS;
        colsA = DEFAULT_COLS;
        rowsB = DEFAULT_ROWS;
        colsB = DEFAULT_COLS;

        addListenerOnSpinnerItemSelection();
        setButtonsOnClickListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.helper:
                intent = new Intent(this, MatrixHelperActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        for(Integer currentKey : matrixViews.keySet()){
            space.fedorenko.matrixcalculator.MatrixView m = matrixViews.get(currentKey);
            assert m != null;
            int col = m.getCols();
            int screenWidth = m.getGridLayout().getWidth();
            for(TextView t : m.getCells()){
                t.setWidth(screenWidth/col);
            }
        }
    }

    public void addValuesToSpinners(){
        numRowsMatrixA = findViewById(R.id.numRowsA);
        numColumnsMatrixA = findViewById(R.id.numColsA);
        numRowsMatrixB = findViewById(R.id.numRowsB);
        numColumnsMatrixB = findViewById(R.id.numColsB);

        ArrayAdapter<String> SizeNumAdapter = new ArrayAdapter<>(MatrixActivity.this,
                android.R.layout.simple_spinner_item, SIZE_SELECT);
        SizeNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        numRowsMatrixA.setAdapter(SizeNumAdapter);
        numRowsMatrixA.setSelection(2);
        numRowsMatrixA.setTag("bug init");

        numColumnsMatrixA.setAdapter(SizeNumAdapter);
        numColumnsMatrixA.setSelection(2);
        numColumnsMatrixA.setTag("bug init");

        numRowsMatrixB.setAdapter(SizeNumAdapter);
        numRowsMatrixB.setSelection(2);
        numRowsMatrixB.setTag("bug init");

        numColumnsMatrixB.setAdapter(SizeNumAdapter);
        numColumnsMatrixB.setSelection(2);
        numColumnsMatrixB.setTag("bug init");
    }

    private void addListenerOnSpinnerItemSelection() {
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int num = Integer.parseInt(parent.getItemAtPosition(pos).toString());
                if(parent.getTag().equals("bug init")) {
                    if(parent == numRowsMatrixA)
                        rowsA = num;
                    if(parent == numColumnsMatrixA)
                        colsA = num;
                    if(parent == numRowsMatrixB)
                        rowsB = num;
                    if(parent == numColumnsMatrixB)
                        colsB = num;
                    parent.setTag("Ok");
                }
                else {
                    switch (parent.getId()) {
                        case R.id.numRowsA:
                            changeSizeMatrix(matrixA, num, colsA);
                            break;
                        case R.id.numColsA:
                            changeSizeMatrix(matrixA, rowsA, num);
                            break;
                        case R.id.numRowsB:
                            changeSizeMatrix(matrixB, num, colsB);
                            break;
                        case R.id.numColsB:
                            changeSizeMatrix(matrixB, rowsB, num);
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        numRowsMatrixA.setOnItemSelectedListener(itemSelectedListener);
        numColumnsMatrixA.setOnItemSelectedListener(itemSelectedListener);
        numRowsMatrixB.setOnItemSelectedListener(itemSelectedListener);
        numColumnsMatrixB.setOnItemSelectedListener(itemSelectedListener);
    }

    private void setMatrixSize(space.fedorenko.matrixcalculator.MatrixView matrix, int numRows, int numCols){
        ArrayList<TextView> cells = new ArrayList<>();

        GridLayout grid = matrix.getGridLayout();
        matrix.setCells(cells);
        matrix.setRows(numRows);
        matrix.setCols(numCols);

        if(numRows <= 0 || numCols <=0) {
            errorToast("Matrix size invalid");
            return;
        }

        grid.removeAllViews();
        grid.setRowCount(numRows);
        grid.setColumnCount(numCols);
        int numCells = numRows * numCols;
        int gridWidth = grid.getWidth();

        for(int i = 0; i < numCells; i++){
            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setText("0");
            editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(8);
            editText.setFilters(filterArray);

            int colWidth = gridWidth / numCols;
            editText.setMinimumWidth(colWidth);

            cells.add(editText);
            grid.addView(editText);
        }
    }

    private void changeSizeMatrix(space.fedorenko.matrixcalculator.MatrixView matrixView, int rows, int cols){
        space.fedorenko.matrixcalculator.Matrix old;
        removeEmptyCells(matrixView);
        try {
            old = new space.fedorenko.matrixcalculator.Matrix(userInputToValues(matrixView.getCells()), matrixView.getRows(), matrixView.getCols());
            setMatrixSize(matrixView, rows, cols);
            space.fedorenko.matrixcalculator.Matrix newMatrix = old.changeDimensions(rows, cols);
            displayMatrix(newMatrix, matrixView);

            if(matrixA.equals(matrixView)){
                rowsA = rows;
                colsA = cols;
            }
            else if(matrixB.equals(matrixView)) {
                rowsB = rows;
                colsB = cols;
            }
        }
        catch (Exception ex){
            errorToast(ex.getMessage());
            setOldSpinnersValues(matrixView, rows, cols);
        }
    }

    private void setOldSpinnersValues(space.fedorenko.matrixcalculator.MatrixView matrixView, int rows, int cols){
        if (matrixA.equals(matrixView)) {
            if(rowsA != rows) {
                numRowsMatrixA.setSelection(rowsA - 1);
                numRowsMatrixA.setTag("bug init");
            }
            if(colsA != cols) {
                numColumnsMatrixA.setSelection(colsA - 1);
                numColumnsMatrixA.setTag("bug init");
            }
        }
        else if(matrixB.equals(matrixView)){
            if(rowsB != rows) {
                numRowsMatrixB.setSelection(rowsB - 1);
                numRowsMatrixB.setTag("bug init");
            }
            if(colsB != cols) {
                numColumnsMatrixB.setSelection(colsB - 1);
                numColumnsMatrixB.setTag("bug init");
            }
        }
    }

    private void removeEmptyCells(space.fedorenko.matrixcalculator.MatrixView matrix){
        for(TextView t : matrix.getCells()){
            if(t.getText().toString().isEmpty())
                t.setText("0");
        }
    }

    private void clearAllCells(space.fedorenko.matrixcalculator.MatrixView matrix){
        for(TextView t: matrix.getCells())
            t.setText("0");
    }

    private ArrayList<Double> userInputToValues(ArrayList<TextView> inputs){
        ArrayList<Double> values = new ArrayList<>();
        for(TextView t : inputs){
            if(!t.getText().toString().isEmpty()){
                values.add(Double.parseDouble(t.getText().toString()));
            }
            else{
                return null;
            }
        }
        return values;
    }

    private void displayMatrix(space.fedorenko.matrixcalculator.Matrix m, space.fedorenko.matrixcalculator.MatrixView matrixView){
        GridLayout grid = matrixView.getGridLayout();
        grid.removeAllViews();
        grid.setRowCount(m.getRows());
        grid.setColumnCount(m.getCols());

        matrixView.setRows(m.getRows());
        matrixView.setCols(m.getCols());
        ArrayList<TextView> cells = new ArrayList<>();
        matrixView.setCells(cells);

        ArrayList<Double> values = m.matrixToList();
        int numCells = m.getCols() * m.getRows();
        int width = grid.getWidth();
        width = width / matrixView.getCols();

        for(int i = 0; i < numCells; i++){
            String num = values.get(i).toString();
            num = !num.contains(".") ? num : num.replaceAll("0*$", "").replaceAll("\\.$", "");
            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setText(num);
            editText.setMinimumWidth(width);
            editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(8);
            editText.setFilters(filterArray);

            grid.addView(editText);
            matrixView.getCells().add(editText);
        }
    }

    public void errorToast(String s){
        Toast t = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        t.show();
    }

    private void setButtonsOnClickListeners(){
        Button add = findViewById(R.id.add);
        Button sub = findViewById(R.id.sub);
        Button mul = findViewById(R.id.mul);
        Button swap = findViewById(R.id.swap);
        tog = findViewById(R.id.toggleButton);
        Button transpose = findViewById(R.id.transpose);
        Button inverse = findViewById(R.id.inverse);
        Button determinant = findViewById(R.id.determinant);
        Button clearA = findViewById(R.id.clearA);
        Button clearB = findViewById(R.id.clearB);
        Button saveA = findViewById(R.id.saveA);
        Button saveB = findViewById(R.id.saveB);
        Button loadA = findViewById(R.id.loadA);
        Button loadB = findViewById(R.id.loadB);

        add.setOnClickListener(v -> {
            try {
                ArrayList<Double> cellsA = userInputToValues(matrixA.getCells());
                ArrayList<Double> cellsB = userInputToValues(matrixB.getCells());

                if(cellsA == null || cellsB == null){
                    errorToast("Fill in all cells before performing an operation.");
                    return;
                }

                space.fedorenko.matrixcalculator.Matrix A = new space.fedorenko.matrixcalculator.Matrix(cellsA, matrixA.getRows(), matrixA.getCols());
                space.fedorenko.matrixcalculator.Matrix B = new space.fedorenko.matrixcalculator.Matrix(cellsB, matrixB.getRows(), matrixB.getCols());

                space.fedorenko.matrixcalculator.Matrix result = A.add(B);
                displayResult(result,0);
            } catch (Exception e) {
                errorToast(e.getMessage());
            }
        });

        sub.setOnClickListener(v -> {
            try {
                ArrayList<Double> cellsA = userInputToValues(matrixA.getCells());
                ArrayList<Double> cellsB = userInputToValues(matrixB.getCells());

                if(cellsA == null || cellsB == null){
                    errorToast("Fill in all cells before performing an operation.");
                    return;
                }

                space.fedorenko.matrixcalculator.Matrix A = new space.fedorenko.matrixcalculator.Matrix(cellsA, matrixA.getRows(), matrixA.getCols());
                space.fedorenko.matrixcalculator.Matrix B = new space.fedorenko.matrixcalculator.Matrix(cellsB, matrixB.getRows(), matrixB.getCols());

                space.fedorenko.matrixcalculator.Matrix result = A.sub(B);
                displayResult(result, 0);
            } catch (Exception e) {
                errorToast(e.getMessage());
            }

        });

        mul.setOnClickListener(v -> {
            try {
                ArrayList<Double> cellsA = userInputToValues(matrixA.getCells());
                ArrayList<Double> cellsB = userInputToValues(matrixB.getCells());

                if(cellsA == null || cellsB == null){
                    errorToast("Fill in all cells before performing an operation.");
                    return;
                }

                space.fedorenko.matrixcalculator.Matrix A = new space.fedorenko.matrixcalculator.Matrix(cellsA, matrixA.getRows(), matrixA.getCols());
                space.fedorenko.matrixcalculator.Matrix B = new space.fedorenko.matrixcalculator.Matrix(cellsB, matrixB.getRows(), matrixB.getCols());

                space.fedorenko.matrixcalculator.Matrix result = A.mult(B);
                displayResult(result, 0);
            } catch (Exception e) {
                errorToast(e.getMessage());
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEmptyCells(matrixA);
                removeEmptyCells(matrixB);

                int bRow = matrixB.getRows();
                int bCol = matrixB.getCols();
                try{
                    ArrayList<Double> valuesB = userInputToValues(matrixB.getCells());
                    ArrayList<Double> valuesA = userInputToValues(matrixA.getCells());

                    displayMatrix(new space.fedorenko.matrixcalculator.Matrix(valuesA, matrixA.getRows(), matrixA.getCols()), matrixB);
                    setSpinnerValuesForMatrixB();

                    displayMatrix(new space.fedorenko.matrixcalculator.Matrix(valuesB, bRow, bCol), matrixA);
                    setSpinnerValuesForMatrixA(bRow, bCol);
                }
                catch (Exception e){
                    errorToast(e.getMessage());
                }
            }

            void setSpinnerValuesForMatrixB(){
                if(rowsB != matrixA.getRows()) {
                    numRowsMatrixB.setSelection(matrixA.getRows() - 1);
                    numRowsMatrixB.setTag("bug init");
                }
                if(colsB != matrixA.getCols()) {
                    numColumnsMatrixB.setSelection(matrixA.getCols() - 1);
                    numColumnsMatrixB.setTag("bug init");
                }
            }

            void setSpinnerValuesForMatrixA(int bRow, int bCol){
                if(rowsA != bRow) {
                    numRowsMatrixA.setSelection(bRow - 1);
                    numRowsMatrixA.setTag("bug init");
                }
                if(colsA != bCol) {
                    numColumnsMatrixA.setSelection(bCol - 1);
                    numColumnsMatrixA.setTag("bug init");
                }
            }
        });

        transpose.setOnClickListener(v -> {
            space.fedorenko.matrixcalculator.MatrixView m;
            Spinner numRows, numCols;
            switch (tog.getText().toString()){
                case "A":
                    m = matrixA;
                    numRows = findViewById(R.id.numRowsA);
                    numCols = findViewById(R.id.numColsA);
                    break;
                case "B":
                    m = matrixB;
                    numRows = findViewById(R.id.numRowsB);
                    numCols = findViewById(R.id.numColsB);
                    break;
                default:
                    return;
            }
            try {
                removeEmptyCells(m);
                space.fedorenko.matrixcalculator.Matrix matrix = new space.fedorenko.matrixcalculator.Matrix(userInputToValues(m.getCells()), m.getRows(), m.getCols());
                space.fedorenko.matrixcalculator.Matrix result = matrix.transpose();

                if(m.getCols() != m.getRows()){
                    numRows.setSelection(m.getCols() - 1);
                    numRows.setTag("bug init");
                    numCols.setSelection(m.getRows() - 1);
                    numCols.setTag("bug init");
                }

                displayMatrix(result, m);
            }
            catch (Exception e){
                errorToast(e.getMessage());
            }

        });

        inverse.setOnClickListener(v -> {
            space.fedorenko.matrixcalculator.MatrixView m;
            switch (tog.getText().toString()){
                case "A":
                    m = matrixA;
                    break;
                case "B":
                    m = matrixB;
                    break;
                default:
                    return;
            }
            try {
                removeEmptyCells(m);
                space.fedorenko.matrixcalculator.Matrix matrix = new space.fedorenko.matrixcalculator.Matrix(userInputToValues(m.getCells()), m.getRows(), m.getCols());
                space.fedorenko.matrixcalculator.Matrix result = matrix.inverse();

                enable(false);
                displayResult(result, 0);
            } catch (Exception e){
                errorToast(e.getMessage());
            }
        });

        determinant.setOnClickListener(v -> {
            space.fedorenko.matrixcalculator.MatrixView m;
            switch (tog.getText().toString()){
                case "A":
                    m = matrixA;
                    break;
                case "B":
                    m = matrixB;
                    break;
                default:
                    return;
            }
            try {
                removeEmptyCells(m);
                space.fedorenko.matrixcalculator.Matrix matrix = new space.fedorenko.matrixcalculator.Matrix(userInputToValues(m.getCells()), m.getRows(), m.getCols());

                double determinant1 = matrix.det();
                displayResult(null, determinant1);
            } catch (Exception e){
                errorToast(e.getMessage());
            }
        });

        clearA.setOnClickListener(v -> clearAllCells(matrixA));

        clearB.setOnClickListener(v -> clearAllCells(matrixB));

        saveA.setOnClickListener(v -> save(matrixA, "matrixA.txt"));
        saveB.setOnClickListener(v -> save(matrixB, "matrixB.txt"));

        loadA.setOnClickListener(v -> load(matrixA, "matrixA.txt"));
        loadB.setOnClickListener(v -> load(matrixB, "matrixB.txt"));
    }

    @SuppressLint("SetTextI18n")
    private void displayResult(space.fedorenko.matrixcalculator.Matrix result, double det){
        enable(false);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View resultView = inflater.inflate(R.layout.result,null);

        if(result != null){
            LinearLayout detLayout = resultView.findViewById(R.id.DetLayout);
            detLayout.setVisibility(View.INVISIBLE);
        }

        resWindow = new PopupWindow(
                resultView,
                1000,
                1000
        );
        resWindow.setElevation(20.0f);

        ImageButton closeButton = resultView.findViewById(R.id.ib_close);

        if(result != null){
            GridLayout resLayout = resultView.findViewById(R.id.matrixRes);
            space.fedorenko.matrixcalculator.MatrixView matrixView = new space.fedorenko.matrixcalculator.MatrixView(resLayout, null, DEFAULT_ROWS, DEFAULT_COLS);

            showResultMatrix(result, matrixView);
        } else {
            TextView detText = resultView.findViewById(R.id.detRes);
            detText.setText(Double.toString(det));
        }

        LinearLayout mainLayout = findViewById(R.id.main);

        closeButton.setOnClickListener(view -> {
            resWindow.dismiss();
            mainLayout.setBackgroundColor(Color.WHITE);
            enable(true);
        });

        resWindow.showAtLocation(mainLayout, Gravity.CENTER,0,0);
        mainLayout.setBackgroundColor(Color.GRAY);
    }

    private void showResultMatrix(space.fedorenko.matrixcalculator.Matrix m, space.fedorenko.matrixcalculator.MatrixView matrixView){
        GridLayout grid = matrixView.getGridLayout();
        grid.removeAllViews();
        grid.setRowCount(m.getRows());
        grid.setColumnCount(m.getCols());

        matrixView.setRows(m.getRows());
        matrixView.setCols(m.getCols());
        ArrayList<TextView> cells = new ArrayList<>();
        matrixView.setCells(cells);

        ArrayList<Double> values = m.matrixToList();
        int numCells = m.getCols() * m.getRows();

        for(int i = 0; i < numCells; i++){
            double val = Math.round(values.get(i) * 10000000.0) / 10000000.0;
            String num = Double.toString(val);
            num = !num.contains(".") ? num : num.replaceAll("0*$", "").replaceAll("\\.$", "");
            EditText editText = new EditText(this);
            editText.setId(View.generateViewId());
            editText.setText(num);
            editText.setMinimumWidth(200);

            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(8);
            editText.setFilters(filterArray);

            grid.addView(editText);
            matrixView.getCells().add(editText);
        }
    }

    public void save(space.fedorenko.matrixcalculator.MatrixView matrixView, String FILE_NAME) {
        ArrayList<TextView> cells = matrixView.getCells();
        FileOutputStream fos = null;
        try {
            int k = 0;
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            for(int i = 0; i < matrixView.getRows(); i++){
                for(int j = 0; j < matrixView.getCols(); j++){
                    String val = cells.get(k).getText().toString();
                    fos.write(val.getBytes());
                    fos.write(" ".getBytes());
                    k++;
                }
                fos.write("\n".getBytes());
            }
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void load(space.fedorenko.matrixcalculator.MatrixView matrixView, String FILE_NAME) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String text;
            List<List<String>> strMatrix = new ArrayList<>();
            while ((text = br.readLine()) != null) {
                List<String> list = Arrays.asList(text.split(" "));
                strMatrix.add(list);
            }

            double[][] matrix;
            if(strMatrix.size() != 0) {
                if (checkLoadMatrixDimension(strMatrix)) {
                    try {
                        matrix = convertMatrixToDouble(strMatrix);
                        space.fedorenko.matrixcalculator.Matrix m = new space.fedorenko.matrixcalculator.Matrix(matrix);
                        displayMatrix(m, matrixView);
                        setSpinnerValues(matrixView);
                    } catch (Exception ex) {
                        errorToast("File contains invalid characters");
                    }
                } else
                    errorToast("Illegal loaded matrix dimensions");
            } else {
                errorToast("File empty");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Boolean checkLoadMatrixDimension(List<List<String>> matrix){
        int N = matrix.size();
        int M = matrix.get(0).size();
        for(int i = 1; i < matrix.size(); i++){
            if(M != matrix.get(i).size())
                return false;
        }

        return N <= 4 && M <= 4;
    }

    private double[][] convertMatrixToDouble(List<List<String>> matrix){
        double[][] res = new double[matrix.size()][matrix.get(0).size()];
        for(int i = 0; i < matrix.size(); i++)
            for(int j = 0; j < matrix.get(0).size(); j++)
                res[i][j] = Double.parseDouble(matrix.get(i).get(j));
        return res;
    }

    private void setSpinnerValues(space.fedorenko.matrixcalculator.MatrixView matrixView){
        if(matrixView == matrixA){
            if(rowsA != matrixView.getRows()) {
                numRowsMatrixA.setSelection(matrixView.getRows() - 1);
                numRowsMatrixA.setTag("bug init");
            }
            if(colsA != matrixView.getCols()) {
                numColumnsMatrixA.setSelection(matrixView.getCols() - 1);
                numColumnsMatrixA.setTag("bug init");
            }
        }
        else{
            if(rowsB != matrixView.getRows()) {
                numRowsMatrixB.setSelection(matrixView.getRows() - 1);
                numRowsMatrixB.setTag("bug init");
            }
            if(colsB != matrixView.getCols()) {
                numColumnsMatrixB.setSelection(matrixView.getCols() - 1);
                numColumnsMatrixB.setTag("bug init");
            }
        }
    }
}