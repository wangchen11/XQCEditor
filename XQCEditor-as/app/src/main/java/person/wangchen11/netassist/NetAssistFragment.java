package person.wangchen11.netassist;

import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import folk.china.util.StringTranslate;

import person.wangchen11.netassist.tcpclient.TCPClient;
import person.wangchen11.netassist.tcpserver.SimpleTCPServer;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NetAssistFragment extends Fragment implements OnClickListener, OnItemSelectedListener{
	private Spinner mProcTypeSpinner = null;
	private AutoCompleteTextView mAddrEditText = null;
	private AutoCompleteTextView mPortEditText = null;
	private Button mConnectButton = null;
	private Button mSendButton = null;
	private Button mClearButton = null;
	private TextView mMsgView = null;
	private CheckBox mShowSenderCheckBox = null;
	private CheckBox mShowAsHexCheckBox = null;
	
	private EditText mMsgEditText = null;
	private ScrollView mMsgScrollView = null;
	
	@SuppressLint("InflateParams")
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_net_assist,null);
		mProcTypeSpinner = (Spinner) relativeLayout.findViewById(R.id.spinner_proc_type);

		String procotolNames[]=getResources().getStringArray(R.array.procotol_names);
		ArrayAdapter<String> adapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, procotolNames);
		mProcTypeSpinner.setOnItemSelectedListener(this);
		mProcTypeSpinner.setAdapter(adapter);
		mAddrEditText = (AutoCompleteTextView) relativeLayout.findViewById(R.id.edit_text_addr);
		mAddrEditText.setThreshold(1);
		mPortEditText = (AutoCompleteTextView) relativeLayout.findViewById(R.id.edit_text_port);
		mPortEditText.setThreshold(1);
		mConnectButton = (Button) relativeLayout.findViewById(R.id.button_connect);
		mConnectButton.setOnClickListener(this);
		mSendButton = (Button) relativeLayout.findViewById(R.id.button_send);
		mSendButton.setOnClickListener(this);
		mClearButton = (Button) relativeLayout.findViewById(R.id.button_clear);
		mClearButton.setOnClickListener(this);
		mMsgView = (TextView) relativeLayout.findViewById(R.id.text_view_show_msg);
		mMsgScrollView = (ScrollView) relativeLayout.findViewById(R.id.scroll_view_msg);
		mMsgEditText = (EditText) relativeLayout.findViewById(R.id.edit_text_msg);
		mShowSenderCheckBox = (CheckBox) relativeLayout.findViewById(R.id.check_box_show_sender);
		mShowSenderCheckBox.setChecked(true);
		mShowAsHexCheckBox = (CheckBox) relativeLayout.findViewById(R.id.check_box_show_as_hex);
		setAutoText();
		return relativeLayout;
	}
	
	public void destory()
	{
		stopTCPClient();
		stopTCPServer();
	}

	public void addMsg(String msg)
	{
		CharSequence oldMsg = mMsgView.getText();
		if(oldMsg.length()>=1024*10)
			oldMsg = oldMsg.subSequence(oldMsg.length()-1024*8, oldMsg.length());
		mMsgView.setText(oldMsg+msg);
		mProcTypeSpinner.post(new Runnable() {
			@Override
			public void run() {
				mMsgScrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	public void clearMsg()
	{
		mMsgView.setText("");
	}
	
	public void addMsgLn(String msg)
	{
		addMsg(msg+"\n");
	}

	@Override
	public void onClick(View v) {
		int procType = mProcTypeSpinner.getSelectedItemPosition();
		switch (v.getId()) {
		case R.id.button_connect:
			saveAutoText();
			
			if(procType==0)
			{
				if(isTCPClientRunning())
					stopTCPClient();
				else
					startTCPClient();
			}
			if(procType==1)
			{
				if(isTCPServerRunning())
					stopTCPServer();
				else
					startTCPServer();
			}
			if(procType==2)
			{
				showToast(getActivity().getString(R.string.unsupport_udp), 0);
			}
			break;
		case R.id.button_send:
			if(procType==0)
			{
				sendToTCPClient();
			}
			if(procType==1)
			{
				sendToTCPServer();
			}
			if(procType==2)
			{
			}
			break;
		case R.id.button_clear:
			clearMsg();
			break;
		default:
			break;
		}
	}
	
	private boolean isTCPServerRunning()
	{
		return mTcpServer != null;
	}
	
	private SimpleTCPServer mTcpServer = null;
	private void startTCPServer()
	{
		if(mTcpServer!=null)
			mTcpServer.destory();
		mTcpServer = null;
		
		int port = 0;
		try {
			port = Integer.valueOf(mPortEditText.getText().toString());
		} catch (Exception e) {
			return;
		}
		
		mTcpServer = new SimpleTCPServer(port);
		mTcpServer.setSimpleTCPServerListener(new SimpleTCPServer.SimpleTCPServerListener() {
			
			@Override
			public void onStartFailed(Throwable throwable) {
				showToastAtUIThread(throwable.getMessage(),0);
			}
			
			@Override
			public void onSocketRemoved(SimpleTCPServer simpleTCPServer, Socket socket) {
				showToastAtUIThread(socketToString(socket),0);
			}
			
			@Override
			public void onSocketAdded(SimpleTCPServer simpleTCPServer, Socket socket) {
				showToastAtUIThread(socketToString(socket),0);
			}
			
			@Override
			public void onServerStoped() {
				mProcTypeSpinner.post(new Runnable(){
					@Override
					public void run() {
						stopTCPServer();
					}
				});
			}
			
			@Override
			public void onReceivedData(SimpleTCPServer simpleTCPServer, final Socket socket,
					byte[] buffer, int offset, int len) {
				doReceivedData(socket, buffer, offset, len);
			}
		});
		mTcpServer.start();
		lockEditAddr();
	}
	
	private String socketToString(Socket socket)
	{
		String hostName = "";
		try {
			hostName = socket.getInetAddress().getHostName();
		} catch (Exception e) {
			hostName = socket.getInetAddress().toString();
			e.printStackTrace();
		}
		return hostName+":"+socket.getPort();
	}
	
	private String getHexString(byte []data,int offset,int count)
	{
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0;i<count;i++)
		{
			stringBuilder.append(String.format("%02x ", (data[offset+i])&0xff ));
		}
		return stringBuilder.toString();
	}
	
	private void stopTCPServer()
	{
		if(mTcpServer!=null)
		{
			mTcpServer.destory();
			mTcpServer = null;
			unlockEditAddr();
		}
	}
	
	private void sendToTCPServer()
	{
		if(mTcpServer!=null)
		{
			CharSequence msg = mMsgEditText.getText();
			if(msg.length()>0)
			{
				List<Socket> clients= mTcpServer.getClientsCopy();
				Iterator<Socket> iterator = clients.iterator();
				while(iterator.hasNext())
				{
					Socket socket = iterator.next();
					mTcpServer.sendData(socket, msg.toString().getBytes());
				}
			}
		}
		else
		{
			showToast(getActivity().getString(R.string.server_isnot_started), 0);
		}
	}

	private TCPClient mTcpClient = null;
	private boolean isTCPClientRunning()
	{
		return mTcpClient != null;
	}
	
	private void startTCPClient()
	{
		String addr = "";
		addr = mAddrEditText.getText().toString();
		if(addr.length()<=0)
			return;
		
		int port = 0;
		try {
			port = Integer.valueOf(mPortEditText.getText().toString());
		} catch (Exception e) {
			return;
		}
		
		mTcpClient = new TCPClient(addr, port)
		{
			@Override
			public void connectHasException(Throwable throwable) {
				showToastAtUIThread(throwable.getMessage(),0);
			}
			
			@Override
			public void onStoped() {
				mProcTypeSpinner.post(new Runnable(){
					@Override
					public void run() {
						stopTCPClient();
					}
				});
			}
			
			@Override
			public void onReceived(Socket socket,byte[] data, int offset, int count) {
				doReceivedData(socket, data, offset, count);
			}
		};
		mTcpClient.start();
		lockEditAddr();
	}

	private Socket mProSocket = null;
	private void doReceivedData(final Socket socket,byte[] data, int offset, int count)
	{
		final String msg = new String(data, offset, count);
		final String hexMsg = getHexString(data, offset, count);
		mProcTypeSpinner.post(new Runnable() {
			@Override
			public void run() {
				if(mShowSenderCheckBox.isChecked())
				{
					if(mProSocket != socket)
					{
						addMsgLn("");
						addMsg("[Received from "+socketToString(socket)+"]:\n");
					}
					mProSocket = socket;
				}
				else
				{
					mProSocket = null;
				}

				if(mShowAsHexCheckBox.isChecked())
				{
					addMsg(hexMsg);
				}
				else
				{
					addMsg(msg);
				}
			}
		});
	}
	
	private void stopTCPClient()
	{
		if(mTcpClient!=null)
		{
			mTcpClient.destory();
			mTcpClient = null;
			unlockEditAddr();
		}
	}
	
	private void sendToTCPClient()
	{
		if(mTcpClient!=null)
		{
			CharSequence msg = mMsgEditText.getText();
			if(msg.length()>0)
			{
				mTcpClient.sendData( msg.toString().getBytes());
			}
		}
		else
		{
			showToast(getActivity().getString(R.string.not_connected), 0);
		}
	}
	
	private void lockEditAddr()
	{
		mAddrEditText.setEnabled(false);
		mPortEditText.setEnabled(false);
		mProcTypeSpinner.setEnabled(false);
		mConnectButton.setText(R.string.disconnect);
	}
	
	private void unlockEditAddr()
	{
		mAddrEditText.setEnabled(true);
		mPortEditText.setEnabled(true);
		mProcTypeSpinner.setEnabled(true);
		mConnectButton.setText(R.string.connect);
	}
	
	private void saveAutoText()
	{
		String addr = mAddrEditText.getText().toString();
		if(addr.length()>0)
		{
			List<String> addrs = loadAddrs();
			if(addrs.contains(addr))
				addrs.remove(addr);
			addrs.add(0, addr);
			if(addrs.size()>10)
				addrs.remove(addrs.size()-1);
			saveAddrs(addrs);
		}
		String port = mPortEditText.getText().toString();
		if(port.length()>0)
		{
			List<String> ports = loadPorts();
			if(ports.contains(port))
				ports.remove(port);
			ports.add(0, port);
			if(ports.size()>10)
				ports.remove(ports.size()-1);
			savePorts(ports);
		}
		setAutoText();
	}
	
	private void setAutoText()
	{
		List<String> addrs = loadAddrs();
		String []array = new String[addrs.size()];
		addrs.toArray(array);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line,array);
		mAddrEditText.setAdapter(arrayAdapter);
		if(mAddrEditText.getText().length()==0&&addrs.size()>0)
		{
			mAddrEditText.setText(addrs.get(0));
		}
		

		List<String> ports = loadPorts();
		array = new String[ports.size()];
		ports.toArray(array);
		arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_dropdown_item_1line,array);
		mPortEditText.setAdapter(arrayAdapter);
		if(mPortEditText.getText().length()==0&&ports.size()>0)
		{
			mPortEditText.setText(ports.get(0));
		}
	}

	private List<String> loadAddrs()
	{
		SharedPreferences preferences = getActivity().getSharedPreferences("saved_addrs_and_ports", 0);
		return StringTranslate.decodeList(preferences.getString("addrs", "127.0.0.1#192.168.1.1"));
	}
	
	private void saveAddrs(List<String> addrs)
	{
		SharedPreferences preferences = getActivity().getSharedPreferences("saved_addrs_and_ports", 0);
		Editor editor = preferences.edit();
		editor.putString("addrs",StringTranslate.encodeList(addrs));
		editor.commit();
	}
	
	private List<String> loadPorts()
	{
		SharedPreferences preferences = getActivity().getSharedPreferences("saved_addrs_and_ports", 0);
		return StringTranslate.decodeList(preferences.getString("ports", "8080"));
	}
	
	private void savePorts(List<String> ports)
	{
		SharedPreferences preferences = getActivity().getSharedPreferences("saved_addrs_and_ports", 0);
		Editor editor = preferences.edit();
		editor.putString("ports",StringTranslate.encodeList(ports));
		editor.commit();
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			mAddrEditText.setVisibility(View.VISIBLE);
			break;
		case 1:
			mAddrEditText.setVisibility(View.GONE);
			break;
		case 2:
			mAddrEditText.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	
	public void showToastAtUIThread(final String msg,final int duration)
	{
		mProcTypeSpinner.post(new Runnable() {
			@Override
			public void run() {
				showToast(msg,duration);
			}
		});
	}
	
	private Toast mToast = null;
	public void showToast(String msg,int duration)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast = Toast.makeText(getActivity(), msg, duration);
		mToast.show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
}
