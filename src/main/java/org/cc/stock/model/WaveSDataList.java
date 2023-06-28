package org.cc.stock.model;

import java.util.ArrayList;
import java.util.Date;

import org.cc.json.JSONObject;
import org.cc.stock.FLineMethod;
import org.cc.stock.FNetonMethod;
import org.cc.text.TextUtils;

/**
 * will delete see SWaveColumns 
 * @author 94017
 *
 */
public class WaveSDataList extends ArrayList<JSONObject>{

	private static final long serialVersionUID = -1694575452626418768L;
	private StockModel sm ;
	private FNetonMethod fn1 ;
	private FLineMethod fn2 ; 
	private int rv;
	private int range;
	public double ma ,pmax,pmin,sd;
	public double sdh ,sdl;
	double[] ratio = new double[7]; 
	double[] price = new double[7]; // 0 , 1 , 2 , 3 , 4 , 5 , 6 
	
	public WaveSDataList(StockModel sm, int rv) {
		super();
		this.sm = sm;
		this.rv = rv ; 
		init_01(); // 統計標淬差
		init_02(); // 機率計算
		//init_03(); // 波段分析
	}
	
	
	private void init_01() {
		ma = 0.0; pmax=0; pmin=9999999;
		for(int i=0;i<sm.data().size();i++) {
			double r = ratio(i);
			ma+=r;
			pmax = r> pmax ? r : pmax ;
			pmin = pmin>r ? r : pmin ; 
		}
		ma = ma/sm.data().size();
		sd = 0.0;
		for(int i=0;i<sm.data().size();i++) {	
			double ratio = ratio(i);
			sd += (ratio-ma)*(ratio-ma);
		}
		sd = Math.sqrt(sd/sm.data().size());
	}
	
	private void init_02() {
		sdh =  (ma+3*sd) > pmax ? (pmax-ma)/3 : sd ;
		sdl = pmin > (ma-3*sd) ? (pmin-ma)/3 : - sd ;
		price[0] =  (ma+3*sdl);
		price[1] =  (ma+2*sdl);
		price[2] =  (ma+1*sdl);
		price[3] =  ma;
		price[4] =  (ma+1*sdh);
		price[5] =  (ma+2*sdh);
		price[6] =  (ma+3*sdh);
		ratio[0]=0;ratio[1]=0;ratio[2]=0;ratio[3]=0;
		ratio[4]=0;ratio[5]=0;ratio[6]=0;
		for(int i=0;i<sm.data().size();i++) {	
			double r = ratio(i);
			if(r<=price[0]) ratio[0] = ratio[0]+1;
			if(r>price[0] && r<=price[1]) ratio[1] = ratio[1]+1;
			if(r>price[1] && r<=price[2]) ratio[2] = ratio[2]+1;
			if(r>price[2] && r<=price[3]) ratio[3] = ratio[3]+1;
			if(r>price[3] && r<=price[4]) ratio[4] = ratio[4]+1;
			if(r>price[4] && r<=price[5]) ratio[5] = ratio[5]+1;
			if(r>price[5] && r<=price[6]) ratio[6] = ratio[6]+1;
	
		}
		ratio[1] = ratio[0]+ratio[1];
		ratio[2] = ratio[1]+ratio[2];
		ratio[3] = ratio[2]+ratio[3];
		ratio[4] = ratio[3]+ratio[4];
		ratio[5] = ratio[4]+ratio[5];
		ratio[6] = ratio[5]+ratio[6];
		
		for(int i=0;i<7;i++) {
			 ratio[i] =  ratio[i] / sm.data().size();
		}
		
		fn1 = new FNetonMethod(7,price,ratio);
		fn2 = new FLineMethod(7,price,ratio);
	}
	
	
	
	
	
	public void procWaveAllItem() {
		if(sm.data()!=null) {
			for(int i=0; i<sm.data().size();i++) {
				procWaveItem(i);
				
			}
		}
	}
	
	public void procWaveItem(int n) {
		int ib =  (n-1) >= 0 ? n-1 : 0 ; 
		int ie = (n+1) < sm.data().size() ? n+1 : sm.data().size()-1 ;
		double vb = sm.sc(ib);
		double sc = sm.sc(n);
		double ve = sm.sc(ie);
		double am =   Math.log(sc/sm.avg(rv,n) );
		double per = fn1.value(am);
		JSONObject row = sm.data().get(n);
		String sd = TextUtils.df("yyyyMMdd", row.optDate("sdate"));
		double ratio = ratio(n);
		if(sc>ve && sc>vb) {
			row.put("isPeek", "+");
			procWaveItemFilter(n,"+" );
		} else if(ve>sc && vb>sc) {
			row.put("isPeek", "-");			
			procWaveItemFilter(n,"-" );
		}
	}
	
	
	public void procWaveItemFilter(int n,String isPeek ) {
		int range = 21 ;
		int ib =  (n-range) >= 0 ? n-range : 0 ; 
		int ie = (n+range) < sm.data().size() ? n+range : sm.data().size()-1 ;
		JSONObject row = sm.data().get(n);
		double pv =  sm.sc(n);
		for(int i=ib ; i<=ie;i++) {
			double sc = sm.sc(i);
			if("+".equals(isPeek) && sc>pv) {
				row.remove("isPeek");
				break;
			} else if ("-".equals(isPeek) && sc<pv) {
				row.remove("isPeek");
				break;
			}
		}
		
	}
	
	public double ratio(int i) {
		JSONObject row = sm.data().get(i);
		double sc = sm.sc(row);
		return Math.log( sc/sm.avg(rv, i));
	}
	
	public double pr(int i) {
		return Math.exp(price[i]);
	}

	
	
	
	public void show_params() {
		System.out.println("===== rv : "+rv +"["+pmax+","+pmin+","+sd+"]"); 
		System.out.println("SH3-->"+price[6]+":::"+ratio[6]); 
		System.out.println("SH2-->"+price[5]+":::"+ratio[5]); 
		System.out.println("SH1-->"+price[4]+":::"+ratio[4]); 
		System.out.println("ma -->"+price[3]+":::"+ratio[3]); 
		System.out.println("SL1-->"+price[2]+":::"+ratio[2]); 
		System.out.println("SL2-->"+price[1]+":::"+ratio[1]); 
		System.out.println("SL3-->"+price[0]+":::"+ratio[0]); 
	}

	
}
