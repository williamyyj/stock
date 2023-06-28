package org.cc.stock.col;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.cc.json.JSONObject;
import org.cc.stock.FNetonMethod;
import org.cc.stock.model.StockModel;

import com.ibm.icu.impl.UResource.Array;

/**
 *  period : 20（月）, 60（季）, 240(年）
 *  評價  price 2sd 分 
 *  評價  vol 
 *  時序  price 
 *  時序  vol  
 *  大衍之数五十，其用四十有九。分而为二以象两，挂一以像三，揲之以四以象四时，归奇于扐以象闰，故再 而后挂。
 *  
 *  准备阶段：取出50根蓍草，抽出一根不用，只用剩余的49根。
 *  第一阶段（分二）：将上述49根蓍草任意分两组，其中每一组最少不低于6根；
 *  第二阶段（挂一）：从上述两组蓍草中左边的一组中抽出1根，将它挂在手里。
 *  第三阶段（一变）：分别将上述两组蓍草以四除（揲四），每组的余数应当是1根，或者是2根，或者是3根，如果正好除尽，将最后剩下的4根也作为余数。
 *  将两组的余数取出不用（归奇），将被除尽的两组合并，总数应当是40根或者44根。
 *  第四阶段（二变）：将上述40根或44根蓍草再任意分为两组，分别以四去除。将两组的余数取出不用，将被除尽的两组蓍草合并，总数应当是40根或36根或32根。
 *  第五阶段（三变）：将上述40根或36根或32根蓍草再任意分为两组，分别以四去除，将两组的余数取出不用，将被除尽的两组蓍草合并，总数应当是36根或32根或28根或24根。
 *  将上述剩余的蓍草除以四，商有四种可能：9、8、7、6。
 *  如果是9或7，则画出一阳爻“—”；是6或8，则画出一阴爻“”。上述则推出第一爻，其余五爻依次类推。
 *  9为老阳，7为少阳；6为老阴，8为少阴。老阳、老阴为变爻。
 *  
 * @author 94017
 *
 */
public class STaiJiColumns {
	
	protected StockModel sm;
	
	private int period;
	
	private String taiTitle ="";

	public STaiJiColumns(StockModel sm, int period) {
		this.sm = sm;
		this.period = period;
		taiTitle = "tai"+period;
		__init_data();
	}

	private void __init_data() {
		int bIdx = 20 ; 
		for(int i=bIdx; i< sm.data().size();i++ ) {
			JSONObject curr = sm.data().get(i);
			JSONObject tai = new JSONObject();
			taijiColumns(tai,i);
			curr.put(taiTitle,tai);
		}
		
	}
	
	private JSONObject prior(int curr) {
		int idx = curr -1 ;
		if(idx<0) {
			return null;
		} else {
			return sm.data().get(idx).optJSONObject(taiTitle);
		}
	}
	
	/**
	 * 直接使用多周期評價
	 * 20 , 60 , 240 (用來取代景氣燈號) 
	 * @param tai
	 * @param idx
	 */
	private void taijiColumns(JSONObject tai, int idx ) {
		JSONObject prior = prior(idx);
		double pg1 =  taijiPriceGrade(tai,20,idx);
		double pg2 =  taijiPriceGrade(tai,60,idx);
		double pg3 =  taijiPriceGrade(tai,240,idx);
		tai.put("pg1", pg1);
		tai.put("pg2", pg2);
		tai.put("pg3", pg3);

		
		double sg1 =  taijiStatGrade(tai,20,idx);
		double sg2 =  taijiStatGrade(tai,60,idx);
		double sg3 =  taijiStatGrade(tai,240,idx);
		tai.put("sg1", (sg1+sg2+sg3)/3);
		tai.put("sg2", (sg1+4*sg2+sg3)/6);
		tai.put("sg3", sg3);
		
		
		double mg1 =  taijiMoneyGrade(tai,20,idx);
		double mg2 =  taijiMoneyGrade(tai,60,idx);
		double mg3 =  taijiMoneyGrade(tai,240,idx);
		tai.put("mg1", mg1);
		tai.put("mg2", mg2);
		tai.put("mg3", mg3);
		
		
		
		
		double vg1 =  taijiVolGrade(tai,20,idx);
		double vg2 =  taijiVolGrade(tai,60,idx);
		double vg3 =  taijiVolGrade(tai,240,idx);
		tai.put("vg1", (vg1+vg2+vg3)/3);
		tai.put("vg2", (vg1+4*vg2+vg3)/6);
		tai.put("vg3", (vg1+2*vg2+4*vg3)/7);
		
		//double pg = (pg1+pg2+pg3)*0.8+(vg1+vg2+vg3)*0.2;
		//pg=(pg-20)/10*0.8+0.1;
	}

