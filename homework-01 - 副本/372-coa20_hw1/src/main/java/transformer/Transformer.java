package transformer;


public class Transformer {

    /**
     * Integer to binaryString
     *
     * @param numStr to be converted
     * @return result
     */
    public String intToBinary(String numStr) {
        String res = Integer.toBinaryString(Integer.parseInt(numStr));
        int len = res.length();
        for (int i = 0; i < 32 - len; i++) {
            res = "0" + res;
        }
        return res;
    }

    /**
     * BinaryString to Integer
     *
     * @param binStr : Binary string in 2's complement
     * @return :result
     */
    public String binaryToInt(String binStr) {
        int res;
        if (binStr.charAt(0) == '1') {
            res = (int) (Integer.parseInt(binStr.substring(1), 2) - Math.pow(2, 31));
        } else {
            res = Integer.parseInt(binStr.substring(1), 2);
        }
        return String.valueOf(res);
    }

    /**
     * 浮点数转二进制字符串：
     * 1、特判无穷大，返回+Inf或-Inf
     * 2、为方便计算，取绝对值
     * 3、算阶，同时判断是否是规格化数，规格化数的阶的范围是[-126, 127]，非规格化数的阶是-126
     * 4、计算去除隐藏位的有效位数值，规格化数要减1，非规格化数不需要
     * 5、循环计算出长度为23的有效位二进制字符串
     * 6、根据正负号返回结果
     * @param floatStr : The string of the float true value
     * */
    public String floatToBinary(String floatStr) {
        // 特判无穷大的情况
        if (Float.parseFloat(floatStr) == Float.POSITIVE_INFINITY) {
            return "+Inf";
        } else if (Float.parseFloat(floatStr) == Float.NEGATIVE_INFINITY) {
            return "-Inf";
        }
        // 取绝对值
        String abs = floatStr;
        if (floatStr.charAt(0) == '-') {
            abs = floatStr.substring(1);
        }
        String expStr;
        String sigStr = "";
        // 算阶
        int exp;
        boolean isNormalized = true;
        // 规格化数的阶的范围是[-126, 127]
        for (exp = -126; ; exp++) {
            if (Math.pow(2, exp) <= Float.parseFloat(abs) && Float.parseFloat(abs) < Math.pow(2, exp + 1)) {
                break;
            }
            // 非规格化数，阶一定是-126
            if (exp == 128) {
                exp = -126;
                isNormalized = false;
                break;
            }
        }
        if (!isNormalized) {
            expStr = "00000000";
            // 算有效位
            double sig = Float.parseFloat(abs) / Math.pow(2, exp);
            for (int i = 0; i < 23; i++) {
                sig = sig * 2;
                if (sig >= 1) {
                    sigStr += "1";
                    sig -= 1;
                } else {
                    sigStr += "0";
                }
            }
            if (floatStr.charAt(0) == '1') {
                return "1" + expStr + sigStr;
            }
            return "0" + expStr + sigStr;
        }
        expStr = Integer.toBinaryString(exp + 127);
        int expStrLen = expStr.length();
        for (int i = 0; i < 8 - expStrLen; i++) {
            expStr = "0" + expStr;
        }
        // 算有效位
        double sig = Float.parseFloat(abs) / Math.pow(2, exp) - 1;
        for (int i = 0; i < 23; i++) {
            sig = sig * 2;
            if (sig >= 1) {
                sigStr += "1";
                sig -= 1;
            } else {
                sigStr += "0";
            }
        }
        if (floatStr.charAt(0) == '-') {
            return "1" + expStr + sigStr;
        }
        return "0" + expStr + sigStr;
    }

