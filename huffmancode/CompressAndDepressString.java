package huffmancode;
//用户交互输入压缩与解压
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.sql.rowset.JoinRowSet;

public class CompressAndDepressString {
	
	static Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();//哈夫曼编码表
	static int endLen;//记录最后一个字节的二进制串的长度
	static Node huffmantree;//哈夫曼树的根结点
	static int wpl;//树的带权路径长度
	
	public static void main(String[] args) {
		// TODO 自动生成的方法存根
		String r = new String();
		System.out.println("请输入一个字符串：");
		Scanner reader = new Scanner(System.in);
		r = reader.nextLine();
		byte [] b = r.getBytes();
		byte [] newbyte = huffmanZip(b);
		System.out.println("压缩比为：");
		System.out.println((double)newbyte.length/b.length);
		
		System.out.println("哈弗曼编码表为：");
        for (Map.Entry<Byte,String > entry : huffmanCodes.entrySet()) {
        	Byte a =entry.getKey();
        	byte c = a.byteValue();
			System.out.println(c+":"+entry.getValue());
		}
		System.out.println("哈弗曼编码为：");
		int count = 0;
		for (byte a : b) {
			System.out.print(huffmanCodes.get(a));
			count++;
			while(count>100) {
				System.out.println();
				count = 0;//使打印出来的编码更加的立体
				}
			}
		System.out.println();
	   System.out.println("Wpl为：");
       System.out.println(Wpl(huffmantree));
	   System.out.println("原来的字节数组：");
	   for (byte c : b) {
		System.out.print(c);
	   }
	   System.out.println();
	   byte  bytes [] = huffmanUnzip(huffmanCodes, newbyte);
	   System.out.println("解压后的字节数组为：");
	   for (int i = 0; i < bytes.length; i++) {
		   System.out.print(bytes[i]);
	   }
	   System.out.println();
	   System.out.println("解压后的字符串为：");
	   System.out.println(new String(bytes));
   
	}
	
	 //哈夫曼编码压缩
    static byte[] huffmanZip(byte[] bytes) {
       List<Node> nodes = getNodes(bytes);
       //哈夫曼树
        huffmantree = createHuffmanTree(nodes);
       //哈夫曼编码表
       Map<Byte, String> huffmanCodes = getCodes(huffmantree);
       byte[] zip = zip(bytes, huffmanCodes);
       return zip;
   }

   //压缩
    static byte[] zip(byte[] bytes, Map<Byte, String> huffmanCodes) {
       StringBuilder stringBuilder = new StringBuilder();
       for (byte b : bytes) {
			stringBuilder.append(huffmanCodes.get(b));
		}
       int len;
       if (stringBuilder.length() % 8 == 0) {//如果编码长度是八的倍数
           len = stringBuilder.length() / 8;//新字节数组的长度为哈夫曼编码长度/8
       } else {
           len = stringBuilder.length() / 8 + 1;//如果不是则最后一组字节算一个字节数组
       }
       endLen = stringBuilder.length()%8;
       byte[] by = new byte[len];
       int index = 0;
       for (int i = 0; i < stringBuilder.length(); i += 8) {
           String strByte;
           if (i + 8 > stringBuilder.length()) {//如果到了最后一组编码不足8位
               strByte = stringBuilder.substring(i);//截取剩下的编码
               by[index] = (byte) Integer.parseInt(strByte, 2);//将二进制的strByte字符串转化为十进制的字节
               index++;
           } else {//如果还没有到最后一组,则每8个一组
               strByte = stringBuilder.substring(i, i + 8);
               by[index] = (byte) Integer.parseInt(strByte, 2);
               index++;
           }
       }
       return by;
   }

   //获取哈夫曼编码
    static void getCodes(Node node, String code, StringBuilder stringBuilder) {
   	StringBuilder builder = new StringBuilder(stringBuilder);
       builder.append(code);
       if (node != null) {
           if (node.data == null) {  //如果不是叶子节点
               getCodes(node.left, "0", builder);
               getCodes(node.right, "1", builder);
           } else {
               huffmanCodes.put(node.data, builder.toString());
           }
       }
   }
   
 //重载
   static Map<Byte, String> getCodes(Node root) {
   	StringBuilder stringBuilder = new StringBuilder();
       if(root!=null) {
    	     getCodes(root.left, "0", stringBuilder);//向左递归
    	     getCodes(root.right, "1", stringBuilder);//向右递归
       }
       return huffmanCodes;
   }

   //生成哈夫曼树
   static Node createHuffmanTree(List<Node> nodes) {
       while (nodes.size() > 1) {
           Collections.sort(nodes);

           Node leftNode = nodes.get(0);
           Node rightNode = nodes.get(1);

           Node parent = new Node(null, leftNode.weight + rightNode.weight);
           parent.left = leftNode;
           parent.right = rightNode;
           nodes.remove(leftNode);
           nodes.remove(rightNode);
           nodes.add(parent);
       }
       return nodes.get(0);
   }

   //接收字节数组
  static List<Node> getNodes(byte[] bytes) {
       List<Node> nodes = new ArrayList<>();
       Map<Byte, Integer> counts = new HashMap<>();
       for (byte b : bytes) {
           Integer count = counts.get(b);
           if (count == null) {//字符第一次出现
               counts.put(b, 1);
           } else {//字符重复
               counts.put(b, count + 1);//对应原来的值加一
           }
       }
       //遍历map
       for (Map.Entry<Byte, Integer> entry : counts.entrySet()) {
           nodes.add(new Node(entry.getKey(), entry.getValue()));//将键值对加到node数组中
       }
       return nodes;
   }
   
  static int Wpl(Node root) {
		if(root!=null) {
			if(root.left!=null&&root.right!=null) {
				wpl+=root.weight;
			}
			Wpl(root.left);
			Wpl(root.right);
		}
		return wpl;
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
      for (int i = 0; i < stringBuilder.length();) {
          int count = 1;
          boolean flag = true;
          Byte b = null;
          while (flag) {
        	 String key = stringBuilder.substring(i,i+count);
             b = map.get(key);
             if (b == null) {//如果在hash表中找不到对应的编码,增加子串的长度
                  count++;
             } else {//找到了
                  flag = false;   
             }
          }
         list.add(b);//将编码对应的字节入list
         i += count;
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
     if (flag || (flag == false && endLen == 0)) {
   //字符串的截取，只拿后八位
         return str.substring(str.length() - 8);
     } else {
   //不满8bit有多少位拿多少位
         return str.substring(str.length() - endLen);
     }
   
  }

}
