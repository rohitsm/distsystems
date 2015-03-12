package Marshaller;

import java.util.ArrayList;
import java.util.List;

//implementation of an array (list) convertor
public class ArrayConvertor implements Convertor{
	//an internal convertor for converting each item in the array to byte array
	private Convertor internalConvertor;
	//size of the array to be converted
	private int size;
	
	public ArrayConvertor(){
		
	}
	
	//getter and setter of private variables
	/////////////////////////////////////////////////////////////
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
/////////////////////////////////////////////////////////////
	
	//returns the byte count of object to be converted
	@Override
	public int getByteCount() {
		// TODO Auto-generated method stub
		return internalConvertor.getByteCount()*size;
	}

	//converts from a byte array to a object
	@Override
	public Object fromBytes(byte[] bytes) {
		// TODO Auto-generated method stub
		if(internalConvertor==null){
			return null;
		}
		//create a buffer to store bytes of one array item
		byte[] itemBytes = new byte[internalConvertor.getByteCount()];
		int position = 0;
		List array = new ArrayList();
		for(int i=0; i<size; i++){
			//retrieve subsets of byte array corresponding to one array item
			System.arraycopy(bytes, position, itemBytes, 0, itemBytes.length);
			//convert byte array to array item and add to list
			array.add(internalConvertor.fromBytes(itemBytes));
			position += itemBytes.length;
		}
		//return item array as list
		return array;
	}

	//converts from a object to a corresponding byte array
	@Override
	public byte[] toBytes(Object data) {
		// TODO Auto-generated method stub
		return toBytes((List)data);
	}
	
	//actual implementation of object to byte array conversion
	private byte[] toBytes(List data) {
		// TODO Auto-generated method stub
		if(internalConvertor==null){
			return null;
		}
		int size = data.size();
		//create buffer to store bytes of array object
		byte[] dataBytes = new byte[getByteCount()];
		int position = 0;
		//for each item in array
		for(Object item:data){
			//convert item to byte array
			byte[] itemBytes = internalConvertor.toBytes(item);
			//add item byte array to buffer
			System.arraycopy(itemBytes, 0, dataBytes, position, itemBytes.length);
			position += itemBytes.length;
		}
		//return buffer
		return dataBytes;
	}
}
