/**
 * В теле класса решения разрешено использовать только переменные делегированные в класс RegularInt.
 * Нельзя volatile, нельзя другие типы, нельзя блокировки, нельзя лазить в глобальные переменные.
 *
 * @author Dziuba Maria
 */

class Solution : MonotonicClock {
    private var c1 by RegularInt(0)
    private var c2 by RegularInt(0)
    private var c3 by RegularInt(0)
    private var c11 by RegularInt(0)
    private var c22 by RegularInt(0)
    private var c33 by RegularInt(0)


    override fun write(time: Time) {
        // write right-to-left
        c11 = time.d1
        c22 = time.d2
        c33 = time.d3
        c3 = c33
        c2 = c22
        c1 = c11
    }

    private fun getTime(a11: Int, a12: Int, a13: Int,
                        a21: Int, a22: Int, a23: Int): Time {
        return if (a11 == a21) {
            if (a12 == a22) {
                if (a13 == a23) {
                    Time(a11, a12, a13)
                } else {
                    Time(a21, a22, a23)
                }
            } else {
                Time(a21, a22, 0)
            }
        } else {
            Time(a21, 0, 0)
        }
    }

    override fun read(): Time {
        // read left-to-right
        val a11: Int = c1
        val a12: Int = c2
        val a13: Int = c3

        val a23: Int = c33
        val a22: Int = c22
        val a21: Int = c11

        return getTime(a11, a12, a13, a21, a22, a23)
    }
}