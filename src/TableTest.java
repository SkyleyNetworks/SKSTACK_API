import com.skyley.skstack_ip.api.SKDevice;
import com.skyley.skstack_ip.api.skevents.SKEAddr;
import com.skyley.skstack_ip.api.skevents.SKEHandle;
import com.skyley.skstack_ip.api.skevents.SKENeighbor;


public class TableTest {
	private SKDevice device;

	public TableTest(SKDevice device) {
		this.device = device;
	}

	public void doTest() {
		SKEAddr[] aryEaddr = device.getIP6AddressTable();
		if (aryEaddr != null) {
			System.out.println("IP6Addrees Table");
			for (SKEAddr a : aryEaddr) {
				System.out.println(a.getIP6Address());
			}
		}

		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF11", "12345678ABCDEF11");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:0000:EF22", "12345678ABCDEF22");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF33", "12345678ABCDEF33");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF44", "12345678ABCDEF44");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF55", "12345678ABCDEF55");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF66", "12345678ABCDEF66");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:0000:0077", "12345678ABCDEF77");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF88", "12345678ABCDEF88");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EF99", "12345678ABCDEF99");
		device.registerNeighborCache("FE80:0000:0000:0000:1034:5678:ABCD:EFAA", "12345678ABCDEFAA");

		SKENeighbor[] aryEneighbor = device.getNeighborTable();
		if (aryEneighbor != null) {
			System.out.println("Neighbor Table");
			for (SKENeighbor n : aryEneighbor) {
				System.out.print(n.getIP6Address() + " ");
				System.out.print(n.getLongAddress() + " ");
				System.out.println(Integer.toHexString(n.getShortAddress()));
			}
		}

		SKEHandle[] aryEhandle = device.getTCPHandleTable();
		if (aryEhandle != null) {
			System.out.println("Handle Table");
			for (SKEHandle h : aryEhandle) {
				System.out.print(h.getHandle() + " ");
				System.out.print(h.getIP6Address() + " ");
				System.out.print(Integer.toHexString(h.getRPort()) + " ");
				System.out.println(Integer.toHexString(h.getLPort()));
			}
		}
	}

}
