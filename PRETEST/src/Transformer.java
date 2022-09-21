

public class Transformer {

    public static void main(String[] args) {
    Transformer transformer=new Transformer();
        System.out.println(transformer.binaryToFloat("11000000000000000000000000000000"));

    }
    public String intToBinary(String numStr){
        int number= Integer.parseInt(numStr);
        String s1=Integer.toBinaryString(number);//toBinaryString
        if(number<=0){
            return s1;
        }else{
            for(int i=s1.length();i<32;i++){
                s1="0"+s1;
            }
            return s1;
        }
    }//completed
    public String intToBinary(String numStr,int index){
        int num= Integer.parseInt(numStr);
        if (num == 0) return "00000000000000000000000000000000";  //0单独判读
        if (num == 0x80000000) return "10000000000000000000000000000000";
        boolean isNeg = false;
        if (num < 0) {  //负数转正数
            num = -num;
            isNeg = true;
        }
        StringBuilder temp = new StringBuilder();
        while (num > 0) {  //转为二进制
            if (num % 2 == 1) temp.append("1");
            else temp.append("0");
            num /= 2;
        }
        //stringbuilder拥有的函数：append delete charAt insert substring indexOf replace reverse
        String ans = temp.reverse().toString();  //反转
        int len = ans.length();
        for (int i = 0; i < 32 - len; i++) ans = "0" + ans;
        if (isNeg) {  //如果是负数那么取反加一
            //取反容易实现
            //oneAdder实现加1
        }
        return ans;
    }
    public String oneAdder(String string){
        char[] temp=string.toCharArray();
        for(int i=31;;i--){//反复构建一位加法器
            if(temp[i]=='0'){
                temp[i]='1';
                break;
            }else{
                temp[i]='0';
            }
        }
        return String.valueOf(temp);
    }
    //stringbuilder见inttobinary

    public String binaryToInt(String binstr){
        //是负数反转 得到的整数取负减一不做讨论
        return String.valueOf(Integer.parseInt(binstr,2));
    }

    public String decimalToNBCD(String demical){
        boolean isNeg=false;
        if(demical.substring(0,1).equals("-")){
            demical=demical.substring(1,demical.length());
            isNeg=true;
        }
        String s1="";
        char[] chararray=demical.toCharArray();
        for(int i=0;i<demical.length();i++){
            int temp=chararray[i]-'0';
            if(temp==0){
                s1=s1+"0000";
            }else if(temp==1){
                s1=s1+"0001";
            }else if(temp==2){
                s1=s1+"0010";
            }else if(temp==3){
                s1=s1+"0011";
            }else if(temp==4){
                s1=s1+"0100";
            }else if(temp==5){
                s1=s1+"0101";
            }else if(temp==6){
                s1=s1+"0110";
            }else if(temp==7){
                s1=s1+"0111";
            }else if(temp==8){
                s1=s1+"1000";
            }else if(temp==9){
                s1=s1+"1001";
            }
        }
        if (isNeg==true){
            s1="1011"+s1;
        }else{
            s1="1010"+s1;
        }
        return s1;

    }

    public String NBCDToDecimal(String NBCDstr){
        boolean isNeg=false;
        if(NBCDstr.substring(0,4).equals("1011")){
            isNeg=true;
        }
        NBCDstr=NBCDstr.substring(4);
        String result="";
        while(!NBCDstr.equals("")){
            String temp=NBCDstr.substring(0,4);
            int true_value=Integer.parseInt(temp,2);
            result=result+String.valueOf(true_value);
            if(!NBCDstr.equals("")){
                NBCDstr=NBCDstr.substring(4);
            }
        }
        if (isNeg == false) {
            result="+"+result;
        }else{
            result="-"+result;
        }
        return result;
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
    public String binaryToFloat(String binstr){
        if(binstr.equals("01111111100000000000000000000000")){
            return "+Inf";
        }else if(binstr.equals("11111111100000000000000000000000")){
            return "-Inf";
        }//在这之后可以判断NaN
        boolean isNormalized=true;
        String expstr=binstr.substring(1,9);
        if(expstr.equals("00000000")){
            isNormalized=false;
        }
        double sig=0;
        double res;
        if(!isNormalized){
            int exp=-126;
            for(int i=9;i<32;i++){
                if(binstr.charAt(i)=='1'){
                    sig+=Math.pow(2,-i+8);//9对应-1 10对应-2
                }
            }
            res=Math.pow(2, exp) * sig;
            if (binstr.charAt(0) == '1') {
                return String.valueOf(-res);
            }
            return String.valueOf(res);
        }else{
            int exp=Integer.parseInt(expstr,2)-127;
            sig=1;
            for (int i = 0; i < 23; i++) {
                if (binstr.charAt(9 + i) == '1') {
                    sig += Math.pow(2, -(i + 1));
                }
            }
            res = Math.pow(2, exp) * sig;
            if (binstr.charAt(0) == '1') {
                return String.valueOf(-res);
            }
            return String.valueOf(res);
        }

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
    public String FloatToBinary(String floatStr){
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
        //之后用的都是abs
        String expStr;
        String sigStr="";
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

}
