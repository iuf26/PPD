import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DataStore {
    public double[][] matrix;
    public double[][]  kernel;
    public int N,M;//matrix size M rows and N columns
    public int m,n; // kernel m rows n columns


    public DataStore(String fileName) {
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            // read M,N,m,n
            if(myReader.hasNextLine()){
                 M = Integer.parseInt( myReader.nextLine());
                 N = Integer.parseInt(myReader.nextLine());
                 m = Integer.parseInt( myReader.nextLine());
                 n = Integer.parseInt( myReader.nextLine());
                 matrix = new double[M][N];
                 kernel = new double[m][n];
            }

            // read matrix values
            int i = 0;
            while (myReader.hasNextLine() && i < M) {

                List<Double> lineValues = Arrays.stream(myReader.nextLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
                for(int j = 0;j < N;j++){
                    matrix[i][j] = lineValues.get(j);
                }
                i++;
            }
            // read kernel values
            i = 0;
            while (myReader.hasNextLine() && i < m) {

                List<Double> lineValues = Arrays.stream(myReader.nextLine().split(" ")).map(Double::parseDouble).collect(Collectors.toList());
                for(int j = 0;j < n;j++){
                    kernel[i][j] = lineValues.get(j);
                }
                i++;
            }

            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public double[] convertMatrixToList(double[][] _matrix,int _m,int _n){
        double[] result = new double[_m * _n];
        int current = 0;
        for(int i = 0;i< _m;i++){
            for(int j = 0;j<_n;j++){
                result[current] = _matrix[i][j];
                current++;
            }
        }
        return result;
    }

}


