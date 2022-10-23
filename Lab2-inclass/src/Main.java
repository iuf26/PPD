import utils.MyThread;
import utils.MyThreadInterval;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Main {
    private static Integer DEFAULT_SIZE = 10000000;
    public static void printVector(Integer[] c){
        System.out.println(Arrays.toString(c));
    }

    public static double operation(Integer a,Integer b){
        return Math.sqrt(Math.pow(a,4) + Math.pow(b,4));
    }
    public static void sequentialSolution(Integer[] a,Integer[] b){
        Double[] c = new Double[a.length];
        for(int i = 0;i<a.length;i++){
            c[i] = operation(a[i],b[i]);
        }
       // printVector(c);

    }

    public static void paralelSol(Integer[] a,Integer[] b,int threadsNr){
        Thread[] threads = new Thread[threadsNr];
        Double[] result = new Double[a.length];
        for(int i = 0;i<threadsNr;i++){
            threads[i] = new MyThread(a,b,result,threadsNr,i);
            threads[i].start();

        }
        for(int i = 0;i<threadsNr;i++){
            try{
                threads[i].join();
            }catch (Exception ex){
                System.out.println(ex);
            }

        }
        //printVector(result);
    }

    public static void threadInterval(Integer[] a,Integer[] b,int threadsNr){
        Thread[] threads = new Thread[threadsNr];
        Double[] result = new Double[a.length];
        Integer whole = a.length / threadsNr;
        Integer rest = a.length % threadsNr;
        Integer start = 0;
        Integer end= whole;
        for(int i = 0;i<threadsNr;i++){
            if(rest > 0){
                end = end + 1;
                rest--;
            }
            threads[i] = new MyThreadInterval(a,b,result,start,end);
            threads[i].start();
            start = end;
            end = start + whole;

        }
        for(int i = 0;i<threadsNr;i++){
            try{
                threads[i].join();
            }catch (Exception ex){
                System.out.println(ex);
            }

        }
       // printVector(result);
    }

    public static Integer[] generateVector(int size){
        Random random = new Random();
            Integer[] result = new Integer[size];
            for(int i=0;i<size;i++){
                result[i] = random.nextInt(900000);
            }
            return result;
    }
    public static void main(String[] args) {
        Integer[] a = generateVector(DEFAULT_SIZE);
        Integer[] b =generateVector(DEFAULT_SIZE);
        Integer[] c =  new Integer[a.length];

       long startTime = System.nanoTime();
        sequentialSolution(a,b);
        long endTime = System.nanoTime();
        System.out.println(startTime + " " + endTime);

        startTime = System.nanoTime();
        paralelSol(a,b,4);
        endTime = System.nanoTime();
        System.out.println(startTime + " " + endTime);

        startTime = System.nanoTime();
        threadInterval(a,b,4);
        endTime = System.nanoTime();
        System.out.println(startTime + " " + endTime);





    }
}
