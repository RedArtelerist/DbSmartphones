package space.fedorenko.matrixcalculator;

import java.util.ArrayList;
import java.util.List;

public class Matrix {
    private final int N;
    private final int M;
    private final double[][] data;

    public Matrix(int N, int M)
    {
        this.N = N;
        this.M = M;
        data = new double[N][M];
    }

    public Matrix(double [][] data){
        N = data.length;
        M = data[0].length;
        this.data = new double[N][M];
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                this.data[i][j] = data[i][j];
    }

    public Matrix(Matrix A) { this(A.data); }

    public Matrix(List<Double> values, int rows, int cols){
        N = rows;
        M = cols;
        data = new double[N][M];
        int ind = 0;
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++){
                data[i][j] = values.get(ind);
                ind++;
            }
    }

    public int getRows() { return N; }

    public int getCols() { return M;}

    public ArrayList<Double> matrixToList(){
        ArrayList<Double> values = new ArrayList<Double>();
        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                values.add(data[i][j]);

        return values;
    }

    public Matrix changeDimensions(int r, int c){
        double[][] newMatrix = new double[r][c];
        int rows, cols;

        if(r > this.getRows())
            rows = this.getRows();
        else
            rows = r;

        if(c > this.getCols())
            cols = this.getCols();
        else
            cols = c;

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                newMatrix[i][j] = this.data[i][j];

        return new Matrix(newMatrix);
    }

    public Matrix transpose(){
        Matrix A = new Matrix(M, N);

        for(int i = 0; i < N; i++)
            for(int j = 0; j < M; j++)
                A.data[j][i] = data[i][j];

        return A;
    }

    public Matrix add(Matrix B) throws Exception {
        Matrix A = this;
        if(B.N != A.N || B.M != A.M)
            throw new Exception("Illegal matrix dimensions.");

        Matrix C = new Matrix(N, M);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }

    public Matrix sub(Matrix B) throws Exception {
        Matrix A = this;
        if(B.N != A.N || B.M != A.M)
            throw new Exception("Illegal matrix dimensions.");

        Matrix C = new Matrix(N, M);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                C.data[i][j] = A.data[i][j] - B.data[i][j];
        return C;
    }

    public Matrix mult(Matrix B) throws Exception {
        Matrix A = this;
        if(A.M != B.N)
            throw new Exception("Illegal matrix dimensions.");

        Matrix C = new Matrix(A.N, B.M);

        for (int i = 0; i < C.N; i++)
            for (int j = 0; j < C.M; j++)
                for (int k = 0; k < A.M; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }

    public Matrix square() throws Exception {
        return this.mult(this);
    }

    public double det() throws Exception {
        if(N != M)
            throw new Exception("Illegal matrix dimensions.");

        if(N == 1)
            return data[0][0];
        if(N == 2)
            return (data[0][0] * data[1][1] - data[0][1] * data[1][0]);

        else {
            double sign = 1, result = 0;
            for(int i = 0; i < N; i++){
                Matrix minor = getMinor(this, i);
                result += sign * data[0][i] * minor.det();
                sign = -sign;
            }
            return result;
        }
    }

    private Matrix getMinor(Matrix matrix, int n){
        Matrix minor = new Matrix(matrix.N - 1, matrix.N - 1);

        for(int i = 1; i < matrix.N; i++){
            for(int j = 0; j < n; j++)
                minor.data[i - 1][j] = matrix.data[i][j];
            for(int j = n + 1; j < matrix.N; j++)
                minor.data[i - 1][j - 1] = matrix.data[i][j];
        }
        return minor;
    }

    private Matrix subMatrix(Matrix matrix, int exclude_row, int exclude_col) {
        Matrix result = new Matrix(matrix.N - 1, matrix.M - 1);

        for (int i = 0, p = 0; i < matrix.N; i++) {
            if (i != exclude_row) {
                for (int j = 0, q = 0; j < matrix.M; j++) {
                    if (j != exclude_col) {
                        result.data[p][q] = matrix.data[i][j];
                        ++q;
                    }
                }
                ++p;
            }
        }

        return result;
    }

    public Matrix inverse() throws Exception {
        double det = det();

        if(det == 0.0)
            throw new Exception("Can't find inverse, determinant is 0");
        else {
            Matrix inverse = new Matrix(N, M);

            for(int i = 0; i < N; i++){
                for (int j = 0; j < M; j++){
                    Matrix sub = subMatrix(this, i, j);

                    inverse.data[i][j] = (1.0 / det *
                            Math.pow(-1, i + j) *
                            sub.det());
                }
            }
            return inverse.transpose();
        }
    }
}