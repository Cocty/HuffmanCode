package huffmancode;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Window.Type;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;

public class CompressDepress extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	
	static Map<Byte, String> huffmanCodes = new HashMap<Byte, String>();//哈夫曼编码表
	static int endLen;//记录最后一个字节的二进制串的长度
	static Node huffmantree;//哈弗曼树的根结点
	static ArrayList<Node> huffman = new ArrayList();//存储先序遍历的哈夫曼树
	static int wpl;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CompressDepress frame = new CompressDepress();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
//下面是哈夫曼算法
	 //哈夫曼编码压缩
    static byte[] huffmanZip(byte[] bytes) {
       List<Node> nodes = CompressAndDepressString.getNodes(bytes);
       //哈夫曼树
     huffmantree = CompressAndDepressString.createHuffmanTree(nodes);
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
    
    static void preorder(Node root) {
    	if(root!=null) {
    		huffman.add(root);//将根结点加到一个节点数组中，方便后面在面板上打印
    		preorder(root.left);
    		preorder(root.right);
    	}
    }
    
    
	/**
	 * Create the frame.
	 */
	public CompressDepress() {
		setForeground(Color.BLACK);
		setTitle("\u57FA\u4E8Ejava\u7684\u54C8\u592B\u66FC\u7F16\u7801");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("\u8F93\u5165\u4E00\u4E32\u5B57\u7B26");
		JTextArea textArea = new JTextArea();
		textArea.setColumns(20);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton btnNewButton_1 = new JButton("\u538B\u7F29");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					String r = textField.getText();
					byte [] b = r.getBytes();
					byte [] newbyte = huffmanZip(b);
					textArea.append("压缩比为："+(double)newbyte.length/b.length+"\n");
					textArea.append("哈弗曼编码表为："+"\n");
					for (Map.Entry<Byte,String > entry : huffmanCodes.entrySet()) {
			        	Byte a =entry.getKey();
			        	byte c = a.byteValue();
			        	textArea.append(c+":"+entry.getValue()+"\n");
					}
					textArea.append("哈弗曼编码为：\n");
					for (byte a : b) {
						textArea.append(huffmanCodes.get(a));
					}
					textArea.append("\nWpl为：");
					textArea.append(CompressAndDepressString.Wpl(huffmantree)+"\n");
					textArea.append("先序遍历哈夫曼树：\n");
					preorder(huffmantree);
					for (Node a : huffman) {
						textArea.append(a.toString()+"\n");
					}	
			}
		});
		
		JButton btnNewButton_2 = new JButton("\u89E3\u538B");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String r = textField.getText();
				byte [] b = r.getBytes();
				byte [] newbyte = huffmanZip(b);
				byte  bytes [] = huffmanUnzip(huffmanCodes, newbyte);
				textField_1.setText(new String(bytes));	
			}
		});
		
		JLabel lblNewLabel_1 = new JLabel("\u89E3\u538B\u540E\u7684\u5B57\u7B26\u4E32");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField, GroupLayout.PREFERRED_SIZE, 313, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(103)
									.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(190)
							.addComponent(btnNewButton_2, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 483, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(83, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(45)
							.addComponent(btnNewButton_1)
							.addGap(5)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 265, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(btnNewButton_2)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel_1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
					.addGap(66))
		);
		scrollPane.setViewportView(textArea);
		contentPane.setLayout(gl_contentPane);
	}
}
