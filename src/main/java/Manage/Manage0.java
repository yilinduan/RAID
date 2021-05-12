package Manage;

import RAID.RAID;
import function.Transform;

import java.io.*;

import static java.lang.Integer.parseInt;

public class Manage0  extends Manage{
    public Manage0(){}
    public Manage0(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        // 磁盘号 块号
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum;j++){
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
        fillnum = raid.StripeDepth - (fcontent.length() * 16) % raid.StripeDepth;
        //字符文件变二进制文件
        fcontent = Transform.strToBinStr(fcontent);
        for (int i = 0; i < fillnum; i++) {
            fcontent += "0";
        }
        int fLength = fcontent.length();
        //计算需要多少块
        int Dnum = fLength % raid.StripeDepth == 0 ? (fLength / raid.StripeDepth) : (fLength / raid.StripeDepth + 1);
        if (Dnum > DMList.size()) {
            System.out.println("存储空间不够");
            return false;
        }
        //磁盘管理：
        int[][] Dseek = seekDisc(Dnum);
        //判断是否有足够空闲位置
        if (Dseek[0][0] == -1) {
            System.out.println("空间不够");
            return false;
        }
        //文件管理：
        upFMlist(fname, Dseek, Dnum);
        //进行写操作
        for (int i = FMList.indexOf(fname) + 1, x = 0; x < Dnum; x++) {
            String line = FMList.get(++i);
            int begin = x * raid.StripeDepth;
            int end = x * raid.StripeDepth + raid.StripeDepth;
            if (end > fLength) end = fLength;
            //    System.out.println(begin+" "+end);
            wfile(fcontent.substring(begin, end), line);
            //修改DMlist
            line = line + " 0";
            int k = DMList.indexOf(line);
            line = line.substring(0, line.length() - 1) + "1";
            DMList.set(k, line);
        }
        System.out.println("写入文件：" + fname);
        upMfile(raid.DMpath, DMList);

        //创建多线程
/*        MyThread t = new MyThread();
        t.start();
        try{
            Thread.sleep(1000);
        }
        catch (Exception x){
            System.out.println("Caught it" + x);
        }
        System.out.println("Exiting main");
*/
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
     if(badnum>0){
         return false;
     }
     else return true;
 }
/*    class MyThread extends Thread{
        public void run(){
            System.out.println("Throwing in " +"MyThread");
            throw new RuntimeException();
        }
    }
    */
}
