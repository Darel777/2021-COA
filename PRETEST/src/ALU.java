import java.util.ArrayList;

public class ALU {

    public static void main(String[] args) {
        ALU alu=new ALU();

        System.out.println(alu.div2("00010","11000"));//除数为负数且能够除尽 需要将余数置为0 商再看
    }
    public String add(String src,String dest){
        //xn=yn时 xn=0yn=0sn=1溢出 xn=1yn=1sn=0溢出
        char[] src_arr=src.toCharArray();
        char[] dest_arr=dest.toCharArray();
        char[] res=new char[32];
        for(int i=0;i<32;i++){
            res[i]='0';
        }
        char addtag='0';
        for(int i=31;i>=0;i--){
            int temp=0;
            if(src_arr[i]=='1'){
                temp+=1;
            }
            if(dest_arr[i] == '1'){
                temp+=1;
            }
            if(addtag=='1'){
                temp+=1;
            }
            if((int)(temp-0)>=2){
                addtag='1';
            }else{
                addtag='0';
            }
            if((int)(temp-0)%2==1){
                res[i]='1';
            }
        }
        return String.valueOf(res);
    }
    public String sub(String src,String dest){//dest被减数 src减数
        Transformer transformer=new Transformer();
        char[] src_arr=src.toCharArray();
        for(int i=0;i<32;i++){
            if(src_arr[i]=='0'){
                src_arr[i]='1';
            }else{
                src_arr[i]='0';
            }
        }//src neg
        src=String.valueOf(src_arr);
        src=transformer.oneAdder(src);
        return add(src,dest);
    }


