package function;

public class Q {
    static int []GFI;//正
    static int []GFL;//反
    //乘法
    static public int multip(int a,int b){
        table();
        arc_table();
        int m=(GFL[a]+GFL[b])%255;
        if(m<0) m=0;
        return GFI[m];
    }
    //除法
    static public int divid(int a,int b){
        table();
        arc_table();
        int m=(GFL[a]-GFL[b]+255)%255;
        if(m<0)m=0;
        return GFI[m];
    }
    //正向表
    static public void table(){
        GFI=new int[256];
        int i;
        GFI[0] = 1;//g^0
        for(i = 1; i < 255; ++i)//生成元为x + 1
        {
            //下面是m_table[i] = m_table[i-1] * (x + 1)的简写形式
            GFI[i] = (GFI[i-1] << 1 ) ^ GFI[i-1];

            //最高指数已经到了8，需要模上m(x)
            if( GFI[i] > 0x100 )
            {
                GFI[i] ^= 0x11B;//用到了前面说到的乘法技巧
            }
        }
    }
    //反向表
    static public void arc_table(){
        GFL=new int[256];
        for(int i = 0; i < 255; ++i)
            GFL[ GFI[i] ] = i;
    }
}
