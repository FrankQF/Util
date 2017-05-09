package com.neusoft;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dragonsoft.adapter.service.QueryAdapterSend;
import com.dragonsoft.pci.exception.InvokeServiceException;

import sun.misc.BASE64Decoder;



public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("D:/enshi/list.txt");
		File logFile=new File("D:/enshi/log.txt");
		File errorFile=new File("D:/enshi/error.txt");
		FileOutputStream log = new FileOutputStream(logFile,true);
		FileOutputStream error = new FileOutputStream(errorFile,true);
		 BufferedReader reader = null;
	        try {
	            System.out.println("以行为单位读取文件内容，一次读一整行：");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            // 一次读入一行，直到读入null为文件结束
	            while ((tempString = reader.readLine()) != null) {
	                // 显示行号
	                String []aStr=tempString.split("-");
	                String idno=aStr[0];
	                String name=aStr[1];
	                System.out.println(idno);
	                byte img[]=null;
	                try {
	                	if(validFile(idno+"-"+name)){
	                		//存在则不读取文件
	                		writeLine(log, idno+"文件已存在");
	                	}else{
	                		//不存在则读取文件
	                		img=readFile(idno);
	                		if(null==img){
	                			//没读到文件
	    	                	writeLine(log, idno+"读取文件失败,文件为空");
	    						writeLine(error,idno+"读取文件失败，文件为空");
	                		}
	                	}
	                	
					} catch (Exception e) {
						//读取文件失败
						writeLine(log, idno+"读取文件失败"+e.getMessage());
						writeLine(error,idno+"读取文件失败，io异常");
						e.printStackTrace();
					}
	                if(null!=img){
	                	
	                	//读取成功保存文件
	                	try {
							//保存文件
	                		String path="D:\\enshi\\img\\";
	                		path=path+idno+"-"+name+".jpg";
	                		getFile(img, path);
	                		writeLine(log,"成功："+idno);
						} catch (Exception e) {
							//保存失败
							writeLine(log, idno+"保存文件失败"+e.getMessage());
							writeLine(error,idno+"保存文件失败");
							e.printStackTrace();
						}
	                }
	            }
	            reader.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                    log.close(); 
	                    error.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
	        System.out.println("--------结束----------");
	}
	/**
	 * 验证文件是否存在
	 * @param idno
	 * @return
	 * @throws Exception 
	 */
	public static boolean validFile(String idno) throws Exception{
		String filePath="D:\\enshi\\img\\"+idno+".jpg";
		File file = new File(filePath);
		return file.exists();
	}
	
	/**
	 * 读取文件
	 * @param idno
	 * @return
	 * @throws Exception 
	 */
	public static byte[] readFile(String idno) throws Exception{
		String filePath="D:\\enshi\\img2\\"+idno+".jpg";
		return getBytes(filePath);
	}
	
	
	/**
	 * 保存文件
	 * @param bfile
	 * @param savePath
	 * @throws IOException
	 */
	
	public static void getFile(byte[] bfile, String savePath) throws IOException {

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		File file = null;
		try {
			file = new File(savePath);
			if (!file.getParentFile().exists()) {
				file.mkdirs();
			}
			if (file.exists()) {
				file.delete();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					throw e;
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}
	/**
	 * 在文件中写入一行 简易方法
	 * @param fs
	 * @param log
	 * @throws IOException
	 */
	public static void writeLine(FileOutputStream fs,String log) throws IOException{
		fs.write((log+"\r").getBytes());
	}
	
	/**
	 * 从文件夹中读取文件
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBytes(String filePath) throws Exception {

		byte[] buffer = null;

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new Exception("文件不存在");
			}

			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
			byte[] bytes = new byte[1024];
			int n;
			while ((n = fis.read(bytes)) != -1) {
				bos.write(bytes, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
		return buffer;
	}
	
	public static String QueryZZRK(String condition) {
		String strReturns = null;
		try {
			QueryAdapterSend adapter = new QueryAdapterSend();
			strReturns = adapter.sendQuery("QueryZZRK", condition, "123456789123456789", "测试", "350211");
		} catch (InvokeServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getErrorCode();//获取错误代码
			e.getErrorMessage();//获取错误详细信息
			
		}
		return strReturns;
	} 
	
	public static String[][] parseXml(String xml) throws DocumentException{
		String[][] resultArys = null;
		Document doc = DocumentHelper.parseText(xml);
		//获取Row节点集合
		List list = doc.selectNodes("RBSPMessage/Method/Items/Item/Value/Row");
		//判断节点是否为空同时节点大于2
		if (list != null && list.size() > 2) {
			//获取第一个Row节点
			Element stateElement = (Element)list.get(0);
			//获取返回数据状态
			String state = ((Element)stateElement.elements().get(0)).getText();			
			if (state.equalsIgnoreCase("000")) {//状态等于000代表结果返回正常
				Element fieldElement = (Element)list.get(1);
				//初始化二维数组
				resultArys = new String[list.size() - 2][fieldElement.elements().size()];				
				for (int i = 2; i < list.size(); i++) {
					Element data = (Element)list.get(i);					
					List fields = data.elements();
					for (int j = 0; j < fields.size(); j++) {
						Element tmpE = (Element)fields.get(j);
						//二维数组赋值
						resultArys[i-2][j] = tmpE.getText();
					}
				}
			} 
		}
		return resultArys;
	}
	 private static void saveImage(String filename, String content) throws IOException {      
	        try {      
	            DataOutputStream dos = null;      
	            try {      
	                byte[] bs = new BASE64Decoder().decodeBuffer(content);      
	                dos = new DataOutputStream(new BufferedOutputStream(      
	                        new FileOutputStream(filename)));      
	                dos.write(bs);      
	            } finally {      
	                dos.close();      
	            }      
	        } catch (IOException e) {      
	            e.printStackTrace(); 
	            throw e;
	        }      
	    } 

	
}
