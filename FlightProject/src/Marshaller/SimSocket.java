package Marshaller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SimSocket extends DatagramSocket{
	private double lossRate;
	
	public SimSocket(double lossRate) throws SocketException{
		super();
		this.lossRate = lossRate;
	}
	
	public void send(DatagramPacket p) throws IOException{
		String message = new String(p.getData(), 0, p.getLength());
		
		if(Math.random() > lossRate){
			super.send(p);
			System.out.println("Send Success: " + message);
			return;
		}
		
		System.out.println("Send Fail: " + message);
	}
	
	public void receive(DatagramPacket p) throws IOException{
		super.receive(p);
		String message = new String(p.getData(), 0, p.getLength());
		
		if(Math.random() > lossRate){
			System.out.println("Receive Success: " + message);
			return;
		}
		try {
			wait(getSoTimeout());
			System.out.println("Receive Fail: " + message);
			throw new SocketTimeoutException();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
