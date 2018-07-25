package com.youmai.project.bean;

import java.io.Serializable;

/**
 * 版本更新的bean
 */
public class DownLoad implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//下载链接
	private String downPath;
	
	//保存链接
	private String savePath;
	
	//标题
	private String title;
	
	//文件名称
	private String fileName;
	
	//标示
	private int notifyId;

	public String getDownPath() {
		return downPath;
	}

	public void setDownPath(String downPath) {
		this.downPath = downPath;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public int getNotifyId() {
		return notifyId;
	}

	public void setNotifyId(int notifyId) {
		this.notifyId = notifyId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
