package model;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;

public class Planet {
	private static final double CIVILIZATION_CHANCE=0.15;
	private static final double MINIMUM_FIELD_STR=0.05;
	private static final double MAX_PARTICLE_STR=50.0;
	private static final double PARTICLE_CONST=25.0;
	private static final int MAX_PARTICLES=50;
	private static final int VOXEL_SIZE=20;
	public static final int WATER=0;
	public static final int LAND=1;
	private final int width,height;
	private final Random r;
	Society[][] taken;
	int[][] grid;
	
	Planet(Simulation sim,int width,int height){
		this.width=width;
		this.height=height;
		this.r=sim.r;
		taken=new Society[width][height];
		grid=new int[width][height];
		generate(sim);
	}
	
	// planet generation and support
	private void generate(Simulation sim) {
		// Generate a Landmass Probability Field (LPF)
		Collection<LandParticle> field=new ArrayList<>();
		for(int a=0;a<MAX_PARTICLES;a++) {
			field.add(new LandParticle(
					((2*r.nextDouble())-1)*MAX_PARTICLE_STR,
					r.nextInt(width),
					r.nextInt(height)
			));
		}
		
		// Assign voxel seeds
		Seed[][] seeds=new Seed[wVoxels()][hVoxels()];
		int minX=0;
		for(Seed[] column:seeds) {
			int minY=0;
			for(int a=0;a<column.length;a++) {
				int x=minX+r.nextInt(VOXEL_SIZE);
				int y=minY+r.nextInt(VOXEL_SIZE);
				int type;
				if(r.nextDouble()<getLandmassProbability(field,x,y)) {
					type=LAND;
					if(r.nextDouble()<CIVILIZATION_CHANCE) {
						addSociety(new Society(sim,x,y));
					}
				}else {
					type=WATER;
				}
				column[a]=new Seed(x,y,type);
				minY+=VOXEL_SIZE;
			}
			minX+=VOXEL_SIZE;
		}
		
		// Populate planetary grid
		for(int vx=0;vx<wVoxels();vx++) {
			int bx=vx*VOXEL_SIZE;
			for(int vy=0;vy<hVoxels();vy++) {
				int by=vy*VOXEL_SIZE;
				for(int x=bx;x<bx+VOXEL_SIZE;x++) {
					for(int y=by;y<by+VOXEL_SIZE;y++) {
						grid[x][y]=getClosestSeed(seeds,vx,vy,x,y).type;
					}
				}
			}
		}
	}
	private int wVoxels() {
		return (int)Math.floor(width/VOXEL_SIZE);
	}
	private int hVoxels() {
		return (int)Math.floor(height/VOXEL_SIZE);
	}
	private double getLandmassProbability(Collection<LandParticle> field,int x,int y) {
		//return 0.2;
		double prob=0.0;
		for(LandParticle p:field) {
			double part=PARTICLE_CONST*p.str/getSquaredDistance(x,y,p.x,p.y);
			prob+=(part>=MINIMUM_FIELD_STR?part:0.0);
		}
		return prob;
	}
	private double getSquaredDistance(int x,int y,int x1,int y1) {
		return Math.pow(x-x1,2)+Math.pow(y-y1,2);
	}
	private int getDistancePrime(int x,int y,int x1,int y1) {
		return Math.abs(x-x1)+Math.abs(y-y1);
	}
	private Seed getClosestSeed(Seed[][] seeds,int vx,int vy,int x,int y) {
		int[][] offsets=new int[][]{{0,0},{-1,-1},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0}};
		Seed closest=null;
		int distPrime=Integer.MAX_VALUE;
		for(int[] o:offsets) {
			int[] vox= {vx+o[0],vy+o[1]};
			if(vox[0]>=0 && vox[0]<wVoxels() && vox[1]>=0 && vox[1]<hVoxels()) {
				Seed s=seeds[vox[0]][vox[1]];
				int dp=0;
				if(x!=s.x || y!=s.y) {
					dp=getDistancePrime(x,y,s.x,s.y);
				}
				if(dp<distPrime) {
					closest=s;
					distPrime=dp;
				}
			}
		}
		return closest;
	}
	
	// package support
	boolean isValid(int x,int y) {
		return x>=0 && x<width && y>=0 && y<height;
	}
	boolean canBuildCity(int x,int y) {
		return isValid(x,y) && taken[x][y]==null && grid[x][y]==LAND;
	}
	void addSociety(Society s) {
		taken[s.x][s.y]=s;
		for(int[] p:new int[][]{{s.x+1,s.y-1},{s.x+1,s.y},{s.x+1,s.y+1},{s.x,s.y+1},{s.x-1,s.y+1},{s.x-1,s.y},{s.x-1,s.y-1},{s.x,s.y-1}}) {
			if(isValid(p[0],p[1]) && taken[p[0]][p[1]]!=null) {
				Society s1=taken[p[0]][p[1]];
				s1.bordering.add(s);
				s.bordering.add(s1);
				s1.redraw=true;
			}
		}
		s.redraw=true;
	}
	boolean isLand(int x,int y) {
		return grid[x][y]==LAND;
	}
	
	private class Seed{
		private final int x,y,type;
		private Seed(int x,int y,int type) {
			this.x=x;
			this.y=y;
			this.type=type;
		}
	}
	private class LandParticle{
		private final int x,y;
		private final double str;
		private LandParticle(double str,int x,int y) {
			this.x=x;
			this.y=y;
			this.str=str;
		}
	}
}