    public String mul(String src,String dest){//0111 src0110 取后三十二位//没有源码补码相互转换过程
        StringBuilder stringbulider=new StringBuilder();
        for(int i=0;i<src.length();i++){
            stringbulider.append('0');
        }
        for(int i=0;i<src.length();i++){
            if(src.charAt(src.length()-i-1)=='0'){
                stringbulider.insert(0,'0');
            }else{
                String s1=stringbulider.substring(0,4);
                String s2=src;
                //String s3=muladd(s1,s2);
                stringbulider.delete(0,4);//muladd是正常加起来
                //stringbulider.insert(0,s3);
                stringbulider.insert(0,'0');
            }
        }
        return stringbulider.toString();
    }
    public char[] mulbools(String src,String dest){
        int length=src.length();
        char[] result=new char[2*length+1];

        //初始化
        for(int i=0;i<length;i++){
            result[i]='0';
        }for(int i=length;i<2*length;i++){
            result[i]=src.charAt(i-length);
        }
        result[2*length]='0';

        //构建Y数组
        int[] Yarr=new int[src.length()];
        for(int i=0;i<src.length();i++){
            Yarr[i]=result[2*length-i]-result[2*length-1-i];
        }

        //构建x -x
        char[] xstring=dest.toCharArray();//X
        char[] xstring2=new char[dest.length()];//-X
        for(int i=0;i<xstring2.length;i++){
            if(xstring[i]=='0'){
                xstring2[i]='1';
            }else{
                xstring2[i]='0';
            }
        }
        for(int i=xstring2.length-1;i>=0;i--){
            if(xstring2[i]=='0'){
                xstring2[i]='1';
                break;
            }else{
                xstring2[i]='0';
            }
        }

        //开始工作
       for(int i=0;i<Yarr.length;i++){
            if(Yarr[i]==0){
                for(int j= result.length-1;j>=1;j--){
                    result[j]=result[j-1];
                }
                if(result[1]=='0'){result[0]='0';}else{result[0]='1';}
                //右移
            }else if(Yarr[i]==1){
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=result[j];
                }
                char[] c3=muladd(c1,xstring);
                for(int j=0;j<src.length();j++){
                    result[j]=c3[j];
                }
                for(int j= result.length-1;j>=1;j--){
                    result[j]=result[j-1];
                }if(result[1]=='0'){result[0]='0';}else{result[0]='1';}//右移
            }else{
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=result[j];
                }
                char[] c3=muladd(xstring2,c1);
                for(int j=0;j<src.length();j++){
                    result[j]=c3[j];
                }
                for(int j= result.length-1;j>=1;j--){
                    result[j]=result[j-1];
                }if(result[1]=='0'){result[0]='0';}else{result[0]='1';}//右移
            }
       }

        return result;
    }
    public char[] muladd(char[] src,char[] dest){
        char[] res=new char[src.length];
        int c=0;
        for(int i=src.length-1;i>=0;i--){
            int temp=0;
            if(src[i]=='1'){
                temp+=1;
            }if(dest[i]=='1'){
                temp+=1;
            }if(c==1){
                temp+=1;
            }
            if(temp%2==1){
                res[i]='1';
            }else{
                res[i]='0';
            }if(temp>=2){
                c=1;
            }else{
                c=0;
            }
        }
        return res;
    }

    //非常重要
    public String div(String src,String dest){
        //dest被除数 src除数
        if(src.equals("00000000000000000000000000000000")){
            throw new ArithmeticException();
        }else if(!src.equals("00000000000000000000000000000000")&&dest=="00000000000000000000000000000000"){
            return "00000000000000000000000000000000";
        }

        //操作数的预置
        char[] Yarr=src.toCharArray();//Y
        char[] Yarr2=src.toCharArray();//-Y
        for(int i=0;i<Yarr2.length;i++){
            if(Yarr2[i]=='1'){
                Yarr2[i]='0';
            }else{
                Yarr2[i]='1';
            }
        }
        for(int i=Yarr2.length-1;i>=0;i--){
            if(Yarr2[i]=='0'){
                Yarr2[i]='1';
                break;
            }else{
                Yarr2[i]='0';
            }
        }
        char[] Reg=new char[dest.length()*2];
        for(int i=dest.length();i<dest.length()*2;i++){
            Reg[i]=dest.charAt(i-dest.length());
        }
        for(int i=0;i<dest.length();i++){
            if(Reg[dest.length()]=='1'){
                Reg[i]='1';
            }else{
                Reg[i]='0';
            }
        }

        //反复执行 串行左移 加减法 右面补0或1
        for(int i=0;i<dest.length();i++){
            for(int j=0;j<=Reg.length-2;j++){
                Reg[j]=Reg[j+1];//leftshift
            }
            if(Reg[0]==Yarr[0]){
                char temp=Reg[0];
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr2);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }//r=r-y
                if(Reg[0]==temp){
                    Reg[Reg.length-1]='1';
                }else{
                    for(int j=0;j<src.length();j++){
                        Reg[j]=c1[j];
                    }
                    Reg[Reg.length-1]='0';
                }
            }else{
                char temp=Reg[0];
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }//r=r+y
                if(Reg[0]==temp){
                    Reg[Reg.length-1]='1';
                }else{
                    for(int j=0;j<src.length();j++){
                        Reg[j]=c1[j];
                    }
                    Reg[Reg.length-1]='0';
                }
            }
        }
        if(dest.charAt(0)!=src.charAt(0)){
            for(int k=src.length();k<src.length()*2;k++){
                if(Reg[k]=='0'){
                    Reg[k]='1';
                }else{
                    Reg[k]='0';
                }
            }
            for(int k=Reg.length-1;;k--){
                if(Reg[k]=='0'){
                    Reg[k]='1';
                    break;
                }else{
                    Reg[k]='0';
                }
            }
        }

        return String.valueOf(Reg);

    }
    public String div2(String src,String dest){
        //dest被除数 src除数
        if(src.equals("00000000000000000000000000000000")){
            throw new ArithmeticException();
        }else if(!src.equals("00000000000000000000000000000000")&&dest=="00000000000000000000000000000000"){
            return "00000000000000000000000000000000";
        }

        //操作数的预置
        char[] Yarr=src.toCharArray();//Y
        char[] Yarr2=src.toCharArray();//-Y
        for(int i=0;i<Yarr2.length;i++){
            if(Yarr2[i]=='1'){
                Yarr2[i]='0';
            }else{
                Yarr2[i]='1';
            }
        }
        for(int i=Yarr2.length-1;i>=0;i--){
            if(Yarr2[i]=='0'){
                Yarr2[i]='1';
                break;
            }else{
                Yarr2[i]='0';
            }
        }
        char[] Reg=new char[dest.length()*2];
        for(int i=dest.length();i<dest.length()*2;i++){
            Reg[i]=dest.charAt(i-dest.length());
        }
        for(int i=0;i<dest.length();i++){
            if(Reg[dest.length()]=='1'){
                Reg[i]='1';
            }else{
                Reg[i]='0';
            }
        }

        //求第一位商Q
        char next;
        if(Reg[0]==Yarr[0]){
            char[] c1=new char[src.length()];
            for(int i=0;i<src.length();i++){
                c1[i]=Reg[i];
            }
            char[] c3=muladd(c1,Yarr2);
            for(int i=1;i<src.length();i++){
                Reg[i]=c3[i];
            }
        }else{
            char[] c1=new char[src.length()];
            for(int i=0;i<src.length();i++){
                c1[i]=Reg[i];
            }
            char[] c3=muladd(c1,Yarr);
            for(int i=0;i<src.length();i++){
                Reg[i]=c3[i];
            }
        }
        if(Reg[0]==Yarr[0]){
            next='1';
        }else{
            next='0';
        }

        //求出响应位置的商
        for(int i=0;i<src.length();i++){
            for(int j=0;j<=Reg.length-2;j++){
                Reg[j]=Reg[j+1];//leftshift
            }
            Reg[Reg.length-1]=next;
            if(Reg[0]==Yarr[0]){
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr2);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }
                if(Reg[0]==Yarr[0]){
                    next='1';
                }else{
                    next='0';
                }
            }else{
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }
                if(Reg[0]==Yarr[0]){
                    next='1';
                }else{
                    next='0';
                }
            }
        }
        //最后一步
        for(int j=Reg.length/2;j<=Reg.length-2;j++){
            Reg[j]=Reg[j+1];//leftshift
        }
        Reg[Reg.length-1]=next;

        if(dest.charAt(0)!=src.charAt(0)){
            for(int j=Reg.length-1;;j--){
                if(Reg[j]=='1'){
                    Reg[j]='0';
                }else{
                    Reg[j]='1';
                    break;
                }
            }
        }

        if(Reg[0]!=dest.charAt(0)){
            if(dest.charAt(0)!=src.charAt(0)){
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr2);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }
            }else{
                char[] c1=new char[src.length()];
                for(int j=0;j<src.length();j++){
                    c1[j]=Reg[j];
                }
                char[] c3=muladd(c1,Yarr);
                for(int j=0;j<src.length();j++){
                    Reg[j]=c3[j];
                }
            }
        }
        return String.valueOf(Reg);

    }
}
