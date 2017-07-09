package com.fanhong.cn.bluetooth.util;

import java.io.Serializable;

/**
 * 用于传输的数据类
 * @author liujian
 *
 */
public class TransmitBean implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String msg = "";
	private String filename = "";
	private String filepath = "";
	private String uppercent = "";
	private String tspeed = "";
	private boolean showflag ;
	private byte[] file ;
	private boolean backflag;
	private boolean commmandflag;       //只发送指令
	private byte[] com;
//	private  BluetoothCommunThread communThread;	


	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getUppercent() {
		return uppercent;
	}

	public void setUppercent(String uppercent) {
		this.uppercent = uppercent;
	}

	public String getTspeed() {
		return tspeed;
	}

	public void setTspeed(String tspeed) {
		this.tspeed = tspeed;
	}

	public boolean isShowflag() {
		return showflag;
	}

	public void setShowflag(boolean showflag) {
		this.showflag = showflag;
	}

	public boolean isbackflag() {
		return backflag;
	}

	public void setbackflag(boolean backflag) {
		this.backflag = backflag;
	}

	public boolean iscommmandflag() {
		return commmandflag;
	}

	public void setcommmandflag(boolean commmandflag) {
		this.commmandflag = commmandflag;
	}

	public void setCom(byte[] com) {
		this.com = com;
	}

	public byte[] getCom() {
		return this.com;
	}


//	public BluetoothCommunThread getCommunThread() {
//		return communThread;
//	}
//
//	public void setCommunThread(BluetoothCommunThread communThread) {
//		this.communThread = communThread;
//	}


}
