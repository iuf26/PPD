#include <iostream>
#include <vector>
#include <thread>
#include <chrono>
#include <cmath>
using namespace std;

void printVector(vector<int> myVector)
{
    for (int elem : myVector)
    {
        cout << elem << " ";
    }
    cout << endl;
}

int add(int a, int b)
{

    return sqrt(pow(a, 4) + pow(b, 4));
}

void addThread(int left, int right, vector<int> &a, vector<int> &b, vector<int> &c)
{
    for (int i = left; i < right; i++)
    {
        c[i] = add(a[i], b[i]);
    }
}

int main()
{
    //Sequential 

    const int n = 1000000;
    const int bound = 900000;
    vector<int> a(n), b(n), c(n), d(n);
    srand(time(NULL));
    for (int i = 0; i < n; i++)
    {
        a[i] = rand() % bound;
        b[i] = rand() % bound;
    }

    auto startWatch = chrono::high_resolution_clock::now();

    for (int i = 0; i < n; i++)
    {
        c[i] = add(a[i], b[i]);
    }

    auto endWatch = chrono::high_resolution_clock::now();
    cout << "Sequential:" << chrono::duration<double, milli>(endWatch - startWatch).count() << endl;
    // cout << "a:";
    // printVector(a);
    // cout << "b:";
    // printVector(b);
    // cout << "c:";
    // printVector(c);

    //Intervals 
    const int p = 4;
    vector<thread> workers(p);

    int whole = n / p;
    int reminder = n % p;
    int left = 0;
    int right = whole;
    startWatch = chrono::high_resolution_clock::now();
    for (int i = 0; i < p; i++)
    {
        if (reminder)
        {
            right++;
            reminder--;
        }

        workers[i] = thread(addThread, left, right, ref(a), ref(b), ref(d)); // thread starts imediatly
        left = right;
        right = right + whole;
    }

    for (int i = 0; i < p; i++)
    {
        workers[i].join();
    }
    endWatch = chrono::high_resolution_clock::now();
    cout << "Intervals:" << chrono::duration<double, milli>(endWatch - startWatch).count() << endl;
    // cout << "d:";
    // printVector(d);
    return 0;
}