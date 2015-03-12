package Entity;

import java.net.DatagramPacket;

//this interface is for the skeleton object
//roles of skeleton
//-unmarshalling of parameters
//-invoking method on actual implemented object
//-marshalling and sending of results

public interface SkeletonInterface {
	//takes in a packet returns a reply message in bytes
	public byte[] processMessage(DatagramPacket packet);
}
