import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.SKEventListener;
import com.skyley.skstack_ip.api.skenums.SKEventNumber;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skevents.SKERxUdp;
import com.skyley.skstack_ip.api.skevents.SKEvent;
import com.skyley.skstack_ip.api.skevents.SKGeneralEvent;


public class UdpTest implements SKEventListener {
	private SKDevice device1;
	private SKDevice device2;
	private SKEventListener oldListener1 = null;
	private SKEventListener oldListener2 = null;

	private String port1, port2; // ポート名
	private int dataLen; // 送信データ長
	private int txDone1, txDone2; // 送信処理完了回数
	private int txOK1, txOK2; // 送信処理完了のうち、送信に成功した回数
	private int txNG1, txNG2; // 送信処理完了のうち。送信に失敗した回数
	private int rxCount1, rxCount2; // 受信回数（device1での受信回数がrxCount1, device2での受信回数がrxCount2）

	public UdpTest(SKDevice device1, SKDevice device2) {
		this.device1 = device1;
		this.device2 = device2;

		oldListener1 = device1.getSKEventListener();
		oldListener2 = device2.getSKEventListener();

		port1 = device1.getPortString();
		port2 = device2.getPortString();

		device1.setSKEventListener(this);
		device2.setSKEventListener(this);
	}

	public void doTest(byte[] data, int numTx, long interval) {
		device1.resetStack();
		device2.resetStack();

		device1.setLongAddress("12345678ABCDEF01");
		device2.setLongAddress("12345678ABCDEF02");

		String ip6Address1 = device1.getIP6Address();
		String longAddress1 = device1.getLongAddress();

		String ip6Address2 = device2.getIP6Address();
		String longAddress2 = device2.getLongAddress();

		device1.registerNeighborCache(ip6Address2, longAddress2);
		device2.registerNeighborCache(ip6Address1, longAddress1);

		SendUdp su1 = new SendUdp(device1, ip6Address2, data, numTx,interval);
		SendUdp su2 = new SendUdp(device2, ip6Address1, data, numTx,interval);
		dataLen = data.length;
		Thread t1 = new Thread(su1);
		Thread t2 = new Thread(su2);

		t1.start();
		t2.start();

		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		System.out.println("Device1:");
		System.out.println(" TxTry=" + numTx);
		System.out.println(" TxDone=" + txDone1 + " TxOK=" + txOK1 + " TxNG=" + txNG1 + " Rx=" + rxCount1);
		System.out.println("");
		System.out.println("Device2:");
		System.out.println(" TxTry=" + numTx);
		System.out.println(" TxDone=" + txDone2 + " TxOK=" + txOK2 + " TxNG=" + txNG2 + " Rx=" + rxCount2);
		System.out.println("");

		/*
		for (int i = 0; i < numTx; i++) {
			device1.sendUDP((byte)2, ip6Address2, 0x0E1A, SKSecOption.PLAIN, data);
			device2.sendUDP((byte)2, ip6Address1, 0x0E1A, SKSecOption.PLAIN, data);
			SKUtil.pause(interval);
		}
		*/

		device1.setSKEventListener(oldListener1);
		device2.setSKEventListener(oldListener2);
	}

	@Override
	public void eventNotified(String port, SKEventType type, SKEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		switch (type) {
			case ERXUDP:
				//System.out.println("ERXUDP");
				//System.out.println("Port: " + port);

				SKERxUdp erxudp = (SKERxUdp)event;
				if (port.compareTo(port1) == 0 && erxudp.getDataLength() == dataLen) {
					rxCount2++;
				}
				else if (port.compareTo(port2) == 0 && erxudp.getDataLength() == dataLen) {
					rxCount1++;
				}
				//System.out.println("senderIP=" + erxudp.getSenderIP6Address());
				//System.out.println("sec=" + erxudp.isSecured());
				//System.out.println("dataLength=" + erxudp.getDataLength());
				//System.out.println("data=" + erxudp.getData());
				break;

			case EVENT:
				SKGeneralEvent gevent = (SKGeneralEvent)event;
				short number = gevent.getEventNumber();

				//System.out.println("Event: " + SKEventNumber.getEventName(number));
				//System.out.println("Port: " + port);

				//System.out.println("number=" + number);
				//System.out.println("sender=" + gevent.getSenderAddress());

				if (number == SKEventNumber.UDP_TX_DONE.getNumber()) {
					byte param = gevent.getParam();
					if (port.compareTo(port1) == 0) {
						txDone1++;
						if (param == 0) {
							txOK1++;
						}
						else if (param == 1){
							txNG1++;
						}
					}
					else if (port.compareTo(port2) == 0) {
						txDone2++;
						if (param == 0) {
							txOK2++;
						}
						else if (param == 1){
							txNG2++;
						}
					}
					//System.out.println("param=" + gevent.getParam());
				}
				break;

			default:
				break;
		}
	}
}
