import Manage.*;
import function.Q;

public class Main {
    public static void main(String []args) throws Exception{
   /*     String fcontent="这是一个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage0 manage0 = new Manage0(3, 500, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID0");
        manage0.write("test",fcontent);
        String rfile=manage0.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        manage0.delete("test");
        fcontent="这是第二个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可1";
        manage0.write("test2",fcontent);
        rfile=manage0.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
/*
        String fcontent="这是一个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage1 manage1 = new Manage1(4, 500, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID1");
        manage1.write("test",fcontent);
        String rfile=manage1.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        fcontent="这是第二个测试：";
        manage1.write("test2",fcontent);
        rfile=manage1.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage1.delete("test");
        fcontent="这是第三个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        manage1.write("test3",fcontent);
        rfile=manage1.read("test3");
        System.out.println("读取文件结果：\n"+rfile);
/*
        String fcontent="这是一个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage10 manage10 = new Manage10(4, 500, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID10");
        manage10.write("test",fcontent);
        String rfile= manage10.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        fcontent="这是第二个测试：";
        manage10.write("test2",fcontent);
        rfile= manage10.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage10.delete("test");
        fcontent="这是第三个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        manage10.write("test3",fcontent);
        rfile= manage10.read("test3");
        System.out.println("读取文件结果：\n"+rfile);

/*        String fcontent="这是一个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage01 manage01 = new Manage01(4, 500, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID01");
        manage01.write("test",fcontent);
        String rfile= manage01.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        fcontent="这是第二个测试：";
        manage01.write("test2",fcontent);
        rfile= manage01.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage01.delete("test");
        fcontent="这是第三个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        manage01.write("test3",fcontent);
        rfile= manage01.read("test3");
        System.out.println("读取文件结果：\n"+rfile);
*/
        String fcontent="1234567890这是一个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage manage3 = new Manage3(4, 500, 9,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID3");
        manage3.write("test",fcontent);
        manage3.setDisc(1);
        String rfile= manage3.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        fcontent="abcdefg这是第二个测试：";
        manage3.write("test2",fcontent);
        manage3.setDisc(2);
        rfile= manage3.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage3.delete("test");
        fcontent="这是第三个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。1234567890";
        manage3.write("test3",fcontent);
        manage3.setDisc(3);
        rfile= manage3.read("test3");
        System.out.println("读取文件结果：\n"+rfile);
/*
        String fcontent="1234567890凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。";
        Manage5 manage5 = new Manage5(4, 500, 7,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID5");
        manage5.write("test",fcontent);
        manage5.setDisc(1);
        String rfile= manage5.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        fcontent="abcdefg这是第二个测试：";
        manage5.write("test2",fcontent);
        manage5.setDisc(2);
        rfile= manage5.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage5.delete("test");
        fcontent="这是第三个测试：凯撒密码：将明文中的每个字母向右移动k位得到密文，解密时将密文的每个字母向左移动k位即可。1234567890";
        manage5.write("test3",fcontent);
        manage5.setDisc(3);
        rfile= manage5.read("test3");
        System.out.println("读取文件结果：\n"+rfile);


       for(int i=1;i<255;i++){
            for(int j=1;j<255;j++){
                int m= Q.multip(i,j);
                System.out.println(i+"*"+j+"="+m);
                System.out.println(m+"/"+i+"="+Q.divid(m,i));
            }
        }

        String fcontent="123456789";
        Manage6 manage6 = new Manage6(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID6");
        manage6.write("test",fcontent);
        manage6.setDisc(0);
        manage6.setDisc(1);
        String rfile= manage6.read("test");
        System.out.println("读取文件结果：\n"+rfile);
        manage6.delete("test");
        fcontent="abcde12345";
        manage6.write("test2",fcontent);
    //    manage6.setDisc(2);
        manage6.setDisc(3);
        rfile= manage6.read("test2");
        System.out.println("读取文件结果：\n"+rfile);
        manage6.delete("test2");
        fcontent="1234567890";
        manage6.write("test3",fcontent);
        manage6.setDisc(0);
        manage6.setDisc(3);
        rfile= manage6.read("test3");
        System.out.println("读取文件结果：\n"+rfile);
*/
    }
}
