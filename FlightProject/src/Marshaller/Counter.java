package Marshaller;

public class Counter {
	private int start;
	private int current;
	
	public Counter(int startValue){
		start = startValue;
		reset();
	}
	
	public Counter(){
		this(0);
	}
	
	public void set(int value){
		current = value;
	}
	
	public void reset(){
		set(start);
	}
	
	public void inc(int amount){
		current += amount;
	}
	
	public void dec(int amount){
		current -= amount;
	}
	
	public void inc(){
		inc(1);
	}
	
	public void dec(){
		dec(1);
	}
	
	public int getValue(){
		return current;
	}
}
