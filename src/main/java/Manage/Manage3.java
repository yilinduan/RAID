package Manage;

import RAID.RAID;
import function.Q;
import function.Transform;

import java.io.IOException;

public class Manage3 extends Manage{
    int []Dcheck;//存储校验盘盘号，索引为块号
    int []Dcheck2;
    public Manage3(){}
    public Manage3(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        setDcheck();//初始化
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        //最后一块磁盘作为校验盘
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum;j++){
                if(Dcheck[i]==j){
                    break;//校验盘，不存储
                }
                String write=j+" "+i+" "+"0";
                DMList.addLast(write);
            }
        upMfile(raid.DMpath,DMList);
        upMfile(raid.FMpath,FMList);
        //初始化磁盘状态
        Dstage=new int[n];
        for(int j=0;j<raid.DiscNum;j++){
            Dstage[j]=0;//0表示正常
        }
    }
    //初始化Dcheck
    public boolean setDcheck(){
        Dcheck=new int [raid.DiscLen/raid.StripeDepth];
        Dcheck2=new int [raid.DiscLen/raid.StripeDepth];
        for(int i=0;i<Dcheck.length;i++){
            Dcheck[i]=raid.DiscNum-1;
            Dcheck2[i]=-1;
        }
        return true;
    }
    //输入文件名、文件内容，进行写操作
    @Override
    public boolean write(String fname,String fcontent) throws IOException {//写文件
        if (FMList.indexOf(fname) >= 0) {
            System.out.println("文件已存在");
            return false;
        }
        fillnum=raid.StripeDepth-(fcontent.length()*16)%raid.StripeDepth;
        //字符文件变二进制文件
        fcontent= Transform.strToBinStr(fcontent);
        for(int i=0;i<fillnum;i++){
            fcontent+="0";
        }
        int fLength=fcontent.length();
        //计算需要多少块
        int Dnum=fLength%raid.StripeDepth==0?(fLength/raid.StripeDepth):(fLength/raid.StripeDepth+1);
        if(Dnum>DMList.size()){
            System.out.println("存储空间不够");
            return false;
        }
        //磁盘管理：
        int [][]Dseek=seekDisc(Dnum);
        //判断是否有足够空闲位置
        if(Dseek[0][0]==-1){
            System.out.println("空间不够");
            return false;
        }
        //文件管理：
        upFMlist(fname,Dseek,Dnum);
        //进行写操作
        for(int i=FMList.indexOf(fname)+1,x=0;x<Dnum;x++){
            String line=FMList.get(++i);
            int begin=x*raid.StripeDepth;
            int end=x*raid.StripeDepth+raid.StripeDepth;
            if(end>fLength) end=fLength;
            //    System.out.println(begin+" "+end);
            wfile(fcontent.substring(begin,end),line);
            //修改DMlist
            line=line+" 0";
            int k=DMList.indexOf(line);
            line=line.substring(0,line.length()-1)+"1";
            DMList.set(k,line);
        }
        System.out.println("写入文件："+fname);
        upMfile(raid.DMpath,DMList);
        Pcheck();//更新校验码
        Qcheck();
        return true;
    }
    //按照文件名进行删除操作
    @Override
    public boolean delete(String fname) throws IOException {
        String fill="";
        for(int k=0;k<raid.StripeDepth;k++){
            fill+="0";
        }
        int i=FMList.indexOf(fname);
        if(i==-1){
            System.out.println("没有此文件");
            return false;
        }
        i++;
        int Dnum=Integer.parseInt(FMList.get(i));
        for(int x=0;x<Dnum;x++){
            i++;
            String line=FMList.get(i);//得到此次记录
            wfile(fill,line);
        }
        Pcheck();
        Qcheck();
        i=FMList.indexOf(fname);
        FMList.remove(i);
        Dnum=Integer.parseInt(FMList.get(i));
        for(int x=0;x<Dnum;x++) {
            FMList.remove(i);//移除上一条记录
            String line=FMList.get(i);//得到此次记录
            //更改DMList
            String l=line+" 1";
            int m=DMList.indexOf(l);
            DMList.set(m,line+" 0");//查找置为0
        }
        FMList.remove(i);
        upMfile(raid.DMpath,DMList);
        upMfile(raid.FMpath,FMList);
        System.out.println("删除文件："+fname);
        return true;
    }
    //P校验盘全部更新
    public boolean Pcheck() throws IOException {
        //以块号为循环
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++){//块号
            int Pnum=Dcheck[i];//P校验盘
            String str="";//初始化
            for(int k=0;k<raid.StripeDepth;k++){
                str+="0";
            }
            for(int j=0;j<raid.DiscNum;j++){//盘号
                //跳过P校验盘
                if(j==Pnum){
                    continue;
                }
                //跳过Q校验盘
                if(j==Dcheck2[i]){
                    continue;
                }
                //跳过空盘
                if(DMList.indexOf(j+" "+i+" 0")>=0){
                    continue;
                }
                String line1=j+" "+i;
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
            String line=Pnum+" "+i;
            wfile(str,line);
        }
        return true;
    }

    //Q校验盘全部更新
    public boolean Qcheck() throws IOException {
        if(Dcheck2[0]==-1)
            return true;
        //以块号为循环
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++){//块号
            int Pnum=Dcheck2[i];//Q校验盘
            String str="";//初始化
            String s0="";
            for(int k=0;k<raid.StripeDepth;k++){
                str+="0";
                s0+="0";
            }
            for(int j=0;j<raid.DiscNum;j++){//盘号
                //跳过Q校验盘
                if(j==Pnum){
                    continue;
                }
                //跳过P校验盘
                if(j==Dcheck[i]){
                    continue;
                }
                //跳过空盘
                if(DMList.indexOf(j+" "+i+" 0")>=0){
                    continue;
                }
                String line1=j+" "+i;
                String str1=rfile(line1);
                String str2="";
                if(!str1.equals(s0)){
                    int a=Transform.BiToIntStr(str1);
                    //计算k*a,k=dnum+1
                    int ka= Q.multip(j+1,a);
                    //转为二进制
                    str1=Transform.IntToBiStr(ka,raid.StripeDepth);
                    // str2存储更新后的校验数据
                }
                //与str异或
                for(int k=0;k<str1.length();k++){
                    int a1=Integer.parseInt(str.substring(k,k+1));
                    int b1=Integer.parseInt(str1.substring(k,k+1));
                    str2+=Integer.toString(a1^b1);
                }
                str=str2;//先将数据转为十进制
            }
            //进行磁盘数据恢复
            String line=Pnum+" "+i;
            wfile(str,line);
        }
        return true;
    }
    //进行磁盘检查
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
        else if(badnum>1){
            //坏磁盘超过一个，无法修复
            return false;
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
            for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++){
                if(DMList.indexOf(bad+" "+i+" 0")>=0){
                    continue;
                }
                String str = "";//存储中间结果
                String str2 = str;
                //当前块号的所有磁盘
                //进行异或操作
                for(int n=0;n<dnum.length;n++){
                    String line1=dnum[n]+" "+i;
                    String str1=rfile(line1);
                    if(str.equals("")){//初始化操作
                            for(int k=0;k<raid.StripeDepth;k++){
                                str+="0";
                            }
                    }
                    //如果读到的数据不为空，则进行异或操作
                    if(DMList.indexOf(dnum[n]+" "+i+" 0")>=0){
                        continue;
                    }
                    for(int k=0;k<str1.length();k++){
                        int a=Integer.parseInt(str.substring(k,k+1));
                        int b=Integer.parseInt(str1.substring(k,k+1));
                        str2+=Integer.toString(a^b);
                    }
                    str=str2;
                    str2="";

                }
                //进行磁盘数据恢复
                String line=bad+" "+i;
                wfile(str,line);
            }
        }
        System.out.println("修复成功");
        Dstage[bad]=0;
        return true;
    }
}