	/**
	 * 20230111 評估非常亂目前沒有一致性 00878,006208 測試
	 * 20230113 評估如果引入長期平均值 w*phase* 區間平均值 --> 可能要再評估
	 * @param tai
	 * @param range
	 * @param idx
	 * @return
	 */
	private double taijiVolGrade(JSONObject tai,int range, int idx ) {
		int cycle = range/4;
		List<TaiPhaseBean> beans = new ArrayList<TaiPhaseBean>();
		double pv4 = sm.dsvol(cycle, idx);
		double pv3 = sm.dsvol(cycle, idx-cycle);
		double pv2 = sm.dsvol(cycle, idx-2*cycle);
		double pv1 = sm.dsvol(cycle, idx-3*cycle);
		double vol = pv4+pv3+pv2+pv1;
		vol = vol>0 ? vol : 1 ;
		beans.add(new TaiPhaseBean(4,pv4/vol));
		beans.add(new TaiPhaseBean(3,pv3/vol));
		beans.add(new TaiPhaseBean(2,pv2/vol));
		beans.add(new TaiPhaseBean(1,pv1/vol));			
		Collections.sort(beans);
		return taiPhaseBeanGrade(beans);
	}
	
	private double taiPhaseBeanGrade(List<TaiPhaseBean> beans) {
		double grade = 0.0;
		int w = 1;
		for(TaiPhaseBean bean:beans) {
			grade += w*bean.getPhase();//*bean.getValue();
			w++;
		}
		return grade;
	}
	
	private double taijiPriceGrade(JSONObject tai, int range, int idx ) {
		int cycle = range/4;
		List<TaiPhaseBean> beans = new ArrayList<TaiPhaseBean>();
		double pv4 = sm.dssc(cycle, idx);
		double pv3 = sm.dssc(cycle, idx-cycle);
		double pv2 = sm.dssc(cycle, idx-2*cycle);
		double pv1 = sm.dssc(cycle, idx-3*cycle);
		double vol = pv4+pv3+pv2+pv1;
		vol = vol>0 ? vol : 1 ;
		beans.add(new TaiPhaseBean(4,pv4/vol));
		beans.add(new TaiPhaseBean(3,pv3/vol));
		beans.add(new TaiPhaseBean(2,pv2/vol));
		beans.add(new TaiPhaseBean(1,pv1/vol));		
		Collections.sort(beans);
		return taiPhaseBeanGrade(beans);
	}
	
	private double taijiStatGrade(JSONObject tai, int range, int idx ) {
		this.sm.stat(tai, range, idx);
		double sh = tai.optDouble("sh");
		double sl = tai.optDouble("sl");
		double sc = sm.avg(5, idx); 
		return (sc-sl)/(sh-sl)*10+20;
	}
	
	/**
	 * 
	 * @param tai
	 * @param range
	 * @param idx
	 * @return
	 */
	private double taijiMoneyGrade(JSONObject tai, int range, int idx ) {
		int cycle = range/4;
		List<TaiPhaseBean> beans = new ArrayList<TaiPhaseBean>();
		double pv4 = sm.dsmem(cycle, idx);///sm.dsvol(cycle, idx);
		double pv3 = sm.dsmem(cycle, idx-cycle);///sm.dsvol(cycle, idx-cycle);
		double pv2 = sm.dsmem(cycle, idx-2*cycle);///sm.dsvol(cycle, idx-2*cycle);
		double pv1 = sm.dsmem(cycle, idx-3*cycle);///sm.dsvol(cycle, idx-3*cycle);
		if(pv4<1.1 || pv3<1.1 || pv2<1.1 || pv1<1.1) {
			pv4 = 1 ; pv2=1 ; pv3=1 ; pv4 = 1;
		}
		double vol = pv1+pv2+pv3+pv4;
		beans.add(new TaiPhaseBean(4,pv4/vol));
		beans.add(new TaiPhaseBean(3,pv3/vol));
		beans.add(new TaiPhaseBean(2,pv2/vol));
		beans.add(new TaiPhaseBean(1,pv1/vol));		
		Collections.sort(beans);
		double grade =taiPhaseBeanGrade(beans);
		return grade ;
	}
	
	
	
