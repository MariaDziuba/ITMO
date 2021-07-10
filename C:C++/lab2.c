#include <stdio.h>
#include <string.h>
#include <stdlib.h>

struct Person {
    char surname[21];
    char name[21];
    char otchestvo[21];
    int telephone;
};

int intcmp(int a, int b) {
    if (a > b) {
        return 1;
    } else {
        if (a < b) {
            return -1;
        } else {
            return 0;
        }
    }
}

int cmp(struct Person a, struct Person b) {
    if ((strcmp(a.surname, b.surname) == 0) && (strcmp(a.name, b.name) == strcmp(a.otchestvo, b.otchestvo)) &&
        (intcmp(a.telephone, b.telephone) == 0)) {
        return 0;
    }
    if (strcmp(a.surname, b.surname) > 0) {
        return 1;
    }
    if (strcmp(a.surname, b.surname) < 0) {
        return -1;
    }
    if (strcmp(a.name, b.name) > 0) {
        return 1;
    }
    if (strcmp(a.name, b.name) < 0) {
        return -1;
    }
    if (strcmp(a.otchestvo, b.otchestvo) > 0) {
        return 1;
    }
    if (strcmp(a.otchestvo, b.otchestvo) < 0) {
        return -1;
    }
    if (intcmp(a.telephone, b.telephone) > 0) {
        return 1;
    }
    if (intcmp(a.telephone, b.telephone) < 0) {
        return -1;
    }
    printf("The field does not exist");
    return -1000000000;
}

void swap(struct Person *a, struct Person *b) {
    struct Person tmp = *a;
    *a = *b;
    *b = tmp;
}

int partition(struct Person* a, int l, int r) {
    int i = l - 1;
    int j = l;
    while (j < r) {
        if (cmp(a[j], a[r]) < 0 || cmp(a[j], a[r]) == 0) {
            i++;
            swap(&a[i], &a[j]);
        }
        j++;
    }
    i++;
    swap(&a[i], &a[j]);
    return i;
}

void quickSort(struct Person* a, int l, int r) {
    while (l < r) {
        int mid = partition(a, l, r);
        quickSort(a, l, mid - 1);
        l = mid + 1;
    }
}

int main(int argc, char **argv) {
    if (argc != 3) {
        printf("Sorry, the number of arguments is invalid\n");
        printf("You should have launched the program with 2 arguments:\n");
        printf("<input_file> <output_file>");
    }
    FILE *input;
    if ((input = fopen(argv[1], "r")) == NULL) {
        printf("Couldn't open the %s file", argv[1]);
        return -10;
    }
    FILE *output;
    if ((output = fopen(argv[2], "w")) == NULL) {
        printf("Couldn't open the %s file", argv[2]);
        return -20;
    }
    struct Person *people;
    if ((people = malloc(10 * sizeof(struct Person))) == NULL) {
        printf("Couldn't allocate memory");
        return -30;
    }
    int tmp_size = 10;
    int real_size = 0;
    for (int i = 0; fscanf(input, "%s %s %s %i", people[i].surname, people[i].name,
                           people[i].otchestvo, &people[i].telephone) != EOF; i++) {
        if (i >= tmp_size - 1) {
            struct Person *old = people;
            if ((people = realloc(people, sizeof(struct Person) * tmp_size * 2)) == NULL) {
                free(old);
                printf("Couldn't allocate memory");
                return -30;
            }
            tmp_size = tmp_size * 2;
            real_size++;

        } else {
            real_size++;
        }
    }
    quickSort(people, 0, real_size - 1);

    for (int i = 0; i < real_size; i++) {
        fprintf(output, "%s %s %s %i \n", people[i].surname, people[i].name, people[i].otchestvo,
                people[i].telephone);
    }
    free(people);
    fclose(output);
    fclose(input);
}

