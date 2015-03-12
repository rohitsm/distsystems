package Entity;

import java.net.DatagramPacket;

public interface RemoteObjectInterface {
	public byte[] processMessage(DatagramPacket packet);
}
