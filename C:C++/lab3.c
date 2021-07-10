#include <stdio.h>
#include <stdlib.h>
#include <math.h>

double **matrix;
double *rightpart;
double *answer;
int rowcount;
int colcount;

const int MULTISOL = 2;
const int NOSOL = 1;
const int STDSOL = 0;

const double EPS = 1.0E-5;

void sortRows(int sortindex) {

    double maxelement = matrix[sortindex][sortindex];
    int maxelementindex = sortindex;
    for (int i = sortindex + 1; i < rowcount; i++) {
        if (matrix[i][sortindex] > maxelement) {
            maxelement = matrix[i][sortindex];
            maxelementindex = i;
        }
    }

    if (maxelementindex > sortindex) {
        double tmp;

        tmp = rightpart[maxelementindex];
        rightpart[maxelementindex] = rightpart[sortindex];
        rightpart[sortindex] = tmp;

        for (int i = 0; i < colcount; i++) {
            tmp = matrix[maxelementindex][i];
            matrix[maxelementindex][i] = matrix[sortindex][i];
            matrix[sortindex][i] = tmp;
        }
    }
}

int doSolve() {
    for (int i = 0; i < rowcount - 1; i++) {
        sortRows(i);
        for (int j = i + 1; j < rowcount; j++) {
            if (matrix[i][i] != 0)
            {
                double multelement = matrix[j][i] / matrix[i][i];
                for (int k = i; k < colcount; k++)
                    matrix[j][k] -= matrix[i][k] * multelement;
                rightpart[j] -= rightpart[i] * multelement;
            }
        }
    }

    for (int i = (int) (rowcount - 1); i >= 0; i--) {
        answer[i] = rightpart[i];

        for (int j = (int) (rowcount - 1); j > i; j--)
            answer[i] -= matrix[i][j] * answer[j];

        if (fabs(matrix[i][i]) < EPS) {
            if (fabs(rightpart[i]) < EPS)
                return MULTISOL;
            else
                return NOSOL;
        }

        answer[i] /= matrix[i][i];

    }
    return STDSOL;
}

void loadData(const char *filename) {
    FILE *f = fopen(filename, "r");
    fscanf(f, "%d", &rowcount);
    colcount = rowcount;

    matrix = (double **) malloc(sizeof(double *) * colcount);
    for (int i = 0; i < colcount; i++)
        matrix[i] = (double *) malloc(sizeof(double) * rowcount);

    rightpart = (double *) malloc(sizeof(double) * rowcount);
    answer = (double *) malloc(sizeof(double) * rowcount);

    for (int j = 0; j < rowcount; j++) {
        for (int i = 0; i < colcount; i++)
            fscanf(f, "%lf", &matrix[j][i]);
        fscanf(f, "%lf", &rightpart[j]);
    }

    fclose(f);
}

void saveSolution(const char *filename, int result) {
    FILE *f = fopen(filename, "w");
    if (result == MULTISOL) fprintf(f, "many solutions\n");
    if (result == NOSOL) fprintf(f, "no solution\n");
    if (result == STDSOL)
        for (int j = 0; j < rowcount; j++)
            fprintf(f, "%lf\n", answer[j]);
    fclose(f);
}

void freeData() {
    for (int i = 0; i < colcount; i++)
        free(matrix[i]);
    free(rightpart);
    free(answer);
}

int main(int argc, char **argv) {
    if (argc < 3) {
        printf("Sorry, the number of arguments is invalid\n");
        printf("You should have launched the program with 2 arguments:\n");
        printf("<input_file> <output_file>");
        return 1;
    }
    
    loadData(argv[1]);
    int r = doSolve();
    saveSolution(argv[2], r);
    freeData();

    return 0;
}