	/**
	 * 評價分數大洐之數五十其用四九
	 * @param tai
	 * @param cycle
	 */
	private double taijiGradePrice(JSONObject tai, int eIdx) {
		double sc = sm.avg(20, eIdx);
		double sd = tai.optDouble("sd");
		double sa = tai.optDouble("sa");
		double sh = tai.optDouble("sh");
		double sl = tai.optDouble("sl");
		//double sh = (sa+2*sd);
		//double sl = (sa-2*sd);
		
		return (sc-sl)/(sh-sl)*100;
	}
	
	
	/**
	 * 20230110 停用是計算出均價和 量*價/量 正相關直接用均價取代
	 * @param tai
	 * @param idx
	 */
	@Deprecated
	private void taijiColumns_20230110(JSONObject tai,int idx) {		
		int bIdx = (idx-period+1)<=0 ? 0 : (idx-period+1);
		int eIdx = idx ;
		double sc = 0 , sh=0,sl=99999999999F;
		double mc = 0 , mh=0,ml=99999999999F;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = sm.data().get(i);
			double sv = ratio(i);
			sc += sv;
			sh = (sv>sh) ? sv : sh ;
			sl = (sl>sv) ? sv : sl ;
			double vv = row.optDouble("mvol");
			mc += vv;
			mh = (vv>mh) ? vv : mh ;
			ml = (ml>vv) ? vv : ml ;
		}
		double sa = sc / (eIdx-bIdx+1);
		double ma = mc / (eIdx-bIdx+1);
		double sd = 0.0,  md=0.0;
		for(int i=bIdx;i<=eIdx;i++) {
			JSONObject row = sm.data().get(i);
			double sv = ratio(i);
			sd += (sa-sv)*(sa-sv);
			double vv = row.optDouble("mvol");
			md += (ma-vv)*(ma-vv);
		}
		sd = Math.sqrt(sd/(eIdx-bIdx+1));
		tai.put("sa", sa);
		tai.put("sl", sl);
		tai.put("sh", sh);
		tai.put("sd", sd);
		//tai.put("sm", sm.dsmem(this.period, eIdx)/sm.dsvol(period, eIdx));
		md = Math.sqrt(md/(eIdx-bIdx+1));
		tai.put("ma", ma);
		tai.put("ml", ml);
		tai.put("mh", mh);
		tai.put("md", md);
	}
	
	private void taijiVolCycleColumns(JSONObject tai,int idx) {
		JSONObject row = sm.data().get(idx);
		if(idx-this.period>=0) {
			int cycle = this.period/4;
			tai.put("tv4", sm.dsvol(cycle, idx));
			tai.put("tv3", sm.dsvol(cycle, idx-cycle));
			tai.put("tv2", sm.dsvol(cycle, idx-2*cycle));
			tai.put("tv1", sm.dsvol(cycle, idx-3*cycle));
		}
		
		
	}
	
	/**			
	 * 20230109 使用量價來看評估,發現問題是資料沒有相關性 
	 * @param tai
	 * @param idx
	 */
	private void taijiMemCycleColumns_20230109(JSONObject tai,int idx) {
		if(idx-this.period+1>=0) {	
			int cycle = this.period/4;
			double mem =  sm.dsmem(cycle, idx)/cycle; //前波
			int bIdx = idx-period+1;
			int eIdx = idx ;
			int count =0;
			for(int i=bIdx;i<=eIdx;i++) {
				JSONObject row = sm.data().get(i);
				double cmem = row.optDouble("mvol");
				System.out.println("Mem --->"+mem+","+cmem);
				
				if(mem>cmem) {
					count++;
				}
			}
			tai.put("mv", count*100.0/period);
	
		}
	}
	
	/**
	 * 20230110 使用 ln 來判別 ln(tv4/tv3) ... 有參考價值可能計算出”pattern“,要用AI分析出相關參數
	 *  past | tv1 | tv2 | tv3 | tv4 | now
	 * 週期模型  tv4,tv3 ,  (tv4+tv3)/(tv1+tv2) 無參考價值 , 導入前一週期(效果不大) 
	 * 週期模型  p4*f4(tv4,區平均)+ p3*f3(...) 
	 * @param tai
	 * @param idx
	 */
	private void taijiMemCycleColumns(JSONObject tai,int idx) {
		if(idx-this.period>=0) {
			JSONObject prior = sm.data().get(idx-1);
			JSONObject ptai = prior.optJSONObject(taiTitle);
			int cycle = this.period/4;
			double tv4 = sm.dsmem(cycle, idx);
			double tv3 = sm.dsmem(cycle, idx-cycle);
			double tv1_2 = sm.dsmem(cycle+cycle, idx-cycle*2);
			double cmv = 0;
			cmv = Math.log(tv4/tv3);
			cmv = cmv+ Math.log((tv4+tv3)/tv1_2);
			double mv = 0 ;
			if(ptai!=null) {
				mv = ptai.optDouble("mv",0.0)*7/9+2*cmv/9;
			} else {
				mv = cmv;
			}
			tai.put("mv", mv);
			
		}
	}
	
	private double ratio(int idx) {
		JSONObject row = sm.data().get(idx);
		//double data = row.optDouble("sc") / sm.avg(720, idx);
		//return Math.log(data);
		return row.optDouble("sc");
	}
	
	/**
	 * 已整體資料面看
	 * @param tai
	 * @param range
	 * @param idx
	 */
	private void taijiBallColumns(JSONObject tai,int range, int idx) {
		sm.stat(tai, range, idx);
	}
	
}
