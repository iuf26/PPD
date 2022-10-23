package utils;

public class MyThreadInterval extends Thread{
    private Integer[] a;
    private Integer[] b;
    private Double[] c;
    private Integer start,end;

    public MyThreadInterval(Integer[] a, Integer[] b, Double[] c, Integer start, Integer end) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        for(int i = start;i<end;i++){
            c[i] = Math.sqrt(Math.pow(a[i],4) + Math.pow(b[i],4));
        }
    }
}
