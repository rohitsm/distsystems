package Marshaller;

public class FloatConvertor implements Convertor{
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return Float.BYTES;
	}
	
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		return Float.intBitsToFloat(new IntegerConvertor().fromBytes(bytes));
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((Float)data);
	}
	
	private byte[] toBytes(Float data){
		return new IntegerConvertor().toBytes(Float.floatToRawIntBits(data));
	}

}
