public class FPU {
    public String add(String a,String b){
//      a和b至少有一个为0时特判，直接返回另一个
        if (a.endsWith("0000000000000000000000000000000")) {
            return b;
        } else if (b.endsWith("0000000000000000000000000000000")) {
            return a;
        }

        ALU alu=new ALU();

//      得到a的阶aExp，b的阶bExp并声明有效位aSig和bSig、结果的符号位sign和两数是否都是非规格化bothUnNormalized
        String aExp=a.substring(1,9);
        String bExp=b.substring(1,9);
        boolean bothUnNormalized=false;
        String aSig;
        String bSig;
        String resSig;
        String sign="0";
//      根据aExp和bExp判断a和b是不是规格化数，并依此给aSig、bSig和bothUnNormalized赋值
        if(aExp.equals("00000000")&&bExp.equals("00000000")){
            bothUnNormalized=true;
        }
//      注意，aSig和bSig包含补充的符号位、进位位、隐藏位和保护位，长度为40，符号位、进位位和保护位初始都为0
        if (aExp.equals("00000000")) {
            aSig = "00" + "0" + a.substring(9) + "0000";
        } else {
            aSig = "00" + "1" + a.substring(9) + "0000";
        }
        if (bExp.equals("00000000")) {
            bSig = "00" + "0" + b.substring(9) + "0000";
        } else {
            bSig = "00" + "1" + b.substring(9) + "0000";
        }
//        5、比较aExp和bExp的大小
//                * 6、若相等则执行如下：
//                * 6.1、根据a和b的首位判断正负，若为负则相应的aSig或bSig取反加一
//                * 6.2、aSig与bSig相加得到resSig
//                * 6.3、若resSig的符号位，即高0位，为1，则对其取反加一，sign设为1
//                * 6.4、bothUnNormalized为false，即a和b至少有一个是规格化数时，判断resSig的进位位，若为1则aExp加一resSig右移
//                * 6.5、由于两规格化数相加可能变成非规格化数，因此不断判断隐藏位是否为1，不是则aExp减一resSig左移，aExp减到0直接返回
//                * 6.6、bothUnNormalized为true，即a和b都是非规格化数时，判断resSig的隐藏位，即高2位，若为1则aExp设为1
//                * 6.7、此种情况统一返回sign + aExp + resSig.subString(3, 26)
        /**
         * 32位浮点数加法：
         * 1、a和b至少有一个为0时特判，直接返回另一个
         * 2、得到a的阶aExp，b的阶bExp并声明有效位aSig和bSig、结果的符号位sign和两数是否都是非规格化bothUnNormalized
         * 3、根据aExp和bExp判断a和b是不是规格化数，并依此给aSig、bSig和bothUnNormalized赋值
         * 4、注意，aSig和bSig包含补充的符号位、进位位、隐藏位和保护位，长度为40，符号位、进位位和保护位初始都为0
         * 5、比较aExp和bExp的大小
         * 6、若相等则执行如下：
         * 6.1、根据a和b的首位判断正负，若为负则相应的aSig或bSig取反加一
         * 6.2、aSig与bSig相加得到resSig
         * 6.3、若resSig的符号位，即高0位，为1，则对其取反加一，sign设为1
         * 6.4、bothUnNormalized为false，即a和b至少有一个是规格化数时，判断resSig的进位位，若为1则aExp加一resSig右移
         * 6.5、由于两规格化数相加可能变成非规格化数，因此不断判断隐藏位是否为1，不是则aExp减一resSig左移，aExp减到0直接返回
         * 6.6、bothUnNormalized为true，即a和b都是非规格化数时，判断resSig的隐藏位，即高2位，若为1则aExp设为1
         * 6.7、此种情况统一返回sign + aExp + resSig.subString(3, 26)
         * 7、若不相等则执行如下：
         * 7.1、声明较大数的阶maxExp、较小数的阶minExp、较大数的有效位maxSig和较小数的有效位minSig
         * 7.2、根据aExp和bExp的大小关系给maxExp、minExp、maxSig和minSig赋值，同时确定sign
         * 7.3、对阶，minExp不断加一直到和maxExp相等，此过程中minSig不断右移
         * 7.4、注意，若minExp初始为0，则较小数为非规格化数，首次加一时不需要右移
         * 7.5、对阶完成后根据aExp和bExp的大小找到相应的maxSig和minSig，若a或b的首位为1则相应的maxSig或minSig取反加一
         * 7.6、maxSig和minSig相加得到resSig
         * 7.7、若resSig的符号位，即高0位，为1，则对其取反加一
         * 7.8、判断resSig的进位位，即高1位，若为1则maxExp加一resSig左移
         * 7.9、不断判断resSig的隐藏位，即高2位，是否为1，不是则maxExp减一resSig左移，maxExp减到0直接返回
         * 7.10、此种情况统一返回sign + maxExp + resSig.subString(3, 26)
         * compute the float add of (a + b)
         **/
        if(Integer.parseInt(aExp)==Integer.parseInt(bExp)){
            if(a.charAt(0)=='1'){
                //aSig=alu.negate(aSig);
                aSig=alu.add("00..1",aSig);
            }
            if(b.charAt(0)=='1'){
                //bSig=alu.negate(bSig);
                bSig=alu.add("00..1",bSig);
            }
            resSig = alu.add(aSig, bSig);
            if (resSig.charAt(0) == '1') {
//                resSig = alu.negate(resSig);
                resSig = alu.add("000000000000000000000000000001", resSig);
                sign = "1";
            }
            if (!bothUnNormalized) {
                if (resSig.charAt(1) == '1') {
                    if (aExp.equals("11111110")) {
                        throw new RuntimeException("阶值上溢");
                    }
                    aExp = alu.add("00000001", aExp);
//                    resSig = alu.sar("000000000000000000000000000001", resSig);
                }
                while (resSig.charAt(2) != '1') {
                    aExp = alu.sub("00000001", aExp);
                    // 规格化数变为非规格化数的同时也使得resSig为0时能正确返回指数0
                    if (aExp.equals("00000000")) {
                        return sign + aExp + resSig.substring(3, 26);
                    }
//                    resSig = alu.sal("000000000000000000000000000001", resSig);
                }
            } else {
                if (resSig.charAt(2) == '1') {
                    aExp = "00000001";
                }
            }
        }


















        return null;

    }
}
