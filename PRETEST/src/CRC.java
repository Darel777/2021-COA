public class CRC {
    public static void main(String[] args) {
        char[] data={'1','0','0','0','1','1'};
        String polynomial="1001";
        System.out.println(Calculate(data,polynomial));

    }
    /**
     * CRC计算器
     *
     * @param data       数据流
     * @param polynomial 多项式
     * @return CheckCode
     **/
    public static char[] Calculate(char[] data,String polynomial){
        //根据数据流和多项式生成校验码
        int poly_length=polynomial.length();
        String dividend=String.valueOf(data);
                for(int i=0;i<poly_length-1;i++){
            dividend=dividend+"0";
        }
        return mod2division(dividend,polynomial);
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
        return mod2division(String.valueOf(data) + String.valueOf(CheckCode), polynomial);
    }
    //                                    9               4
    public static char[] mod2division(String dividend,String divisor){
        char[] res=new char[divisor.length()-1];//3位余数
        char[] dividend_arr=dividend.toCharArray();//被除数arr
        char[] divisor_arr=divisor.toCharArray();//生成多项式ARR
        char[] dend_temp=new char[divisor.length()];//每次计算的被除数
        for(int i=0;i<dend_temp.length;i++){
            dend_temp[i]=dividend_arr[i];
        }
        for(int i=0;i<dividend.length()-divisor.length()+1;i++){
            if(Integer.parseInt(String.valueOf(dend_temp[0]))==1){
                dend_temp=xor(dend_temp,divisor_arr);
                if(i!=dividend.length()-divisor.length()){
                    for(int j=0;j<divisor.length()-1;j++){
                        dend_temp[j]=dend_temp[j+1];
                    }}
                if(i!=dividend.length()-divisor.length()){
                    dend_temp[divisor.length()-1]=dividend_arr[i+divisor.length()];
                }
            }else{
                if(i!=dividend.length()-divisor.length()){
                    for(int j=0;j<divisor.length()-1;j++){
                        dend_temp[j]=dend_temp[j+1];
                    }}
                if(i!=dividend.length()-divisor.length()){
                    dend_temp[divisor.length()-1]=dividend_arr[i+divisor.length()];
                }
            }
        }
        return dend_temp;
    }
    public static char[] xor(char[] a,char[] b){
        char[] res=new char[a.length];
        for(int i=0;i< a.length;i++){
            res[i]=(a[i]==b[i]?'0':'1');
        }
        return res;
    }
}
