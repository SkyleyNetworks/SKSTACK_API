import java.util.Map;

import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.SKEventListener;
import com.skyley.skstack_ip.api.SKUtil;
import com.skyley.skstack_ip.api.skenums.SKEventNumber;
import com.skyley.skstack_ip.api.skenums.SKEventType;
import com.skyley.skstack_ip.api.skenums.SKScanMode;
import com.skyley.skstack_ip.api.skevents.SKEEdScan;
import com.skyley.skstack_ip.api.skevents.SKEPanDesc;
import com.skyley.skstack_ip.api.skevents.SKEvent;
import com.skyley.skstack_ip.api.skevents.SKGeneralEvent;


public class ScanTest implements SKEventListener {
	private SKDevice device1; // Coordinator
	private SKDevice device2;
	private SKEventListener oldListener1 = null;
	private SKEventListener oldListener2 = null;
	private boolean isScanning;

	public ScanTest(SKDevice device1, SKDevice device2) {
		this.device1 = device1;
		this.device2 = device2;

		oldListener1 = device1.getSKEventListener();
		oldListener2 = device2.getSKEventListener();

		device1.setSKEventListener(this);
		device2.setSKEventListener(this);

		isScanning = false;
	}

	private void waitScan() {
		isScanning = true;
		while (isScanning) {
			SKUtil.pause(1000);
		}
	}

	public void doTest() {
		device1.resetStack();
		device2.resetStack();

		System.out.println("ED Scan.");
		device1.scanChannel(SKScanMode.ED_SCAN, "FFFFFFFF", (byte)6);
		waitScan();

		System.out.println("Set Channel and PAN ID.");
		device1.setChannel((byte)0x24);
		device1.setPanID(0x1234);
		device1.setBeaconResponseOn();

		System.out.println("Active Scan with IE.");
		device2.scanChannel(SKScanMode.ACTIVE_SCAN_WITH_IE, "FFFFFFFF", (byte)6);
		waitScan();

		device2.setParingID("SCANTEST");
		System.out.println("Set ParingID SCANTEST");
		System.out.println("Acitve Scan with IE.");
		device2.scanChannel(SKScanMode.ACTIVE_SCAN_WITH_IE, "FFFFFFFF", (byte)6);
		waitScan();

		System.out.println("Active Scan without IE.");
		device2.scanChannel(SKScanMode.ACTIVE_SCAN_WITHOUT_IE, "FFFFFFFF", (byte)6);
		waitScan();

		device1.setSKEventListener(oldListener1);
		device2.setSKEventListener(oldListener2);
	}

	@Override
	public void eventNotified(String port, SKEventType type, SKEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		switch (type) {
			case EEDSCAN:
				System.out.println("EEDSCAN");
				System.out.println("Port: " + port);

				SKEEdScan eedscan = (SKEEdScan)event;
				for (Map.Entry<Byte, Short> entry : eedscan.getEdLevel().entrySet()) {
					System.out.println("ch:" + entry.getKey() + " rssi:" + entry.getValue());
				}
				break;

			case EPANDESC:
				System.out.println("EPANDESC");
				System.out.println("Port: " + port);

				SKEPanDesc epandesc = (SKEPanDesc)event;
				System.out.println("Channel:" + epandesc.getChannel());
				System.out.println("ChannelPage:" + epandesc.getChannelPage());
				System.out.println("Pan ID:" + epandesc.getPanID());
				System.out.println("Address:" + epandesc.getAddress());
				System.out.println("LQI:" + epandesc.getLQI());
				System.out.println("Pair ID:" + epandesc.getPairID());
				break;

			case EVENT:
				SKGeneralEvent gevent = (SKGeneralEvent)event;
				short number = gevent.getEventNumber();

				System.out.println("Event: " + SKEventNumber.getEventName(number));
				System.out.println("Port: " + port);
				System.out.println("number=" + number);
				System.out.println("sender=" + gevent.getSenderAddress());

				if (number == SKEventNumber.ED_SCAN_DONE.getNumber() ||
					number == SKEventNumber.ACTIVE_SCAN_DONE.getNumber()) {
					isScanning = false;
				}
				break;

			default:
				break;
		}
	}

}
