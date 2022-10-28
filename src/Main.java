import com.sun.tools.jconsole.JConsoleContext;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.CyclicBarrier;

public class Main {


    public static void printResult(double[][] result,int M,int N,String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for(int i = 0;i < M;i++){
            for (int j=0; j< N;j++){
                printWriter.print(result[i][j] + " ");
            }
            printWriter.println();
        }

        printWriter.close();
    }



    public static void computeResultMatrixUsingThreads(int p,int matrixListLength,Sequential sequential,DataStore store){
        Thread[] threads = new Thread[p];
        CyclicBarrier threadsBarrier = new CyclicBarrier(p);
        int whole = matrixListLength/ p;
        int reminder = matrixListLength % p;
        int left = 0;
        int right = whole;
        for(int i = 0 ;i < p;i++){

            if(reminder > 0){
                right++;
                reminder--;
            }
            threads[i] = new IntervalThread(sequential,left,right,store,threadsBarrier);
            threads[i].start();

            left=  right;
            right = right + whole;
        }
        for(int i = 0;i<p;i++){

            try{
                threads[i].join();
            }catch (Exception ex){
                System.out.println(ex);
            }

        }

    }


    public static void main(String[] args) {

        String outputFileResult = args[1];
        DataStore store = new DataStore("D:\\FACULTATE-AN3-SEM1\\PPD\\Tema-Lab2\\src\\date.txt");
        double[] listMatrix = store.convertMatrixToList(store.matrix, store.M,store.N);
        double[] listKernel = store.convertMatrixToList(store.kernel,store.m,store.n);
        Sequential sequential = new Sequential(store,listMatrix,listKernel);
        int threadsNumber = Integer.parseInt(args[0]);

//                for(int i = 0;i<store.M;i++){
//                    for(int j = 0;j<store.N;j++){
//                        System.out.print(store.matrix[i][j] +  "  ");
//                    }
//                    System.out.println();
//                }

            long startTime = System.nanoTime();
            computeResultMatrixUsingThreads(threadsNumber,listMatrix.length,sequential,store);
            long endTime = System.nanoTime();
            System.out.println((double)endTime - startTime/1E6);
            try {
                printResult(store.matrix,store.M,store.N,outputFileResult);
            } catch (IOException e) {
                e.printStackTrace();
            }









    }
}
