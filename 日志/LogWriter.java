package com.log;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by liuben on 2019/9/9.
 */
public class LogWriter implements Runnable{

    FileWriter fw = null;

    String content = null;

    public LogWriter(FileWriter fw, String content){
        this.fw = fw;
        this.content = content;
    }

    public void run(){
        try {
            fw.write(content + "\n");
            fw.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}