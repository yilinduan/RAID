package Manage;

import RAID.RAID;
import function.Transform;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class Manage1 extends Manage{
    public Manage1(){}
    public Manage1(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        //初始化DMlist，先遍历同一磁盘的所有块号
        //磁盘号、块号，只存偶数编号磁盘，相应+1为镜像磁盘
        for(int j=0;j<raid.DiscNum;j=j+2)
            for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++){
                String write=j+" "+i+" "+"0";
                DMList.addLast(write);
            }
        //初始化磁盘状态
        Dstage=new int[n];
        for(int j=0;j<raid.DiscNum;j++){
            Dstage[j]=0;//0表示正常
        }
        upMfile(raid.DMpath,DMList);
        upMfile(raid.FMpath,FMList);
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
            String []lines=line.split(" ");
            String line2=(Integer.parseInt(lines[0])+1)+line.substring(1);
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
            wfile(fcontent.substring(begin,end),line2);
        }
        System.out.println("写入文件："+fname);
        upMfile(raid.DMpath,DMList);

        return true;
    }
    //按照文件名进行删除操作
    @Override
    public boolean delete(String fname) throws IOException {
        int i=FMList.indexOf(fname);
        if(i==-1){
            System.out.println("没有此文件");
            return false;
        }
        FMList.remove(i);
        int Dnum=Integer.parseInt(FMList.get(i));
        for(int x=0;x<Dnum;x++){
            FMList.remove(i);
            String line=FMList.get(i);
            String l=line+" 1";
            int m=DMList.indexOf(l);
            DMList.set(m,line+" 0");
        }
        FMList.remove(i);
        upMfile(raid.DMpath,DMList);
        upMfile(raid.FMpath,FMList);
        System.out.println("删除文件："+fname);
        return true;
    }

    //建立文件存储表
    public boolean upFMlist(String fname,int [][]num,int Dnum) throws IOException {
        FMList.addLast(fname);
        FMList.addLast(String.valueOf(Dnum));
        for(int i=0;i<Dnum;i++){
            FMList.addLast(num[i][0]+" "+num[i][1]);
        }
        upMfile(raid.FMpath,FMList);
        return true;
    }
    @Override
    public boolean dCheck() throws IOException {
        int badnum=0;
        int []bad;
        for(int i=0;i<Dstage.length;i++){
            if(Dstage[i]==1){
                badnum++;
                System.out.println("发现坏磁盘:"+i);
            }
        }
        bad=new int[badnum];
        for(int i=0,j=0;i<Dstage.length;i++){
            if(Dstage[i]==1){
                bad[j]=i;
            }
        }
        for (int i=0;i<badnum;i++){
            if(bad[i]%2==0){
                if(Dstage[bad[i]+1]==1){
                    System.out.println("无法修复");
                    return false;
                }
                else{
                    for(int j=0;j<raid.DiscLen/raid.StripeDepth;j++){
                        String line=(bad[i]+1)+" "+j;
                        String str=rfile(line);
                        wfile(str,line);
                    }
                    Dstage[bad[i]]=0;
                }
            }
            else{
                if(Dstage[bad[i]-1]==1){
                    System.out.println("无法修复");
                    return false;
                }
                else{
                    for(int j=0;j<raid.DiscLen/raid.StripeDepth;j++){
                        String line=(bad[i]-1)+" "+j;
                        String str=rfile(line);
                        wfile(str,line);
                    }
                    Dstage[bad[i]]=0;
                }
            }
        }
        return true;
    }
}
