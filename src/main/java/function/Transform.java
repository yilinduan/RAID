package function;

public class Transform {
    //汉字转换成二进制字符串
    public static String strToBinStr(String txt) {
        char[] chars=txt.toCharArray();
        StringBuffer result = new StringBuffer();
        for(int i=0; i<chars.length; i++) {
            String Bistr=Integer.toBinaryString(chars[i]);
            int num=16-Bistr.length();
            while(num!=0){
                Bistr='0'+Bistr;
                num--;
            }
            result.append(Bistr);
            //     result.append(" ");
     //       System.out.println(chars[i]+" "+Bistr);
        }
        return result.toString();
    }
    //将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp=binStr.toCharArray();
        int[] result=new int[temp.length];
        for(int i=0;i<temp.length;i++) {
            result[i]=temp[i]-48;
      //      System.out.print(result[i]);
        }
   //     System.out.println();
        return result;
    }
    //将二进制转换成字符
    public static char BinstrToChar(String binStr){
        int[] temp=BinstrToIntArray(binStr);
        int sum=0;
        for(int i=0; i<temp.length;i++){
            sum +=temp[temp.length-1-i]<<i;
        }
        return (char)sum;
    }
    //二进制字符串转换成汉字
    public static String BinStrTostr(String binary) {
        String[] tempStr=new String[binary.length()/16];
        for(int i=0;i<binary.length()/16;i++){
            tempStr[i]=binary.substring(i*16,i*16+16);
         //   System.out.println(tempStr[i]+" "+binary.length());
        }
        char[] tempChar=new char[tempStr.length];
        for(int i=0;i<tempStr.length;i++) {
            tempChar[i]=BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }
    //十进制数转二进制字符串
    public static String IntToBiStr(int n,int len){
        String result = Integer.toBinaryString(n);
        int length=len-result.length();
        for(int i=0;i<length;i++){
            result='0'+result;
        }
        return result;
    }
    //二进制字符串转10进制数
    public static int BiToIntStr(String binStr){
        int r = Integer.parseInt(binStr,2);
        return r;
    }
    public static long[] ToIntStr(String binStr){
        int length=binStr.length()/4;
        long r[]=new long[length];
        for(int i=0;i<length;i++){
            r[i] = Integer.parseInt(binStr.substring(4*i,4*i+4),16);
        }
        return r;
    }
}
