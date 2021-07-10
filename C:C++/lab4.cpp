#include<iostream>
#include<vector>
#include<string>
#include <string.h>
#include <fstream>

using namespace std;

class LongNum {
public:
    int z;//знак
    vector<int> d;//цифры
    int Np;//длина числа
    LongNum(string s) {
        int ii = 1;
        z = 1;
        if (s.size() == 0) {
            Np = 0;
        } else if ((s[0] != '-') && (s[0] != '+') && ((s[0] < '0') || (s[0] > '9'))) { Np = 0; }
        else if ((s[0] == '-') || (s[0] == '+')) {
            ii = 2;
            if (s[0] == '-') z = -1;
            Np = s.size() - 1;
        } else { Np = s.size(); }
        if ((ii == 2) && (s.size() == 1)) { Np = 0; }
        if (Np > 0) {
            int cod = 0;
            for (int i = ii; i <= s.size(); i++)
                if ((s[i - 1] < '0') || (s[i - 1] > '9')) {
                    cod = 1;
                    break;
                }
            if (cod > 0) { Np = 0; }
            else {

                Np = s.size() - ii + 1;
                for (int i = s.size(); i >= ii; i--)
                    d.push_back(s[i - 1] - '0');

            }

        }
        while ((Np > 0) && (d[Np - 1] == 0)) {
            Np--;
            d.pop_back();
        }
        if (Np == 0) {
            z = 1;
            Np = 1;
            d.push_back(0);
        }


    }

    LongNum(int znak, int N)//число из N нулей
    {
        z = znak;
        Np = N;
        for (int i = 1; i <= N; i++) d.push_back(0);
    }

    LongNum() { Np = 0; }

    friend LongNum operator+(LongNum q, LongNum val) {
        if (q.z == val.z) {
            int N = q.Np;
            int d1, d2;
            int carry = 0;
            if (val.Np > N) N = val.Np;
            LongNum Q(q.z, N);
            for (int i = 1; i <= N; i++) {
                if (i <= q.Np) d1 = q.d[i - 1]; else d1 = 0;
                if (i <= val.Np) d2 = val.d[i - 1]; else d2 = 0;
                int d0 = d1 + d2 + carry;
                Q.d[i - 1] = d0 % 10;
                carry = d0 / 10;
            }
            if (carry > 0) {
                Q.Np = N + 1;
                Q.d.push_back(carry);
            }
            else Q.Np = N;
            while ((Q.Np > 0) && (Q.d[Q.Np - 1] == 0)) {
                Q.d.pop_back();
                Q.Np--;
            }
            if (Q.Np == 0) {
                Q.d.push_back(0);
                Q.Np = 1;
                Q.z = 1;
            }
            return Q;

        } else if ((q.z == 1) && (val.z == -1)) return (q - abs(val));
        else return (val - abs(q));


    }

    friend LongNum abs(LongNum q) {
        if (q.z > 0) return q;
        else return -q;

    }

    friend LongNum operator-(LongNum q) {
        if (q.isZero()) {
            q.z = 1;
            return q;
        }
        LongNum q1 = q;
        q1.z = q1.z * (-1);
        return q1;

    }

//Квадратный корень ! вместо #
    friend LongNum operator!(LongNum q) {
        if (q.isZero()) {
            LongNum z = LongNum("0");
            return z;
        }
        else if (q.z < 0) {
            LongNum zt;
            return zt;
        }
        else {
            LongNum z1 = LongNum("0");
            LongNum z2 = q;
            LongNum Q1 = LongNum("1");
            LongNum Q2 = LongNum("2");
            while ((z2 - z1) > Q1) {
                LongNum zz = ((z1 + z2) / Q2);
                if ((zz * zz) >= q) z2 = zz; else z1 = zz;

            }
            if (((z2 * z2) - q).isZero()) return z2; else return z1;
        }


    }

    friend bool operator>(LongNum q, LongNum val) {

        bool z = (val >= q);
        return !z;

    }

