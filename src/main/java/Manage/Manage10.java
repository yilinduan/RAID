package Manage;

import RAID.RAID;
import function.Transform;

import java.io.IOException;

public class Manage10 extends Manage1{
    public Manage10(int n, int l, int d, String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        // 磁盘号 块号，磁盘号只存偶数，奇数号默认为镜像
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum;j=j+2){
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
    //写操作调用Manage1的写操作

    //按文件名读取文件
    //读操作调用Manage1的读操作

    //按照文件名进行删除操作
    //删除操作调用Manage1的删除操作

    //建立文件存储表
    //调用Manage1

}
