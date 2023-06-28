package org.cc.stock;

import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.model.StockModel;

/**
 * 
 * @author 94017
 *
 */
public class RatioStockParams {
	
	private StockModel sm;
	public int rv = -1 ; // 統計區間
	public int rmax = -1 ; // 最大區間
	public double ma ;
	public double sd ; 
	public double sdh ; 
	public double sdl ; 
	public double pmin = 0 ;
	public double pmax = 0 ;
	public double pv = 1 ; 
	double[] ratio = new double[8]; 
	double[] price = new double[7]; // 0 , 1 , 2 , 3 , 4 , 5 , 6 
	
	public RatioStockParams(StockModel sm, int rv ) {
		this.sm = sm ;
		this.rv = rv;
		init();
		init_fix();
	}
	
	/**
	 * 調整不合
	 */
	public void init_fix() {
		System.out.println("===== rv : "+rv +"["+pmax+","+pmin+"]");
		sdh =  (ma+3*sd) > pmax*pv ? (pmax*pv-ma)/3 : sd ;
		sdl = pmin*pv > (ma-3*sd) ? (pmin*pv-ma)/3 : - sd ;
		System.out.println(ma+3*sd+"--->"+ (ma+3*sdh));
		System.out.println(ma+2*sd+"--->"+ (ma+2*sdh));
		System.out.println(ma+sd+"--->"+ (ma+1*sdh));
		System.out.println(ma);
		System.out.println(ma-sd+  "--->"+ (ma+1*sdl));
		System.out.println(ma-2*sd+ "--->"+(ma+2*sdl));
		System.out.println(ma-3*sd+ "--->"+ (ma+3*sdl));
		for(int i=0;i<sm.data().size();i++) {
			
		}
		
	}
	
	public void init() {
		ma = 0.0; pmax=0; pmin=9999999;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double r = sc/sm.avg(rv, i);
			ma+=r;
			pmax = r> pmax ? r : pmax ;
			pmin = pmin>r ? r : pmin ; 
		}
		ma = ma/sm.data().size();
		sd = 0.0;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double ratio =  sc/sm.avg(rv, i);
			sd += (ratio-ma)*(ratio-ma);
		}
		sd = Math.sqrt(sd/sm.data().size());
	}
	
	
	
	public void init_old(StockModel sm, int rv ) {
		ma = 0.0; pmax=0; pmin=9999999;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double r = sc/sm.avg(rv, i);
			ma+=r;
			pmax = r> pmax ? r : pmax ;
			pmin = pmin>r ? r : pmin ; 
		}
		ma = ma/sm.data().size();
		sd = 0.0;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double ratio =  sc/sm.avg(rv, i);
			sd += (ratio-ma)*(ratio-ma);
		}
		sd = Math.sqrt(sd/sm.data().size());
		
		
		ratio[3] = ma ; ratio[1]=0; ratio[5]=0;
		int l1 = 0 , l5=0;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double r =  sc/sm.avg(rv, i);
			if(r>ma) {
				l5++;
				ratio[5]+=r;
			} else {
				l1++;
				ratio[1]+=r;
			}
		}
		System.out.println(rv+"--->"+ l5+","+l1);
		ratio[5] = ratio[5]/l5;
		ratio[1] = ratio[1]/l1;
		// 0 1 2 3 4 5 6
		//   1       5   
		int l0 = 0 , l2=0 , l4=0,l6=0;
		for(int i=0;i<sm.data().size();i++) {
			JSONObject row = sm.data().get(i);
			double sc = sm.sc(row);
			double r =  sc/sm.avg(rv, i);
			if(r>=ratio[5]) {
				l6++;
				ratio[6]+= r;
			} else if(ratio[5]>r && r>=ratio[3] ) {
				l4++;
				ratio[4]+= r;
			} else if(ratio[3]>r && r>=ratio[1]) {
				l2++;
				ratio[2]+= r;
			} if(ratio[1]>r) {
				l0++;
				ratio[0]+= r;
				
			}
		}
		System.out.println(rv+"--->"+ l6+","+l4+":::"+l2+","+l0);
		ratio[6] = ratio[6]/l6;
		ratio[4] = ratio[4]/l4;
		ratio[2] = ratio[2]/l2;
		ratio[0] = ratio[0]/l0;		
		System.out.println("===== rv : "+rv +"["+pmax+","+pmin+"]");
		System.out.println(ma+3*sd+"-->"+ratio[6]);
		System.out.println(ma+2*sd+"-->"+ratio[5]);
		System.out.println(ma+sd+"-->"+ratio[4]);
		System.out.println(ma+"-->"+ratio[3]);
		System.out.println(ma-sd+"-->"+ratio[2]);
		System.out.println(ma-2*sd+"-->"+ratio[1]);
		System.out.println(ma-3*sd+"-->"+ratio[0]);
	}
	
}
