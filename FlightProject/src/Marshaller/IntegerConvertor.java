package Marshaller;



public class IntegerConvertor implements Convertor{
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Integer.BYTES;
	}
	
	@Override
	public Integer fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		Integer data = 0;
		for(int i=0; i<data.BYTES; i++){
			data = data << 8;
			data = data | (bytes[i] & 0xff);
		}
		return data;
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((Integer)data);
	}
	
	private byte[] toBytes(Integer data){
		int numOfBytes = data.BYTES;
		byte[] bytes = new byte[numOfBytes];
		for(int i=0; i<numOfBytes; i++){
			bytes[numOfBytes-1-i] = (byte)(data >> (8*i) & 0xff);
		}
		return bytes;
	}
}
