package com.fanhong.cn.bluetooth.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.util.Calendar;

import com.fanhong.cn.util.CommonUtility;


import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 蓝牙通讯线程
 * @author liujian
 *
 */
public class BluetoothClientCommun {
	private Handler serviceHandler;		//与Service通信的Handler
	public BluetoothSocket socket;
	public DataInputStream inStream;		//对象输入流
	public DataOutputStream outStream;	//对象输出流
	//	public volatile boolean isRun = true;	//运行标志位
	private long downbl;

	public void close(){
		//关闭流
		if (inStream != null) {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				Log.v("调试" , "socket已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 构造函数
	 * @param handler 用于接收消息
	 * @param socket
	 */
	public BluetoothClientCommun(Handler handler, BluetoothSocket socket) {
		this.serviceHandler = handler;
		this.socket = socket;
		try {
			this.inStream= new DataInputStream(this.socket.getInputStream());
			this.outStream= new DataOutputStream(this.socket.getOutputStream());
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
		}
	}

	public void read() {
		try {
			Log.i("调试" , "begin read()");
			while(true)
			{
				TransmitBean transmit = new TransmitBean();
				long totalLen = inStream.readLong();//总长度
				byte type = inStream.readByte();//类型
				if(type==1){//文本类型
					try {
						byte len = inStream.readByte();//消息长度
				/*	byte[] ml = new byte[len];
					int size=0;
					int receivelen=0;
					while (receivelen <len){
						size=inStream.read(ml,0,ml.length);
						receivelen+=size;
					}*/

						ByteArrayOutputStream boutput = new ByteArrayOutputStream();
						byte[] ml = new byte[1024];
						int size=0;
						int receivelen=0;
						while (receivelen <len){
							size=inStream.read(ml,0,ml.length);
							receivelen+=size;
							boutput.write(ml, 0, size);
						}
						byte[] buf = boutput.toByteArray();
						boutput.close();

						String msg = new String(buf,"GBK");
						Log.i("调试" , "msg:"+msg);
						transmit.setMsg(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(type==2){//文件类型
					try {
						byte len = inStream.readByte();//文件名长度
						byte[] fn = new byte[len];
						inStream.read(fn);//读取文件名
						String filename = new String(fn,"GBK");
						Log.i("调试" , "filename:"+filename);
						transmit.setFilename(filename);
						Log.e("hu","接受文件名 filename="+filename);
						long  datalength = totalLen-1-4-1-fn.length;//文件数据
						String savePath = CommonUtility.recordFilePath + CommonUtility.timefileName;

						boolean isWrite = filename.equals(CommonUtility.timefileName);

						File fPath = new File(CommonUtility.recordFilePath);
						//判断文件夹是否存在,如果不存在则创建文件夹
						if (!fPath.exists()) {
							fPath.mkdirs();
						}


						Log.i(CommonUtility.TAG, "BluetoothCommunThreads接收文件名" + savePath);
						transmit.setFilepath(savePath);
						FileOutputStream file=new FileOutputStream(savePath, false);
						byte[] buffer = new byte[1024*1024];
						int size = -1;
						long receivelen=0;
						int i=0;
						float tspeed=0;
						long time1=Calendar.getInstance().getTimeInMillis();
						while (receivelen <datalength){
							size=inStream.read(buffer);
							if(isWrite)
								file.write(buffer, 0 ,size);
							receivelen+=size;
							i++;
							if(i%10==0){
								long time2=Calendar.getInstance().getTimeInMillis();
								tspeed=receivelen/(time2-time1)*1000/1024;
							}
							downbl = (receivelen * 100) / datalength;
							TransmitBean up = new TransmitBean();
							up.setUppercent(String.valueOf(downbl));
							up.setTspeed(String.valueOf(tspeed));
							if(i==1){
								up.setShowflag(true);
							}else{
								up.setShowflag(false);
							}
							Message msg = serviceHandler.obtainMessage();
							msg.what = BluetoothTools.FILE_RECIVE_PERCENT;
							msg.obj = up;
							msg.sendToTarget();
							Log.i("hu" , "文件发送进度 up="+up.getUppercent());
						}
						Log.i("hu" , "接收完成,receivelen:"+receivelen+" isWrite="+isWrite);
						file.flush();
						file.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				//发送成功读取到对象的消息，消息的obj参数为读取到的对象
				Message msg = serviceHandler.obtainMessage();
				msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
				msg.obj = transmit;
				msg.sendToTarget();

				TransmitBean transmit_s = new TransmitBean();
				if(type==1){
//				transmit_s.setFilename("mu.zip");
//				transmit_s.setFilepath(Environment.getExternalStorageDirectory().getPath() + "/" + "mu.zip");

					//此处如果需要显示发送进度，则需要在Runnable中调用write。
					transmit_s.setMsg("收到时间");
					write(transmit_s);
				}else{
					transmit_s.setMsg("收到文件");
					write(transmit_s);
				}
			}
			//String endflag = "EOF";
			//outStream.write(endflag.getBytes());    //发送报文通知客户端关闭SOCKET
			//	Log.i("调试" ,"发送EOF");
			//outStream.flush();
			//close();
		} catch (Exception ex) {
			Log.i("调试" , "通讯中断Exception:");
			Log.i("hu" , "蓝牙客服端连接中断");
			//发送通讯失败消息
			//serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			close();
		}
	}

	public void read2() {
		try {
			Log.i("调试" , "模拟探管程序开始！begin read()");
			while(true)
			{
				//TransmitBean transmit = new TransmitBean();
				byte[] ml = new byte[15];
				int size=0;
				int receivelen=0;
				while (receivelen <15){
					size=inStream.read(ml,0,ml.length);
					receivelen+=size;
					Log.e("hu","%11111%%%%%%%size="+size+" ml="+CalculateFunction.byteTo16hex(ml));
				}
				if(ml[0]==0x24 && ml[1]==0x43 && ml[2]==0x4d && ml[3]==0x44&& ml[14]==0x24)
				{
					if(ml[4] == CalculateFunction.intTobyte(0x5a) && ml[7] == 0x03) //收到读取探管文件指令
					{
						byte[] recv = backtgCommand();
						outStream.write(recv);
						outStream.flush();
					}else if(ml[4] == CalculateFunction.intTobyte(0x5b) && ml[7] == 0x04)
					{
						int frame = CalculateFunction.byte2Toint(ml[10], ml[11]);
						int all = CalculateFunction.byte2Toint(ml[8], ml[9]);
						byte[] buf = getFileByte(all,frame);
						outStream.write(buf);
						outStream.flush();
					}
				}else
				{

				}
			}
		} catch (Exception ex) {
			Log.i("调试" , "通讯中断Exception:");
			Log.i("hu" , "蓝牙客服端连接中断");
			//发送通讯失败消息
			//serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			close();
		}
	}

	/**
	 * 写入流
	 * @param obj
	 */
	public void write(Object obj) {
		try {
			TransmitBean transmit = (TransmitBean) obj;
			if(transmit.getFilename()!=null&&!"".equals(transmit.getFilename())){
				Log.v("调试" , "type:"+2);
				String filename=transmit.getFilename();
				byte type = 2; //类型为2，即传文件
				//读取文件长度
				FileInputStream fins=new FileInputStream(transmit.getFilepath());
				long fileDataLen = fins.available(); //文件的总长度
				int f_len=filename.getBytes("GBK").length; //文件名长度
				byte[] data=new byte[f_len];
				data=filename.getBytes("GBK");
				long totalLen = 4+1+1+f_len+fileDataLen;//数据的总长度
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入文件名的长度
				outStream.write(data);    //4.写入文件名的数据
				outStream.flush();
				//读取文件并发送
				try {
					byte[] buffer=new byte[1024*32];
					downbl=0;
					int size=0;
					long sendlen=0;
					float tspeed=0;
					int i=0;
					long time1=Calendar.getInstance().getTimeInMillis();
					while((size=fins.read(buffer, 0, 1024*32))!=-1)
					{
						outStream.write(buffer, 0, size);
						outStream.flush();
						sendlen+=size;
						i++;
						if(i%10==0){
							long time2=Calendar.getInstance().getTimeInMillis();
							tspeed=sendlen/(time2-time1)*1000/1024;
						}
						Log.v("调试" ,"tspeed："+tspeed);
						downbl = (sendlen * 100) / fileDataLen;
						TransmitBean up = new TransmitBean();
						up.setUppercent(String.valueOf(downbl));
						up.setTspeed(String.valueOf(tspeed));
						Message msg = serviceHandler.obtainMessage();
						msg.what = BluetoothTools.FILE_SEND_PERCENT;
						msg.obj = up;
						msg.sendToTarget();
					}
					fins.close();
					Log.v("调试" , "文件发送完成");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				Log.v("调试" , "type:"+1);
				byte type = 1; //类型为1，即传文本消息
				String msg=transmit.getMsg();
				int f_len=msg.getBytes().length; //消息长度
				long totalLen = 4+1+1+f_len;//数据的总长度
				byte[] data=new byte[f_len];
				data=msg.getBytes();
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入消息的长度
				outStream.write(data);    //4.写入消息数据
				outStream.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] backtgCommand(){
		String filePath = CommonUtility.backupFilePath + CommonUtility.tgfileName2;
		File fPath = new File(CommonUtility.backupFilePath);
		//判断文件夹是否存在,如果不存在则创建文件夹
		if (!fPath.exists()) {
			fPath.mkdirs();
		}

		FileInputStream tgfile = null;
		long len = 0;
		try {
			tgfile = new FileInputStream(filePath);
			len = tgfile.available(); //文件的总长度
			tgfile.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] blen = CalculateFunction.longTobyte((int) (len));
		Log.i("hu","要传送的文件总长：len="+len+" blen="+CalculateFunction.byteTo16hex(blen));

		byte[] buf = new byte[15];
		buf[0] = 0x24;
		buf[1] = 0x43;
		buf[2] = 0x4d;
		buf[3] = 0x44;

		buf[4] = CalculateFunction.intTobyte(0x5a);   // -96的补码是0xa0
		buf[5] = 0x06;
		buf[6] = 0x00;
		buf[7] = 0x03;

 /*       buf[8] = 0x00; //514,541字节
        buf[9] = 0x07;
        buf[10] = CalculateFunction.intTobyte(0xD9);
        buf[11] = CalculateFunction.intTobyte(0xED);
  */
		buf[8] = blen[0]; //514,541字节
		buf[9] = blen[1];
		buf[10] = blen[2];
		buf[11] = blen[3];

		buf[12] = 0x00;
		buf[13] = 0x05; //分5帧

		buf[14] = 0x24;
		return buf;
	}

	private byte[] getFileByte(int all,int frame)
	{
		String filePath = CommonUtility.backupFilePath + CommonUtility.tgfileName2;
		File fPath = new File(CommonUtility.backupFilePath);
		//判断文件夹是否存在,如果不存在则创建文件夹
		if (!fPath.exists()) {
			fPath.mkdirs();
		}

		FileInputStream tgfile = null;
		long len = 0;
		try {
			tgfile = new FileInputStream(filePath);
			len = tgfile.available(); //文件的总长度
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("hu","发送探管文件 ,文件长len="+len+" all="+all +" frame="+frame);
		long framelen = (len%all == 0)?(len/all):(len/all+1);
		long start = framelen*(frame-1);
		long readlen = (frame==all)?(len-framelen*(frame-1)):framelen;
		Log.i("hu","framelen="+framelen+" start="+start+" readlen="+readlen);
		if(readlen<0)
			readlen=0;
		byte[] buffer = new byte[(int) (readlen+9)];
		byte[] blen = CalculateFunction.longTobyte((int) (readlen+9));
		int i=0;
		for(;i<4;i++)
			buffer[i]=blen[i];

		byte[] num = CalculateFunction.intTo2byte(all);
		buffer[4]=num[0];
		buffer[5]=num[1];

		num = CalculateFunction.intTo2byte(frame);
		buffer[6]=num[0];
		buffer[7]=num[1];

		buffer[8]=0;  //校验位

		int size=0;
		int receivelen=0;
		try {
			if(start<=len)
				tgfile.skip(start);
			while (receivelen < readlen){
				size=tgfile.read(buffer, 9, (int) readlen);
				receivelen+=size;
				Log.e("hu","$$$$$读探管文件字节。size="+size+" readlen="+readlen);
				Log.i("hu","buffer="+CalculateFunction.byteTo16hex(buffer));
			}
			tgfile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte crc = CalculateFunction.calcCrc8(buffer);
		buffer[8]=crc;
		Log.e("hu","$$$$$crc="+crc);

		return buffer;
	}
}
