package Manage;

import RAID.RAID;
import function.Q;
import function.Transform;

import java.io.IOException;

public class Manage6 extends Manage5 {
    public Manage6(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        setDcheck();//初始化
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        //最后一块磁盘作为校验盘
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum;j++){
                if(Dcheck[i]!=j&&Dcheck2[i]!=j){
                    //i 块号；j 磁盘号
                    String write=j+" "+i+" "+"0";
                    DMList.addLast(write);
                }
            }
        upMfile(raid.DMpath,DMList);
        upMfile(raid.FMpath,FMList);
        //初始化磁盘状态
        Dstage=new int[n];
        for(int j=0;j<raid.DiscNum;j++){
            Dstage[j]=0;//0表示正常
        }
    }
    @Override
    public boolean setDcheck(){
        Dcheck=new int [raid.DiscLen/raid.StripeDepth];
        Dcheck2=new int [raid.DiscLen/raid.StripeDepth];
        int n=raid.DiscNum;
        for(int i=0;i<Dcheck.length;i++){
            Dcheck[i]=n%raid.DiscNum;
            n++;
            Dcheck2[i]=n%raid.DiscNum;
        }
        return true;
    }
    //输入文件名、文件内容，进行写操作
    //与3相同

    //按照文件名进行删除操作
    //与3相同

    //进行磁盘检查
    @Override
    public boolean dCheck() throws IOException {
        int badnum=0;
        int bad=0;
        for(int i=0;i<Dstage.length;i++){
            if(Dstage[i]==1){
                badnum++;
                bad=i;
                System.out.println("发现坏磁盘:"+bad);
            }
        }
        if(badnum==0){
            //无坏磁盘
            return true;
        }
        else if(badnum>2){
            //坏磁盘超过两个，无法修复
            return false;
        }
        else if(badnum==2){
            return dCheck2();
        }
        else if(badnum==1) {
            //每次读一个块大小，循环
            //按照块号，读出一个块号对应的所有磁盘的内容，进行异或

            //先保存除了坏磁盘以外其他磁盘的盘号
            int dnum[]=new int[raid.DiscNum-1];
            for(int j=0,m=0;m<raid.DiscNum;m++){
                if(m!=bad){
                    dnum[j]=m;
                    j++;
                }
            }
            //块号作为循环
            for(int i=0;i<Dcheck.length;i++){
                if(DMList.indexOf(bad+" "+i+" 0")>=0){
                    continue;
                }
                String str = "";//存储中间结果
                String str2 = str;
                String line1="";
                String str1="";
                //当前块号的所有磁盘
                //进行异或操作
                if(Dcheck[i]==bad||Dcheck2[i]==bad){
                    continue;
                }
                else{
                    //数据盘进行异或操作
                    for(int n=0;n<dnum.length;n++){
                        //跳过Q校验盘
                        if(dnum[n]==Dcheck2[i]){
                            continue;
                        }
                        if(DMList.indexOf(dnum[n]+" "+i+" 0")>=0){
                            continue;
                        }
                        if(str.equals("")){//初始化操作
                            for(int k=0;k<raid.StripeDepth;k++){
                                str+="0";
                            }
                        }
                        line1=dnum[n]+" "+i;
                        str1=rfile(line1);
                        for(int k=0;k<str1.length();k++){
                            int a=Integer.parseInt(str.substring(k,k+1));
                            int b=Integer.parseInt(str1.substring(k,k+1));
                            str2+=Integer.toString(a^b);
                        }
                        str=str2;
                        str2="";
                    }
                }
                //进行磁盘数据恢复
                String line=bad+" "+i;
                wfile(str,line);
            }
            Pcheck();
            Qcheck();
        }
        System.out.println("修复成功");
        Dstage[bad]=0;
        return true;
    }
    public boolean dCheck2() throws IOException {
        int []bad=new int[2];
        for(int i=0,j=0;i<Dstage.length;i++){
            if(Dstage[i]==1){
                bad[j++]=i;
            }
        }
        //先保存除了坏磁盘以外其他磁盘的盘号
        int dnum[]=new int[raid.DiscNum-2];
        for(int j=0,m=0;m<raid.DiscNum;m++){
            if(m!=bad[0]&&m!=bad[1]){
                dnum[j]=m;
                j++;
            }
        }
        //保存坏磁盘上所有用于校验的块号
        int []cnum=new int[Dcheck.length];
        for(int i=0;i<Dcheck.length;i++){
            if(Dcheck[i]==bad[0]){
                cnum[i]=1;//P校验块
            }
            else if(Dcheck2[i]==bad[0]){
                cnum[i]=2;//Q校验块
            }
            else cnum[i]=0;//数据块
        }
        int []cnum2=new int[Dcheck.length];
        for(int i=0;i<Dcheck.length;i++){
            if(Dcheck[i]==bad[1]){
                cnum2[i]=1;//P校验块
            }
            else if(Dcheck2[i]==bad[1]){
                cnum2[i]=2;//Q校验块
            }
            else cnum2[i]=0;//数据块
        }
        for(int i=0;i<cnum.length;i++){//i为块号
            if(cnum[i]==0&&cnum2[i]==0) {
                //两块数据块
                int num = bad[0],bnum=bad[1];
                //先恢复数据块b
                String str = "";
                String str1 = "";
                String str2 = "";
                for (int j = 0; j < dnum.length; j++) {
                    String line1 = dnum[j] + " " + i;
                    str1 = rfile(line1);
                    if(str.equals(""))
                        for(int k=0;k<raid.StripeDepth;k++){
                            str+="0";
                        }
                    //如果是Q盘，直接异或
                    if (Dcheck2[i] == dnum[j]) {
                    }
                    //如果是P盘，乘以Ka，再异或
                    else if (Dcheck[i] == dnum[j]) {
                        int a = Transform.BiToIntStr(str1);
                        //计算k*a,k=dnum+1
                        int ka = Q.multip(num + 1, a);
                        //转为二进制
                        str1 = Transform.IntToBiStr(ka, raid.StripeDepth);
                    }
                    else {//数据盘，先计算GF乘积，计算两次，一次Ka，一次自己K，再计算异或
                        int a = Transform.BiToIntStr(str1);
                        //计算k*a,k=dnum+1
                        int ka = Q.multip(dnum[j] + 1, a);
                        //转为二进制
                        str1 = Transform.IntToBiStr(ka, raid.StripeDepth);
                        // str2存储更新后的校验数据
                        //与str异或
                        for (int k = 0; k < str1.length(); k++) {
                            int a1 = Integer.parseInt(str.substring(k, k + 1));
                            int b1 = Integer.parseInt(str1.substring(k, k + 1));
                            str2 += Integer.toString(a1 ^ b1);
                        }
                        str = str2;
                        str2="";
                        str1 = rfile(line1);
                        a = Transform.BiToIntStr(str1);
                        //计算k*a,k=dnum+1
                        ka = Q.multip(num + 1, a);
                        //转为二进制
                        str1 = Transform.IntToBiStr(ka, raid.StripeDepth);
                    }
                    //与str异或
                    for (int k = 0; k < str1.length(); k++) {
                        int a1 = Integer.parseInt(str.substring(k, k + 1));
                        int b1 = Integer.parseInt(str1.substring(k, k + 1));
                        str2 += Integer.toString(a1 ^ b1);
                    }
                    str = str2;
                    str2="";
                }
                //进行除法
                int a = Transform.BiToIntStr(str);
                //计算k*a,k=dnum+1
                int k = (num + 1) ^(bnum + 1);
                int ka = Q.divid(a, k);
                //转为二进制
                str = Transform.IntToBiStr(ka, raid.StripeDepth);
                //进行磁盘数据恢复
                String line = bnum + " " + i;
                wfile(str, line);

                //恢复a
                for(int j=0;j<dnum.length;j++){
                    if(Dcheck2[i]==dnum[j]){
                        continue;
                    }
                    String line1=dnum[j]+" "+i;
                    str1=rfile(line1);
                    str2="";
                    for(int l=0;l<str1.length();l++){
                        int a1=Integer.parseInt(str.substring(l,l+1));
                        int b1=Integer.parseInt(str1.substring(l,l+1));
                        str2+=Integer.toString(a1^b1);
                    }
                    str=str2;
                }
                //进行磁盘数据恢复
                line=num+" "+i;
                wfile(str,line);
            }
            else if((cnum[i]==1&&cnum2[i]==2)||(cnum[i]==2&&cnum2[i]==1)){
                //两块校验块
            }
            else if((cnum[i]==0&&cnum2[i]==1)||(cnum[i]==1&&cnum2[i]==0)){
                //一块数据，一块P
                int num=0,pnum=0;
                if(cnum[i]==0){
                    //d\P
                    num=bad[0];
                    pnum=bad[1];
                }
                else if(cnum[i]==2){
                    //P\d
                    num=bad[1];
                    pnum=bad[0];
                }
                //先恢复数据块
                String str="";
                String str1="";
                String str2="";
                for(int j=0;j<dnum.length;j++){
                    String line1=dnum[j]+" "+i;
                    str1=rfile(line1);
                    if(str.equals(""))
                        for(int k=0;k<raid.StripeDepth;k++){
                            str+="0";
                        }
                    //如果是Q盘，直接异或
                    if(Dcheck2[i]==dnum[j]){

                    }
                    else {//数据盘，先计算GF乘积，再计算异或
                        int a = Transform.BiToIntStr(str1);
                        //计算k*a,k=dnum+1
                        int ka = Q.multip(dnum[j] + 1, a);
                        //转为二进制
                        str1 = Transform.IntToBiStr(ka, raid.StripeDepth);
                        // str2存储更新后的校验数据
                    }
                    //与str异或
                        for(int k=0;k<str1.length();k++){
                            int a1=Integer.parseInt(str.substring(k,k+1));
                            int b1=Integer.parseInt(str1.substring(k,k+1));
                            str2+=Integer.toString(a1^b1);
                        }
                        str=str2;
                        str2="";
                }
                //进行除法
                int a=Transform.BiToIntStr(str);
                //计算k*a,k=dnum+1
                int ka= Q.divid(a,num+1);
                //转为二进制
                str=Transform.IntToBiStr(ka,raid.StripeDepth);
                //进行磁盘数据恢复
                String line=num+" "+i;
                wfile(str,line);

                //恢复P
            }
            else if((cnum[i]==0&&cnum2[i]==2)||(cnum[i]==2&&cnum2[i]==0)){
                //一块数据，一块Q
                int num=0,qnum=0;
                if(cnum[i]==0){
                    //d\Q
                    num=bad[0];
                    qnum=bad[1];
                }
                else if(cnum[i]==2){
                    //Q\d
                    num=bad[1];
                    qnum=bad[0];
                }
                //先恢复数据块
                String str="";
                for(int j=0;j<dnum.length;j++){
                    String line1=dnum[j]+" "+i;
                    if(str.equals(""))
                        for(int k=0;k<raid.StripeDepth;k++){
                            str+="0";
                        }
                    String str1=rfile(line1);
                    String str2="";
                    for(int k=0;k<str1.length();k++){
                        int a=Integer.parseInt(str.substring(k,k+1));
                        int b=Integer.parseInt(str1.substring(k,k+1));
                        str2+=Integer.toString(a^b);
                    }
                    str=str2;
                }
                //进行磁盘数据恢复
                String line=num+" "+i;
                wfile(str,line);
                //恢复Q
            }
        }
        Dstage[bad[0]]=0;
        Dstage[bad[1]]=0;
        Pcheck();
        Qcheck();
        return true;
    }
}