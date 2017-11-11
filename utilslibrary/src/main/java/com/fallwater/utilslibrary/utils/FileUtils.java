package com.fallwater.utilslibrary.utils;


import com.fallwater.utilslibrary.init.UtilLib;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    private FileUtils() {/*工具类构建方法私有化*/}

    public static boolean writeFile(Context context, String filename, String content,
            boolean append) {
        boolean isSuccess = false;
        OutputStreamWriter streamWriter = null;
        try {
            FileOutputStream statStream = context.openFileOutput(filename, Context.MODE_APPEND);
            streamWriter = new OutputStreamWriter(statStream);
            streamWriter.write(content);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(streamWriter);
        }
        return isSuccess;
    }

    public static boolean writeFile(String filename, String content, boolean append) {
        boolean isSuccess = false;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(filename, append));
            bufferedWriter.write(content);
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedWriter);
        }
        return isSuccess;
    }

    public static boolean deleteFile(String filename) {
        return new File(filename).delete();
    }

    public static void deleteFileByDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                file.delete();
            }
        }
    }

    public static boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    public static String readFile(String filename) {
        File file = new File(filename);
        BufferedReader bufferedReader = null;
        String str = null;
        try {
            if (file.exists()) {
                bufferedReader = new BufferedReader(new FileReader(filename));
                str = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(bufferedReader);
        }
        return str;
    }

    public static StringBuilder readFile(File file, String charsetName) {
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeIO(reader);
        }
    }

    public static void copyFile(InputStream in, OutputStream out) {
        try {
            byte[] b = new byte[2 * 1024 * 1024]; //2M memory
            int len = -1;
            while ((len = in.read(b)) > 0) {
                out.write(b, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(in, out);
        }
    }

    public static void copyFileFast(File in, File out) {
        FileChannel fileIn = null;
        FileChannel fileOut = null;
        try {
            fileIn = new FileInputStream(in).getChannel();
            fileOut = new FileOutputStream(out).getChannel();
            fileIn.transferTo(0, fileIn.size(), fileOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(fileIn, fileOut);
        }
    }


    public static void Stream2File(InputStream is, File file) {
        byte[] b = new byte[1024];
        int len;
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            while ((len = is.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(is, os);
        }
    }

    public static boolean createFolder(String filePath) {
        return createFolder(filePath, false);
    }

    public static boolean createFolder(String filePath, boolean recreate) {
        String folderName = getFolderName(filePath);
        if (folderName == null || folderName.length() == 0 || folderName.trim().length() == 0) {
            return false;
        }
        File folder = new File(folderName);
        if (folder.exists()) {
            if (recreate) {
                deleteFile(folderName);
                return folder.mkdirs();
            } else {
                return true;
            }
        } else {
            return folder.mkdirs();
        }
    }

    public static String getFileName(String filePath) {
        if (isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static long getFileSize(String filepath) {
        if (isEmpty(filepath)) {
            return -1;
        }
        File file = new File(filepath);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }

    public static boolean rename(String filepath, String newName) {
        File file = new File(filepath);
        return file.exists() && file.renameTo(new File(newName));
    }

    public static boolean rename(File rawFile, String newName) {
        if (rawFile.exists()) {
            return rawFile.renameTo(new File(newName));
        }
        return false;
    }

    public static String getFolderName(String filePath) {
        if (filePath == null || filePath.length() == 0 || filePath.trim().length() == 0) {
            return filePath;
        }
        int filePos = filePath.lastIndexOf(File.separator);
        return (filePos == -1) ? "" : filePath.substring(0, filePos);
    }

    public static ArrayList<File> getFilesArray(String path) {
        File file = new File(path);
        File files[] = file.listFiles();
        ArrayList<File> listFile = new ArrayList<File>();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    listFile.add(files[i]);
                }
                if (files[i].isDirectory()) {
                    listFile.addAll(getFilesArray(files[i].toString()));
                }
            }
        }
        return listFile;
    }

    public static boolean deleteFiles(String folder) {
        if (folder == null || folder.length() == 0 || folder.trim().length() == 0) {
            return true;
        }
        File file = new File(folder);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public static boolean isSDCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getExtraPath(String folder) {
        String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + folder;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdir();
        }
        return storagePath;
    }

    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static InputStream getInputStream(FileInputStream fileInput) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = -1;
        InputStream inputStream = null;
        try {
            while ((n = fileInput.read(buffer)) != -1) {
                baos.write(buffer, 0, n);
            }
            byte[] byteArray = baos.toByteArray();
            inputStream = new ByteArrayInputStream(byteArray);
            return inputStream;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将文件转换成Gzip
     *
     * @param delete true 转换成功后删除之前文件
     */
    public static boolean toGzipFile(File file, File toGzipFile, boolean delete) {
        boolean gzipSuc = false;
        FileInputStream fileIn;
        BufferedInputStream fileBuffer = null;
        BufferedOutputStream bufferOut;
        GZIPOutputStream gzipOut = null;
        FileOutputStream fileOut;
        try {
            fileIn = new FileInputStream(file);
            fileOut = new FileOutputStream(toGzipFile);
            fileBuffer = new BufferedInputStream(fileIn);
            bufferOut = new BufferedOutputStream(fileOut);
            gzipOut = new GZIPOutputStream(bufferOut);
            FileUtils.copyFile(fileBuffer, gzipOut);
            gzipSuc = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(fileBuffer);
            FileUtils.closeIO(gzipOut);
            if (gzipSuc && delete) {
                file.delete();
            }
        }
        return gzipSuc;
    }

    public static File[] getFileList(String folder, FileFilter fileFilter, boolean sort) {
        File[] files = getFileList(folder, fileFilter);
        if (files != null && sort) {
            sort(files);
        }
        return files;
    }

    /**
     * 获取文件列表
     */
    public static File[] getFileList(String folder, FileFilter fileFilter) {
        File fd = new File(folder);
        if (fd.exists()) {
            return fd.listFiles(fileFilter);
        }
        return null;
    }

    public static void sort(File[] list) {
        try {
            Arrays.sort(list, (lhs, rhs) -> {
                if (lhs.lastModified() == rhs.lastModified()) {
                    return 0;
                } else if (lhs.lastModified() > rhs.lastModified()) {
                    return -1;
                }
                return 1;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取akulaku数据保存的文件夹
     */
    public static String getAkulakuDataFolder() {
        File file = UtilLib.getsDefaultInstance().getContext().getExternalFilesDir(null);
        if (file == null) {
            file = UtilLib.getsDefaultInstance().getContext().getFilesDir();
        }
        return file.getAbsolutePath();
    }

    private final static String CACHE_FOLDER_FACE = "face";

    public static String getImageCacheFolder() {
        File cacheFile = UtilLib.getsDefaultInstance().getContext().getExternalCacheDir();
        if (cacheFile == null) {
            cacheFile = UtilLib.getsDefaultInstance().getContext().getCacheDir();
        }
        if (cacheFile == null) {
            return null;
        }
        return cacheFile.getAbsolutePath() + "/" + CACHE_FOLDER_FACE;
    }

    public static String getImageCachePath(String fileName) {
        String folder = getImageCacheFolder();
        if (TextUtils.isEmpty(folder)) {
            return null;
        }
        return folder + "/" + fileName;
    }
}
