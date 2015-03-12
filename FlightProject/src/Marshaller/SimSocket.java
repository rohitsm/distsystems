package Marshaller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

//a dummy datagram socket used for simulating packet loss
public class SimSocket extends DatagramSocket{
	//rate of packet loss
	private double lossRate;
	
	public SimSocket(double lossRate) throws SocketException{
		super();
		this.lossRate = lossRate;
	}
	
	@Override
	//sends datagram packet
	public void send(DatagramPacket p) throws IOException{
		String message = new String(p.getData(), 0, p.getLength());
		//use random generator to simulate chance
		if(Math.random() > lossRate){
			//if packet not loss
			//send packet
			super.send(p);
			//print success message
			System.out.println("Send Success: " + message);
			return;
		}
		
		//if packet loss
		//print fail message
		System.out.println("Send Fail: " + message);
	}
	
	@Override
	//receives datagram packet
	public void receive(DatagramPacket p) throws IOException{
		super.receive(p);
		String message = new String(p.getData(), 0, p.getLength());
		
		//use random generator to simulate chance
		if(Math.random() > lossRate){
			//if packet not loss
			//print success message
			System.out.println("Receive Success: " + message);
			return;
		}
		try {
			//if time out is set
			if(getSoTimeout()>0){
				//wait for time out
				wait(getSoTimeout());
				//print fail message
				System.out.println("Receive Fail: " + message);
				//throw time out exception
				throw new SocketTimeoutException();
			//else
			}else{
				//print fail message
				System.out.println("Receive Fail: " + message);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
