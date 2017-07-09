package com.fanhong.cn.bluetooth.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.util.Log;


public class CalculateFunction {
	static byte[] crc8_tab = { (byte) 0, (byte) 94, (byte) 188, (byte) 226, (byte) 97, (byte) 63, (byte) 221, (byte) 131, (byte) 194, (byte) 156, (byte) 126, (byte) 32, (byte) 163, (byte) 253, (byte) 31, (byte) 65, (byte) 157, (byte) 195, (byte) 33, (byte) 127, (byte) 252, (byte) 162, (byte) 64, (byte) 30, (byte) 95, (byte) 1, (byte) 227, (byte) 189, (byte) 62, (byte) 96, (byte) 130, (byte) 220, (byte) 35, (byte) 125, (byte) 159, (byte) 193, (byte) 66, (byte) 28, (byte) 254, (byte) 160, (byte) 225, (byte) 191, (byte) 93, (byte) 3, (byte) 128, (byte) 222, (byte) 60, (byte) 98, (byte) 190, (byte) 224, (byte) 2, (byte) 92, (byte) 223, (byte) 129, (byte) 99, (byte) 61, (byte) 124, (byte) 34, (byte) 192, (byte) 158, (byte) 29, (byte) 67, (byte) 161, (byte) 255, (byte) 70, (byte) 24,
			(byte) 250, (byte) 164, (byte) 39, (byte) 121, (byte) 155, (byte) 197, (byte) 132, (byte) 218, (byte) 56, (byte) 102, (byte) 229, (byte) 187, (byte) 89, (byte) 7, (byte) 219, (byte) 133, (byte) 103, (byte) 57, (byte) 186, (byte) 228, (byte) 6, (byte) 88, (byte) 25, (byte) 71, (byte) 165, (byte) 251, (byte) 120, (byte) 38, (byte) 196, (byte) 154, (byte) 101, (byte) 59, (byte) 217, (byte) 135, (byte) 4, (byte) 90, (byte) 184, (byte) 230, (byte) 167, (byte) 249, (byte) 27, (byte) 69, (byte) 198, (byte) 152, (byte) 122, (byte) 36, (byte) 248, (byte) 166, (byte) 68, (byte) 26, (byte) 153, (byte) 199, (byte) 37, (byte) 123, (byte) 58, (byte) 100, (byte) 134, (byte) 216, (byte) 91, (byte) 5, (byte) 231, (byte) 185, (byte) 140, (byte) 210, (byte) 48, (byte) 110, (byte) 237,
			(byte) 179, (byte) 81, (byte) 15, (byte) 78, (byte) 16, (byte) 242, (byte) 172, (byte) 47, (byte) 113, (byte) 147, (byte) 205, (byte) 17, (byte) 79, (byte) 173, (byte) 243, (byte) 112, (byte) 46, (byte) 204, (byte) 146, (byte) 211, (byte) 141, (byte) 111, (byte) 49, (byte) 178, (byte) 236, (byte) 14, (byte) 80, (byte) 175, (byte) 241, (byte) 19, (byte) 77, (byte) 206, (byte) 144, (byte) 114, (byte) 44, (byte) 109, (byte) 51, (byte) 209, (byte) 143, (byte) 12, (byte) 82, (byte) 176, (byte) 238, (byte) 50, (byte) 108, (byte) 142, (byte) 208, (byte) 83, (byte) 13, (byte) 239, (byte) 177, (byte) 240, (byte) 174, (byte) 76, (byte) 18, (byte) 145, (byte) 207, (byte) 45, (byte) 115, (byte) 202, (byte) 148, (byte) 118, (byte) 40, (byte) 171, (byte) 245, (byte) 23, (byte) 73, (byte) 8,
			(byte) 86, (byte) 180, (byte) 234, (byte) 105, (byte) 55, (byte) 213, (byte) 139, (byte) 87, (byte) 9, (byte) 235, (byte) 181, (byte) 54, (byte) 104, (byte) 138, (byte) 212, (byte) 149, (byte) 203, (byte) 41, (byte) 119, (byte) 244, (byte) 170, (byte) 72, (byte) 22, (byte) 233, (byte) 183, (byte) 85, (byte) 11, (byte) 136, (byte) 214, (byte) 52, (byte) 106, (byte) 43, (byte) 117, (byte) 151, (byte) 201, (byte) 74, (byte) 20, (byte) 246, (byte) 168, (byte) 116, (byte) 42, (byte) 200, (byte) 150, (byte) 21, (byte) 75, (byte) 169, (byte) 247, (byte) 182, (byte) 232, (byte) 10, (byte) 84, (byte) 215, (byte) 137, (byte) 107, 53 };

