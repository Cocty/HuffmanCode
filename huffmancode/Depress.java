package huffmancode;
//文件解压
import java.io.*;
import java.util.*;

public class Depress {
	
	static int endLen;
    public static void main(String[] args) {
    	System.out.println("解压文件!");
        String zipFile = "D:\\BaiduNetdiskDownload\\哈夫曼编码\\huffmancode\\AfterDepressing.code";
        String dstFile = "D:\\BaiduNetdiskDownload\\哈夫曼编码\\huffmancode\\AfterCoding.docx";
        unZipFile(zipFile, dstFile);
        System.out.println("解压成功!");
    }

    public static void unZipFile(String zipFile, String dstFile) {
        InputStream is = null;
        ObjectInputStream ois = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(zipFile);
            ois = new ObjectInputStream(is);
            //对象的反序列化,从文件中读取对象
            byte[] huffmanBytes = (byte[]) ois.readObject();
            Map<Byte, String> huffmanCodes = (Map<Byte, String>) ois.readObject();
            endLen = (int)ois.readObject();
            byte[] bytes = huffmanUnzip(huffmanCodes, huffmanBytes);
            os = new FileOutputStream(dstFile);
            os.write(bytes);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                os.close();
                ois.close();
                is.close();
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
            }
        }
    }

    //哈夫曼解压
    static byte[] huffmanUnzip(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < huffmanBytes.length; i++) {
            byte b = huffmanBytes[i];
            boolean flag = (i == huffmanBytes.length - 1);//标记是否到编码的最后一位
            stringBuilder.append(byteToBitString(!flag, b));
        }

        //解码,反向编码表,将键值对反过来
        HashMap<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }

        //根据编码扫描到对应的ASCLL码对应的字符
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < stringBuilder.length(); ) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = stringBuilder.substring(i, i + count);
                b = map.get(key);
                if (b == null) {//如果在hash表中找不到对应的编码,增加子串的长度
                    count++;
                } else {//找到了，退出循环
                    flag = false;
                }
            }
            list.add(b);//将编码对应的字节入list
            i += count;//增加在字符串中的索引
        }

        byte b[] = new byte[list.size()];//将list中的字节转换为字节数组
        for (int i = 0; i < b.length; i++) {
            b[i] = list.get(i);
        }
        return b;

    }

  //转化二进制
    static String byteToBitString(boolean flag, byte b) {
  	int temp = b;//将b转换为int
  	temp|=256;
  	 String str = Integer.toBinaryString(temp);// 返回的是temp对应的二进制补码
       if (flag || (flag == false && endLen  == 0)) {
     //字符串的截取，只拿后八位
           return str.substring(str.length() - 8);
       } else {
     //不满8bit有多少位拿多少位
           return str.substring(str.length() - endLen);
       }
     
    }
}
