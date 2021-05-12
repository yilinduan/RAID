package RAID;


import java.io.*;

public class RAID {
    public int DiscNum;//磁盘数量
    public int DiscLen;//单块磁盘容量
    public int StripeDepth;//条带深度
    public String FMpath;//文件管理路径
    public String DMpath;//磁盘管理路径

    public RAID() { DiscNum = 0; DiscLen = 0; StripeDepth = 0; }
    public RAID(int n,int l,int d,String Dir)throws Exception{
        DiscNum = n;
        DiscLen = l;
        StripeDepth = d;
        DMpath=Dir+"\\DiscManage.txt";
        FMpath=Dir+"\\FileManage.txt";
        int num=l/d;//每个磁盘分块块数
        //创建n个磁盘文件
        //从0开始编号
        File file= new File(Dir);
        if (file.mkdirs()){}
        for(int i=0;i<n;i++){
            String fname=Dir + "\\Disc"+i+".txt";
            File file1= new File(Dir + "\\Disc"+i+".txt");
            if (file1.createNewFile()){}
            //创建RandomAccessFile对象并向文件写入内容
            //初始化磁盘文件
            RandomAccessFile rf = new RandomAccessFile(fname,"rw");
            int begin=0;
            rf.seek(begin);
            String content="";
            for(int j=0;j<DiscLen;j++)
                content+="0";
            rf.writeBytes(content);
            rf.close();
        }
    }

}
