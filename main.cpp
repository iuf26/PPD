#include <iostream>
#include <cstdlib>
#include <fstream>
#include <sstream>
#include <cstring>
#include <thread>
#include <chrono>
#include <vector>
using namespace std;

double *convertMatrixToList(double **_matrix, int _m, int _n)
{
    const int lsize = _m * _n;
    double *result = new double[lsize];
    int c = 0;
    for (int i = 0; i < _m; i++)
    {
        for (int j = 0; j < _n; j++)
        {
            result[c] = _matrix[i][j];
            c++;
        }
    }
    return result;
}

int *mapListToMatrixCoordinates(int index, int _m, int _n)
{
    int line = index / _m;
    int col = index % _n;
    int *result = new int[2];
    result[0] = line;
    result[1] = col;
    return result;
}

double getGuardedMatrixElement(int i, int j, double **matrix, int M, int N)
{
    if (i < -1 || j < -1)
    {
        if (i == j)
            return getGuardedMatrixElement(i + 1, j + 1, matrix, M, N);
        if (i > j)
            return getGuardedMatrixElement(i, j + 1, matrix, M, N);
        return getGuardedMatrixElement(i + 1, j, matrix, M, N);
    }

    if (i > M || j > N)
    {
        if (i == j)
            return getGuardedMatrixElement(i - 1, j - 1, matrix, M, N);
        if (i < j)
            return getGuardedMatrixElement(i, j - 1, matrix, M, N);
        return getGuardedMatrixElement(i - 1, j, matrix, M, N);
    }

    if (i == -1 && j == -1)
        return matrix[0][0];
    if (i == -1 && j < N)
        return matrix[0][j];
    if (j == -1 && i < M)
        return matrix[i][0];
    if (i == M && j >= 0 && j < N)
        return matrix[M - 1][j];
    if (j == N && i >= 0 && i < M)
        return matrix[i][N - 1];
    if (i >= 0 && i < M)
        return matrix[i][j];
    return 0;
}

double getResultMatrixCorespondentElement(int index, int _m, int _n, int _M, int _N, double *listKernel, double **matrix)
{
    int kernelMiddle = (_m * _n) / 2;
    int *kernelMiddleCoordinates = mapListToMatrixCoordinates(kernelMiddle, _m, _n);
    int *matrixMiddleCoordinates = mapListToMatrixCoordinates(index, _M, _N);
    int matrixMiddleX = matrixMiddleCoordinates[0];
    int matrixMiddleY = matrixMiddleCoordinates[1];
    double result = 0;
    for (int current = 0; current < _m * _n; current++)
    {
        int *currentKernelMatrixCoordinates = mapListToMatrixCoordinates(current, _m, _n);
        int *coordinatesToGetValueFromOriginalMatrix = new int[2];
        coordinatesToGetValueFromOriginalMatrix[0] = matrixMiddleX + (currentKernelMatrixCoordinates[0] - kernelMiddleCoordinates[0]);
        coordinatesToGetValueFromOriginalMatrix[1] = matrixMiddleY + (currentKernelMatrixCoordinates[1] - kernelMiddleCoordinates[1]);
        double value = getGuardedMatrixElement(coordinatesToGetValueFromOriginalMatrix[0], coordinatesToGetValueFromOriginalMatrix[1], matrix, _M, _N);
        result += listKernel[current] * value;
    }
    return result;
}

void computeResultListMatrix(int start, int end, double *result, int _m, int _n, int _M, int _N, double *listKernel, double **matrix)
{
    for (int i = start; i < end; i++)
    {
        result[i] = getResultMatrixCorespondentElement(i, _m, _n, _M, _N, listKernel, matrix);
    }
}

double **convertListToMatrix(double *list, int _m, int _n)
{

    double **result = new double *[_m];
    for (int i = 0; i < _m; i++)
    {
        result[i] = new double[_n]{0};
    }
    int l = 0;
    int c = 0;
    for (int i = 0; i < _m * _n; i++)
    {
        if (c == _n)
        {
            c = 0;
            l++;
        }
        if (l < _m)
        {
            result[l][c] = list[i];
            c++;
        }
    }
    return result;
}

const string filename = "date.txt";

int main(int argc, char **argv)
{
    // Citire din fisier + initializare

    string outputFile = argv[2];

    string a, b, c, d, row;
    ifstream MyReadFile(filename);

    getline(MyReadFile, a);
    const int M = std::stoi(a);
    getline(MyReadFile, b);
    const int N = std::stoi(b);
    getline(MyReadFile, c);
    const int m = std::stoi(c);
    getline(MyReadFile, d);
    const int n = std::stoi(d);

    double **matrix = new double *[M];
    for (int i = 0; i < M; i++)
    {
        matrix[i] = new double[N]{0};
        int j = 0;
        string row;
        getline(MyReadFile, row);
        std::stringstream ls;
        ls << row;
        double c;
        while (!ls.eof())
        {
            ls >> c;
            matrix[i][j] = c;
            j++;
        }
    }

    double **kernel = new double *[m];
    for (int i = 0; i < m; i++)
    {
        kernel[i] = new double[n]{0};
        int j = 0;
        string row;
        getline(MyReadFile, row);
        std::stringstream ls;
        ls << row;
        double c;
        while (!ls.eof())
        {
            ls >> c;
            kernel[i][j] = c;
            j++;
        }
    }

    MyReadFile.close();

    // logic
    double *listMatrix = convertMatrixToList(matrix, M, N);
    double *listKernel = convertMatrixToList(matrix, m, n);
    if (strcmp(argv[1], "seq") == 0)
    {
        // sequential run
       
        const int resSize = M * N;
        double *result = new double[resSize];
        auto startWatch = chrono::high_resolution_clock::now();
        computeResultListMatrix(0, M * N, result, m, n, M, N, listKernel, matrix);
        double **finalResult = convertListToMatrix(result, M, N);
        auto endWatch = chrono::high_resolution_clock::now();
        cout << chrono::duration<double, milli>(endWatch - startWatch).count();

        ofstream MyFile(outputFile);
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                MyFile << finalResult[i][j] << " ";
            }
            MyFile << "\n";
        }
        MyFile.close();
    }
    else
    {
        // threads run
      
        int threadsNr = atoi(argv[1]);
        double *result = new double[M * N];
        vector<thread> workers(threadsNr);
        int whole = (M * N) / threadsNr;
        int reminder = (M * N) % threadsNr;
        int left = 0;
        int right = whole;
        auto startWatch = chrono::high_resolution_clock::now();
        for (int i = 0; i < threadsNr; i++)
        {
            if (reminder)
            {
                right++;
                reminder--;
            }

            workers[i] = thread(computeResultListMatrix, left, right, result, m, n, M, N, listKernel, matrix); // thread starts imediatly
            left = right;
            right = right + whole;
        }

        for (int i = 0; i < threadsNr; i++)
        {
            workers[i].join();
        }
        double **finalResult = convertListToMatrix(result, M, N);
        auto endWatch = chrono::high_resolution_clock::now();
        cout << chrono::duration<double, milli>(endWatch - startWatch).count();

        ofstream MyFile(outputFile);
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                MyFile << finalResult[i][j] << " ";
            }
            MyFile << "\n";
        }
        MyFile.close();
    }
}