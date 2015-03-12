package Marshaller;

public class BooleanConvertor implements Convertor{

	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		if(bytes[0]==0){
			return false;
		}
		return true;
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return fromBytes((Boolean) data);
	}
	
	private byte[] fromBytes(Boolean data){
		return data?new byte[]{1}: new byte[]{0};
	}
	
}
