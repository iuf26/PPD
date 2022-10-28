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
    private double getGuardedMatrixElement(int i,int j,double[][] buffer,int[] bufferStartCoordsInMatrix,int nrOfRec) {
        if (nrOfRec > 10) return 0;
        int iaux = i - bufferStartCoordsInMatrix[0];
        if(iaux < 0 || iaux >= store.M) return 0;

        if(i < -1 * store.m / 2)return 0;
        if(j < -1 * store.n / 2) return 0;
        if ( i > store.M + store.m / 2) return 0;
        if( j > store.N + store.n / 2) return 0;
        if (i < -1 || j < -1){
                if( i == j) return getGuardedMatrixElement(i + 1,j + 1,buffer,bufferStartCoordsInMatrix,nrOfRec+1);
                if( i > j) return getGuardedMatrixElement(i,j + 1,buffer,bufferStartCoordsInMatrix,nrOfRec+1);
                return getGuardedMatrixElement(i + 1,j,buffer,bufferStartCoordsInMatrix,nrOfRec+1);

        }

        if(i > store.M || j > store.N){
                if( i  == j) return getGuardedMatrixElement(i-1,j-1,buffer,bufferStartCoordsInMatrix,nrOfRec+1);
                if( i< j) return getGuardedMatrixElement(i,j-1,buffer,bufferStartCoordsInMatrix,nrOfRec+1);
                return getGuardedMatrixElement(i-1,j,buffer,bufferStartCoordsInMatrix,nrOfRec+1);
        }

        if(i == -1 && j==-1) return buffer[0][0];
        if(i == -1 && j < store.N) return buffer[0][j];
        if(j == -1 && i < store.M) return buffer[iaux][0];
        if( i == store.M && j >=0 && j < store.N) return buffer[store.M-1-bufferStartCoordsInMatrix[0]][j];
        if(j == store.N && i >= 0 && i < store.M) return buffer[iaux][store.N-1];
        if(i >= 0 && i < store.M) return buffer[iaux][j];
        return 0;
    }

    //returns matrix coordinates corespondent given one current index from the linear representation of the matrix
    public int[] mapListToMatrixCoordinates(int index,int _m,int _n){
            int line = index / _n;
            int col = index  - line * _n;
            return new int[]{line,col};
    }

    private double getResultMatrixCorespondentElement(int index,double[][] buffer,int[] bufferStartCoordsInMatrix){
        int kernelMiddle = (store.m * store.n) /2;
        int[] kernelMiddleCoordinates = mapListToMatrixCoordinates(kernelMiddle,store.m,store.n);
        int[] matrixMiddleCoordinates = mapListToMatrixCoordinates(index,store.M,store.N);
        int matrixMiddleX =matrixMiddleCoordinates[0];
        int matrixMiddleY = matrixMiddleCoordinates[1];
        double result = 0;
        for(int current = 0;current < listKernel.length;current++){
            int[] currentKernelMatrixCoordinates = mapListToMatrixCoordinates(current,store.m,store.n);
            int[] coordinatesToGetValueFromOriginalMatrix = {matrixMiddleX + (currentKernelMatrixCoordinates[0] - kernelMiddleCoordinates[0]),matrixMiddleY +  (currentKernelMatrixCoordinates[1] - kernelMiddleCoordinates[1])};
            double value = getGuardedMatrixElement(coordinatesToGetValueFromOriginalMatrix[0],coordinatesToGetValueFromOriginalMatrix[1],buffer,bufferStartCoordsInMatrix,0);
            result += listKernel[current] * value;
        }
        return result;

    }



    public void computeResultListMatrix(int start,int end,Sequential result,DataStore store,double[][] buffer,int[] bufferStartCoordsInMatrix){
            for(int i = start; i< end;i++){
                int[] coords = mapListToMatrixCoordinates(i,store.M,store.N);

                store.matrix[coords[0]][coords[1]]  = getResultMatrixCorespondentElement(i,buffer,bufferStartCoordsInMatrix);
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
