import java.util.ArrayList;
import java.util.List;

public class Sequential {
    private final double[] listMatrix;
    private final double[] listKernel;
    private final DataStore store;


    public Sequential(DataStore store,double[] _listMatrix,double[] _listKernel) {


        this.store = store;
        listMatrix = _listMatrix;
        listKernel = _listKernel;


    }

    /**
     *
     * @param i
     * @param j
     * @return a value  that is always valid for the original store.matrix following the frontier rules
     *
     *
     */
    private double getGuardedMatrixElement(int i,int j) {
        if (i < -1 || j < -1){
                if( i == j) return getGuardedMatrixElement(i + 1,j + 1);
                if( i > j) return getGuardedMatrixElement(i,j + 1);
                return getGuardedMatrixElement(i + 1,j);

        }

        if(i > store.M || j > store.N){
                if( i  == j) return getGuardedMatrixElement(i-1,j-1);
                if( i< j) return getGuardedMatrixElement(i,j-1);
                return getGuardedMatrixElement(i-1,j);
        }

        if(i == -1 && j==-1) return store.matrix[0][0];
        if(i == -1 && j < store.N) return store.matrix[0][j];
        if(j == -1 && i < store.M) return store.matrix[i][0];
        if( i == store.M && j >=0 && j < store.N) return store.matrix[store.M-1][j];
        if(j == store.N && i >= 0 && i < store.M) return store.matrix[i][store.N-1];
        if(i >= 0 && i < store.M) return store.matrix[i][j];
        return 0;
    }

    //returns matrix coordinates corespondent given one current index from the linear representation of the matrix
    private int[] mapListToMatrixCoordinates(int index,int _m,int _n){
            int line = index / _m;
            int col = index % _n;
            return new int[]{line,col};
    }

    private double getResultMatrixCorespondentElement(int index){
        int kernelMiddle = (store.m * store.n) /2;
        int[] kernelMiddleCoordinates = mapListToMatrixCoordinates(kernelMiddle,store.m,store.n);
        int[] matrixMiddleCoordinates = mapListToMatrixCoordinates(index,store.M,store.N);
        int matrixMiddleX =matrixMiddleCoordinates[0];
        int matrixMiddleY = matrixMiddleCoordinates[1];
        double result = 0;
        for(int current = 0;current < listKernel.length;current++){
            int[] currentKernelMatrixCoordinates = mapListToMatrixCoordinates(current,store.m,store.n);
            int[] coordinatesToGetValueFromOriginalMatrix = {matrixMiddleX + (currentKernelMatrixCoordinates[0] - kernelMiddleCoordinates[0]),matrixMiddleY +  (currentKernelMatrixCoordinates[1] - kernelMiddleCoordinates[1])};
            double value = getGuardedMatrixElement(coordinatesToGetValueFromOriginalMatrix[0],coordinatesToGetValueFromOriginalMatrix[1]);
            result += listKernel[current] * value;
        }

        return result;

    }

    public void computeResultListMatrix(int start,int end,double[] result){

            for(int i = start; i< end;i++){
                result[i] = getResultMatrixCorespondentElement(i);
            }
    }

    public double[][] convertListToMatrix(double[] list,int _m,int _n){
            double[][] result = new double[_m][_n];
            int l=0;
            int c = 0;
        for (double v : list) {
            if (c == _n) {
                c = 0;
                l++;
            }
            if (l < _m) {
                result[l][c] = v;
                c++;
            }
        }
            return result;
    }




}
