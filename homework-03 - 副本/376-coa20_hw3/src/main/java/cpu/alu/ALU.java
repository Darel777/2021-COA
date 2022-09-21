package cpu.alu;

import transformer.Transformer;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    /**
     * 模拟寄存器中的进位标志位
     */
    private String CF = "0";

    /**
     * 模拟寄存器中的溢出标志位
     */
    private String OF = "0";

    /**
     * 二进制整数除法：
     * 1、A初始为被除数Q符号扩展所得的高位，运算后为余数
     * 2、Q初始为被除数，运算后为商的绝对值
     * 3、A与Q一起进行左移，记为AQ
     * 4、M为除数，定值不会改变
     * 5、A与M运算时，dest和src符号相同则做减法，符号相反则做加法，为方便计算统一成减法，符号相反时将M取反加一
     * 6、判断A - M够不够减时，若A - M与dest符号相同则够减，符号不同则不够减
     * 7、若dest与src符号不同，则商应是负数，故Q需要取反加一
     * 8、先左移再运算
     *
     * @param src 除数
     * @param dest 被除数
     * @return dest / src
     */
    public String imod(String src, String dest) {
        String Q = dest;
        String M = src;
        String A = "00000000000000000000000000000000";
        if (dest.charAt(0) == '1') {
            A = "11111111111111111111111111111111";
        }
        if (dest.charAt(0) != src.charAt(0)) {
            M = this.negate(M);
            M = this.add("00000000000000000000000000000001", M);
        }
        String AQ = A + Q;
        for (int i = 0; i < dest.length(); i++) {
            AQ = this.sal("0000000000000000000000000000000000000000000000000000000000000001", AQ);
            A = AQ.substring(0, A.length());
            Q = AQ.substring(A.length());
            A = this.sub(M, A);
            if (A.charAt(0) == dest.charAt(0)) {
                Q = Q.substring(0, Q.length() - 1) + "1";
            } else {
                A = this.add(M, A);
            }
            AQ = A + Q;
        }
        if (dest.charAt(0) != src.charAt(0)) {
            Q = this.negate(Q);
            Q = this.add("00000000000000000000000000000001", Q);
        }
		return A;
    }

    /**
     * 逻辑左移，低位补0
     * 注意，左移次数要取除以32的余数
     */
    public String shl(String src, String dest) {
        int time = Integer.parseInt(src, 2) % 32;
        char[] res = new char[dest.length()];
        for (int i = 0; i < res.length - time; i++) {
            res[i] = dest.charAt(i + time);
        }
        for (int i = res.length - time; i < res.length; i++) {
            res[i] = '0';
        }
		return String.valueOf(res);
    }

    /**
     * 逻辑右移，高位补0
     * 注意，右移次数要取除以32的余数
     */
    public String shr(String src, String dest) {
        int time = Integer.parseInt(src, 2) % 32;
        char[] res = new char[dest.length()];
        for (int i = 0; i < time; i++) {
            res[i] = '0';
        }
        for (int i = time; i < res.length; i++) {
            res[i] = dest.charAt(i - time);
        }
		return String.valueOf(res);
    }

    /**
     * 算术左移，与逻辑左移相同
     */
    public String sal(String src, String dest) {
		return this.shl(src, dest);
    }

    /**
     * 算术右移，高位补的数与dest的最高位相同
     * 注意，右移次数要取除以32的余数
     */
    public String sar(String src, String dest) {
        int time = Integer.parseInt(src, 2) % 32;
        char[] res = new char[dest.length()];
        if (dest.charAt(0) == '0') {
            for (int i = 0; i < time; i++) {
                res[i] = '0';
            }
        } else {
            for (int i = 0; i < time; i++) {
                res[i] = '1';
            }
        }
        for (int i = time; i < res.length; i++) {
            res[i] = dest.charAt(i - time);
        }
		return String.valueOf(res);
    }

    /**
     * 循环左移，低位按序补dest的高位
     * 注意，左移次数要取除以32的余数
     */
    public String rol(String src, String dest) {
        int time = Integer.parseInt(src, 2) % 32;
        char[] res = new char[dest.length()];
        for (int i = 0; i < res.length - time; i++) {
            res[i] = dest.charAt(i + time);
        }
        for (int i = res.length - time; i < res.length; i++) {
            res[i] = dest.charAt(i - (res.length - time));
        }
		return String.valueOf(res);
    }

    /**
     * 循环右移，高位按序补dest的低位
     * 注意，右移次数要取除以32的余数
     */
    public String ror(String src, String dest) {
        int time = Integer.parseInt(src, 2) % 32;
        char[] res = new char[dest.length()];
        for (int i = 0; i < time; i++) {
            res[i] = dest.charAt(i + (res.length - time));
        }
        for (int i = time; i < res.length; i++) {
            res[i] = dest.charAt(i - time);
        }
		return String.valueOf(res);
    }

    public String negate(String str) {
        char[] res = new char[str.length()];
        for (int i = 0; i < res.length; i++) {
            if (str.charAt(i) == '0') {
                res[i] = '1';
            } else {
                res[i] = '0';
            }
        }
        return String.valueOf(res);
    }

    public String add(String src, String dest) {
        char[] res = new char[dest.length()];
        boolean hasCarry = false;
        for (int i = dest.length() - 1; i >= 0; i--) {
            if (dest.charAt(i) == '0' && src.charAt(i) == '0' && !hasCarry) {
                res[i] = '0';
            } else if (dest.charAt(i) == '0' && src.charAt(i) == '0' && hasCarry) {
                res[i] = '1';
                hasCarry = false;
            } else if (dest.charAt(i) == '1' && src.charAt(i) == '0' && !hasCarry
                    || dest.charAt(i) == '0' && src.charAt(i) == '1' && !hasCarry) {
                res[i] = '1';
            } else if (dest.charAt(i) == '1' && src.charAt(i) == '0' && hasCarry
                    || dest.charAt(i) == '0' && src.charAt(i) == '1' && hasCarry) {
                res[i] = '0';
            } else if (dest.charAt(i) == '1' && src.charAt(i) == '1' && !hasCarry) {
                res[i] = '0';
                hasCarry = true;
            } else if (dest.charAt(i) == '1' && src.charAt(i) == '1' && hasCarry) {
                res[i] = '1';
            }
        }
        if (hasCarry) {
            this.OF = "1";
        } else {
            this.OF = "0";
        }
        return String.valueOf(res);
    }

    public String sub(String src, String dest) {
        String one = "";
        for (int i = 0; i < dest.length() - 1; i++) {
            one += "0";
        }
        one += "1";
        String temp = this.add(one, this.negate(src));
        return this.add(temp, dest);
    }

    public static void main(String[] args) {
        Transformer tf = new Transformer();
        ALU alu = new ALU();
        System.out.println(alu.imod(tf.intToBinary("-3"), tf.intToBinary("7")));
    }

}
