package Manage;

import RAID.RAID;
import function.Transform;

import java.io.IOException;

public class Manage01 extends Manage0{

    public Manage01(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        //一半磁盘按照Manage0存储，剩下一半做镜像
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum/2;j++){
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
            String line2=(Integer.parseInt(lines[0])+raid.DiscNum/2)+line.substring(1);//镜像
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
                if(Dstage[bad[i]+raid.DiscNum/2]==1){
                    System.out.println("无法修复");
                    return false;
                }
                else{
                    for(int j=0;j<raid.DiscLen/raid.StripeDepth;j++){
                        String line=(bad[i]+raid.DiscNum/2)+" "+j;
                        String str=rfile(line);
                        wfile(str,line);
                    }
                }
            }
            else{
                if(Dstage[bad[i]-raid.DiscNum/2]==1){
                    System.out.println("无法修复");
                    return false;
                }
                else{
                    for(int j=0;j<raid.DiscLen/raid.StripeDepth;j++){
                        String line=(bad[i]-raid.DiscNum/2)+" "+j;
                        String str=rfile(line);
                        wfile(str,line);
                    }
                }
            }
        }
        return true;
    }
}
