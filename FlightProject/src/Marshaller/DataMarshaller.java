package Marshaller;
import java.util.ArrayList;
import java.util.List;

import Entity.FlightDetails;

public class DataMarshaller {
	private final byte nullByte = 0;
	private final byte boolByte = 1;
	private final byte integerByte = 2;
	private final byte longByte = 3;
	private final byte stringByte = 4;
	private final byte arrayByte = 5;
	private final byte floatByte = 6;
	private final byte flightDetailsByte = 7;
	private final byte remoteObjByte = 8;
	
	public byte[] appendBytes(byte[] bytes1, byte[] bytes2){
		byte[] bytes = new byte[bytes1.length + bytes2.length];
		System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
		System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
		return bytes;
	}
	
	public byte[] subBytes(byte[] bytes, int start, int end){
		byte[] subBytes = new byte[end-start];
		System.arraycopy(bytes, start, subBytes, 0, subBytes.length);
		return subBytes;
	}
	
	private Convertor getConvertor(byte type){
		switch(type){
		case boolByte:
			return new BooleanConvertor();
		case integerByte:
			return new IntegerConvertor();
		case longByte:
			return new LongConvertor();
		case stringByte:
			return new StringConvertor();
		case arrayByte:
			return new ArrayConvertor();
		case floatByte:
			return new FloatConvertor();
		case flightDetailsByte:
			return new FlightDetailsConvertor();
		}
		return null;
	}
	
	public byte[] toMessage(boolean data){
		return appendBytes(new byte[]{boolByte}, getConvertor(boolByte).toBytes(data));
	}
	
	public byte[] toMessage(int data){
		return appendBytes(new byte[]{integerByte}, getConvertor(integerByte).toBytes(data));
	}
	
	public byte[] toMessage(long data){
		return appendBytes(new byte[]{longByte}, getConvertor(longByte).toBytes(data));
	}
	
	public byte[] toMessage(String data){
		if(data==null)
			return nullMessage();
		return appendBytes(new byte[]{stringByte, (byte)data.length()}, getConvertor(stringByte).toBytes(data));
	}
	
	public byte[] toMessage(List<Integer> data){
		if(data==null)
			return nullMessage();
		ArrayConvertor convertor = (ArrayConvertor) getConvertor(arrayByte);
		convertor.setInternalConvertor(getConvertor(integerByte));
		convertor.setSize(data.size());
		return appendBytes(new byte[]{arrayByte, integerByte ,(byte)data.size()}, convertor.toBytes(data));
	}
	
	public byte[] toMessage(float data){
		return appendBytes(new byte[]{floatByte}, getConvertor(floatByte).toBytes(data));
	}
	
	public byte[] toMessage(FlightDetails data){
		if(data==null)
			return nullMessage();
		return appendBytes(new byte[]{flightDetailsByte}, getConvertor(flightDetailsByte).toBytes(data));
	}
	
	public byte[] nullMessage(){
		return new byte[]{nullByte};
	}
	
	public Object fromMessage(byte[] message){
		Object data = null;
		List dataBuffer = new ArrayList();
		if(message.length < 2){
			return data;
		}
		int counter = 0;
		while(counter < message.length){
			Convertor convertor = null;
			switch(message[counter]){
			case nullByte:
				convertor = null;
			case boolByte:
			case integerByte:
			case longByte:
			case floatByte:
			case flightDetailsByte:
				convertor = getConvertor(message[counter]);
				counter++;
				break;
			case stringByte:
				StringConvertor stringConvertor = (StringConvertor) getConvertor(message[counter]);
				counter++;
				stringConvertor.setSize(message[counter]);
				counter++;
				convertor = stringConvertor;
				break;
			case arrayByte:
				ArrayConvertor arrayConvertor = (ArrayConvertor) getConvertor(message[counter]);
				counter++;
				arrayConvertor.setInternalConvertor(getConvertor(message[counter]));
				counter++;
				arrayConvertor.setSize(message[counter]);
				counter++;	
				convertor = arrayConvertor;
				break;
			case remoteObjByte:
				break;
			}
			if(convertor == null)
				data = null;
			else{
				data = convertor.fromBytes(subBytes(message, counter, counter+convertor.getByteCount()));
				counter += convertor.getByteCount();
			}
			dataBuffer.add(data);
		}
		if(dataBuffer.size() > 1){
			return dataBuffer;
		}
		return data;
	}
	
	public static void main(String[] args){
		byte[] a = {0, 0, 0};
		byte[] b = {1, 3, 5};
		byte[] c = new DataMarshaller().appendBytes(a, b);
		byte[] d = new DataMarshaller().subBytes(c, 3, 5);
		
		byte[] printing = d;
		for(int i=0; i<printing.length; i++){
			System.out.println(printing[i]);
		}
	}
}
