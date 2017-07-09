package com.fanhong.cn.bluetooth.util;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 服务器连接线程
 * @author liujian
 *
 */
public class BluetoothServerConnThread extends Thread {

	private Handler serviceHandler;		//用于同Service通信的Handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;		//用于通信的Socket
	private BluetoothServerSocket serverSocket;
	private boolean isInterrupted=false;
	//蓝牙通讯线程
	private  BluetoothCommunThreads communThread;
	private BluetoothClientCommun clientcommun;

	/**
	 * 构造函数
	 * @param handler
	 */
	public BluetoothServerConnThread(Handler handler) {
		this.serviceHandler = handler;
		adapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void close(){
		isInterrupted = true;
		if (communThread != null) {
			communThread.close();
			communThread = null;
		}
		if (serverSocket != null) {
			try {
				serverSocket.close();
				serverSocket = null;
				Log.i("调试" , "serverSocket已关闭");
			} catch (IOException e) {
				Log.e("调试", "serverSocket关闭 failed", e);
				e.printStackTrace();
			}
		}
		if (socket != null) {
			try {
				socket.close();
				socket = null;
				Log.i("调试" , "socket已关闭");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.interrupt();
	}

	@Override
	public void run() {
		try {
			serverSocket = adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			while(!isInterrupted){
				Log.i("hu","********BluetoothServerConnThread.java 1");
				socket = serverSocket.accept();
				if (socket != null) {
					//发送连接成功消息，消息的obj字段为连接的socket
					Message msg = serviceHandler.obtainMessage();
					msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
					msg.obj = socket;
					msg.sendToTarget();
					Log.i("hu","********BluetoothServerConnThread.java 2");
					//开启通讯线程
					//communThread = new BluetoothCommunThreads(serviceHandler, socket);
					//communThread.start();

					clientcommun = new BluetoothClientCommun(serviceHandler, socket);
					clientcommun.read();
					//	clientcommun.read2();   //read2()为模拟探管发送文件

					//发送连接成功消息，消息的obj字段为连接的socket
					//Message msg = serviceHandler.obtainMessage();
					//msg.what = BluetoothTools.MESSAGE_CONNECT_SUCCESS;
					//msg.obj = socket;
					//msg.sendToTarget();
					serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_BREAK).sendToTarget();
					Log.i("hu","********BluetoothServerConnThread.java 3");
				} else {
					//发送连接失败消息
					//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
					break;
				}
			}
		} catch (Exception e) {
			//发送连接失败消息
			//	serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			return;
		}
		finally {
			Log.i("调试" , "BluetoothServerConnThread退出");
			try {
				serverSocket.close();
			} catch (Exception e) {
				Log.i("调试" , "serverSocket.close()  failed");
				e.printStackTrace();
			}
		}
	}

}
