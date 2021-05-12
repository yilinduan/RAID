import Manage.*;
import function.Transform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxBuilder;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;


public class RAIDController {

    @FXML
    public ChoiceBox RAIDnum;
    @FXML
    public Button newRAID;
    @FXML
    public Button begin;
    @FXML
    public Button fread;
    @FXML
    public Button fwrite;
    @FXML
    public Button fdelete;
    @FXML
    public Button setBad;
    @FXML
    public Button check;
    @FXML
    public TextField inputFName;
    @FXML
    public TextField inputFContent;
    @FXML
    public TextField Badnum;
    @FXML
    public TextField outputFContent;

    @FXML
    //初始化
    private void Begin()
    {
        new Thread(new Runnable() {
            // @Override
            public void run() {
                RAIDnum.setItems(FXCollections.observableArrayList("0","1","3","5","6","01","10"));
            }
        }).start();
    }

    Manage manage;
    @FXML
    //创建RAID
    private void NewRAID()
    {
        new Thread(new Runnable() {
           // @Override
            public void run() {
                int num=Integer.parseInt(RAIDnum.getSelectionModel().getSelectedItem().toString());
                System.out.println(num);
                switch (num){
                    case 0:
                        try {
                            manage=new Manage0(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID0");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            manage=new Manage1(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID1");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            manage=new Manage3(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID3");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            manage=new Manage5(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID5");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        try {
                            manage=new Manage6(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID6");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 10:
                        try {
                            manage=new Manage10(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID10");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            manage=new Manage01(4, 1000, 8,"D:\\wenjian\\课程\\网络存储技术\\RaidManage\\Disc\\RAID01");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }).start();
    }

    @FXML
    //设置坏磁盘
    private void SetBad()
    {
        new Thread(new Runnable() {
          //  @Override
            public void run() {
                if(manage.setDisc(Integer.parseInt(Badnum.getText()))){
                    outputFContent.setText("设置成功");
                }
                else {
                    outputFContent.setText("修复失败");
                }
            }
        }).start();
    }
    @FXML
    //设置坏磁盘
    private void Check()
    {
        new Thread(new Runnable() {
            //  @Override
            public void run() {
                try {
                    if(manage.dCheck()){
                        outputFContent.setText("修复成功");
                    }
                    else {
                        outputFContent.setText("修复失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @FXML
    //读文件
    private void ReadFile()
    {
        new Thread(new Runnable() {
            //  @Override
            public void run() {
                String fname=inputFName.getText();
                try {
                    outputFContent.setText(manage.read(fname));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @FXML
    //写文件
    private void WriteFile()
    {
        new Thread(new Runnable() {
            //  @Override
            public void run() {
                String fname=inputFName.getText();
                String fcontent=inputFContent.getText();
                try {
                    if(manage.write(fname,fcontent)){
                        outputFContent.setText("写入成功");
                    }
                    else {
                        outputFContent.setText("写入失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @FXML
    //删除文件
    private void DeleteFile()
    {
        new Thread(new Runnable() {
            //  @Override
            public void run() {
                String fname=inputFName.getText();
                try {
                    if(manage.delete(fname)){
                        outputFContent.setText("删除成功");
                    }
                    else {
                        outputFContent.setText("删除失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
