package Marshaller;

import java.util.ArrayList;
import java.util.List;

public class ArrayConvertor implements Convertor{
	private Convertor internalConvertor;
	private int size;
	
	public ArrayConvertor(){
		
	}
	
	public ArrayConvertor(Convertor internalConvertor, int size){
		this.internalConvertor = internalConvertor;
		this.size = size;
	}
	
	public Convertor getInternalConvertor() {
		return internalConvertor;
	}



	public void setInternalConvertor(Convertor internalConvertor) {
		this.internalConvertor = internalConvertor;
	}



	public void setSize(int size){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
	
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return internalConvertor.getByteCount()*size;
	}

	@Override
	public Object fromBytes(byte[] bytes) {
		if(internalConvertor==null){
			return null;
		}
		// TODO Auto-generated method stub
		byte[] itemBytes = new byte[internalConvertor.getByteCount()];
		int position = 0;
		List array = new ArrayList();
		for(int i=0; i<size; i++){
			System.arraycopy(bytes, position, itemBytes, 0, itemBytes.length);
			array.add(internalConvertor.fromBytes(itemBytes));
			position += itemBytes.length;
		}
		return array;
	}

	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((List)data);
	}
	
	private byte[] toBytes(List data) {
		if(internalConvertor==null){
			return null;
		}
		// TODO Auto-generated method stub
		int size = data.size();
		byte[] dataBytes = new byte[internalConvertor.getByteCount() * size];
		int position = 0;
		for(Object item:data){
			byte[] itemBytes = internalConvertor.toBytes(item);
			System.arraycopy(itemBytes, 0, dataBytes, position, itemBytes.length);
			position += itemBytes.length;
		}
		return dataBytes;
	}
}
