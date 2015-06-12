import com.skyley.skstack_ip.api.SKDevice;


public class RegisterTest {
	private SKDevice device;
	private final long INTERVAL = 10;

	public RegisterTest(SKDevice device) {
		this.device = device;
	}

	private void output(String label, String value) {
		System.out.println(label + ":" + value);
	}

	private void output(String label, byte value) {
		System.out.printf("%s:%X\n", label, value);
	}

	/*
	private void output(String label, short value) {
		System.out.printf("%s:%X\n", label, value);
	}
	 */

	private void output(String label, int value) {
		System.out.printf("%s:%X\n", label, value);
	}

	private void output(String label, long value) {
		System.out.printf("%s:%X\n", label, value);
	}

	private void SKWait() {
		try {
			Thread.sleep(INTERVAL);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void doTest() {
		device.resetStack();

		System.out.println("###");
		System.out.println("### Register Test");
		System.out.println("### Port:" + device.getPortString());
		System.out.println("###");
		System.out.println("Version:" + device.getVersion());
		SKWait();

		output("IP6Address", device.getIP6Address());
		SKWait();

		output("ShortAddress", device.getShortAddress());
		SKWait();

		output("LongAddress", device.getLongAddress());
		SKWait();
		device.setLongAddress("1111222233334444");
		SKWait();
		output("LongAddress", device.getLongAddress());
		SKWait();

		output("Channel", device.getChannel());
		SKWait();
		device.setChannel((byte)0x2A);
		SKWait();
		output("Channel", device.getChannel());
		SKWait();

		output("PanID", device.getPanID());
		SKWait();
		device.setPanID(0xABCD);
		SKWait();
		output("PanID", device.getPanID());
		SKWait();

		output("SecFrameCounter", device.getSecFrameCounter());
		SKWait();

		output("ParingID", device.getParingID());
		SKWait();
		device.setParingID("PAIR1234");
		SKWait();
		output("ParingID", device.getParingID());
		SKWait();

		output("BeaconResponse", device.getBeaconResponse());
		SKWait();
		device.setBeaconResponseOn();
		SKWait();
		output("BeaconResponse", device.getBeaconResponse());
		SKWait();

		output("PANA SessionLifetime", device.getPANASessionLifetime());
		SKWait();
		device.setPANASessionLifetime((long)0x12345678L);
		SKWait();
		output("PANA SessionLifetime", device.getPANASessionLifetime());
		SKWait();

		output("PANA AutoAuth", device.getPANAAutoAuth());
		SKWait();
		device.setPANAAutoAuthOff();
		SKWait();
		output("PANA AutoAuth", device.getPANAAutoAuth());
		SKWait();

		output("MAC BroadcastSec", device.getMACBroadcastSec());
		SKWait();
		device.setMACBroadcastSecOff();
		SKWait();
		output("MAC BroadcastSec", device.getMACBroadcastSec());
		SKWait();

		output("ICMP Rx", device.getICMPRx());
		SKWait();
		device.setICMPRxOff();
		SKWait();
		output("ICMP Rx", device.getICMPRx());
		SKWait();

		output("Tx PowerLevel", device.getTxPowerLevel());
		SKWait();
		device.setTxPowerLevel((byte)4);
		SKWait();
		output("Tx PowerLevel", device.getTxPowerLevel());
		SKWait();

		output("AckWaitingTime", device.getAckWaitingTime());
		SKWait();
		device.setAckWaitingTime((short)500);
		SKWait();
		output("AckWaitingTime", device.getAckWaitingTime());
		SKWait();

		output("DataWhitening", device.getDataWhitening());
		SKWait();
		device.setDataWhiteningOff();
		SKWait();
		output("DataWhitening", device.getDataWhitening());
		SKWait();

		output("LowDataRate", device.getLowDataRateFlag());
		SKWait();
		device.setLowDataRateOn();
		SKWait();
		output("LowDataRate", device.getLowDataRateFlag());
		SKWait();

		//svalue = device.getCCAThresholdForHighRate();
		//output("CCA High", svalue);

		//svalue = device.getCCAThresholdForLowRate();
		//output("CCA Low", svalue);

		//bvalue = device.getDSNMultipleCheck();
		//output("DSN", bvalue);

		output("TxLimit", device.getTxLimit());
		SKWait();
		device.setTxLimitOff();
		SKWait();
		output("TxLimit", device.getTxLimit());
		SKWait();

		output("TxLimitWorking", device.getTxLimitWorking());
		SKWait();

		output("CumulativeTxTime", device.getCumulativeTxTime());
		SKWait();

		output("Echoback", device.getCommandEchoback());
		device.setCommandEchobackOff();
		output("Echoback", device.getCommandEchoback());

		output("AutoLoad", device.getAutoLoad());
		SKWait();
		device.setAutoLoadOn();
		SKWait();
		output("AutoLoad", device.getAutoLoad());
		SKWait();

		System.out.println("###");
		System.out.println("### Register Test Done");
		System.out.println("###");
	}
}
