package com.xp.media.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author: wangxp
 * @date: 2016-09-29
 * @Desc: 文件管理 工具类
 */
public class FileUtil {
    public static final String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();

    private static final String FILE_PATH = "/file/"; //文件目录
    private static final String MEDIA_PATH = "/media/"; //音视频频目录
    private static final String IMAGE_PATH = "/image/"; //图片目录
    private static final String PHOTO_PATH = "/photo/"; //头像目录

    /**
     * 获取 目录地址
     *
     * @return
     */
    public static File getFileDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File sdFile = context.getExternalCacheDir();
            if (!sdFile.exists()) {
                sdFile.mkdirs();
            }
//            String path = sdFile.getAbsolutePath();
//            LogUtil.t("SD卡目录 = " + path);
            return sdFile;
        } else {
            File cacheFile = context.getCacheDir();
//            LogUtil.t("默认缓存目录 = " + strCacheDir);
            return cacheFile;
        }
    }

    /**
     * 获取文件路径
     *
     * @return
     */
    public static String getFilePath(Context context) {
        String path = getFileDir(context).getAbsolutePath();
        path += FILE_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取音频文件路径
     *
     * @return
     */
    public static String getMediaPath(Context context) {
        String path = getFileDir(context).getAbsolutePath();
        path += MEDIA_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取图片文件路径
     *
     * @return
     */
    public static String getImagePath(Context context) {
        String path = getFileDir(context).getAbsolutePath();
        path += IMAGE_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取图片文件路径
     *
     * @return
     */
    public static String getPhotoPath(Context context) {
        String path = getFileDir(context).getAbsolutePath();
        path += PHOTO_PATH;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 读取目录文件  并按照时间排序
     *
     * @param dirname 目录名称
     * @return list集合
     */
    public static List<String> getFiles(String dirname) throws Exception {
        List file_names = null;
        File dir = new File(dirname);
        if (dir.exists()) {
            file_names = new ArrayList();
            File[] files = dir.listFiles();
            //排序
            Arrays.sort(files, new CompratorByLastModified());
            for (int i = 0; i < files.length; i++) {
//                LogUtil.t("文件名字 = " + files[i].getName());
                file_names.add(files[i].getName());
            }
        } else {
            Log.d("Test", "该目录没有任何文件信息！");
        }
        return file_names;
    }

    /**
     * 进行文件排序时间
     *
     * @author 谈情
     */
    private static class CompratorByLastModified implements Comparator<File> {
        public int compare(File f1, File f2) {
            long diff = f2.lastModified() - f1.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;

    }

    /**
     * 车辆上传图片名称 固定  index 是位置标识
     *
     * @param index 车辆图片ID
     * @return
     */
    public static String getPhotoFilePath(Context context, int index) {
        String filePath = getImagePath(context) + "IMG_" + index + "_" + System.currentTimeMillis() + ".jpg";
        return filePath;
    }

    /**
     * 车辆上传图片名称
     *
     * @param startName 车辆图片开头名字
     * @return
     */
    public static String getPhotoFilePath(Context context, String startName) {
        String filePath = getImagePath(context) + startName + System.currentTimeMillis() + ".jpg";
        return filePath;
    }

    public static String getRecordViewName(Context context) {
        String filePath = getMediaPath(context) + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        return filePath;
    }


    /**
     * 缓存 json 数据
     *
     * @param context
     * @param fileName   文件名称
     * @param strContent 内容
     * @return
     */
    public static boolean setCacheTextData(Context context, String fileName, String strContent) {
        FileOutputStream os = null;
        OutputStreamWriter outWriter = null;
        try {
            String file = getFilePath(context) + "/" + fileName;
            File newsFile = new File(file);
            if (newsFile.exists()) {
                newsFile.delete();
            }
            os = new FileOutputStream(newsFile);
            outWriter = new OutputStreamWriter(os);
            outWriter.write(strContent);
            outWriter.flush();
            return true;
        } catch (Exception ex) {
            Log.d("Test", "setCacheTextData" + ex);
            return false;
        } finally {
            try {
                if (outWriter != null) {
                    outWriter.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCacheTextData(Context context, String fileName) {
        String strContent = null;
        FileInputStream ins = null;
        InputStreamReader inReader = null;
        try {
            String strDir = getFilePath(context) + "/" + fileName;
            File newsFile = new File(strDir);
            if (!newsFile.exists()) return null;
            StringBuffer sBuf = new StringBuffer();
            ins = new FileInputStream(newsFile);
            inReader = new InputStreamReader(ins);
            char[] buf = new char[256];
            int len = -1;
            while ((len = inReader.read(buf, 0, 256)) != -1) {
                sBuf.append(buf, 0, len);
            }
            strContent = sBuf.toString();
        } catch (Exception ex) {
            Log.e("FileUtil", "getCacheTextData", ex);
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (inReader != null) {
                    inReader.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return strContent;
    }
}
