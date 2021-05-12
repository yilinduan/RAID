package Manage;

import RAID.RAID;
import function.Transform;

import java.io.IOException;

public class Manage5 extends Manage3 {
    public Manage5(){}
    public Manage5(int n, int l, int d,String Dira) throws Exception {
        raid=new RAID(n, l, d,Dira);
        Dir=Dira;
        setDcheck();//初始化
        //初始化DMlist,先遍历同一块号的所有磁盘号，
        //最后一块磁盘作为校验盘
        for(int i=0;i<raid.DiscLen/raid.StripeDepth;i++)
            for(int j=0;j<raid.DiscNum;j++){
                if(Dcheck[i]!=j){
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
    public boolean setDcheck(){
        Dcheck=new int [raid.DiscLen/raid.StripeDepth];
        Dcheck2=new int [raid.DiscLen/raid.StripeDepth];
        int n=raid.DiscNum;
        for(int i=0;i<Dcheck.length;i++){
            Dcheck[i]=n%raid.DiscNum;
            Dcheck2[i]=-1;
            n++;
        }
        return true;
    }
    //输入文件名、文件内容，进行写操作
   //与manage3相同

    //按文件名读取文件
    //与manage3相同

    //按照文件名进行删除操作
    //与manage3相同

    //建立文件存储表
    //与manage3相同

    //获取异或结果
    // 与manage3相同

    //进行磁盘检查
    //与manage3相同
}
