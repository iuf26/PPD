package utils;

public class MyThread extends Thread{

    private Integer[] a;
    private Integer[] b;
    private Double[] c;
    private int p;
    private int startIndex;

    public MyThread(Integer[] a, Integer[] b, Double[] c, int p, int startIndex) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.p = p;
        this.startIndex = startIndex;
    }

    @Override
    public void run() {
        int current = startIndex;
        while(current < c.length){
            c[current] = Math.sqrt(Math.pow(a[current],4) + Math.pow(b[current],4));
            current += p;
        }
    }
}
