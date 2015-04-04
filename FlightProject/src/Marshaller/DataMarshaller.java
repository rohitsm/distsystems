package Marshaller;
import java.util.ArrayList;
import java.util.List;

import Entity.FlightDetails;

public class DataMarshaller {
	//byte representation for data types
	private final byte nullByte = 0;
	private final byte boolByte = 1;
	private final byte integerByte = 2;
	private final byte longByte = 3;
	private final byte stringByte = 4;
	private final byte arrayByte = 5;
	private final byte floatByte = 6;
	private final byte flightDetailsByte = 7;
	private final byte remoteObjByte = 8;
	
	//combines two byte arrays together
	public byte[] appendBytes(byte[] bytes1, byte[] bytes2){
		byte[] bytes = new byte[bytes1.length + bytes2.length];
		System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
		System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
		return bytes;
	}
	
	//retrieves a subset of a byte array
	public byte[] subBytes(byte[] bytes, int start, int end){
		byte[] subBytes = new byte[end-start];
		System.arraycopy(bytes, start, subBytes, 0, subBytes.length);
		return subBytes;
	}
	
	//returns the appropriate convert based on data type byte
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
	
	private Convertor getConvertor(byte[] data, Counter pos){
		Convertor convertor = null;
		switch(data[pos.getValue()]){
		case nullByte:
		case boolByte:
		case integerByte:
		case longByte:
		case floatByte:
		case flightDetailsByte:
			//if null, boolean, integer, long, float, flightDetails
			//get convertor
			convertor = getConvertor(data[pos.getValue()]);
			pos.inc();
			break;
		case stringByte:
			//if string
			//get convertor
			StringConvertor stringConvertor = (StringConvertor) getConvertor(data[pos.getValue()]);
			pos.inc();
			//initialize size parmameter of convertor
			stringConvertor.setSize(data[pos.getValue()]);
			pos.inc();
			convertor = stringConvertor;
			break;
		case arrayByte:
			//if array
			//get convertor
			ArrayConvertor arrayConvertor = (ArrayConvertor) getConvertor(data[pos.getValue()]);
			pos.inc();
			//get second convertor array item type
			arrayConvertor.setInternalConvertor(getConvertor(data, pos));
			//initialize size parmameter of convertor
			arrayConvertor.setSize(data[pos.getValue()]);
			pos.inc();	
			convertor = arrayConvertor;
			break;
		case remoteObjByte:
			//remote object not implemented
			break;
		}
		return convertor;
	}
	
	//converts java data/object to byte array message
	//format of message as follows
	//null
	//(null type byte)
	//boolean, int, long, float, flightDetails:
	//(data type byte)(byte array of data)
	//string:
	//(string type byte)(length of string)(byte array of data)
	//array
	//(array type byte)(data type byte of array item)(length of array)(byte array of data)
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//unmarshalls data from byte array
	public Object fromMessage(byte[] message){
		//data object
		Object data = null;
		//data object buffer
		List dataBuffer = new ArrayList();
		//if message is too short
		if(message.length < 2){
			//return null
			return data;
		}
		//keep track of starting position of unread bytes
		Counter position = new Counter();
		//while not end of byte array
		while(position.getValue() < message.length){
			//retrieve convertor
			Convertor convertor = getConvertor(message, position);
			//if convertor is null
			if(convertor == null)
				//data is null
				data = null;
			//else
			else{
				//convert bytes to data using convertor
				data = convertor.fromBytes(subBytes(message, position.getValue(), position.getValue()+convertor.getByteCount()));
				//updates position
				position.inc(convertor.getByteCount());
			}
			//adds data to bufffer
			dataBuffer.add(data);
		}
		//buffer has more than 1 item
		if(dataBuffer.size() > 1){
			//return buffer
			return dataBuffer;
		}
		//else return data
		return data;
	}
}
