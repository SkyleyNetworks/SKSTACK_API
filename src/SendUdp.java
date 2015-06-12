import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.SKUtil;
import com.skyley.skstack_ip.api.skenums.SKSecOption;


public class SendUdp implements Runnable {
	private SKDevice device;
	private String ip6Address;
	private byte[] data;
	private int numTx;
	private long interval;

	public SendUdp(SKDevice device, String ip6Address, byte[] data, int numTx, long interval) {
		this.device = device;
		this.ip6Address = ip6Address;
		this.data = data;
		this.numTx = numTx;
		this.interval = interval;
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		for (int i = 0; i < numTx; i++) {
			device.sendUDP((byte)2, ip6Address, 0x0E1A, SKSecOption.PLAIN, data);
			SKUtil.pause(interval);
		}

	}

}
