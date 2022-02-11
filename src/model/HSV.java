package model;

public class HSV implements Comparable<HSV>{
	private final float h,s,v;
	HSV(float h,float s,float v) {
		this.h=h;
		this.s=s;
		this.v=v;
	}
	
	public float getH() {return h;}
	public float getS() {return s;}
	public float getV() {return v;}
	
	@Override
	public int compareTo(HSV a) {
		double diff=(h-a.h)+(s-a.s)+(v-a.v);
		if(diff<0) {return -1;}
		if(diff>0) {return 1;}
		return 0;
	}
}
