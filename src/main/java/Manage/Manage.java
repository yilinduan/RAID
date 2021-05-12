package Manage;

import RAID.*;
import function.Transform;

import java.io.*;
import java.util.LinkedList;

import static java.lang.Integer.parseInt;

public class Manage {
    public RAID raid ;
    String Dir="";
    int fillnum;
    public LinkedList<String> DMList=new LinkedList<String>();
    public LinkedList<String> FMList=new LinkedList<String>();
    int[] Dstage;
    public boolean write(String fname,String fcontent) throws IOException {
        return false;
    }
    public boolean delete(String fname) throws IOException {
        return false;
    }
    public boolean dCheck() throws IOException {
        return false;
    }
    public String read(String fname) throws IOException {//读文件
        if(!dCheck()) {
            return "磁盘损坏";
        }
        int i=FMList.indexOf(fname);
        if(i==-1){
            System.out.println("没有此文件");
            return "没有此文件";
        }
        int Dnum=Integer.parseInt(FMList.get(++i));
        String txt="";
        //读文件：
        for(int x=0;x<Dnum;x++){
            String line=FMList.get(++i);
            //   System.out.println(begin+" "+end);
            txt+=rfile(line);
        }
        String file= Transform.BinStrTostr(txt.substring(0,txt.length()-fillnum));
        return file;
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
    //更新管理文件
    public boolean upMfile(String path,LinkedList list) throws IOException {

        File file= new File(path);
        if (!file.exists())
            file.createNewFile();
        //更新磁盘:编号、是否空闲
        //创建BufferedWriter对象并向文件写入内容
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for(int i=0;i<list.size();i++){
            bw.write(list.get(i)+"\n");//0表示空闲
            bw.flush();
        }
        bw.close();
        return true;
    }

    //设置一个坏磁盘，用来演示磁盘恢复功能
    public boolean setDisc(int num){
        if(num<Dstage.length&&num>=0){
            Dstage[num]=1;
            return true;
        }
        else{
            return false;
        }
    }
    public boolean wfile(String content,String num) throws IOException {
        String Dnum="Disc"+(num.split(" "))[0]+".txt";//磁盘编号
        int Bnum=Integer.parseInt((num.split(" "))[1]);//磁盘内部编号
        //    System.out.println(Dnum+" "+Bnum+" "+content);
        //创建RandomAccessFile对象并向文件写入内容
        RandomAccessFile rf = new RandomAccessFile(Dir+"\\"+Dnum,"rw");
        int begin=Bnum*raid.StripeDepth;
        rf.seek(begin);
        rf.writeBytes(content);
        rf.close();
        return true;
    }
    public String rfile(String num) throws IOException {
        String Dnum="Disc"+(num.split(" "))[0]+".txt";//磁盘编号
        int Bnum=Integer.parseInt((num.split(" "))[1]);//磁盘内部编号
        //  System.out.println("\n随机读取一段文件内容：");
        // 打开一个随机访问文件流，按只读方式
        RandomAccessFile randomFile = new RandomAccessFile(Dir+"\\"+Dnum, "r");
        // 文件长度，字节数
        long fileLength = randomFile.length();
        // 读文件的起始位置
        int beginIndex =Bnum*raid.StripeDepth;
        //将读文件的开始位置移到beginIndex位置。
        randomFile.seek(beginIndex);
        byte[] bytes = new byte[raid.StripeDepth];
        int byteread = 0;
        //一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
        //将一次读取的字节数赋给byteread
        if ((byteread = randomFile.read(bytes)) != -1){
            //  System.out.write(bytes, 0, byteread);
        }
        String bistr="";
        for(int i=0;i<bytes.length;i++){
            bistr+=bytes[i]-48;
        }
        return bistr;
    }
    //查找磁盘空闲位置，返回空闲编号数组
    public int[][] seekDisc(int num) throws IOException {
        int [][]Dseek=new int[num][2];
        //  System.out.println(num);
        int j=0,i=0;
        for(;i<DMList.size()&&j<num;i++){
            String line=DMList.get(i);
            String []splitLine=line.split(" ");//以空格分离
            if(splitLine[2].contentEquals("0")){
                Dseek[j][0]= parseInt(splitLine[0]);
                Dseek[j][1]=parseInt(splitLine[1]);
                j++;
            }
        }
        if(j<num){
            //"没有足够空间"
            return new int[][]{{-1, -1}};
        }
        return Dseek;
    }
}