    friend bool operator<(LongNum q, LongNum val) {
        return (val > q);
    }

    friend bool operator<=(LongNum q, LongNum val) {
        return !(q > val);
    }

    friend bool operator==(LongNum q, LongNum val) {
        return ((q >= val) && (val >= q));
    }

    friend bool operator>=(LongNum q, LongNum val) {
        if ((q.z == 1) && (val.z == -1)) return true;
        if ((q.z == -1) && (val.z == 1)) return false;
        if ((q.z == -1) && (val.z == -1)) return (-val >= -q);
        if (q.Np > val.Np) return true;
        if (val.Np > q.Np) return false;
        for (int i = 1; i <= q.Np; i++) {
            if (q.d[q.Np - i] > val.d[q.Np - i]) return true;
            if (q.d[q.Np - i] < val.d[q.Np - i]) return false;


        }
        return true;
    }

    friend LongNum operator-(LongNum q, LongNum val) {
        if ((val.z == -1) && (q.z == 1)) return q + (-val);
        if ((val.z == 1) && (q.z == -1)) return -(abs(q) + val);
        if ((val.z == 1) && (q.z == 1)) {
            if (val > q) return -(val - q);

        }
        if ((val.z == -1) && (q.z == -1)) {
            return ((-val) - (-q));

        }

        int N = q.Np;
        int d1, d2;
        int carry = 0;
        LongNum Q(1, N);
        for (int i = 1; i <= N; i++) {
            d1 = q.d[i - 1];
            if (i <= val.Np) d2 = val.d[i - 1]; else d2 = 0;
            int d0 = d1 - d2 - carry;
            if (d0 < 0) {
                d0 = d0 + 10;
                carry = 1;
            }
            else carry = 0;
            Q.d[i - 1] = d0;
        }
        while ((Q.Np > 0) && (Q.d[Q.Np - 1] == 0)) {
            Q.d.pop_back();
            Q.Np--;
        }
        if (Q.Np == 0) {
            Q.d.push_back(0);
            Q.Np = 1;
            Q.z = 1;
        }

        return Q;
    }

    LongNum mult10k(int k) {
        for (int i = 1; i <= k; i++)
            d.insert(d.begin(), 0);
        Np += k;
        return *this;
    }

        LongNum multDig(int k) {
            LongNum Q(1, Np);
            int carry = 0;
            for (int i = 1; i <= Np; i++) {
                int q = d[i - 1] * k + carry;
                Q.d[i - 1] = q % 10;
                carry = q / 10;

            }
            if (carry > 0) {
                Q.d.push_back(carry);
                Q.Np++;
            }
            return Q;
        }

        friend LongNum operator*(LongNum q1, LongNum q2) {
            LongNum Q = LongNum("0");
            if (q1.z == q2.z) Q.z = 1; else Q.z = -1;
            for (int i = 1; i <= q2.Np; i++) {
                LongNum Z = q1.multDig(q2.d[i - 1]);
                Z = Z.mult10k(i - 1);
                Q = Q + Z;


            }
            Q.Np = Q.d.size();
            while ((Q.Np > 0) && (Q.d[Q.Np - 1] == 0)) {
                Q.Np--;
                Q.d.pop_back();
            }
            if (Q.Np == 0) {
                Q.Np = 1;
                Q.d.push_back(0);
                Q.z = 1;
            }
            return Q;
        }
        bool isZero() {
            if (Np == 0) return false;
            for (int i = 1; i <= Np; i++)
                if (d[i - 1] > 0) return false;
            return true;

        }
        friend LongNum operator/(LongNum q1, LongNum q2) {
            LongNum Q;
            if (q2.isZero()) return Q;
            if (q1.z == q2.z) Q.z = 1; else Q.z = -1;
            int k = q1.Np;
            LongNum q11;
            q11.z = 1;
            q11.Np = 0;
            while (k >= 1) {
                if ((q1.d[k - 1] > 0) || (q11.Np > 0)) {
                    q11.d.insert(q11.d.begin(), q1.d[k - 1]);
                    q11.Np++;
                }
                if (q2 > q11)
                    Q.d.insert(Q.d.begin(), 0);
                else {
                    int j;
                    for (j = 1; j <= 10; j++) {
                        bool z = (q2.multDig(j) > q11);
                        if (z) break;
                    }
                    q11 = q11 - q2.multDig(j - 1);
                    while ((q11.Np > 0) && (q11.d[q11.Np - 1] == 0)) {
                        q11.Np--;
                        q11.d.pop_back();
                    }
                    Q.d.insert(Q.d.begin(), j - 1);

                }
                k--;
            }
            Q.Np = Q.d.size();
            while ((Q.Np > 0) && (Q.d[Q.Np - 1] == 0)) {
                Q.Np--;
                Q.d.pop_back();
            }
            if (Q.Np == 0) {
                Q.Np = 1;
                Q.d.push_back(0);
                Q.z = 1;
            }
            return Q;
        }

