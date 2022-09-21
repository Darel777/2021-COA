package util;

import cpu.alu.ALU;

import java.util.Arrays;

/**
 * @CreateTime: 2020-11-23 22:13
 */
public class CRC {

    /**
     * CRC计算器
     *
     * @param data       数据流
     * @param polynomial 多项式
     * @return CheckCode
     */
    public static char[] Calculate(char[] data, String polynomial) {
        //TODO
        char[] redundancy = new char[data.length + polynomial.length() - 1];
        for (int i = 0; i < data.length; i++) {
            redundancy[i] = data[i];
        }
        for (int i = data.length; i < redundancy.length; i++) {
            redundancy[i] = '0';
        }
        return modTwoDiv(redundancy, polynomial.toCharArray());
    }

    private static char[] modTwoDiv(char[] redundancy, char[] polynomial) {
        char[] quotient = new char[redundancy.length - polynomial.length + 1];
        char[] res = new char[polynomial.length - 1];
        char[] temp = new char[polynomial.length];
        for (int i = polynomial.length - 1; i < redundancy.length; i++) {
            if (i == polynomial.length - 1) {
                for (int j = 0; j < temp.length; j++) {
                    temp[j] = redundancy[j];
                }
            }
            quotient[i - polynomial.length + 1] = temp[0];
            if (temp[0] == '1') {
                temp = xor(temp, polynomial);
            }
            temp = shiftLeft(temp);
            if (i != redundancy.length - 1) {
                temp[temp.length - 1] = redundancy[i + 1];
            }
        }
        for (int i = 0; i < res.length; i++) {
            res[i] = temp[i];
        }
        return res;
    }

    private static char[] xor(char[] c1, char[] c2) {
        char[] res = new char[c1.length];
        for (int i = 0; i < c1.length; i++) {
            if (c1[i] == c2[i]) {
                res[i] = '0';
            } else {
                res[i] = '1';
            }
        }
        return res;
    }

    private static char[] shiftLeft(char[] c) {
        char[] res = new char[c.length];
        for (int i = 0; i < res.length - 1; i++) {
            res[i] = c[i + 1];
        }
        res[res.length - 1] = '0';
        return res;
    }

    /**
     * CRC校验器
     *
     * @param data       接收方接受的数据流
     * @param polynomial 多项式
     * @param CheckCode  CheckCode
     * @return 余数
     */
    public static char[] Check(char[] data, String polynomial, char[] CheckCode) {
        //TODO
        char[] redundancy = new char[data.length + polynomial.length() - 1];
        for (int i = 0; i < data.length; i++) {
            redundancy[i] = data[i];
        }
        for (int i = data.length; i < redundancy.length; i++) {
            redundancy[i] = CheckCode[i - data.length];
        }
        return modTwoDiv(redundancy, polynomial.toCharArray());
    }

    /**
     * 这个方法仅用于测试，请勿修改
     *
     * @param data
     * @param polynomial
     */
    public static void CalculateTest(char[] data, String polynomial) {
        System.out.print(Calculate(data, polynomial));
    }

    /**
     * 这个方法仅用于测试，请勿修改
     *
     * @param data
     * @param polynomial
     */
    public static void CheckTest(char[] data, String polynomial, char[] CheckCode) {
        System.out.print(Check(data, polynomial, CheckCode));
    }

    public static void main(String[] args) {
        CalculateTest(new char[]{'1', '0', '0', '0', '1', '1'}, "1001");
        System.out.println();
        CheckTest(new char[]{'1', '0', '0', '0', '1', '1'}, "1001", new char[]{'1', '1', '1'});
    }

}
