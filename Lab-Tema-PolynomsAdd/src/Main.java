import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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
    synchronized
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

class MyQueue{
    ArrayList<Node> queue;
    public int queueReadsLeft;
    public ReentrantLock lock = new ReentrantLock();

    public MyQueue(int queueReadsLeft) {
        this.queue = new ArrayList<Node>();
        this.queueReadsLeft = queueReadsLeft;
    }
    synchronized
    public void enqueue(Node node){
            System.out.println("Producer is adding elem in queue....");
            queue.add(node);


    }
    synchronized
    public Node dequeue(){

        if(queue.size() == 0) {return null;}
        Node result = queue.get(0);
        queue.remove(0);
        queueReadsLeft--;

        return result;

    }
    synchronized
    public boolean keepWaiting(){

            return this.queueReadsLeft > 0;

    }
}


class SequentialSolution {
    String inputDirectoryPath;
    int polynomsCountInDirectory;

    public SequentialSolution(String inputDirectoryPath, int polynomsCountInDirectory) {
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

class ThreadsSoltion {
    int threadNr;
    String inputDirectoryPath;
    int polynomsCountInDirectory;


    public ThreadsSoltion(int threadNr, String inputDirectoryPath, int polynomsCountInDirectory) {
        this.threadNr = threadNr;
        this.inputDirectoryPath = inputDirectoryPath;
        this.polynomsCountInDirectory = polynomsCountInDirectory;
    }



    public MyList solve() {

        MyList resultList = new MyList();
        int readInfoCount = 0;// how many monoms should be read in total
        System.out.println("Start threads solution...");
        for (int i = 0; i < this.polynomsCountInDirectory; i++) {
            try {
                File text = new File(inputDirectoryPath + "/" + "polynom" + i + ".txt");
                Scanner scanner = new Scanner(text);
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    scanner.next();
                    scanner.next();
                   readInfoCount++;
                }

                // close the scanner
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println(readInfoCount);
        MyQueue queue = new MyQueue(readInfoCount);
        Thread producer = new Thread(() -> {
            for (int i = 0; i < this.polynomsCountInDirectory; i++) {
                try {
                    File text = new File(inputDirectoryPath + "/" + "polynom" + i + ".txt");
                    Scanner scanner = new Scanner(text);
                    scanner.useLocale(Locale.US);
                    while (scanner.hasNext()) {
                        int exponent = Integer.parseInt(scanner.next());
                        int coefficient = Integer.parseInt(scanner.next());
                        //System.out.print(exponent + " -> "+ coefficient + "\n");
                        queue.enqueue(new Node(exponent, coefficient, null));
                        //System.out.println(queue.queue.get(0));
                    }

                    // close the scanner
                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        producer.setPriority(Thread.MIN_PRIORITY);
        List<Thread> consumers = new ArrayList<Thread>();
        for (int i = 0; i < this.threadNr - 1; i++) {
            Thread t = new Thread(() -> {
                while (queue.keepWaiting()) {//until when?
                    Node elem = queue.dequeue();
                    if (elem != null) {
                        System.out.println("Consumer is adding element to result List... " + elem.exponent + "->" + elem.coefficient);
                        resultList.addItem(elem);
                    }
                }
            });
            t.setPriority(Thread.NORM_PRIORITY);
            consumers.add(t);
        }
        producer.start();
        consumers.forEach(Thread::start);
        try {
            producer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumers.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    return resultList;
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

    public static boolean isPairPresent(Node node, List<Node> expCoeffPair){

        for(int i = 0;i< expCoeffPair.size() ;i++){
            if(expCoeffPair.get(i).coefficient == node.coefficient && expCoeffPair.get(i).exponent == node.exponent){
                expCoeffPair.remove(i)  ;return true;}
        }
        return false;
    }

    public static void compareSequentialSolutionToThreadSolution(String seqPath,String currentCasePath){
        List<Node> expCoeffPair = new ArrayList<Node>();
            try {
                File text = new File(seqPath);
                Scanner scanner = new Scanner(text);
                scanner.useLocale(Locale.US);
                while (scanner.hasNext()) {
                    int exponent = Integer.parseInt(scanner.next());
                    int coefficient = Integer.parseInt(scanner.next());

                    expCoeffPair.add(new Node(exponent, coefficient, null));

                }

                // close the scanner
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int actualSize = expCoeffPair.size();

                int matchCount = 0;
                try {
                    File text = new File(currentCasePath);
                    Scanner scanner = new Scanner(text);
                    scanner.useLocale(Locale.US);
                    while (scanner.hasNext()) {
                        int exponent = Integer.parseInt(scanner.next());
                        int coefficient = Integer.parseInt(scanner.next());

                        if(isPairPresent(new Node(exponent, coefficient, null),expCoeffPair)){
                            matchCount++;
                        }

                    }

                    // close the scanner

                    scanner.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if( matchCount != actualSize){
                    System.out.println(seqPath + "file content" + "IS NOT THE SAME as" + currentCasePath);
                }
                else{
                    System.out.println(seqPath + " matches " + currentCasePath);
                }




    }

    public static void compareCase1(){
        String sequential = "src/output/caz1/caz1.txt";
        String _4t = "src/output/caz1/caz1-4t.txt";
        String _6t = "src/output/caz1/caz1-6t.txt";
        String _8t = "src/output/caz1/caz1-8t.txt";
        compareSequentialSolutionToThreadSolution(sequential,_4t);
        compareSequentialSolutionToThreadSolution(sequential,_6t);
        compareSequentialSolutionToThreadSolution(sequential,_8t);
    }

    public static void compareCase2(){
        String sequential = "src/output/caz2/caz2.txt";
        String _4t = "src/output/caz2/caz2-4t.txt";
        String _6t = "src/output/caz2/caz2-6t.txt";
        String _8t = "src/output/caz2/caz2-8t.txt";
        compareSequentialSolutionToThreadSolution(sequential,_4t);
        compareSequentialSolutionToThreadSolution(sequential,_6t);
        compareSequentialSolutionToThreadSolution(sequential,_8t);
    }


    public static void main(String[] args) {
	// write your code here
        //generatePolynoms(10,1000,50,"src/input/caz1");
        //generatePolynoms(5,10000,100,"src/input/caz2");
        //SequentialSolution sol = new SequentialSolution("src/input/caz1",10);
        //SequentialSolution sol = new SequentialSolution("src/input/caz2",5);
        //MyList result = sol.solve();
        //sol.printMyList(result,"src/output/caz1.txt");
        //sol.printMyList(result,"src/output/caz2.txt");

        //ThreadsSoltion sol = new ThreadsSoltion(4,"src/input/caz1",10);
        //ThreadsSoltion sol = new ThreadsSoltion(6,"src/input/caz1",10);
        //ThreadsSoltion sol = new ThreadsSoltion(8,"src/input/caz1",10);
        //ThreadsSoltion sol = new ThreadsSoltion(4,"src/input/caz2",5);
        //ThreadsSoltion sol = new ThreadsSoltion(6,"src/input/caz2",5);
        //ThreadsSoltion sol = new ThreadsSoltion(8,"src/input/caz2",5);
//        long startTime = System.nanoTime();
//        MyList result = sol.solve();
//        long endTime = System.nanoTime();
//        System.out.println((double)endTime - startTime/1E6);
        //result.printMyList(result,"src/output/caz1-4t.txt");
        //result.printMyList(result,"src/output/caz1-6t.txt");
        //result.printMyList(result,"src/output/caz1-8t.txt");
        //result.printMyList(result,"src/output/caz2-4t.txt");
        //result.printMyList(result,"src/output/caz2-6t.txt");
        //result.printMyList(result,"src/output/caz2-8t.txt");
        //result.printMyList(result,"src/output/caz1.txt");
        //result.printMyList(result,"src/output/caz2.txt");
        compareCase1();
        compareCase2();
    }
}