	/**
	 * 计算数组的CRC8校验值
	 *
	 * @param data
	 *            需要计算的数组
	 * @return CRC8校验值
	 */
	public static byte calcCrc8(byte[] data) {
		return calcCrc8(data, 0, data.length, (byte) 0);
	}

	/**
	 * 计算CRC8校验值
	 *
	 * @param data
	 *            数据
	 * @param offset
	 *            起始位置
	 * @param len
	 *            长度
	 * @return 校验值
	 */
	public static byte calcCrc8(byte[] data, int offset, int len) {
		return calcCrc8(data, offset, len, (byte) 0);
	}

	/**
	 * 计算CRC8校验值
	 *
	 * @param data
	 *            数据
	 * @param offset
	 *            起始位置
	 * @param len
	 *            长度
	 * @param preval
	 *            之前的校验值
	 * @return 校验值
	 */
	public static byte calcCrc8(byte[] data, int offset, int len, byte preval) {
		byte ret = preval;
		for (int i = offset; i < (offset + len); ++i) {
			ret = crc8_tab[(0x00ff & (ret ^ data[i]))];
		}
		return ret;
	}

	// 测试
	//  public static void main(String[] args) {
	//      byte crc = CRC8.calcCrc8(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
	//       System.out.println("" + Integer.toHexString(0x00ff & crc));
	//   }

	public static byte intTobyte(int num){  //0~255的int数据转成byte存储，大于128的存放补码
		if(num >= 0x8f){
			int a = (~num)&0x7f;
			int b= a+1;
			return (byte)(b*(-1));
		}else
			return (byte)num;
	}

	public static long getlong(byte[] buf)  //4字节的byte数组转int型数据
	{
		int i=0,val;
		long ret = 0;
		for(i=0;i<4;i++)
		{
			if(buf[i]<0)
			{
				val = buf[i]&0x7f;
				val += 0x80;
			}else
				val = buf[i];
			ret = ret|(((long) val & 0xff) << (8*(3-i)));
		}
		return ret;
	}

	public static byte[] longTobyte(int num)  //int型数据转4字节的byte数组
	{
		byte[] buf = new byte[4];

		ByteArrayOutputStream boutput = new ByteArrayOutputStream();
		DataOutputStream doutput = new DataOutputStream(boutput);
		try {
			doutput.writeInt(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buf1 = boutput.toByteArray();
		int j=3;
		for(int i=buf1.length-1;i>=0;i--,j--){
			buf[j]=buf1[i];
		}
		while(j>=0)
		{
			buf[j]=0;
			j--;
		}
		return buf;
	}

	//把byte数变为16进制显示
	public static String byteTo16hex(byte bye[]){
		int len = bye.length;
		int i;
		StringBuffer buf = new StringBuffer();
		for(i=0;i<len;i++)
		{
			if(bye[i]>=0){
				int a = toChar(bye[i]>>4);
				buf.append((char)a);
			}
			else{
				int a = bye[i]&0x70;
				a = a>>4;
				buf.append((char)toChar(a+8));
				//	Log.e("hu","*111*****3 a="+a);
			}
			int b = toChar(bye[i]&0x0f);
			buf.append((char)b);
			buf.append(" ");
		}
		//Log.e("hu","*222*****buf="+buf);
		return buf.toString();
	}

	//把2字节的byte数组转int型数据
	public static int byte2Toint(byte high, byte low)  //2字节的byte数组转int型数据
	{
		byte[] buf = new byte[2];
		buf[0]=high;
		buf[1]=low;
		int i=0,val=0;
		int ret = 0;
		for(i=0;i<2;i++)
		{
			if(buf[i]<0)
			{
				val = buf[i]&0x7f;
				val += 0x80;
			}else
				val = buf[i];
			ret = ret|((val & 0xff) << (8*(1-i)));
		}
		return ret;
	}

	//把int型数据转2字节的byte数组
	public static byte[] intTo2byte(int num)  //int型数据转4字节的byte数组
	{
		if(num > 0xffff)
			return null;
		byte[] buf = new byte[2];

		ByteArrayOutputStream boutput = new ByteArrayOutputStream();
		DataOutputStream doutput = new DataOutputStream(boutput);
		try {
			doutput.writeInt(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] buf1 = boutput.toByteArray();
		int len = buf1.length;
		//Log.i("hu","num="+num+" buf1="+CalculateFunction.byteTo16hex(buf1));
		buf[0] = buf1[len-2];
		buf[1] = buf1[len-1];
		return buf;
	}

	public static int toChar(int n){
		if(n>=0 && n<=9){
			return n+'0';
		}else if(n>9 && n<16){
			return n-10+'A';
		}
		return 0;
	}

	//代码转自：java int 与 byte转换
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	//java 合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
		byte[] byte_3 = new byte[byte_1.length+byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
}