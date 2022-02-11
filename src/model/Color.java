package model;
import java.util.Random;

class Color{
	private static final float grid=0.05f;
	private static final float hMin=0.0f;
	private static final float sMin=0.3f;
	private static final float vMin=0.5f;
	private Random r;
	Color(Random r) {
		this.r=r;
	}
	
	HSV newColor() {
		float h=gridValue(hMin);
		float s=gridValue(sMin);
		float v=gridValue(vMin);
		return new HSV(h,s,v);
	}
	private float gridValue(float min) {
		float value=(r.nextFloat()*(1.0f-min))+min;
		return (float)Math.floor(value/grid)*grid;
	}
	
	@Override
	public String toString() {
		int total=(int)Math.round((1.0-hMin)*(1.0-sMin)*(1.0-vMin)/Math.pow(grid,3));
		return "Color object ("+total+" total possible values)";
	}
}
