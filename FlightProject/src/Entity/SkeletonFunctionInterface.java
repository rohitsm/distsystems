package Entity;

import java.net.InetAddress;

//interface for method handler of skeleton class
public interface SkeletonFunctionInterface {
	public byte[] resolve(int messageNo, InetAddress sourceAddress, int sourcePort, byte[] data);
}
