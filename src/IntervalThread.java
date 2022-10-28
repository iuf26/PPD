import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class IntervalThread extends Thread{
    private final Sequential sequential;
    private final int left;
    private final int right;
    private final DataStore store;
    private final CyclicBarrier barrier;
    private int[] bufferStartCoordinatesInMatrix;
    public IntervalThread(Sequential sequential, int left, int right, DataStore store, CyclicBarrier barrier) {
        this.sequential = sequential;
        this.left = left;
        this.right = right;
        this.store = store;
        this.barrier = barrier;
    }

    double[][] computeBuffer(){
            int[] matrixCoordsLeft = sequential.mapListToMatrixCoordinates(this.left,store.M,store.N);
            int[] matrixCoordsRight = sequential.mapListToMatrixCoordinates(this.right-1,store.M,store.N);


            int lengthR = Math.min(matrixCoordsRight[0] + store.m / 2,store.M ) - Math.max(matrixCoordsLeft[0] - store.m / 2, 0) + 1;

           double[][] buffer  = new double[lengthR][store.N];
           bufferStartCoordinatesInMatrix = new int[2];
            bufferStartCoordinatesInMatrix[0] = Math.max(matrixCoordsLeft[0] - store.m/2,0);

            int istart = Math.max(matrixCoordsLeft[0] - store.m/2,0);
            int ifin = Math.min(matrixCoordsRight[0] + store.m/2,store.M );
        int line = 0;

           for(int i = istart;i <ifin ;i++) {
               for (int j = 0; j < store.N; j++) {

                       buffer[line][j] = store.matrix[i][j];

               }
               line++;
           }
//        for(int i = 0;i<store.M;i++){
//            for(int j = 0;j<store.N;j++){
//                System.out.print(buffer[i][j] +  "  ");
//            }
//            System.out.println();
//        }

           return buffer;
    }


    @Override
    public void run() {
        try {

            double[][] buffer = computeBuffer();
            barrier.await();
            sequential.computeResultListMatrix(left, right, sequential, store, buffer,bufferStartCoordinatesInMatrix);
        }catch (InterruptedException | BrokenBarrierException e){
            System.out.println(e);
        }
    }
}
