package com.stormdzh.androidcustomview.utils.mediacamera;

import android.content.Context;
import android.os.Environment;
import java.io.File;

public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */
    private final static String FOLDER_NAME = "/ffmpeg";

	public final static String IMAGE_NAME = "/cache";

	public FileUtils(Context context){
		mDataRootPath = context.getCacheDir().getPath();
		makeAppDir();
	}
	
	public String makeAppDir(){
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		path = path + IMAGE_NAME;
		folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		return path;
	}

	/**
	 * 获取储存Image的目录
	 * @return
	 */
	public String getStorageDirectory(){
		String localPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
		File folderFile = new File(localPath);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		return localPath;
	}

	public String getMediaVideoPath(){
		String directory = getStorageDirectory();
		directory += "/video";
		File file = new File(directory);
		if(!file.exists()){
			file.mkdir();
		}
		return directory;
	}
	
	/**
	 * 删除文件
	 */
	public void deleteFile(String deletePath,String videoPath) {
		File file = new File(deletePath);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if(f.isDirectory()){
					if(f.listFiles().length==0){
						f.delete();
					}else{
						deleteFile(f.getAbsolutePath(),videoPath);
					}
				}else if(!f.getAbsolutePath().equals(videoPath)){
					f.delete();
				}
			}
		}
	}

}
