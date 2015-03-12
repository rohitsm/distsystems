package Marshaller;

public interface Convertor {
	public int getByteCount();
	public Object fromBytes(byte[] bytes);
	public byte[] toBytes(Object data);
}
