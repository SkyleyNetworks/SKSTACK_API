import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.SKEventListener;
import com.skyley.skstack_ip.api.SKUtil;
import com.skyley.skstack_ip.api.skenums.SKEventNumber;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skenums.SKSecOption;
import com.skyley.skstack_ip.api.skevents.SKERxUdp;
import com.skyley.skstack_ip.api.skevents.SKEvent;
import com.skyley.skstack_ip.api.skevents.SKGeneralEvent;


public class PanaTest implements SKEventListener {
	private SKDevice device1; // PAA
	private SKDevice device2; // PaC
	private SKEventListener oldListener1 = null;
	private SKEventListener oldListener2 = null;
	private boolean isProcessing;

	public PanaTest(SKDevice device1, SKDevice device2) {
		this.device1 = device1;
		this.device2 = device2;

		oldListener1 = device1.getSKEventListener();
		oldListener2 = device2.getSKEventListener();

		device1.setSKEventListener(this);
		device2.setSKEventListener(this);

		isProcessing = false;
	}

	private void waitProcess() {
		isProcessing = true;
		while (isProcessing) {
			SKUtil.pause(1000);
		}
	}

	public void doTest() {
		device1.resetStack();
		device2.resetStack();

		device1.setLongAddress("12345678ABCDEF01");
		device1.setPanID(0x8888);
		device1.setPSKFromPassword("0123456789AB");
		device1.setRouteBID("00112233445566778899AABBCCDDEEFF");
		device1.startPAA();

		device2.setLongAddress("12345678ABCDEF02");
		device2.setPanID(0x8888);
		device2.setPSKFromPassword("0123456789AB");
		device2.setRouteBID("00112233445566778899AABBCCDDEEFF");
		device2.joinPAA("FE80:0000:0000:0000:1034:5678:ABCD:EF01");
		waitProcess();

		byte[] data1 = {0x12, 0x34, 0x56, 0x78};
		byte[] data2 = {0x21, 0x43, 0x65, (byte) 0xFF};

		device2.sendUDP((byte)1, "FE80:0000:0000:0000:1034:5678:ABCD:EF01", 0xE1A, SKSecOption.SEC_OR_NO_TX, data1);
		device1.sendUDP((byte)1, "FE80:0000:0000:0000:1034:5678:ABCD:EF02", 0xE1A, SKSecOption.SEC_OR_NO_TX, data2);

		device2.termPAA();
		waitProcess();

		device1.setSKEventListener(oldListener1);
		device2.setSKEventListener(oldListener2);
	}

	@Override
	public void eventNotified(String port, SKEventType type, SKEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		switch (type) {
			case ERXUDP:
				System.out.println("ERXUDP");
				System.out.println("Port: " + port);

				SKERxUdp erxudp = (SKERxUdp)event;
				System.out.println("senderIP=" + erxudp.getSenderIP6Address());
				System.out.println("sec=" + erxudp.isSecured());
				System.out.println("dataLength=" + erxudp.getDataLength());
				System.out.println("data=" + erxudp.getData());
				break;

			case EVENT:
				SKGeneralEvent gevent = (SKGeneralEvent)event;
				short number = gevent.getEventNumber();

				System.out.println("Event: " + SKEventNumber.getEventName(number));
				System.out.println("Port: " + port);
				System.out.println("number=" + number);
				System.out.println("sender=" + gevent.getSenderAddress());
				if (number == SKEventNumber.UDP_TX_DONE.getNumber()) {
					System.out.println("Param=" + gevent.getParam());
				}
				else if (number == SKEventNumber.PANA_CONNECT_DONE.getNumber() ||
							number == SKEventNumber.PANA_CONNECT_FAIL.getNumber() ||
							number == SKEventNumber.PANA_SESSION_CLOSE_DONE.getNumber() ||
							number == SKEventNumber.PANA_SESSION_CLOSE_TIMEOUT.getNumber()) {
						isProcessing = false;
				}
				break;

			default:
				break;
		}
	}


}
