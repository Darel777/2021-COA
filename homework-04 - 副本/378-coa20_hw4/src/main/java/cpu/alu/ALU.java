package cpu.alu;

import util.BinaryIntegers;

/**
 * Arithmetic Logic Unit
 * ALU封装类
 */
public class ALU {

    private String CF = "0";

    private String OF = "0";

	/**
	 * 二进制整数乘法：
	 * 1、A为被乘数Q零扩展所得的高位
	 * 2、A与Q一起右移，记为AQ
	 * 3、Q_为右移被移除的最低位，初始为0
	 * 4、M为乘数，恒定不变
	 * 5、先运算再右移
	 * 返回两个二进制整数的乘积(结果低位截取后32位)
	 *
	 * @param src 32-bits
	 * @param dest 32-bits
	 * @return 32-bits
	 */
	public String mul(String src, String dest){
		String Q = dest;
		String A = "00000000000000000000000000000000";
		String M = src;
		String AQ = A + Q;
		String Q_ = "0";
		for (int i = 0; i < dest.length(); i++) {
			A = AQ.substring(0, A.length());
			Q = AQ.substring(A.length());
			if (Q.endsWith("0") && Q_.equals("1")) {
				A = this.add(M, A);
			} else if (Q.endsWith("1") && Q_.equals("0")) {
				A = this.sub(M, A);
			}
			AQ = A + Q;
			Q_ = AQ.substring(AQ.length() - 1);
			AQ = this.sar("0000000000000000000000000000000000000000000000000000000000000001", AQ);
		}
	    return AQ.substring(A.length());
    }

    /**
     * 返回两个二进制整数的除法结果 operand1 ÷ operand2
     * @param operand1 32-bits
     * @param operand2 32-bits
     * @return 65-bits overflow + quotient + remainder
     */
    public String div(String operand1, String operand2) {
    	String Q = operand1;
    	String A = "00000000000000000000000000000000";
    	String M = operand2;
		if (operand1.charAt(0) == '1') {
			A = "11111111111111111111111111111111";
		}
		String AQ = A + Q;
		if (operand1.charAt(0) != operand2.charAt(0)) {
			M = this.negate(M);
			M = this.add("00000000000000000000000000000001", M);
		}
		for (int i = 0; i < operand1.length(); i++) {
			AQ = this.sal("0000000000000000000000000000000000000000000000000000000000000001", AQ);
			A = AQ.substring(0, A.length());
			Q = AQ.substring(A.length());
			A = this.sub(M, A);
			if (A.charAt(0) != operand1.charAt(0)) {
				A = this.add(M, A);
			} else {
				Q = Q.substring(0, Q.length() - 1) + "1";
			}
			AQ = A + Q;
		}
		if (operand1.charAt(0) != operand2.charAt(0)) {
			Q = negate(Q);
			Q = this.add("00000000000000000000000000000001", Q);
		}
		AQ = A + Q;
		if (operand1.equals("10000000000000000000000000000000")
				&& operand2.equals("11111111111111111111111111111111")) {
			return "1" + AQ.substring(A.length()) + AQ.substring(0, A.length());
		}
        return "0" + AQ.substring(A.length()) + AQ.substring(0, A.length());
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

	public String sal(String src, String dest) {
		return this.shl(src, dest);
	}

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

}
