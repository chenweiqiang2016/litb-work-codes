

import java.util.ArrayList;
import java.util.List;

public class TestSplit {
	public static void main(String[] args){
		List<Integer> items = new ArrayList<Integer>();
		for(int i=0; i<101; i++){
			items.add(i);
		}
		
		
		//每个商户每次最多倒入50个商品
		if (items.size() > 50){
			int times = new Double(java.lang.Math.ceil(items.size() / 50.0)).intValue();
			System.out.println(times);
			System.out.println(items.size() / 50.0);
			for(int i=0; i<times; i++){
				List<Integer> sub;
				if (i==(times-1)){
					sub = items.subList((times-1)*50, items.size());
				}else{
				    sub = items.subList(i*50, (i+1)*50);
				}
				System.out.println(sub);
			}
		}else{
		    System.out.println(items);
		}
	}
}
