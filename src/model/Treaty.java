package model;
import java.util.Random;

public class Treaty implements Event{
	private final Leader l,l1;
	private int timer;
	
	Treaty(Random r,Leader l,Leader l1){
		timer=15+r.nextInt(36);
		this.l1=l1;
		this.l=l;
	}
	
	@Override
	public void act(Simulation sim) {
		timer--;
	}
	
	@Override
	public boolean finished() {return timer==0;}
	
	@Override
	public boolean equals(Object obj) {
		if(obj!=null && obj instanceof Treaty) {
			Treaty t=(Treaty)obj;
			return (l==t.l && l1==t.l1) || (l==t.l1 && l1==t.l);
		}
		return false;
	}
}
