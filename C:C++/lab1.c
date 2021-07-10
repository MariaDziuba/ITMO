#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>

const unsigned char BOM_UTF32LE[5] = {0x04, 0xFF, 0xFE, 0x00, 0x00};
const unsigned char BOM_UTF32BE[5] = {0x04, 0x00, 0x00, 0xFE, 0xFF};
const unsigned char BOM_UTF16LE[3] = {0x02, 0xFF, 0xFE};
const unsigned char BOM_UTF16BE[3] = {0x02, 0xFE, 0xFF};
const unsigned char BOM_UTF8[4] = {0x03, 0xEF, 0xBB, 0xBF};

unsigned long int *buf;

long int bufsize;

int isBOM(unsigned char *data, const unsigned char *BOM) {
    for (unsigned int i = 0; i < BOM[0]; i++) {
        if (data[i] != BOM[i + 1]) return 0;
    }
    return 1;
}


void decodeUTF8(FILE *f, int blen, int usebom) {
    if (usebom == 1)
        fseek(f, BOM_UTF8[0], SEEK_SET);
    else
        fseek(f, 0, SEEK_SET);

    buf = (unsigned long int *) malloc(blen * sizeof(unsigned long int));
    if (buf == 0) {
        printf("Couldn't allocate memory,\n");
        return;
    }
    long bufp = 0;
    unsigned char c0;
    unsigned char c1;
    while (!feof(f)) {
        if (fread(&c0, 1, 1, f) == 0) break;
        if ((c0 >> 7) == 0) {
            buf[bufp] = c0;
        } else {
            fread(&c1, 1, 1, f);
            buf[bufp] = (c1 & 0b00111111) + ((c0 & 0b00011111) << 6);
        }
        bufp++;
    }
    bufsize = bufp;
}

void decodeUTF16LE(FILE *f, int blen) {
    fseek(f, BOM_UTF16LE[0], SEEK_SET);
    buf = (unsigned long int *) malloc(blen * sizeof(unsigned long int));
    if (buf == 0) {
        printf("Couldn't allocate memory,\n");
        return;
    }
    long bufp = 0;
    unsigned char c0;
    unsigned char c1;
    while (!feof(f)) {
        if (fread(&c0, 1, 1, f) == 0) break;
        fread(&c1, 1, 1, f);
        buf[bufp] = (c1 << 8) + c0;
        bufp++;
    }
    bufsize = bufp;
}

void decodeUTF16BE(FILE *f, int blen) {
    fseek(f, BOM_UTF16BE[0], SEEK_SET);
    buf = (unsigned long int *) malloc(blen * sizeof(unsigned long int));
    if (buf == 0) {
        printf("Couldn't allocate memory,\n");
        return;
    }
    long bufp = 0;
    unsigned char c0;
    unsigned char c1;
    while (!feof(f)) {
        if (fread(&c0, 1, 1, f) == 0) break;
        fread(&c1, 1, 1, f);
        buf[bufp] = (c0 << 8) + c1;
        bufp++;
    }
    bufsize = bufp;
}

void decodeUTF32LE(FILE *f, int blen) {
    fseek(f, BOM_UTF32LE[0], SEEK_SET);
    buf = (unsigned long int *) malloc(blen * sizeof(unsigned long int));
    if (buf == 0) {
        printf("Couldn't allocate memory,\n");
        return;
    }
    long bufp = 0;
    unsigned char c0;
    unsigned char c1;
    unsigned char zero;
    while (!feof(f)) {
        if (fread(&c0, 1, 1, f) == 0) break;
        fread(&c1, 1, 1, f);
        fread(&zero, 1, 1, f);
        fread(&zero, 1, 1, f);
        buf[bufp] = (c1 << 8) + c0;
        bufp++;
    }

    bufsize = bufp;
}

void decodeUTF32BE(FILE *f, int blen) {
    fseek(f, BOM_UTF32BE[0], SEEK_SET);
    buf = (unsigned long int *) malloc(blen * sizeof(unsigned long int));
    if (buf == 0) {
        printf("Couldn't allocate memory,\n");
        return;
    }
    long bufp = 0;
    unsigned char c0;
    unsigned char c1;
    unsigned char zero;
    while (!feof(f)) {
        if (fread(&zero, 1, 1, f) == 0) break;
        fread(&zero, 1, 1, f);
        fread(&c0, 1, 1, f);
        fread(&c1, 1, 1, f);
        buf[bufp] = (c0 << 8) + c1;
        bufp++;
    }

    bufsize = bufp;
}


