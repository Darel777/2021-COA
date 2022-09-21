public class nbcdu {
    public static void main(String[] args) {
        nbcdu nbcdu=new nbcdu();
        System.out.println(nbcdu.sub("000100100101","001100001001"));
//        System.out.println(Integer.parseInt("1010",2));
    }
    public String add(String operand1,String operand2){
        //建立在两个操作数位数都相等的前提上
        int length=operand1.length();
        char next='0';
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<operand1.length()/4;i++){
            String s1=operand1.substring(operand1.length()-4-4*i,operand1.length()-4*i);
            String s2=operand2.substring(operand2.length()-4-4*i,operand2.length()-4*i);
            String s3=add_sup(s1,s2,next);
            if(s3.charAt(4)=='1'){
                next='1';
            }else{
                next='0';
            }
            stringBuilder.insert(0,s3.substring(0,4));
        }
        if(next=='1'){
            stringBuilder.insert(0,"0001");
        }



        return stringBuilder.toString();
    }
    public String add_sup(String operand1,String operand2,char next){
        char[] res=new char[5];//前四位是和 后一位是进位
        for(int i=3;i>=0;i--){
            int temp=0;
            if(operand1.charAt(i)=='1'){
                temp+=1;
            }
            if(operand2.charAt(i)=='1'){
                temp+=1;
            }
            if(next=='1'){
                temp+=1;
            }
            if(temp>=2){
                next='1';
            }else{
                next='0';
            }
            if(temp%2==1){
                res[i]='1';
            }else{
                res[i]='0';
            }
        }
        res[4]=next;
        if(res[4]=='1'||Integer.parseInt(String.valueOf(res).substring(0,4),2)>=10){
            res[4]='1';
            char[] update=add_sup2("0110",String.valueOf(res).substring(0,4)).toCharArray();
            for(int i=0;i<=3;i++){
                res[i]=update[i];
            }
        }
        return String.valueOf(res);
    }
    public String add_sup2(String operand1,String operand2){
        char[] res=new char[4];
        char next='0';
        for(int i=3;i>=0;i--){
            int temp=0;
            if(operand1.charAt(i)=='1'){
                temp+=1;
            }
            if(operand2.charAt(i)=='1'){
                temp+=1;
            }
            if(next=='1'){
                temp+=1;
            }
            if(temp>=2){
                next='1';
            }else{
                next='0';
            }
            if(temp%2==1){
                res[i]='1';
            }else{
                res[i]='0';
            }
        }
        return String.valueOf(res);
    }

    public String sub(String operand1,String operand2){
        //1 被减数 2 减数//-0归原值
        int length=operand1.length();
        String operand3=reverse(operand2);
        operand3=oneadder(operand3);

        String temp1=add(operand1,operand3);
        if(temp1.length()>length){
            return temp1.substring(4);
        }else{
            temp1=reverse(temp1);
            temp1=oneadder(temp1);
            temp1="-"+temp1;
            return temp1;
        }


    }
    public String reverse(String operand1){
        int length=operand1.length();
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<operand1.length()/4;i++){
            String s1=operand1.substring(length-4-4*i,length-4*i);
            if(s1.equals("0000")){
                stringBuilder.insert(0,"1001");
            }
            if(s1.equals("0001")){
                stringBuilder.insert(0,"1000");
            }
            if(s1.equals("0010")){
                stringBuilder.insert(0,"0111");
            }
            if(s1.equals("0011")){
                stringBuilder.insert(0,"0110");
            }
            if(s1.equals("0100")){
                stringBuilder.insert(0,"0101");
            }
            if(s1.equals("0101")){
                stringBuilder.insert(0,"0100");
            }
            if(s1.equals("0110")){
                stringBuilder.insert(0,"0011");
            }
            if(s1.equals("0111")){
                stringBuilder.insert(0,"0010");
            }
            if(s1.equals("1000")){
                stringBuilder.insert(0,"0001");
            }
            if(s1.equals("1001")){
                stringBuilder.insert(0,"0000");
            }
        }
        return stringBuilder.toString();
    }
    public String oneadder(String operand){
        String temp="1";
        for(int i=0;i<operand.length()-1;i++){
            temp="0"+temp;
        }
        return add(temp,operand);
    }
}
