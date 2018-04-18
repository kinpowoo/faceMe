
package com.jinhanyu.jack.faceme.tool;


import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class ACache {

    private static ReadFinishListener readFinishlistener;
    private static SaveFinishListener saveFinishListener;
    private static ACache instance;
    static String rootDir = "";

    public void setReadFinishListener(ReadFinishListener listener) {
        this.readFinishlistener = listener;
    }

    public void setSaveFinishListener(SaveFinishListener listener) {
        this.saveFinishListener = listener;
    }


    public static ACache with(Context context) {
        if (instance == null) {
            instance = new ACache();
        }

        if (ConstantFunc.isEmpty(rootDir)) {

            File root = new File(context.getFilesDir() + File.separator + "final");

            /**
             File file = context.getFilesDir();
             if(file.isFile()){
             ConstantFunc.hint("ACache file :",file.getAbsolutePath());
             }
             if(file.isDirectory()){
             for(File sub: file.listFiles()){
             ConstantFunc.hint("ACache dir :",sub.getAbsolutePath());
             }
             }
             */

            if (!root.exists()) {
                root.mkdir();
            }
            rootDir = root.getAbsolutePath();
        }
        return instance;
    }


    public void saveObject(Object obj, String fileName) {
        this.saveObject(obj, fileName, null);
    }

    public void saveObject(Object obj, String fileName, SaveFinishListener listener) {
        if (obj == null || ConstantFunc.isEmpty(fileName)) {
            ConstantFunc.hint("ACache:", "object is null or fileName is empty");
            return;
        }

        File file = getFile(fileName);

        byte[] buffer = objToByteArray(obj);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(buffer);
            os.flush();
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (listener != null) {
                    listener.saveFinish(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public Object readObject(final String fileName) {
        if (ConstantFunc.isEmpty(fileName)) {
            return null;
        }
        File file = getFile(fileName);
        if (file.length() == 0) {
            return null;
        }
        byte[] buffer;
        Object obj = null;

        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            buffer = new byte[is.available()];
            is.read(buffer);
            obj = byteArrayToObject(buffer);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public Object readObject(final String fileName, final ObjectLoadedListener listener) {
        if (ConstantFunc.isEmpty(fileName)) {
            return null;
        }
        final File file = getFile(fileName);
        if (file.length() == 0) {
            return null;
        }

        byte[] buffer;
        Object obj = null;

        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            buffer = new byte[is.available()];
            is.read(buffer);
            obj = byteArrayToObject(buffer);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (listener != null) {
                    listener.objectLoaded(obj);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public void removeObject(String filename) {
        removeObject(filename, null);
    }

    public void removeObject(String filename, RemoveFinishListener removeFinishListener) {
        if (ConstantFunc.isEmpty(filename)) {
            return;
        }
        File file = getFile(filename);
        if (file != null) {
            file.delete();
        }
        if (removeFinishListener != null) {
            removeFinishListener.removeFinish();
        }
    }


    public static void clearCache(Context context) {
        File root = new File(context.getFilesDir() + File.separator + "final");
        if (root.exists()) {
            if (root.isDirectory()) {
                File[] files = root.listFiles();
                for (File file : files) {
                    if (file.getName().equals("user_info.txt")) {
                        continue;
                    } else {
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }


    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    private byte[] objToByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    private Object byteArrayToObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }


    public static void saveToFile(Context context, String result, String filename, SaveFinishListener listener) {
        if (ConstantFunc.isEmpty(result) || ConstantFunc.isEmpty(filename)) {
            ConstantFunc.hint("ACache:", "要存入的字符串为空或文件名为空");
            return;
        }
        saveFinishListener = listener;
        if (isSdCardExist()) {
            File res = new File(Environment.getExternalStorageDirectory().getPath()+ File.separator+"Android"+ File.separator+"data"+ File.separator+context.getPackageName()+ File.separator+"files"+ File.separator + filename);

           // File res = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() +File.separator + filename);
            BufferedOutputStream bos = null;
            FileOutputStream fos = null;
            try {
                if (!res.exists()) {
                    res.createNewFile();
                } else {
                    res.delete();
                    res.createNewFile();
                }
                fos = new FileOutputStream(res);
                bos = new BufferedOutputStream(fos);
                byte[] buffer = result.getBytes();
                bos.write(buffer, 0, buffer.length);
                bos.flush();
                if (listener != null) {
                    listener.saveFinish(res);
                }
            } catch (IOException e) {
                ConstantFunc.hint("ConstantFunc", e.getMessage());
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }


    public static void readFromFile(Context context, String filename, ReadFinishListener listener) {
        readFinishlistener = listener;
        if (isSdCardExist()) {
            File file = new File(Environment.getExternalStorageDirectory().getPath()+ File.separator+"Android"+ File.separator+"data"+ File.separator+context.getPackageName()+ File.separator+"files"+ File.separator + filename);
            if (file.exists()) {

                InputStream is = null;
                ByteArrayOutputStream bos = null;
                try {
                    is = new FileInputStream(file);
                    bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    bos.write(buffer);
                    String content = bos.toString();
                    if (readFinishlistener != null) {
                        readFinishlistener.readFinish(content);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (bos != null) {
                            bos.close();
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            }
        }
    }


    private File getFile(String filename) {
        File file = new File(rootDir + File.separator + filename + ".txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        return file;
    }



    public static boolean isFileExist(String filename){
        File file = new File(rootDir + File.separator + filename + ".txt");
        return  file.exists();
    }

    public static boolean isSdCardExist() {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {//判断是否已经挂载
            return true;
        }
        return false;
    }


    public synchronized static void writeByteToFile(final byte[] buffer, final String folder, final String fileName, final boolean append, final boolean autoLine) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean sdCardExist = Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED);
                String folderPath = "";
                if (sdCardExist) {
                    //TextUtils为android自带的帮助类
                    if (TextUtils.isEmpty(folder)) {
                        //如果folder为空，则直接保存在sd卡的根目录
                        folderPath = Environment.getExternalStorageDirectory()
                                + File.separator;
                    } else {
                        folderPath = Environment.getExternalStorageDirectory()
                                + File.separator + folder + File.separator;
                    }
                } else {
                    return;
                }

                File fileDir = new File(folderPath);
                if (!fileDir.exists()) {
                    if (!fileDir.mkdirs()) {
                        return;
                    }
                }
                File file;
                //判断文件名是否为空
                if (TextUtils.isEmpty(fileName)) {
                    file = new File(folderPath + "app_log.txt");
                } else {
                    file = new File(folderPath + fileName);
                }
                RandomAccessFile raf = null;
                FileOutputStream out = null;
                try {
                    if (append) {
                        //如果为追加则在原来的基础上继续写文件
                        raf = new RandomAccessFile(file, "rw");
                        raf.seek(file.length());
                        raf.write(buffer);
                        if (autoLine) {
                            raf.write("\n".getBytes());
                        }
                    } else {
                        //重写文件，覆盖掉原来的数据
                        out = new FileOutputStream(file);
                        out.write(buffer);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (raf != null) {
                            raf.close();
                        }
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public interface ReadFinishListener {
        public void readFinish(String str);
    }

    public interface ObjectLoadedListener {
        public void objectLoaded(Object obj);
    }

    public interface SaveFinishListener {
        public void saveFinish(File path);
    }

    public interface RemoveFinishListener {
        public void removeFinish();
    }
}