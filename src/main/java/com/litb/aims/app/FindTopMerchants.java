package com.litb.aims.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class FindTopMerchants {
	public static void analyFiles(String productsFile, String merchantsFile) {
		System.out.println("Begin this time process...");
		System.out.println("products file name is: " + productsFile);
		System.out.println("merchants file name is: " + merchantsFile);
		String dataDir = ""; //"E:/";
		Workbook readwb = null;
		List<Map.Entry<String, Integer>> productsPerMerchantList = null;
		try {
			InputStream instream = new FileInputStream(dataDir + productsFile);
			readwb = Workbook.getWorkbook(instream);
			Sheet readsheet = readwb.getSheet(0);
			int rsColumns = readsheet.getColumns();
			int rsRows = readsheet.getRows();
			//System.out.println(rsRows);
			Cell[] firstRow = readsheet.getRow(0);
			int url_idx = -1;
			for(int index=0; index<firstRow.length; index++){
				
				if (firstRow[index].getContents().equals("merchant_url"))
					url_idx = index;
			}
			if (url_idx < 0){
				throw new IndexOutOfBoundsException("没有找到merchant_url所在列");
			}
			System.out.println("merchant_url所在的列号是: " + url_idx);
			
			Map<String, Integer> productsPerMerchant = new HashMap<>();
			
			for(int r=1; r<rsRows; r++){
				String url = readsheet.getRow(r)[url_idx].getContents();
				if (productsPerMerchant.containsKey(url)){
					productsPerMerchant.put(url, productsPerMerchant.get(url)+1);
				}else{
					productsPerMerchant.put(url, 1);
				}
			}
			
			productsPerMerchantList = new ArrayList<Map.Entry<String, Integer>>(productsPerMerchant.entrySet());
			//打印排序前的list
//			for(int i=0; i<productsPerMerchantList.size(); i++){
//				System.out.println(productsPerMerchantList.get(i).toString());
//			}
			Collections.sort(productsPerMerchantList, new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
					return (o2.getValue() - o1.getValue());
				}
			});
			//打印排序后list
//			for(int i=0; i<productsPerMerchantList.size(); i++){
//				System.out.print(productsPerMerchantList.get(i).getKey());
//				System.out.println("{" + productsPerMerchantList.get(i).getValue() + "}");
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			readwb.close();
		}
		
		try{
			Workbook wb = Workbook.getWorkbook(new FileInputStream(dataDir + merchantsFile));
			WritableWorkbook writewb = Workbook.createWorkbook(new File(dataDir + merchantsFile.replace(".xls", "-new.xls")), wb);

		    WritableSheet writesheet = writewb.getSheet(0);
		    int r = writesheet.getRows();
		    int c = writesheet.getColumns();
//		    WritableCell wc = writesheet.getWritableCell(c, 0);
//		    Label l = (Label) wc; //jxl.biff.EmptyCell cannot be cast to jxl.write.Label
//		    l.setString("TOP商品数目");
		    Label label = new Label(c, 0, "TOP商品数目");
		    writesheet.addCell(label);

			Cell[] firstRow = writesheet.getRow(0);
			int url_idx = -1;
			for(int index=0; index<firstRow.length; index++){
				
				if (firstRow[index].getContents().equals("url"))
					url_idx = index;
			}
			if (url_idx < 0){
				throw new IndexOutOfBoundsException("没有找到url所在列");
			}
			int count = 0;
			int REQUEST_COUNT = 6;
			//格式
            WritableFont wfc = new WritableFont(WritableFont.ARIAL,10,WritableFont.NO_BOLD,false,   
            		  
                    UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.DARK_RED); 
            WritableCellFormat wcfFC = new WritableCellFormat(wfc);
            
			for(Map.Entry<String, Integer> e : productsPerMerchantList){
				String url = e.getKey();
				for(int i=1; i<r; i++){
					//截取最后的问号
					String current_url = writesheet.getCell(url_idx, i).getContents();
					if (current_url.lastIndexOf("?") == current_url.length()-1){
						current_url = current_url.substring(0, current_url.length()-1);
					}
					if (current_url.equals(url)){
						writesheet.addCell(new Label(c, i, e.getValue() + "", wcfFC));
						count ++;
					}
					if (count >= REQUEST_COUNT){
						break;
					}
				}
			}

		    writewb.write();
		    wb.close();
		    writewb.close();
		    System.out.println("this time process over.");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}

	}
	
	public static void main(String[] args){
		File dirFile = new File("datas");
		File[] files = dirFile.listFiles();
		for(File file: files){
			if (file.getAbsolutePath().indexOf("products") > 0){
				String productsFile = file.getAbsolutePath();
				String suppliersFile = file.getAbsolutePath().replaceAll("products", "suppliers");
				analyFiles(productsFile, suppliersFile);
			}
		}
		//analyFiles("slt-p.xls", "slt-s.xls");
	}
}
