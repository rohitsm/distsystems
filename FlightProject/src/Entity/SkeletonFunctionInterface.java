package Entity;

import java.net.InetAddress;

public interface SkeletonFunctionInterface {
	public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data);
}
