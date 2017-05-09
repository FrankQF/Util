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
	            System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
	            while ((tempString = reader.readLine()) != null) {
	                // ��ʾ�к�
	                String []aStr=tempString.split("-");
	                String idno=aStr[0];
	                String name=aStr[1];
	                System.out.println(idno);
	                byte img[]=null;
	                try {
	                	if(validFile(idno+"-"+name)){
	                		//�����򲻶�ȡ�ļ�
	                		writeLine(log, idno+"�ļ��Ѵ���");
	                	}else{
	                		//���������ȡ�ļ�
	                		img=readFile(idno);
	                		if(null==img){
	                			//û�����ļ�
	    	                	writeLine(log, idno+"��ȡ�ļ�ʧ��,�ļ�Ϊ��");
	    						writeLine(error,idno+"��ȡ�ļ�ʧ�ܣ��ļ�Ϊ��");
	                		}
	                	}
	                	
					} catch (Exception e) {
						//��ȡ�ļ�ʧ��
						writeLine(log, idno+"��ȡ�ļ�ʧ��"+e.getMessage());
						writeLine(error,idno+"��ȡ�ļ�ʧ�ܣ�io�쳣");
						e.printStackTrace();
					}
	                if(null!=img){
	                	
	                	//��ȡ�ɹ������ļ�
	                	try {
							//�����ļ�
	                		String path="D:\\enshi\\img\\";
	                		path=path+idno+"-"+name+".jpg";
	                		getFile(img, path);
	                		writeLine(log,"�ɹ���"+idno);
						} catch (Exception e) {
							//����ʧ��
							writeLine(log, idno+"�����ļ�ʧ��"+e.getMessage());
							writeLine(error,idno+"�����ļ�ʧ��");
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
	        System.out.println("--------����----------");
	}
	/**
	 * ��֤�ļ��Ƿ����
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
	 * ��ȡ�ļ�
	 * @param idno
	 * @return
	 * @throws Exception 
	 */
	public static byte[] readFile(String idno) throws Exception{
		String filePath="D:\\enshi\\img2\\"+idno+".jpg";
		return getBytes(filePath);
	}
	
	
	/**
	 * �����ļ�
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
	 * ���ļ���д��һ�� ���׷���
	 * @param fs
	 * @param log
	 * @throws IOException
	 */
	public static void writeLine(FileOutputStream fs,String log) throws IOException{
		fs.write((log+"\r").getBytes());
	}
	
	/**
	 * ���ļ����ж�ȡ�ļ�
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public static byte[] getBytes(String filePath) throws Exception {

		byte[] buffer = null;

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new Exception("�ļ�������");
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
			strReturns = adapter.sendQuery("QueryZZRK", condition, "123456789123456789", "����", "350211");
		} catch (InvokeServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.getErrorCode();//��ȡ�������
			e.getErrorMessage();//��ȡ������ϸ��Ϣ
			
		}
		return strReturns;
	} 
	
	public static String[][] parseXml(String xml) throws DocumentException{
		String[][] resultArys = null;
		Document doc = DocumentHelper.parseText(xml);
		//��ȡRow�ڵ㼯��
		List list = doc.selectNodes("RBSPMessage/Method/Items/Item/Value/Row");
		//�жϽڵ��Ƿ�Ϊ��ͬʱ�ڵ����2
		if (list != null && list.size() > 2) {
			//��ȡ��һ��Row�ڵ�
			Element stateElement = (Element)list.get(0);
			//��ȡ��������״̬
			String state = ((Element)stateElement.elements().get(0)).getText();			
			if (state.equalsIgnoreCase("000")) {//״̬����000��������������
				Element fieldElement = (Element)list.get(1);
				//��ʼ����ά����
				resultArys = new String[list.size() - 2][fieldElement.elements().size()];				
				for (int i = 2; i < list.size(); i++) {
					Element data = (Element)list.get(i);					
					List fields = data.elements();
					for (int j = 0; j < fields.size(); j++) {
						Element tmpE = (Element)fields.get(j);
						//��ά���鸳ֵ
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