        string toStr() {
            if (Np == 0) return "NaN";
            string s = "";
            for (int i = 1; i <= Np; i++)
                s = (char) ('0' + d[i - 1]) + s;
            if (z == -1) s = "-" + s;
            return s;

        }
    };

int main(int argc, char *argv[]) {
    setlocale(LC_ALL, "Russian");
    char s11[80];
    char s22[80];
    if (argc < 3) {
        cout << "Sorry, the number of arguments is invalid" << endl;
        cout << "You should have launched the program with 2 arguments:"<<endl;
        cout << "<input_file> <output_file>" << endl;
        return 1;
    }
    strcpy_s(s11, _countof(s11), argv[1]);
    strcpy_s(s22, _countof(s22), argv[2]);
    ifstream F(s11);
    if (!F) {
        cout << "Couldn't open the input file " << endl;
        return 1;

    }
    string s1, s2, s3;
    LongNum z1, z2, z3;
    F >> s1 >> s2;
    z1 = LongNum(s1);
    if (z1.Np == 0) {
        cout << "Mistake in the first number" << endl;
        cout << "Correct the input file and launch the program again" << endl;
        F.close();
        return 1;
    }
    if ((s2 != "+") && (s2 != "-") && (s2 != "*") && (s2 != "/") && (s2 != "<") && (s2 != "<=")
        && (s2 != ">") && (s2 != ">=") && (s2 != "==") && (s2 != "!")) {
        cout << "Mistake in the symbol of operation" << endl;
        cout << "Correct the input file and launch the program again" << endl;
        F.close();
        return 1;

    }
    if (s2 != "!") {
        F >> s3;
        z2 = LongNum(s3);
        if (z2.Np == 0) {
            cout << "Mistake in the second file" << endl;
            cout << "Correct the input file and launch the program again" << endl;
            F.close();
            return 1;
        }

    }
    ofstream F1(s22);
    if (!F1) {
        cout << "Couldn't open the input file "<<endl;
        return 1;

    }
    bool z;
    F.close();
    if (s2 == "+") {
        z3 = z1 + z2;
        F1 << z3.toStr();
    }
    else if (s2 == "-") {
        z3 = z1 - z2;
        F1 << z3.toStr();
    }
    else if (s2 == "*") {
        z3 = z1 * z2;
        F1 << z3.toStr();
    }
    else if (s2 == "/") {
        z3 = z1 / z2;
        F1 << z3.toStr();
    }
    else if (s2 == "!") {
        z3 = !(z1);
        F1 << z3.toStr();
    }
    else if (s2 == "<") {
        z = (z1 < z2);
        F1 << z;
    }
    else if (s2 == "<=") {
        z = (z1 <= z2);
        F1 << z;
    }
    else if (s2 == ">") {
        z = (z1 > z2);
        F1 << z;
    }
    else if (s2 == ">=") {
        z = (z1 >= z2);
        F1 << z;
    }
    else if (s2 == "==") {
        z = (z1 == z2);
        F1 << z;
    }
    F1.close();
    return 0;
}
