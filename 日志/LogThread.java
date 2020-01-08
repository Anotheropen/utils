package com.log;

import com.conf.Config;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuben on 2019/9/9.
 */
public class LogThread {

    private static ExecutorService TASK_POOL = null;

    static final String SEPARATOR = File.separator;

    static FileWriter UPDATE_WR = null; //更新数据日志

    static FileWriter NO_UPDATE_WR_WRONG = null; //未更新数据地址日志

    static FileWriter NO_UPDATE_WR_ERROR = null; //未更新数据地址日志

    static  final  String LOGDIR = Config.getDamageDir();

    public static void load(){
        if(StringUtils.isNotBlank(LOGDIR)){
            try {
                File file = new File(LOGDIR);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File update = new File(file + SEPARATOR + "filePath.txt");
                if(!update.exists()){
                    update.createNewFile();
                }
            }catch(Exception e){
                System.out.println("日志文件加载错误:" + e.getMessage());
                System.exit(0);
            }
            TASK_POOL = Executors.newFixedThreadPool(100 * 3);
        }
    }

    public static void write(String content){
        if(TASK_POOL != null && !TASK_POOL.isShutdown()){
            File update = new File(LOGDIR + SEPARATOR + "filePath.txt");
            if(!update.exists()){
                try {
                    update.createNewFile();
                }catch (IOException e){
                    System.out.println("创建文件失败:" + e.getMessage());
                }
            }
            FileWriter fw=null;

            try {
                fw = new FileWriter(update,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(fw != null){
                TASK_POOL.execute(new LogWriter(fw, content));
            }

        }
    }

    static final AtomicInteger count = new AtomicInteger(0);
    static final AtomicInteger count1 = new AtomicInteger(0);

    public static void dir_write(String path,String result){

        if(TASK_POOL != null && !TASK_POOL.isShutdown()){
            if("wrong".equalsIgnoreCase(result)){
                int c = count.getAndIncrement();
                File update = new File(LOGDIR + SEPARATOR + "wrong" + (c/200000) + ".log");
                if(c % 200000 == 0){
                    if(!update.exists()){
                        try {
                            update.createNewFile();

                        }catch (IOException e){
                            System.out.println("创建文件失败:" + e.getMessage());
                        }
                    }
                }
                FileWriter fw=null;

                try {
                    fw = new FileWriter(update,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(fw != null){
                    TASK_POOL.execute(new LogWriter(fw, path));
                }
            }else if("error".equalsIgnoreCase(result)){
                int d = count1.getAndIncrement();
                File update = new File(LOGDIR + SEPARATOR + "error" + (d/200000) + ".log");
                if(d % 200000 == 0){
                    if(!update.exists()){
                        try {
                            update.createNewFile();

                        }catch (IOException e){
                            System.out.println("创建文件失败:" + e.getMessage());
                        }
                    }
                }
                FileWriter fw=null;

                try {
                    fw = new FileWriter(update,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(fw != null){
                    TASK_POOL.execute(new LogWriter(fw, path));
                }
            }

        }

    }

    public static void close(){
        if(TASK_POOL != null && !TASK_POOL.isShutdown()){
            TASK_POOL.shutdown();
        }
    }

}
