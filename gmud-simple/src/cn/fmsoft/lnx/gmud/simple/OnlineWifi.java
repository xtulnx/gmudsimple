package cn.fmsoft.lnx.gmud.simple;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 *
 目的：模拟器(5556)连接到模拟器(5554)的7100端口\n

 要达到此目的，也要先进行端口映射。\n
 把本机端口TCP/6100映射到模拟器(5554)TCP/7100端口(UDP也是一样)。\n

 >adb -s emulator-5554 forward tcp:6100 tcp:7100\n

 端口映射成功后，再进行下面的实验。\n

 方法1(正确)：\n
 模拟器(5554)-SERVER：\n
 ServerSocket server = new ServerSoket(7100);\n
 模拟器(5556)-CLIENT：\n
 Socket socket = new Socket("10.0.2.2", 6100);\n

 */

public class OnlineWifi {

	static final int DEF_PORT_SERVER = 7100;
	static final int DEF_PORT_CLINET = 6100;
	static final String DEF_HOST_SERVER = "10.0.2.2";

	static final int DEF_TIMEOUT = 30 * 1000; // 30s

	private Socket mSocket;
	private ServerSocket mServerSocket;
	private InputStream mInputStream;
	private OutputStream mOutputStream;
	
	private int mPortServer = DEF_PORT_SERVER;
	private int mPortClient = DEF_PORT_CLINET;
	private String mHostName = DEF_HOST_SERVER;
	

	public void clean() {
		close();

		if (mServerSocket != null) {
			ServerSocket socket = mServerSocket;
			mServerSocket = null;
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void close() {
		if (mSocket != null) {
			Socket socket = mSocket;
			mSocket = null;

			if (mInputStream != null) {
				InputStream is = mInputStream;
				mInputStream = null;
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (mOutputStream != null) {
				OutputStream os = mOutputStream;
				mOutputStream = null;
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ServerSocket createServer() throws IOException {
		ServerSocket socket;
		if (mServerSocket == null) {
			socket = new ServerSocket(mPortServer);
			// socket.setReuseAddress(true);
			// socket.bind(new InetSocketAddress(PORT_SERVER));
			socket.setSoTimeout(DEF_TIMEOUT);
		} else {
			socket = mServerSocket;
		}
		return socket;
	}

	/**
	 * 开启联机。如果是建立端，则有等待超时 {@value #DEF_TIMEOUT} ms.
	 * 
	 * @param bServer
	 * @return
	 */
	public int start(boolean bServer) {
		close();

		if (bServer) {
			try {
				ServerSocket socket = createServer();
				init(socket.accept());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Socket socket = new Socket(mHostName, mPortClient);
				init(socket);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return mSocket != null ? 0 : -1;
	}

	private void init(Socket socket) throws IOException {
		socket.setSoTimeout(DEF_TIMEOUT);
		socket.setKeepAlive(true);
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		mSocket = socket;
		mInputStream = is;
		mOutputStream = os;
	}

	/**
	 * 发送数据
	 * 
	 * @param buffer
	 * @param offset
	 * @param count
	 * @return 是否成功
	 */
	public boolean send(byte[] buffer, int offset, int count) {
		try {
			OutputStream os = mOutputStream;
			if (count > 0)
				os.write(buffer, offset, count);
			os.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读取数据
	 * 
	 * @param buffer
	 * @return 读取到的数据大小 -1表示出错
	 */
	public int read(byte[] buffer) {
		if (mInputStream != null) {
			try {
				InputStream is = mInputStream;
				return is.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public DataOutputStream getDataOutputStream() {
		if (mOutputStream != null) {
			return new DataOutputStream(mOutputStream);
		}
		return null;
	}

	public DataInputStream getDataInputStream() {
		if (mInputStream != null) {
			return new DataInputStream(mInputStream);
		}
		return null;
	}
}