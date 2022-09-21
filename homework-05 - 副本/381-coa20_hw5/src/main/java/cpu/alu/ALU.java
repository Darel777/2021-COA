package cpu.alu;

public class ALU {

    public String mul(String src, String dest){
        String Q = dest;
        String A = "";
        for (int i = 0; i < dest.length(); i++) {
            A += "0";
        }
        String M = src;
        String AQ = A + Q;
        String Q_ = "0";
        String one = A + A.substring(0, dest.length() - 1) + "1";
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
            AQ = this.sar(one, AQ);
        }
        return AQ.substring(A.length());
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

    public String sal(String src, String dest) {
        return this.shl(src, dest);
    }

    public String div(String operand1, String operand2) {
        String Q = operand1;
        String A = "";
        for (int i = 0; i < operand1.length(); i++) {
            A += "0";
        }
        String M = operand2;
        if (operand1.charAt(0) == '1') {
            A = "";
            for (int i = 0; i < operand1.length(); i++) {
                A += "1";
            }
        }
        String AQ = A + Q;
        String oneQLen = "";
        for (int i = 0; i < operand1.length() - 1; i++) {
            oneQLen += "0";
        }
        oneQLen += "1";
        String oneAQLen = "";
        for (int i = 0; i < AQ.length() - 1; i++) {
            oneAQLen += "0";
        }
        oneAQLen += "1";
        if (operand1.charAt(0) != operand2.charAt(0)) {
            M = this.negate(M);
            M = this.add(oneQLen, M);
        }
        for (int i = 0; i < operand1.length(); i++) {
            AQ = this.sal(oneAQLen, AQ);
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
            Q = this.add(oneQLen, Q);
        }
        AQ = A + Q;
        if (operand1.equals("10000000000000000000000000000000")
                && operand2.equals("11111111111111111111111111111111")) {
            return "1" + AQ.substring(A.length()) + AQ.substring(0, A.length());
        }
        return "0" + AQ.substring(A.length()) + AQ.substring(0, A.length());
    }

}