    /**
     * 二进制字符串转浮点数：
     * 1、判断无穷大的特殊情况
     * 2、将阶转换成整数并判断是否是规格化数
     * 3、非规格化数的阶是-126，规格化数的阶转换成整数后-127
     * 4、计算加上隐藏位后的有效位，规格化数的隐藏位是1，非规格化数是0
     * 5、根据正负号返回结果
     * 6、注意，返回结果的数值是double类型，否则精度不准
     * Binary code to its float true value
     * */
    public String binaryToFloat(String binStr) {
        if (binStr.equals("01111111100000000000000000000000")) {
            return "+Inf";
        } else if (binStr.equals("11111111100000000000000000000000")) {
            return "-Inf";
        }
        String expStr = binStr.substring(1, 9);
        boolean isNormalized = true;
        if (expStr.equals("00000000")) {
            isNormalized = false;
        }
        int exp;
        double sig = 0;
        double res;
        if (!isNormalized) {
            exp = -126;
            for (int i = 0; i < 23; i++) {
                if (binStr.charAt(9 + i) == '1') {
                    sig += Math.pow(2, -(i + 1));
                }
            }
            res = Math.pow(2, exp) * sig;
            if (binStr.charAt(0) == '1') {
                return String.valueOf(-res);
            }
            return String.valueOf(res);
        }
        exp = Integer.parseInt(expStr, 2) - 127;
        sig = 1;
        for (int i = 0; i < 23; i++) {
            if (binStr.charAt(9 + i) == '1') {
                sig += Math.pow(2, -(i + 1));
            }
        }
        res = Math.pow(2, exp) * sig;
        if (binStr.charAt(0) == '1') {
            return String.valueOf(-res);
        }
        return String.valueOf(res);
    }

    /**
     * 十进制数转NBCD：
     * 1、NBCD共32位，前4位是符号位，1100表示正数，1101表示负数
     * 2、为方便计算，将十进制数取绝对值后扩充成7位
     * 3、循环7次将十进制数的每一位转成二进制并扩充成4位
     * 4、返回结果
     * The decimal number to its NBCD code
     * */
    public String decimalToNBCD(String decimal) {
        String signal = "1100";
        String abs = decimal;
        if (decimal.charAt(0) == '-') {
            signal = "1101";
            abs = decimal.substring(1);
        }
        int absLen = abs.length();
        for (int i = 0; i < 7 - absLen; i++) {
            abs = "0" + abs;
        }
        String res = "";
        for (int i = 0; i < 7; i++) {
            String temp = Integer.toBinaryString(Integer.parseInt(String.valueOf(abs.charAt(i))));
            int tempLen = temp.length();
            for (int j = 0; j < 4 - tempLen; j++) {
                temp = "0" + temp;
            }
            res += temp;
        }
        return signal + res;
    }

    /**
     * NBCD转十进制数：
     * 1、从第4位开始，每四位转成一个十进制数
     * 2、根据前4位表示的符号返回结果，1100表示正数，1101表示负数
     * 3、注意，去除返回结果中多余的0
     * NBCD code to its decimal number
     * */
    public String NBCDToDecimal(String NBCDStr) {
        String res = "";
        for (int i = 4; i < 32; i += 4) {
            res += String.valueOf(Integer.parseInt(NBCDStr.substring(i, i + 4), 2));
        }
        res = String.valueOf(Integer.parseInt(res));
        if (NBCDStr.startsWith("1101")) {
            return "-" + res;
        }
        return res;
    }

    public static void main(String[] args) {
        Transformer tf = new Transformer();
        System.out.println(tf.floatToBinary(String.valueOf(Math.pow(2, -126))));
        System.out.println(tf.floatToBinary(String.valueOf(Math.pow(2, -127))));
        System.out.println(tf.floatToBinary(String.valueOf(Math.pow(2, -128))));
        System.out.println(tf.floatToBinary(String.valueOf(66)));

        System.out.println(tf.binaryToFloat("00000000001000000000000000000000"));
        System.out.println(tf.binaryToFloat("01000010100001000000000000000000"));

        System.out.println(tf.decimalToNBCD("2333"));
        System.out.println(tf.NBCDToDecimal("11000000000000000010001100110011"));

        System.out.println(tf.intToBinary("-1"));
        System.out.println(tf.binaryToInt("11111111111111111111111111111111"));
    }

}
