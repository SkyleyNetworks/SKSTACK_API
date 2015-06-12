import com.skyley.skstack_ip.api.SKDebugListener;
import com.skyley.skstack_ip.api.SKHanDevice;



public class Test {

	public static void main(String args[]) {
		System.err.println("Test Start!");

		SKHanDevice device1 = new SKHanDevice();
		device1.connect("COM3");

		SKHanDevice device2 = new SKHanDevice();
		device2.connect("COM4");

		Debug debug = new Debug();
		device1.setSKDebugListener(debug);
		device2.setSKDebugListener(debug);

		HanPanaTest hpt = new HanPanaTest(device1, device2);
		hpt.doTest();

		/*
		RegisterTest rt = new RegisterTest(device1);
		rt.doTest();

		TableTest tt = new TableTest(device1);
		tt.doTest();

		ScanTest st = new ScanTest(device1, device2);
		st.doTest();

		PanaTest pt = new PanaTest(device1, device2);
		pt.doTest();

		UdpTest udptest = new UdpTest(device1, device2);
		byte[] data = {0x12, 0x34, 0x56, 0x78};


		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 300; i++) {
			sb.append("01020304");
		}

		udptest.doTest(data, 10, 10); // (送信文字列, 送信試行回数, 送信間隔[msec])

		TcpTest tcptest = new TcpTest(device1, device2);
		tcptest.doTest(data, 1);
		*/

		device1.close();
		device2.close();

		System.err.println("Test Done!");
	}

}

class Debug implements SKDebugListener {

	@Override
	public void debugOut(String port, String raw) {
		// TODO 自動生成されたメソッド・スタブ
		System.err.println(port + ":" + raw);
	}
}