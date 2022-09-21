package cpu.alu;

import transformer.Transformer;
import util.IEEE754Float;

/**
 * floating point unit
 * 执行浮点运算的抽象单元
 * 浮点数精度：使用4位保护位进行计算，计算完毕直接舍去保护位
 * TODO: 浮点数运算
 */
public class FPU {

    /**
     * 32位浮点数乘法：
     * 1、判断是否结果为非数，其中至少一个非数或者0乘无穷大会出现这种情况
     * 2、得到a的阶aExp和b的阶bExp，并声明结果的阶resExp，同时确定结果的符号sign
     * 3、若a和b都是非规格化数数，则返回结果一定是0
     * 4、声明a和b的有效位aSig和bSig，并根据是否规格化补符号位、隐藏位、保护位
     * 5、上一步得到长度为29的aSig和bSig，为进行布斯乘法再在前面补29位，共58位
     * 6、aExp和bExp在前面补两位表示符号和进位，这样阶就是10位
     * 7、若a和b都是规格化数，则执行如下：
     * 7.1、resExp = aExp + bExp - 127
     * 7.2、resSig = aSig * bSig（布斯算法）
     * 7.3、判断resSig的高12位，若为1则阶加一有效位右移
     * 7.4、判断resExp的高1位，若为1，或其低8位全为1则上溢，返回无穷
     * 7.5、判断resExp的高0位，若为1，或其全为0则结果为非规格化数，不断右移阶加一直到加到0
     * 7.6、注意，右移的次数比阶加一的次数多一次
     * 7.7、统一返回sign + resExp.substring(2) + resSig.substring(14, 14 + 23)
     * 8、若a和b其中之一是非规格化数，则执行如下：
     * 8.1、resExp = aExp + bExp -126
     * 8.2、resSig = aSig * bSig（布斯算法）
     * 8.3、判断resExp的高0位，若为1，或其全为0则结果为非规格化数，不断右移阶加一直到加到0
     * 8.4、其他情况结果为可能规格化数，此时阶是正数，不断减一同时有效位左移直到resSig的高13位为1
     * 8.5、上一步中若阶减到0则停止左移并跳出循环
     * 8.6、统一返回sign + resExp.subString(2) + resSig.subString(14, 14 + 23)
     * compute the float mul of a * b
     */
    public String mul(String a, String b) {
        if (a.matches(IEEE754Float.NaN) || b.matches(IEEE754Float.NaN)) {
            return IEEE754Float.NaN;
        }
        if (a.endsWith("0000000000000000000000000000000") && b.endsWith("1111111100000000000000000000000")
        || a.endsWith("1111111100000000000000000000000") && b.endsWith("0000000000000000000000000000000")) {
            return IEEE754Float.NaN;
        }
        String aExp = a.substring(1, 9);
        String bExp = b.substring(1, 9);
        String resExp;
        if (aExp.equals("00000000") && bExp.equals("00000000")) {
            return IEEE754Float.P_ZERO;
        }
        String sign = a.charAt(0) == b.charAt(0) ? "0" : "1";
        String aSig;
        String bSig;
        String resSig;
        boolean bothNormalized = true;
        if (aExp.equals("00000000")) {
            aSig = "0" + "0" + a.substring(9) + "0000";
            bothNormalized = false;
        } else {
            aSig = "0" + "1" + a.substring(9) + "0000";
        }
        if (bExp.equals("00000000")) {
            bSig = "0" + "0" + b.substring(9) + "0000";
            bothNormalized = false;
        } else {
            bSig = "0" + "1" + b.substring(9) + "0000";
        }
        aSig = "000000000000000000000000000000000000000" + aSig;
        bSig = "000000000000000000000000000000000000000" + bSig;
        ALU alu = new ALU();
        aExp = "00" + aExp;
        bExp = "00" + bExp;
        if (bothNormalized) {
            resExp = alu.add(aExp, bExp);
            resExp = alu.sub("0001111111", resExp);
            resSig = alu.mul(aSig, bSig);
            if (resSig.charAt(12) == '1') {
                resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
                resExp = alu.add("0000000001", aExp);
            }
            if (resExp.charAt(1) == '1' || resExp.equals("0011111111")) {
                if (sign.equals("0")) {
                    return IEEE754Float.P_INF;
                } else {
                    return IEEE754Float.N_INF;
                }
            }
            if (resExp.charAt(0) == '1' || resExp.equals("0000000000")) {
                resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
                while (!resExp.equals("0000000000")) {
                    resExp = alu.add("0000000001", resExp);
                    resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
                }
            }
            return sign + resExp.substring(2) + resSig.substring(14, 14 + 23);
        }
        resExp = alu.add(aExp, bExp);
        resExp = alu.sub("0001111110", resExp);
        resSig = alu.mul(aSig, bSig);
        if (resExp.charAt(0) == '1' || resExp.equals("0000000000")) {
            resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            while (!resExp.equals("0000000000")) {
                resExp = alu.add("0000000001", resExp);
                resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            }
        } else {
            while (resSig.charAt(13) != '1') {
                resExp = alu.sub("0000000001", resExp);
                if (resExp.equals("0000000000")) {
                    break;
                }
                resSig = alu.sal("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            }
        }
        return sign + resExp.substring(2) + resSig.substring(14, 14 + 23);
    }

    /**
     * compute the float mul of a / b
     */
    public String div(String a, String b) {
        if (b.endsWith("0000000000000000000000000000000")) {
            if (a.endsWith("0000000000000000000000000000000")) {
                return IEEE754Float.NaN;
            }
            throw new ArithmeticException("/ by zero");
        }
        ALU alu = new ALU();
        String sign = a.charAt(0) == b.charAt(0) ? "0" : "1";
        String aExp = "00" + a.substring(1, 9);
        String bExp = "00" + b.substring(1, 9);
        String aSig;
        String bSig;
        if (aExp.equals("0000000000")) {
            aExp = "0000000001";
            aSig = "0" + "0" + a.substring(9) + "0000";
        } else {
            aSig = "0" + "1" + a.substring(9) + "0000";
        }
        if (bExp.equals("0000000000")) {
            bExp = "0000000001";
            bSig = "0" + "0" + b.substring(9) + "0000";
        } else {
            bSig = "0" + "1" + b.substring(9) + "0000";
        }
        aSig = aSig + "00000000000000000000000000000";
        bSig = "00000000000000000000000000000" + bSig;
        String resExp = alu.sub(bExp, aExp);
        resExp = alu.add("0001111111", resExp);
        String resSig = alu.div(aSig, bSig).substring(1, 1 + 58);
        System.out.println("resExp->" + resExp);
        System.out.println("resSig->" + resSig);
        if (resExp.charAt(0) == '1' || resExp.equals("0000000000")) {
            resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            while (!resExp.equals("0000000000")) {
                resExp = alu.add("0000000001", resExp);
                resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            }
            return sign + resExp.substring(2) + resSig.substring(29, 29 + 23);
        }
        if (resExp.charAt(1) == '1' || resExp.equals("0011111111")) {
            while (resSig.charAt(28) != '1') {
                resExp = alu.sub("0000000001", resExp);
                resSig = alu.sal("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
            }
            if (resExp.charAt(1) == '1' || resExp.equals("0011111111")) {
                if (sign.equals("0")) {
                    return IEEE754Float.P_INF;
                }
                return IEEE754Float.N_INF;
            }
        }
        int index = -1;
        boolean flag = true;
        for (int i = 0; i < 28; i++) {
            if (resSig.charAt(i) == '1') {
                index = i;
                flag = false;
                break;
            }
        }
        while (resSig.charAt(28) != '1' && index != -1 && flag) {
            resExp = alu.sub("0000000001", resExp);
            if (resExp.equals("0000000000")) {
                break;
            }
            resSig = alu.sal("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
        }
        while ((28 - index) > 0 && !flag) {
            index++;
            resExp = alu.add("0000000001", resExp);
            if (resExp.equals("0011111111")) {
                if (sign.equals("0")) {
                    return IEEE754Float.P_INF;
                }
                return IEEE754Float.N_INF;
            }
            resSig = alu.sar("000000000000000000000000000000000000000000000000000000000000000000000000000001", resSig);
        }
        return sign + resExp.substring(2) + resSig.substring(29, 29 + 23);
    }

    public static void main(String[] args) {
        Transformer tf = new Transformer();
        FPU fpu = new FPU();
        /*
        System.out.println(fpu.mul(tf.floatToBinary("1.5"), tf.floatToBinary("1.5")));
        System.out.println(tf.binaryToFloat("01000000000100000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary("6.2"), tf.floatToBinary("0.5")));
        System.out.println(tf.binaryToFloat("01000000010001100110011001100110"));

        System.out.println(fpu.mul(tf.floatToBinary(String.valueOf(Math.pow(2, 127))), tf.floatToBinary("2")));
        System.out.println(tf.binaryToFloat("01111111100000000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary("0.5"), tf.floatToBinary(String.valueOf(Math.pow(2, -126)))));
        System.out.println(tf.binaryToFloat("00000000010000000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary(String.valueOf(Math.pow(2, -127))), tf.floatToBinary("2")));
        System.out.println(tf.binaryToFloat("00000000100000000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary(String.valueOf(Math.pow(2, -129))), tf.floatToBinary("2")));
        System.out.println(tf.binaryToFloat("00000000001000000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary(String.valueOf(Math.pow(2, -127))), tf.floatToBinary(String.valueOf(Math.pow(2, 127)))));
        System.out.println(tf.binaryToFloat("00111111100000000000000000000000"));

        System.out.println(fpu.mul(tf.floatToBinary(String.valueOf(Math.pow(2, -127))), tf.floatToBinary(String.valueOf(Math.pow(2, -22)))));
        System.out.println(tf.binaryToFloat("00000000000000000000000000000001"));

        System.out.println(fpu.mul(tf.floatToBinary("0"), tf.floatToBinary("36.5")));
        System.out.println(tf.binaryToFloat("00000000000000000000000000000000"));
         */
//        System.out.println(fpu.div(tf.floatToBinary("6.6"), tf.floatToBinary("3.3")));
//        System.out.println(tf.binaryToFloat("01000000000000000000000000000000"));
//
//        System.out.println(fpu.div(tf.floatToBinary("33.12"), tf.floatToBinary("6.07")));
//        System.out.println(tf.binaryToFloat("01000000101011101001101001011100"));
//
//        System.out.println(fpu.div(tf.floatToBinary(String.valueOf(Math.pow(2, -126))), tf.floatToBinary("2")));
//        System.out.println(tf.binaryToFloat("00000000010000000000000000000000"));

        System.out.println(fpu.div(tf.floatToBinary("2"), tf.floatToBinary(String.valueOf(Math.pow(2, -126)))));
        System.out.println(tf.binaryToFloat("01111111000000000000000000000000"));

        System.out.println(fpu.div(tf.floatToBinary("2"), tf.floatToBinary(String.valueOf(Math.pow(2, -127)))));
        System.out.println(tf.binaryToFloat("01111111100000000000000000000000"));

//        System.out.println(fpu.div(tf.floatToBinary("2"), tf.floatToBinary(String.valueOf(Math.pow(2, -11)))));
//        System.out.println(tf.binaryToFloat("01000101100000000000000000000000"));

        System.out.println(fpu.div(tf.floatToBinary(String.valueOf(Math.pow(2, -128))), tf.floatToBinary(String.valueOf(Math.pow(2, -129)))));
        System.out.println(tf.binaryToFloat("01000000000000000000000000000000"));
    }

}
