import java.io.*;
import java.util.*;

class Node{
    int exponent;
    int coefficient;
    Node next;

    public Node(int exponent, int coefficient, Node next) {
        this.exponent = exponent;
        this.coefficient = coefficient;
        this.next = next;
    }
}

class MyList {
    Node head;
    Node front;

    public MyList() {
        this.head = null;
        this.front = null;
    }

    public void addItem(Node node){
        // if there are no elements in list yet
        if(head == null) {
            head = node;
            front = node;
            return;
        }
        if(this.head.exponent == node.exponent){
            this.head.coefficient = this.head.coefficient + node.coefficient;
            return;
        }
        Node headCopy = new Node(head.exponent,head.coefficient,head.next);
        while(headCopy != null){
            if(headCopy.exponent == node.exponent){
                //if thre exists an element with that specific exponent
                headCopy.coefficient = headCopy.coefficient + node.coefficient;
                return;
            }
            headCopy = headCopy.next;
        }
        //if there is no exponent with that specific value add a new node
        this.front.next = node;
        this.front = node;

        return;
    }


    }


class RecursiveSolution{
    String inputDirectoryPath;
    int polynomsCountInDirectory;

    public RecursiveSolution(String inputDirectoryPath,int polynomsCountInDirectory) {
        this.inputDirectoryPath = inputDirectoryPath;
        this.polynomsCountInDirectory = polynomsCountInDirectory;
    }

    public MyList solve(){
        MyList result = new MyList();
        for(int i = 0;i<this.polynomsCountInDirectory;i++ ){
            try {
                File text = new File(inputDirectoryPath + "/" + "polynom" + i + ".txt");
                Scanner scanner = new Scanner(text);
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    int exponent = Integer.parseInt(scanner.next());
                    int coefficient = Integer.parseInt(scanner.next());
                    //System.out.print(grade + " -> "+ coefficient + "\n");
                    result.addItem(new Node(exponent,coefficient,null));
                }

                // close the scanner
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    public void printMyList(MyList list,String outputFilePath){

        String outputFileName = outputFilePath;
        try {
            FileWriter fileWriter = new FileWriter(outputFileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            Node headCopy = list.head;
            while(headCopy != null){
                printWriter.print(headCopy.exponent + " " + headCopy.coefficient);
                printWriter.println();
                headCopy = headCopy.next;
            }
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


public class Main {

    public static int generateNumber(int min,int max){
        return (int)Math.floor(Math.random()*(max-min+1)+min);
    }
    public static void generatePolynoms(int nrOfPolynoms,int maxGrade,int maxMonoms,String outputDirectory){

        for(int i = 0;i < nrOfPolynoms;i++){
            //HERE: generate polynom
            int monoms =  generateNumber(4,maxMonoms);
            List<Integer> polynomGrades = new ArrayList<Integer>();
            for(int j = 0; j< monoms;j++){
                int elem = generateNumber(1,maxGrade);
                while(polynomGrades.contains(elem)){
                    elem = generateNumber(1,maxGrade);
                }
                polynomGrades.add(elem);
            }
            List<Integer> polynomCoeff = new ArrayList<Integer>();
            for(int j = 0;j<monoms;j++){
                polynomCoeff.add(generateNumber(2,50));
            }

            //HERE add polynom to file
            String outputFileName = outputDirectory + "/" + "polynom" + i + ".txt";
            try {
                FileWriter fileWriter = new FileWriter(outputFileName);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                for(int j = 0;j< monoms;j++){
                    printWriter.print(polynomGrades.get(j) + " " + polynomCoeff.get(j));
                    printWriter.println();
                }
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


    public static void main(String[] args) {
	// write your code here
        //generatePolynoms(10,1000,50,"src/input/caz1");
        //generatePolynoms(5,10000,100,"src/input/caz2");
        //RecursiveSolution sol = new RecursiveSolution("src/input/caz1",10);
        RecursiveSolution sol = new RecursiveSolution("src/input/caz2",5);
        MyList result = sol.solve();
        //sol.printMyList(result,"src/output/caz1.txt");
        sol.printMyList(result,"src/output/caz2.txt");
    }
}
