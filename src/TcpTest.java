import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.SKEventListener;
import com.skyley.skstack_ip.api.SKUtil;
import com.skyley.skstack_ip.api.skcommands.SKTcpStatus;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skevents.SKERxTcp;
import com.skyley.skstack_ip.api.skevents.SKETcp;
import com.skyley.skstack_ip.api.skevents.SKEvent;


public class TcpTest implements SKEventListener {
	private SKDevice device1;
	private SKDevice device2;
	private SKEventListener oldListener1 = null;
	private SKEventListener oldListener2 = null;
	private byte handle;
	private boolean isProcessing;

	public TcpTest(SKDevice device1, SKDevice device2) {
		this.device1 = device1;
		this.device2 = device2;

		oldListener1 = device1.getSKEventListener();
		oldListener2 = device2.getSKEventListener();

		device1.setSKEventListener(this);
		device2.setSKEventListener(this);

		handle = 0;
		isProcessing = false;
	}

	private void waitProcess() {
		isProcessing = true;
		while (isProcessing) {
			SKUtil.pause(1000);
		}
	}

	public void doTest(byte[] data, int numTx) {
		device1.resetStack();
		device2.resetStack();

		// Coonect device1 --> device2
		device1.setLongAddress("12345678ABCDEF01");
		device2.setLongAddress("12345678ABCDEF02");

		String ip6Address = device2.getIP6Address();
		device1.connectTCP(ip6Address, 0x0E1A, 0x0E1A);
		waitProcess();

		if (handle == 0) {
			return;
		}

		// TxRx device1 <--> device2
		for (int i = 0; i < numTx; i++) {
			device1.sendTCP(handle, data);
			waitProcess();
			device2.sendTCP(handle, data);
			waitProcess();
		}

		// Close connection
		device1.closeTCP(handle);
		waitProcess();

		device1.setSKEventListener(oldListener1);
		device2.setSKEventListener(oldListener2);
	}

	@Override
	public void eventNotified(String port, SKEventType type, SKEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		switch (type) {
			case ERXTCP:
				System.out.println("ERXTCP");
				System.out.println("Port: " + port);

				SKERxTcp erxtcp = (SKERxTcp)event;
				System.out.println("senderIP=" + erxtcp.getSenderIP6Address());
				System.out.println("dataLength=" + erxtcp.getDataLength());
				System.out.println("data=" + erxtcp.getData());
				break;

			case ETCP:
				System.out.println("ETCP");
				System.out.println("Port: " + port);

				SKETcp etcp = (SKETcp)event;
				byte status = etcp.getStatus();
				handle = etcp.getHandle();
				System.out.println("status=" + status + "(" + SKTcpStatus.getName(status) + ")");
				System.out.println("handle=" + handle);

				if (status == SKTcpStatus.CONNECT_DONE.getStatus()) {
					System.out.println("ip6Address=" + etcp.getIP6Address());
					System.out.println("rport=" + etcp.getRPort());
					System.out.println("lport=" + etcp.getLPort());
				}

				isProcessing = false;
				break;

			default:
				break;
		}
	}

}