void encodeUTF8(FILE *f, int writebom) {
    if (writebom) fwrite(BOM_UTF8 + 1, BOM_UTF8[0], 1, f);
    unsigned char c0;
    unsigned char c1;
    for (int i = 0; i < bufsize; i++) {

        if (buf[i] <= 0x80)
            fwrite(buf + i, 1, 1, f);
        else {
            c1 = (buf[i] & 0b00111111) + 0b10000000;
            c0 = ((buf[i] >> 6) & 0b00011111) + 0b11000000;
            fwrite(&c0, 1, 1, f);
            fwrite(&c1, 1, 1, f);
        }
    }
}

void encodeUTF16LE(FILE *f) {
    fwrite(BOM_UTF16LE + 1, BOM_UTF16LE[0], 1, f);
    unsigned char c0;
    unsigned char c1;
    for (int i = 0; i < bufsize; i++) {
        c0 = buf[i] & 0xFF;
        c1 = (buf[i] >> 8) & 0xFF;
        fwrite(&c0, 1, 1, f);
        fwrite(&c1, 1, 1, f);
    }
}

void encodeUTF16BE(FILE *f) {
    fwrite(BOM_UTF16BE + 1, BOM_UTF16BE[0], 1, f);
    unsigned char c0;
    unsigned char c1;
    for (int i = 0; i < bufsize; i++) {
        c0 = buf[i] & 0xFF;
        c1 = (buf[i] >> 8) & 0xFF;
        fwrite(&c1, 1, 1, f);
        fwrite(&c0, 1, 1, f);
    }
}

void encodeUTF32LE(FILE *f) {
    fwrite(BOM_UTF32LE + 1, BOM_UTF32LE[0], 1, f);

    unsigned char zero = 0x00;
    unsigned char c0;
    unsigned char c1;
    for (int i = 0; i < bufsize; i++) {
        c0 = buf[i] & 0xFF;
        c1 = (buf[i] >> 8) & 0xFF;
        fwrite(&c0, 1, 1, f);
        fwrite(&c1, 1, 1, f);
        fwrite(&zero, 1, 1, f);
        fwrite(&zero, 1, 1, f);
    }
}

void encodeUTF32BE(FILE *f) {
    fwrite(BOM_UTF32BE + 1, BOM_UTF32BE[0], 1, f);

    unsigned char zero = 0x00;
    unsigned char c0;
    unsigned char c1;
    for (int i = 0; i < bufsize; i++) {
        c0 = buf[i] & 0xFF;
        c1 = (buf[i] >> 8) & 0xFF;
        fwrite(&zero, 1, 1, f);
        fwrite(&zero, 1, 1, f);
        fwrite(&c1, 1, 1, f);
        fwrite(&c0, 1, 1, f);
    }
}

int main(int argc, char **argv) {
    if (argc < 4) {
        printf("Sorry, the number of arguments is invalid\n");
        printf("You should have launched the program with 3 arguments:\n");
        printf("<input_file> <output_file> <output_encoding (0-5)>");
        return 1;
    }

    unsigned char testbom[4];

    struct stat st;
    stat(argv[1], &st);
    int sz = st.st_size;

    FILE *fi = fopen(argv[1], "rb");
    if (fi == 0) {
        printf("Couldn't open the %s file\n", argv[1]);
        return 1;
    }

    fread(testbom, 1, 4, fi);

    if (isBOM(testbom, BOM_UTF8)) decodeUTF8(fi, sz - BOM_UTF8[0], 1);
    else if (isBOM(testbom, BOM_UTF32LE)) decodeUTF32LE(fi, sz - BOM_UTF32LE[0]);
    else if (isBOM(testbom, BOM_UTF32BE)) decodeUTF32BE(fi, sz - BOM_UTF32BE[0]);
    else if (isBOM(testbom, BOM_UTF16LE)) decodeUTF16LE(fi, sz - BOM_UTF16LE[0]);
    else if (isBOM(testbom, BOM_UTF16BE)) decodeUTF16BE(fi, sz - BOM_UTF16BE[0]);
    else decodeUTF8(fi, sz, 0);
    fclose(fi);

    FILE *fo = fopen(argv[2], "wb");
    if (fo == 0) {
        printf("Couldn't open the %s file\n", argv[2]);
        return 1;
    }

    if (argv[3][0] == '0') encodeUTF8(fo, 0);
    else if (argv[3][0] == '1') encodeUTF8(fo, 1);
    else if (argv[3][0] == '2') encodeUTF16LE(fo);
    else if (argv[3][0] == '3') encodeUTF16BE(fo);
    else if (argv[3][0] == '4') encodeUTF32LE(fo);
    else if (argv[3][0] == '5') encodeUTF32BE(fo);
    else printf("Unknown encoding number: %c\n", argv[3][0]);
    fclose(fo);

    free(buf);

    return 0;
}
