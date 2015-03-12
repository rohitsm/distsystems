package Server;

public class RequestHistory {
	private int requestNo;
	private byte[] replyMessage;
	
	public RequestHistory(int requestNo, byte[] replyMessage){
		this.setRequestNo(requestNo);
		this.setReplyMessage(replyMessage);
	}

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
}
