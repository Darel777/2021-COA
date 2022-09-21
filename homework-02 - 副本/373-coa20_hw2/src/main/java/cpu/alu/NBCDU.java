package cpu.alu;

import transformer.Transformer;

/**
 * 补充：
 * 1、加六取反实际上是凑九，即0->9, 1->8, 2->7, ...
 * 2、加六和减十是同样的效果
 * 3、对于NBCD来说也存在取反加一，是每四位为一组，取反指“加六取反”，加一指“最低一组加一”
 * 4、正数加负数时，对负数取反加一后去除符号位与正数相加
 * 5、相加的结果有进位则说明取反加一借的10的若干次幂已经还上，得到的正数就是最终结果
 * 6、没有进位则说明取反加一借的10个若干次幂没有还上，最终结果应是其相反数，需再次取反加一
 */
public class NBCDU {

	/**
	 * 模拟寄存器中的进位标志位
	 */
	private String CF = "0";

	/**
	 * 模拟寄存器中的溢出标志位
	 */
	private String OF = "0";

	/**
	 * 两个NBCD数相加：
	 * 1、将可能有的一个负数转换成正数，两正或两负则不需要处理
	 * 2、每四位为一组相加，同时判断进位
	 * 3、注意，有进位时先两数其中一个加一再与另一个相加，避免alu的溢出位被覆盖
	 * 4、如果两数符号相反则根据是否有进位返回结果，符号相同则取其符号返回
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return a + b
	 */
	public String add(String a, String b) {
		ALU alu = new ALU();
		String absA = a.substring(4);
		String absB = b.substring(4);
		if (a.startsWith("1101") && b.startsWith("1100")) {
			absA = this.invert(absA);
			absA = alu.add("0000000000000000000000000001", absA);
		} else if (a.startsWith("1100") && b.startsWith("1101")) {
			absB = this.invert(absB);
			absB = alu.add("0000000000000000000000000001", absB);
		}
		String res = "";
		boolean hasCarry = false;
		for (int i = 24; i >= 0; i -= 4) {
			String temp;
			if (!hasCarry) {
				temp = alu.add(absA.substring(i, i + 4), absB.substring(i, i + 4));
			} else {
				temp = alu.add("0001", absA.substring(i, i + 4));
				temp = alu.add(temp, absB.substring(i, i + 4));
			}
			if (alu.getOF().equals("1") || Integer.parseInt(temp, 2) >= 10) {
				temp = alu.add("0110", temp);
				hasCarry = true;
			} else {
				hasCarry = false;
			}
			res = temp + res;
		}
		if (a.charAt(3) != b.charAt(3)) {
			if (hasCarry) {
				return "1100" + res;
			}
			res = this.invert(res);
			res = alu.add("0000000000000000000000000001", res);
			return "1101" + res;
		}
		return a.substring(0, 4) + res;
	}

	/**
	 * 两个NBCD数相减：
	 * 1、对减数进行取反加一
	 * 2、以上结果与被减数做加法，注意，减数取反加一后其符号位不变
	 *
	 * @param a A 32-bits NBCD String
	 * @param b A 32-bits NBCD String
	 * @return b - a
	 */
	public String sub(String a, String b) {
		ALU alu = new ALU();
		String temp = this.invert(a.substring(4));
		temp = alu.add("0000000000000000000000000001", temp);
		if (a.startsWith("1100")) {
			return this.add("1100" + temp, b);
		}
		return this.add("1101" + temp, b);
	}

	/**
	 * NBCD数的取反，即每四位凑九，str为28位
	 */
	private String invert(String str) {
		ALU alu = new ALU();
		String res = "";
		for (int i = 0; i < 28; i += 4) {
			String temp = alu.add("0110", str.substring(i, i + 4));
			temp = alu.negate(temp);
			res = res + temp;
		}
		return res;
	}

	public static void main(String[] args) {
		NBCDU nbcdu = new NBCDU();
		Transformer tf = new Transformer();

		System.out.println(nbcdu.add(tf.decimalToNBCD("2"), tf.decimalToNBCD("3")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000000101"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("9"), tf.decimalToNBCD("7")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000010110"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("9"), tf.decimalToNBCD("4")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000010011"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("2333"), tf.decimalToNBCD("-666")));
		System.out.println(tf.NBCDToDecimal("11000000000000000001011001100111"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("-2333"), tf.decimalToNBCD("666")));
		System.out.println(tf.NBCDToDecimal("11010000000000000001011001100111"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("500"), tf.decimalToNBCD("-500")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000000000"));

		System.out.println(nbcdu.add(tf.decimalToNBCD("-500"), tf.decimalToNBCD("500")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000000000"));

		System.out.println(nbcdu.sub(tf.decimalToNBCD("500"), tf.decimalToNBCD("500")));
		System.out.println(tf.NBCDToDecimal("11000000000000000000000000000000"));
	}

}
