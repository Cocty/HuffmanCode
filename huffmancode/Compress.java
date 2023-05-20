package huffmancode;
import java.io.*;
import java.util.*;
//文件压缩
public class Compress {
	    public static void main(String[] args) {
	    	System.out.println("压缩文件！");
	        String zipFile = "D:\\BaiduNetdiskDownload\\哈夫曼编码\\huffmancode\\哈夫曼编码课设报告.docx";
	        String dstFile = "D:\\BaiduNetdiskDownload\\哈夫曼编码\\huffmancode\\AfterDepressing.code";
	        zipFile(zipFile, dstFile);
	        System.out.println("压缩成功!");
	    }
	    
	    static Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();//哈夫曼编码表
	    static Node huffmantree;
	    static int endLen;//记录最后一个字节的二进制串的长度
	    static int wpl;
	    
	    public static void zipFile(String srcFile, String dstFile) {
	        OutputStream os = null;
	        ObjectOutputStream oos = null;
	        FileInputStream is = null;
	        try {
	            is = new FileInputStream(srcFile);
	            byte[] b = new byte[is.available()];
	            is.read(b);
	            byte[] huffmanBytes = huffmanZip(b);
	            os = new FileOutputStream(dstFile);
	            oos = new ObjectOutputStream(os);
	            //对象序列化
	            oos.writeObject(huffmanBytes);//将编码后的字节数组存入文件
	            oos.writeObject(huffmanCodes);//将哈夫曼表也存入文件
	            oos.writeObject(endLen);
	            System.out.println("压缩比为");
	            System.out.println((double)huffmanBytes.length/b.length);
	            System.out.println("Wpl为：");
		        System.out.println(Wpl(huffmantree));
	            System.out.println("哈弗曼编码表为：");
	            for (Map.Entry<Byte,String > entry : huffmanCodes.entrySet()) {
					System.out.println(entry.getKey()+":"+entry.getValue());
				}
	            System.out.println();
//	            System.out.println("哈弗曼编码为：");
//	            int count = 0;
//	            for (byte a : b) {
//					System.out.print(huffmanCodes.get(a));
//					count++;
//					while(count>100) {
//						System.out.println();
//						count = 0;//使打印出来的编码更加的立体
//					}
//				}
//	            System.out.println();
	 
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } finally {
	            try {
	                is.close();
	                oos.close();
	                os.close();
	            } catch (Exception e) {
	                System.out.println(e.getMessage());
	            }
	        }
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
	        if (root == null) {//哈夫曼树为空
	            return null;
	        }
	        getCodes(root.left, "0", stringBuilder);//向左递归
	        getCodes(root.right, "1", stringBuilder);//向右递归
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
}


