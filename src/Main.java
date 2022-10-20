public class Main {


    public static void printResult(double[][] result,int M,int N){
        for(int i = 0;i < M;i++){
            for (int j=0; j< N;j++){
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void printList(double[] list,int size){
        for(int i = 0;i<size;i++){
            System.out.print(list[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        DataStore store = new DataStore("src/ex1.txt");
        double[][] resultMatrix = new double[store.M][store.N];
        Sequential sequential = new Sequential(resultMatrix,store);
        sequential.computeResultListMatrix();
        printResult(resultMatrix,store.M ,store.N);

    }
}
