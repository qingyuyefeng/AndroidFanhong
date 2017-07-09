package com.fanhong.cn.bluetooth.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
public class BluetoothCommunSocket {
	private Handler serviceHandler;		//与Service通信的Handler
	public BluetoothSocket socket;
	public DataInputStream inStream;		//对象输入流
	public DataOutputStream outStream;	//对象输出流
	public volatile boolean isRun = true;	//运行标志位
	private long downbl;
	private long time1;
	private long receivelen1=0;
	public boolean readflag = true;
	public FileOutputStream tgfile;  //探管文件

	/**
	 * 构造函数
	 * @param handler 用于接收消息
	 * @param socket
	 */
	public BluetoothCommunSocket(Handler handler, BluetoothSocket socket) {
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
	public void close(){
		if (inStream != null) {
			try {
				inStream.close();
				inStream = null;
				Log.i("调试" , "clientsocket已关闭22");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (outStream != null) {
			try {
				outStream.close();
				outStream = null;
				Log.i("调试" , "clientsocket已关闭33");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				socket = null;
				Log.i("调试" , "clientsocket已关闭44");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入流
	 * @param obj
	 */
	public void write(Object obj) {
		try {
			TransmitBean transmit_s = (TransmitBean) obj;
			if(transmit_s.getFilename()!=null&&!"".equals(transmit_s.getFilename())){

				Log.i("调试" , "type:"+2);
				String filename=transmit_s.getFilename();
				byte type = 2; //类型为2，即传文件
				//读取文件长度
				FileInputStream fins=new FileInputStream(transmit_s.getFilepath());
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
					byte[] buffer=new byte[1024*64];
					downbl=0;
					int size=0;
					long sendlen=0;
					float tspeed=0;
					int i=0;
					long time1=Calendar.getInstance().getTimeInMillis();
					while((size=fins.read(buffer, 0, 1024*64))!=-1)
					{
						outStream.write(buffer, 0, size);
						outStream.flush();
						sendlen+=size;
						Log.i("调试" , "fileDataLen:"+fileDataLen);
						i++;
						if(i%5==0){
							long time2=Calendar.getInstance().getTimeInMillis();
							tspeed=sendlen/(time2-time1)*1000/1024;
						}
						//	Log.v("调试" ,"tspeed："+tspeed);
						downbl = ((sendlen * 100) / fileDataLen);
						TransmitBean up = new TransmitBean();
						up.setUppercent(String.valueOf(downbl));
						up.setTspeed(String.valueOf(tspeed));
						if(i==1){
							up.setShowflag(true);
						}else{
							up.setShowflag(false);
						}
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
			}else if(!transmit_s.iscommmandflag()){
				Log.i("调试" , "type:"+1);
				byte type = 1; //类型为1，即传文本消息
				String msg=transmit_s.getMsg();
				int f_len=msg.getBytes("GBK").length; //消息长度
				long totalLen = 4+1+1+f_len;//数据的总长度
				byte[] data=new byte[f_len];
				data=msg.getBytes("GBK");
				outStream.writeLong(totalLen); //1.写入数据的总长度
				outStream.writeByte(type);//2.写入类型
				outStream.writeByte(f_len); //3.写入消息的长度
				outStream.write(data);    //4.写入消息数据
				outStream.flush();
			}else{
				Log.i("调试" , "type:"+3);
				byte type = 3; //类型为3，即传命令

				byte[] data=transmit_s.getCom();
				outStream.write(data);    //4.写入消息数据
				outStream.flush();
				//	Log.e("hu","*****msg="+msg+" data[4]="+data[4]);
			}

			if(transmit_s.iscommmandflag())
				this.read2();
			else
				this.read();

			//byte[] ef = new byte[3];
			//inStream.read(ef);//读取消息
			//String eof = new String(ef);
			//if("EOF".equals(eof)){
			//	Log.v("调试" ,"接收EOF");
			//}
		}catch (Exception ex) {
			Log.i("调试" , "通讯中断Exception:");
			//发送通讯失败消息
			//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			//	close();
		}
	}

	public void write2(int all,int frame) {
		byte[] data = receiveFrameCommand(all,frame);
		Log.i("hu","write2 data="+CalculateFunction.byteTo16hex(data));
		try {
			outStream.write(data);
			outStream.flush();
			//this.read4();
		}catch (Exception ex) {
			Log.i("调试" , "通讯中断Exception:");
			//发送通讯失败消息
			//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			ex.printStackTrace();
		}
		finally {
			//	close();
		}
	}


	public void read() throws IOException{
		Log.i("hu","**********read()");
		TransmitBean transmit_r = new TransmitBean();
		long totalLen = inStream.readLong();//总长度
		if(totalLen>0){
			byte type = inStream.readByte();//类型
			if(type==1){//文本类型
				try {
					byte len = inStream.readByte();//消息长度
					byte[] ml = new byte[len];
					int size=0;
					int receivelen=0;
					while (receivelen <len){
						size=inStream.read(ml,0,ml.length);
						receivelen+=size;
					}
					String msg = new String(ml);
					Log.v("调试" , "msg:"+msg);
					transmit_r.setMsg(msg);
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
					Log.v("调试" , "filename:"+filename);
					transmit_r.setFilename(filename);
					long datalength = totalLen-1-4-1-fn.length;//文件数据
					String savePath = CommonUtility.recordFilePath + CommonUtility.timefileName;

					Log.i(CommonUtility.TAG, "接收文件名" + savePath);//没有来到这里

					transmit_r.setFilepath(savePath);
					FileOutputStream file=new FileOutputStream(savePath, false);
					byte[] buffer = new byte[1024*1024];
					int size = -1;
					long receivelen=0;
					int i=0;
					float tspeed=0;
					long time1=Calendar.getInstance().getTimeInMillis();
					while (receivelen <datalength){
						size=inStream.read(buffer);
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
					}
					Log.v("调试" , "接收完成,receivelen:"+receivelen);
					file.flush();
					file.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			transmit_r.setbackflag(true);
			//发送成功读取到对象的消息，消息的obj参数为读取到的对象
			Message msg = serviceHandler.obtainMessage();
			msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
			msg.obj = transmit_r;
			msg.sendToTarget();
		}
	}

	public void read2() throws IOException{
		Log.i("hu","**********read2()");
		TransmitBean transmit_r = new TransmitBean();
		try {
			//	byte[] ml = new byte[100];
			byte[] buf = new byte[100];
			int size=0;
			//size=inStream.read(ml,0,ml.length);
			int len = 0;
			//	while (len<15){
			//			size = inStream.read(ml);
			//			bytecopy(ml,buf,size,len);
			//			len+=size;
			//			Log.i("hu" , " 1size="+size+ " len="+len);
			//		}
			while (len<15){
				int num = inStream.read();
				if(num < 0)
					break;
				buf[len] = (byte)num;
				//	Log.i("hu","len="+len+" buf[]="+buf[len]);
				len++;
			}

			//	for(int j=0;j<15;j++)
			//		Log.i("hu" ,"ml[j]="+buf[j]);
			byte[]m2 = new byte[len];
			for(int i=0;i<len;i++)
				m2[i]=buf[i];

			String msg = new String(m2);
			Log.i("调试" , "收到远方msg:"+msg+" size="+size);
			transmit_r.setbackflag(true);
			transmit_r.setcommmandflag(true);
			transmit_r.setCom(m2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Message msg = serviceHandler.obtainMessage();
		msg.what = BluetoothTools.MESSAGE_READ_OBJECT;
		msg.obj = transmit_r;
		msg.sendToTarget();
	}

	public void read3(){	//建立一个读通道线程
		Log.i("hu","**********读通道线程  read3()");
		try {
			byte[] ml = new byte[100];
			byte[] buf = new byte[100];
			int size=0;
			int len = 0;
			//	while (len<15){
			//			size = inStream.read(ml);
			//			bytecopy(ml,buf,size,len);
			//			len+=size;
			//			Log.i("hu" , " 1size="+size+ " len="+len);
			//		}
			int i=0;
			int num;
			while((num=inStream.read(buf)) != -1){
				buf[i] = (byte)num;
				Log.i("hu","i="+i+" buf[i]="+buf[i]);
				i++;
				if(i>14)
					i=testbuffer(buf,i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean read4(long filelength){
		Log.i("hu","**********read4()");
		boolean ret = true;
		try {
			byte[] bt = new byte[4];
			int i=0;
			byte letter;
			while (i <4){
				letter = inStream.readByte();
				bt[i] = letter;
			}
			long totalLen = CalculateFunction.getlong(bt);

			byte[] bt2 = new byte[2];
			for(i=0;i<2;i++)
			{
				letter = inStream.readByte();
				bt2[i] = letter;
			}
			int all = CalculateFunction.byte2Toint(bt2[0], bt2[1]);//总帧数
			for(i=0;i<2;i++)
			{
				letter = inStream.readByte();
				bt2[i] = letter;
			}
			int frame =  CalculateFunction.byte2Toint(bt2[0], bt2[1]);//第几帧
			byte crc = inStream.readByte();//类型
			Log.e("hu","收到文件长度totalLen="+totalLen+" all="+all+" frame="+frame+" crc="+crc);

			int len = (int)totalLen-4-5;
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
			//Log.i("hu","收到文件buf="+CalculateFunction.byteTo16hex(buf));

			byte[] head = new byte[9];
			for(int j=0;j<4;j++)
				head[j]=bt[j];
			byte[] bt3 = CalculateFunction.intTo2byte(all);
			head[4] = bt3[0];
			head[5] = bt3[1];

			bt3 = CalculateFunction.intTo2byte(frame);
			head[6] = bt3[0];
			head[7] = bt3[1];
			head[8] = 0;
			byte[] data = CalculateFunction.byteMerger(head, buf);
			byte crc2 = CalculateFunction.calcCrc8(data);
			Log.e("hu","收到的数据校验 crc2="+crc2+" 原crc="+crc);

	/*		String buf5="收到第"+frame+"帧。校验后的crc="+crc2+",传来的校验位为"+crc;
			Message msg1 = serviceHandler.obtainMessage();
			msg1.what = BluetoothTools.PHONE_SHOW_BUFFER;
			msg1.obj = buf5;
			msg1.sendToTarget();
	*/

			if(crc == crc2) //校验通过
			{
				saveDate(buf,all,frame);

				float tspeed=0;
				//	if(i%10==0){
				long time2=Calendar.getInstance().getTimeInMillis();
				tspeed=receivelen/(time2-time1)*1000/1024;
				//}
				receivelen1 +=len;
				downbl = (receivelen1 * 100) / filelength;
				TransmitBean up = new TransmitBean();
				up.setUppercent(String.valueOf(downbl));
				up.setTspeed(String.valueOf(tspeed));
				//if(i==1){
				//	up.setShowflag(true);
				//}else{
				//	up.setShowflag(false);
				//	}
				Message msg = serviceHandler.obtainMessage();
				msg.what = BluetoothTools.FILE_RECIVE_PERCENT;
				msg.obj = up;
				msg.sendToTarget();
				Log.i("hu" , "探管文件接收进度 up="+up.toString());

			}else
			{
				ret = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	void bytecopy(byte[] ml,byte[] buf,int size,int index){
		for(int i=0;i<size;i++){
			buf[index+i] = ml[i];
		}
	}
	int testbuffer(byte[] buf,int lg)
	{
		int i=0,len=lg;
		while(len>14)
		{
			if(buf[i]==0x24 && buf[i+1]==0x43 && buf[i+2]==0x4d && buf[i+3]==0x44 && buf[i+14]==0x24)
			{
				TransmitBean transmit_r = new TransmitBean();
				byte[]m2 = new byte[15];
				for(int p=0;p<15;p++)
					m2[p]=buf[i+p];
				String msg = new String(m2);
				Log.i("调试" , "收到远方msg:"+msg);
				transmit_r.setbackflag(true);
				transmit_r.setcommmandflag(true);
				transmit_r.setCom(m2);
				Message msg1 = serviceHandler.obtainMessage();
				msg1.what = BluetoothTools.MESSAGE_READ_OBJECT;
				msg1.obj = transmit_r;
				msg1.sendToTarget();

				i+=15;
			}else
			{
				len--;
				i++;
			}
		}
		for(int j=0;j<len;j++)
			buf[j]=buf[j+i];
		return len;
	}

	public void receiveFile(long filelength,int frame){ //接收文件
		if(frame<1)return;
		int i = 0;
		time1=Calendar.getInstance().getTimeInMillis();
		receivelen1=0;

		TransmitBean up = new TransmitBean();
		up.setUppercent(String.valueOf(0));
		up.setTspeed(String.valueOf(0));
		Message msg = serviceHandler.obtainMessage();
		msg.what = BluetoothTools.FILE_RECIVE_PERCENT;
		msg.obj = up;
		msg.sendToTarget();
		//Log.i("hu" , "探管文件接收进度 up="+up.toString());

		for(;i<frame;)
		{
			i++;

			if (socket == null)
				break;

			write2(frame,i);
			if(!read4(filelength))
				i--;
		}
	}

	private byte[] receiveFrameCommand(int all, int frame){
		byte[] buf = new byte[15];
		buf[0] = 0x24;
		buf[1] = 0x43;
		buf[2] = 0x4d;
		buf[3] = 0x44;

		buf[4] = intTobyte(0x5b);   // -96的补码是0xa0
		buf[5] = 0x05;
		buf[6] = 0x00;

		buf[7] = 0x04;

		byte[] num = CalculateFunction.intTo2byte(all);
		buf[8] = num[0];
		buf[9] = num[1];
		num = CalculateFunction.intTo2byte(frame);
		buf[10] = num[0];
		buf[11] = num[1];
		buf[12] = 0x00;
		buf[13] = 0x00;
		buf[14] = 0x24;
		return buf;
	}
	private byte intTobyte(int num){
		if(num >= 0x8f){
			int a = (~num)&0x7f;
			int b= a+1;
			return (byte)(b*(-1));
		}else
			return (byte)num;
	}

	private void saveDate(byte[] bt, int all, int frame)
	{
		if(frame == 1)
		{
			Date date=new Date();
			DateFormat format=new SimpleDateFormat("_yyyyMMdd_HHmmss");
			String time=format.format(date);
			String savePath = CommonUtility.recordFilePath + CommonUtility.tgfileName+time+".CSV";
			try {
				tgfile = new FileOutputStream( savePath, false);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i("hu","*****建文件 tgfile = "+savePath);
		}
		try {
			tgfile.write(bt, 0 ,bt.length);
			if(frame == all)
			{
				tgfile.close();
				tgfile = null;
				//探管文件存储完毕
				Log.e("hu","*********探管文件存储完毕");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	/*	Message msg = serviceHandler.obtainMessage();											
		msg.what = BluetoothTools.FILE_SEND_PERCENT;					
		msg.obj = up;
		msg.sendToTarget();*/
	}
}
