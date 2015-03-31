package Marshaller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

//a dummy datagram socket used for simulating packet loss
public class SimSocket extends DatagramSocket{
	//rate of packet loss
	private double lossRate;
	//network dealy
	private int networkDelay;
	//queue to mimic received buffer
	private Queue<DatagramPacket> receive;
	
	public SimSocket(double lossRate, int networkDelay) throws SocketException{
		super();
		this.lossRate = lossRate;
		this.networkDelay = networkDelay;
		receive = new ConcurrentLinkedQueue();
		
		//sets time out to 1000 ms
		setSoTimeout(1000);
		
		//create thread to recieve packets in advance and store them in queue after a delay
		new Thread(){
			public void run(){
				while(true){
					byte[] buffer = new byte[65536];
					DatagramPacket received = new DatagramPacket(buffer, buffer.length);
					try {
						realReceive(received);
						//if packet is not loss
						if(Math.random() > lossRate){
							//generate delay
							long delay = (long) (Math.random() * networkDelay);
							new Thread(){
								public void run(){
									try {
										Thread.sleep(delay);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									receive.add(received);
								}
							}.start();
						}else{
							String message = new String(received.getData(), 0, received.getLength());
							System.out.println("Receive Fail: " + message);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}.start();
	}
	
	@Override
	//sends datagram packet
	public void send(DatagramPacket p) throws IOException{
		String message = new String(p.getData(), 0, p.getLength());
		//use random generator to simulate chance
		if(Math.random() > lossRate){
			//if packet not loss
			//generate delay
			long delay = (long) (Math.random() * networkDelay);
			//delay sending of packet 
			new Thread(){
				public void run(){
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						//send packet
						realSend(p);
						//print success message
						System.out.println("Send Success: " + message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			return;
		}
		
		//if packet loss
		//print fail message
		System.out.println("Send Fail: " + message);
	}
	
	@Override
	//receives datagram packet
	public void receive(DatagramPacket p) throws IOException{
		//store waiting time
		int waitingTime = getSoTimeout();
		//store start time
		long startTime = System.currentTimeMillis();
		//wait time not over
		while((System.currentTimeMillis() - startTime) <= waitingTime){
			//if receive queue has packet
			if(receive.peek()!=null){
				//initialize packet from packet in queue
				DatagramPacket received = receive.poll();
				byte[] data = received.getData();
				p.setAddress(received.getAddress());
				p.setPort(received.getPort());
				System.arraycopy(received.getData(), received.getOffset() , p.getData(), 0, received.getLength());
				p.setData(p.getData(), received.getOffset(), received.getLength());
				String message = new String(p.getData(), 0, p.getLength());
				System.out.println("Receive Success: " + message);
				return;
			}
		}
		//if wait time over, throw time out exception
		throw new SocketTimeoutException();
	}
	
	private void realSend(DatagramPacket p) throws IOException{
		super.send(p);
	}
	
	private void realReceive(DatagramPacket p) throws IOException{
		super.receive(p);
	}
}
