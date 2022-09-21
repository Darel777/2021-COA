package cpu.alu;

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
     * 二进制字符串相加：
     * 1、32位从低到高依次相加，注意进位
     * 2、最后有进位则使用该方法的ALU实例有进位
     *
     * @param src 源二进制字符串
     * @param dest 目标二进制字符串
     * @return dest + src
     */
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

    /**
     * 二进制字符串相减：
     * 1、减数取反加一
     * 2、上面的结果与被减数相加
     *
     * @param src 源二进制字符串
     * @param dest 目标二进制字符串
     * @return dest - src
     */
    public String sub(String src, String dest) {
        String temp = this.add("00000000000000000000000000000001", this.negate(src));
		return this.add(temp, dest);
	}

    /**
     * 两个二进制字符串进行与运算
     */
    public String and(String src, String dest) {
        char[] res = new char[dest.length()];
        for (int i = 0; i < res.length; i++) {
            if (dest.charAt(i) == '1' && src.charAt(i) == '1') {
                res[i] = '1';
            } else {
                res[i] = '0';
            }
        }
		return String.valueOf(res);
    }

    /**
     * 两个二进制字符串进行或运算
     */
    public String or(String src, String dest) {
        char[] res = new char[dest.length()];
        for (int i = 0; i < res.length; i++) {
            if (dest.charAt(i) == '0' && src.charAt(i) == '0') {
                res[i] = '0';
            } else {
                res[i] = '1';
            }
        }
		return String.valueOf(res);
    }

    /**
     * 两个二进制字符串进行异或运算
     */
    public String xor(String src, String dest) {
        char[] res = new char[dest.length()];
        for (int i = 0; i < res.length; i++) {
            if (dest.charAt(i) == src.charAt(i)) {
                res[i] = '0';
            } else {
                res[i] = '1';
            }
        }
		return String.valueOf(res);
    }

    /**
     * 二进制字符串取反
     *
     * @param str 二进制字符串
     * @return 取反得到的字符串
     */
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

    public String getOF() {
        return OF;
    }

}
