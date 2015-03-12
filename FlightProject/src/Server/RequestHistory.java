package Server;

//class used for caching reply messages
public class RequestHistory {
	//message number
	private int requestNo;
	//reply message in bytes
	private byte[] replyMessage;
	
	public RequestHistory(int requestNo, byte[] replyMessage){
		this.setRequestNo(requestNo);
		this.setReplyMessage(replyMessage);
	}

	//getter and setter for attributes
	//////////////////////////////////////////////////
	public int getRequestNo() {
		return requestNo;
	}

	public void setRequestNo(int requestNo) {
		this.requestNo = requestNo;
	}

	public byte[] getReplyMessage() {
		return replyMessage;
	}

	public void setReplyMessage(byte[] replyMessage) {
		this.replyMessage = replyMessage;
	}
	//////////////////////////////////////////////////
}
