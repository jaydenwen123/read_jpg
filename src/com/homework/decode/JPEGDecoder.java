package com.homework.decode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.homework.recovery.LoadOriginalImage;
import com.homework.util.Tool;

public class JPEGDecoder {

	// 用来存储jpeg文件的关键参数
	public static Map<String, Object> params;
	private int height;

	// Private variables and constants
	private static final int MSB = 0x80000000;

	// max size =MAX_HUFFMAN_SUBTREE *256
	private static final int MAX_HUFFMAN_SUBTREE = 50;

	// number of Components in a scan
	private int nComp;

	// quantization table for the i-th Comp
	// in a scan
	private int[] qTab[] = new int[10][];

	// dc HuffTab for the i-th Comp in a
	// scan
	private int[] dcTab[] = new int[10][];

	// ac HuffTab for the i-th Comp in a
	// scan
	private int[] acTab[] = new int[10][];

	// number of blocks in the i-th Comp in
	// a scan
	private int nBlock[] = new int[10];

	// i=0, ,Ns-1

	private int YH, YV, Xsize, Ysize;

	private int marker;

	private int marker_index = 0;

	// RestartInterval
	private int Ri = 0;

	// at most 10 data units in a MCU
	private int DU[][][] = new int[10][4][64];

	// at most 4 data units in one component

	// the begin point of MCU
	private int x = 0, y = 0, num = 0, yp = 0;

	private int IDCT_Source[] = new int[64];

	private final static int IDCT_P[] = {

	0, 5, 40, 16, 45, 2, 7, 42,

	21, 56, 8, 61, 18, 47, 1, 4,

	41, 23, 58, 13, 32, 24, 37, 10,

	63, 17, 44, 3, 6, 43, 20, 57,

	15, 34, 29, 48, 53, 26, 39, 9,

	60, 19, 46, 22, 59, 12, 33, 31,

	50, 55, 25, 36, 11, 62, 14, 35,

	28, 49, 52, 27, 38, 30, 51, 54

	};

	private final static int table[] = {

	0, 1, 5, 6, 14, 15, 27, 28,

	2, 4, 7, 13, 16, 26, 29, 42,

	3, 8, 12, 17, 25, 30, 41, 43,

	9, 11, 18, 24, 31, 40, 44, 53,

	10, 19, 23, 32, 39, 45, 52, 54,

	20, 22, 33, 38, 46, 51, 55, 60,

	21, 34, 37, 47, 50, 56, 59, 61,

	35, 36, 48, 49, 57, 58, 62, 63

	};

	// 定义SOS0段，
	private FrameHeader FH = new FrameHeader();

	// 定义SOS段的信息
	private ScanHeader SH = new ScanHeader();

	// 定义量化表的信息
	private QuantizationTable QT = new QuantizationTable();

	// 定义哈弗曼表的信息
	private HuffmanTable HT = new HuffmanTable();

	private void error(String message) throws Exception {

		throw new Exception(message);

	}

	// Report progress in the range 0100

	public int progress() {

		if (height == 0)

			return 0;

		if (yp > height)
			return 100;

		return yp * 100 / height;

	}

	public interface PixelArray {

		public void setSize(int width, int height) throws Exception;

		public void setPixel(int x, int y, int argb);

	}

	/**
	 * 定义组件的类。该类里面主要的内容是组件的id，组件的采样系数。以及量化表号
	 * 
	 * @author Administrator
	 * 
	 */
	class ComponentSpec {

		// Component id
		int C;

		// 以下的两个参数通过对采样系数进行高四位和低四位移位来得到
		// Horizontal sampling factor水平采样系数
		int H;

		// Vertical .垂直采样系数，
		int V;

		// 对应的量化表号
		// Quantization table destination selector
		int Tq;

	}

	/**
	 * 定义SOF0段的数据格式
	 * 
	 * @author Administrator
	 * 
	 */
	class FrameHeader {

		// Start of frame in different type
		int SOF;

		// Length
		int Lf;

		// Sample Precision (from the orignal image)
		int P;

		// Number of lines
		int Y;

		// Number of samples per line
		int X;

		// Number of component in the frame
		int Nf;

		// 定义盛放组件的数组，其实也可以使用java里面的list集合来存放
		ComponentSpec Comp[]; // Components C H V Tq

		// 该方法用来从得到的数据中读取SOF0段的信息、
		public int get(InputStream in, int sof) throws Exception {

			// get data from file stream in

			// return 0 : correct otherwise : error

			int i, temp, count = 0, c;

			// 该值指的是短的标识符
			SOF = sof;

			// 该方法get16()是用来读取十六个二进制数。get8()是指用来读取8个二进制数
			Lf = get16(in);
			count += 2;

			P = get8(in);
			count++;

			Y = get16(in);
			count += 2;

			height = Y;

			X = get16(in);
			count += 2;

			// width=X;

			Nf = get8(in);
			count++;

			// 首先初始化存放组件的数组容器
			Comp = new ComponentSpec[Nf + 1];

			// 初始化数组中的每歌组件
			for (i = 0; i <= Nf; i++) {
				Comp[i] = new ComponentSpec();
			}

			// 为了方便起见。是从1到n开始存放组件的
			for (i = 1; i <= Nf; i++) {

				if (count > Lf) {

					error("ERROR: frame format error");

				}

				c = get8(in);
				count++;

				if (c >= Lf) {

					error("ERROR: fram format error [c>=Lf]");

				}

				// 一下的过程都是用来不断的读取数据。然后给组件赋值。
				Comp[c].C = c;

				// 该变量temp是的值是读进来的一个字节的数值，表示的才样系数
				temp = get8(in);
				count++;

				// 然后将采样系数进行右移四位，得到的是水平的采样系数
				Comp[c].H = temp >> 4;

				// 如果不进行右移，直接和0xf进行做&的运算，得到的是垂直采样系数
				Comp[c].V = temp & 0x0F;

				// 一下是读进来一个字节的值，表示量化后的表号
				Comp[c].Tq = get8(in);
				count++;

			}

			if (count != Lf) {

				error("ERROR: frame format error [Lf!=count]");

			}

			return 1;

		}

	}

	/**
	 * 定义的扫描行开始的行内组件数
	 * 
	 * @author Administrator
	 * 
	 */
	class ScanComponent {

		// Scan component selector 扫描的组件的id，1=Y，2=Cb，3=Cr，4=I,5=Q
		int Cs;

		// 以下的直流表号和交流表号，需要从一个字节中进行解析，4-7即高四位指的是DC表号0-3位表示的是AC表号

		// DC table selector 对应的霍夫曼的直流表号
		int Td;

		// AC table selector 对应的霍夫曼的交流表号
		int Ta;

	}

	/**
	 * 定义SOS扫描行开始的数据结构
	 * 
	 * @author Administrator
	 * 
	 */
	class ScanHeader {

		// length,数据长度
		int Ls;

		// Number of components in the scan 扫描行内组件的数量
		int Ns;

		// 定义最后三个字节的值。
		// Start of spectral or predictor selection
		int Ss;

		// End of spectral selection
		int Se;

		int Ah;

		int Al;

		// 定义存放组件的数据容器
		ScanComponent Comp[]; // Components Cs Td Ta

		// from [0] to [Ns-1]

		int get(InputStream in) throws Exception {

			// get data from file stream in

			// return 0 : correct otherwise : error

			int i, temp, count = 0;

			// 读入段的长度
			Ls = get16(in);
			count += 2;

			// 读入扫描行内组件的数量
			Ns = get8(in);
			count++;

			// 创建数组容器
			Comp = new ScanComponent[Ns];

			// 循环遍历来初始化数组
			for (i = 0; i < Ns; i++) {

				Comp[i] = new ScanComponent();

				if (count > Ls) {

					error("ERROR: scan header format error");

				}

				Comp[i].Cs = get8(in);
				count++;

				temp = get8(in);
				count++;

				// 分别对读进来的一个字节的数据进行解析，得到直流和交流的表号
				Comp[i].Td = temp >> 4;

				Comp[i].Ta = temp & 0x0F;

			}

			Ss = get8(in);
			count++;

			Se = get8(in);
			count++;

			temp = get8(in);
			count++;

			Ah = temp >> 4;

			Al = temp & 0x0F;

			if (count != Ls) {

				error("ERROR: scan header format error [count!=Ns]");

			}

			return 1;

		}

	}

	/**
	 * 定义量化表段的数据结构即DQT段的数据结构
	 * 
	 * @author Administrator
	 * 
	 */
	class QuantizationTable {

		// length定义数据长度
		int Lq;

		// Quantization precision 8 or 16 定义QT的
		int Pq[] = new int[4];

		// 1: this table is presented
		int Tq[] = new int[4];

		// Tables
		int Q[][] = new int[4][64];

		public QuantizationTable() {

			Tq[0] = 0;
			Tq[1] = 0;
			Tq[2] = 0;
			Tq[3] = 0;
			Pq[0] = 0;
			Pq[1] = 0;
			Pq[2] = 0;
			Pq[3] = 0;

		}

		int get(InputStream in) throws Exception {

			// get data from file stream in

			// return 0 : correct otherwise : error

			int i, count = 0, temp, t;

			Lq = get16(in);
			count += 2;

			while (count < Lq) {

				// 读入一个字节的数据
				temp = get8(in);
				count++;

				// 首先该字节的低四位表示的是QT号
				t = temp & 0x0F;

				if (t > 3) {

					error("ERROR: Quantization table ID > 3");

				}

				// 将temp的数据右移四位后得到高四位，该值表示的是QT的精度
				Pq[t] = temp >> 4;

				if (Pq[t] == 0)
					Pq[t] = 8;

				else if (Pq[t] == 1)
					Pq[t] = 16;

				else {

					error("ERROR: Quantization table precision error");

				}

				Tq[t] = 1;

				if (Pq[t] == 8) {

					for (i = 0; i < 64; i++) {

						if (count > Lq) {

							error("ERROR: Quantization table format error");

						}

						Q[t][i] = get8(in);
						count++;

					}
					// 打印一种的表
					// printQT(QT.Q[0]);
					// 打印QT中的量化表
					// printQT(QT.Q[1]);
					// 对该量化矩阵进行加强，一次通过循环遍历该数组来实现
					EnhanceQuantizationTable(Q[t]);

				} else {

					for (i = 0; i < 64; i++) {

						if (count > Lq) {

							error("ERROR: Quantization table format error");

						}

						Q[t][i] = get16(in);
						count += 2;

					}
					// 对量化表中的数据进行加强
					EnhanceQuantizationTable(Q[t]);

				}

			}

			if (count != Lq) {

				error("ERROR: Quantization table error [count!=Lq]");

			}

			return 1;

		}

		/**
		 * 打印量化表的中的值
		 */
		private void printQT(int[] QTValue) {
			// 打印量化表中数据和增强后的量化表的值。
			int[] data = QTValue;
			StringBuffer buffer = new StringBuffer("量化表中的关键信息：\r\n");
			buffer.append("量化表的值：\r\n");
			StringBuilder builder = new StringBuilder();
			for (int k = 0; k < data.length; k++) {
				// 将该整数通过十六进制的数据展现出来
				String str = Tool
						.Bytes2HexString(new byte[] { (byte) data[k] });
				if (k % 8 == 0 && k > 0) {
					builder.append("\n");
				}
				builder.append(str + "   ");
			}
			buffer.append(builder + "\r\n");
			System.out.println(buffer.toString());
			// 将信息写入到文件中
			OutputInfoToFile(buffer);
		}

	}

	/**
	 * 定义DHT段的数据结构
	 * 
	 * @author Administrator
	 * 
	 */
	class HuffmanTable {

		// Length
		int Lh;

		// 1: this table is presented
		// 存储HT类型
		int Tc[][] = new int[4][2];

		// 1: this table is presented
		// 存储HT号
		int Th[] = new int[4];

		// 存储HT的位表，存储哈弗曼的编码长度的个数
		int L[][][] = new int[4][2][16];

		// tables
		// 存储HT的值表
		int V[][][][] = new int[4][2][16][200];

		public HuffmanTable() {

			Tc[0][0] = 0;
			Tc[1][0] = 0;
			Tc[2][0] = 0;
			Tc[3][0] = 0;

			Tc[0][1] = 0;
			Tc[1][1] = 0;
			Tc[2][1] = 0;
			Tc[3][1] = 0;

			Th[0] = 0;
			Th[1] = 0;
			Th[2] = 0;
			Th[3] = 0;

		}

		/**
		 * 通过输入流来读取。数据
		 * 
		 * @param in
		 * @return
		 * @throws Exception
		 */
		int get(InputStream in) throws Exception {

			// get data from file stream in

			// return 0 : correct otherwise : error

			int i, j, temp, count = 0, t, c;

			// 读取DHT段的段长度
			Lh = get16(in);
			count += 2;

			while (count < Lh) {

				// 读进来一个字节的数据
				temp = get8(in);
				count++;

				t = temp & 0x0F;

				if (t > 3) {

					error("ERROR: Huffman table ID > 3");

				}

				// 读取temp的高四位的值。
				c = temp >> 4;

				if (c > 2) {

					error("ERROR: Huffman table [Table class > 2 ]");

				}

				Th[t] = 1;
				Tc[t][c] = 1;

				// 依次读入HT的位表的值，读十六个字节。
				for (i = 0; i < 16; i++) {

					L[t][c][i] = get8(in);
					count++;

				}

				// // 通过二重循环来读取哈弗曼的码表的内容。
				// int p=0;
				// StringBuilder builder=new StringBuilder("哈弗曼码表的内容如下：\n");
				for (i = 0; i < 16; i++) {
					for (j = 0; j < L[t][c][i]; j++) {
						// p++

						if (count > Lh) {

							error("ERROR: Huffman table format error [count>Lh]");

						}

						V[t][c][i][j] = get8(in);
						count++;
						// 以下用来输出哈弗曼码表的值
						// if(p % 6==0 && p>0){
						// builder.append("\n");
						// }
						// builder.append(Integer.toHexString(V[t][c][i][j])+"\t");
					}
				}
				// System.out.println(builder);

			}

			if (count != Lh) {

				error("ERROR: Huffman table format error [count!=Lf]");

			}

			// 读取完之后，建立哈弗曼树
			for (i = 0; i < 4; i++)

				for (j = 0; j < 2; j++)

					if (Tc[i][j] != 0) {
						// 建立霍夫曼表。
						Build_HuffTab(HuffTab[i][j], L[i][j], V[i][j]);

					}

			return 1;

		}

		private void printHT(int[] QTValue) {
			// 打印量化表中数据和增强后的量化表的值。
			int[] data = QTValue;
			System.out.println("哈弗曼位表的值：");
			StringBuilder builder = new StringBuilder();
			for (int k = 0; k < data.length; k++) {
				String str = Tool
						.Bytes2HexString(new byte[] { (byte) data[k] });
				if (k % 8 == 0 && k > 0) {
					builder.append("\n");
				}
				builder.append(str + "\t");
			}
			System.out.println(builder.toString());
		}

	}

	private int readNumber(InputStream in) throws Exception {

		int Ld;

		Ld = get16(in);

		if (Ld != 4) {

			error("ERROR: Define number format error [Ld!=4]");

		}

		return get16(in);

	}

	/**
	 * 读取COM注释段
	 * 
	 * @param in
	 * @return 返回的是注释信息
	 * @throws Exception
	 */
	private String readComment(InputStream in) throws Exception {

		int Lc, count = 0, i;

		StringBuffer sb = new StringBuffer();
		// 获取段长度。
		Lc = get16(in);
		count += 2;

		for (i = 0; count < Lc; i++) {

			sb.append((char) get8(in));
			count++;

		}

		return sb.toString();

	}

	/**
	 * 读取app0段的数据
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private int readApp(InputStream in) throws Exception {

		int Lp;

		int count = 0;

		Lp = get16(in);
		count += 2;

		while (count < Lp) {

			get8(in);
			count++;

		}

		return Lp;

	}

	/**
	 * 从输入流中读取一个字节的数据，即8位
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private final int get8(InputStream in) throws Exception {

		try {

			return in.read();

		} catch (IOException e) {

			error("get8() read error: " + e.toString());

			return -1;

		}

	}

	// get 16-bit data

	/**
	 * 从输入流中读入两个字节的数据。即转为二进制是16位
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private final int get16(InputStream in) throws Exception {

		int temp;

		try {

			temp = in.read();

			temp <<= 8;

			return temp | in.read();

		} catch (IOException e) {

			error("get16() read error: " + e.toString());

			return -1;

		}

	}

	/** */
	/********************************************************************
	 * Huffman table for fast search: (HuffTab) 8-bit Look up table
	 * 
	 * 2-layer search architecture, 1st-layer represent 256 node (8 btis)
	 * 
	 * if codeword-length > 8 bits, then
	 * 
	 * the entry of 1st-layer = (# of 2nd-layer table) | MSB
	 * 
	 * and it is stored in the 2nd-layer
	 * 
	 * Size of tables in each layer are 256.
	 * 
	 * HuffTab[*][*][0-256] is always the only 1st-layer table.
	 * 
	 * 
	 * 
	 * An entry can be:
	 * 
	 * (1) (# of 2nd-layer table) | MSB , for code length > 8 in 1st-layer
	 * 
	 * (2) (Code length) << 8 | HuffVal
	 ********************************************************************/

	private int HuffTab[][][] = new int[4][2][MAX_HUFFMAN_SUBTREE * 256];

	/**//*
		 * 建立霍夫曼的二叉树 Build_HuffTab()
		 * 
		 * Parameter: t table ID
		 * 
		 * c table class ( 0 for DC, 1 for AC )
		 * 
		 * L[i] # of codewords which length is i
		 * 
		 * V[i][j] Huffman Value (length=i)
		 * 
		 * Effect:
		 * 
		 * build up HuffTab[t][c] using L and V.
		 */

	private void Build_HuffTab(int tab[], int L[], int V[][]) throws Exception {

		int current_table, i, j, n, table_used, temp;

		int k;

		temp = 256;

		k = 0;

		for (i = 0; i < 8; i++) { // i+1 is Code length

			for (j = 0; j < L[i]; j++) {

				for (n = 0; n < (temp >> (i + 1)); n++) {

					tab[k] = V[i][j] | ((i + 1) << 8);

					k++;

				}

			}

		}

		for (i = 1; k < 256; i++, k++)
			tab[k] = i | MSB;

		if (i > 50) {

			error("ERROR: Huffman table out of memory!");

		}

		table_used = i;

		current_table = 1;

		k = 0;

		for (i = 8; i < 16; i++) { // i+1 is Code length

			for (j = 0; j < L[i]; j++) {

				for (n = 0; n < (temp >> (i - 7)); n++) {

					tab[current_table * 256 + k] = V[i][j] | ((i + 1) << 8);

					k++;

				}

				if (k >= 256) {

					if (k > 256) {

						error("ERROR: Huffman table error(1)!");

					}

					k = 0;
					current_table++;

				}

			}

		}

	}

	/**//*
		 * HuffmanValue():
		 * 
		 * return: Huffman Value of table
		 * 
		 * 0xFF?? if it receives a MARKER
		 * 
		 * Parameter: table HuffTab[x][y] (ex) HuffmanValue(HuffTab[1][0],)
		 * 
		 * temp temp storage for remainded bits
		 * 
		 * index index to bit of temp
		 * 
		 * in FILE pointer
		 * 
		 * Effect:
		 * 
		 * temp store new remainded bits
		 * 
		 * index change to new index
		 * 
		 * in change to new position
		 * 
		 * NOTE:
		 * 
		 * Initial by temp=0; index=0;
		 * 
		 * NOTE: (explain temp and index)
		 * 
		 * temp: is always in the form at calling time or returning time
		 * 
		 * | byte 4 | byte 3 | byte 2 | byte 1 |
		 * 
		 * | 0 | 0 | 00000000 | 00000??? | if not a MARKER
		 * 
		 * ^index=3 (from 0 to 15)
		 * 
		 * 
		 * NOTE (marker and marker_index):
		 * 
		 * If get a MARKER from 'in', marker=the low-byte of the MARKER
		 * 
		 * and marker_index=9
		 * 
		 * If marker_index=9 then index is always > 8, or HuffmanValue()
		 * 
		 * will not be called.
		 */

	private int HuffmanValue(int table[], int temp[], int index[],
			InputStream in) throws Exception {

		int code, input, mask = 0xFFFF;

		if (index[0] < 8) {

			temp[0] <<= 8;

			input = get8(in);

			if (input == 0xFF) {

				marker = get8(in);

				if (marker != 0)
					marker_index = 9;

			}

			temp[0] |= input;

		} else
			index[0] -= 8;

		code = table[temp[0] >> index[0]];

		if ((code & MSB) != 0) {

			if (marker_index != 0) {
				marker_index = 0;
				return 0xFF00 | marker;
			}

			temp[0] &= (mask >> (16 - index[0]));

			temp[0] <<= 8;

			input = get8(in);

			if (input == 0xFF) {

				marker = get8(in);

				if (marker != 0)
					marker_index = 9;

			}

			temp[0] |= input;

			code = table[(code & 0xFF) * 256 + (temp[0] >> index[0])];

			index[0] += 8;

		}

		index[0] += 8 - (code >> 8);

		if (index[0] < 0)
			error("index=" + index[0] + " temp=" + temp[0] + " code=" + code
					+ " in HuffmanValue()");

		if (index[0] < marker_index) {
			marker_index = 0;
			return 0xFF00 | marker;
		}

		temp[0] &= (mask >> (16 - index[0]));

		return code & 0xFF;

	}

	// get n-bit signed data from file 'in'

	// temp is defined as before

	// return signed integer or 0x00FF??00 if it sees a MARKER

	private int getn(InputStream in, int n, int temp[], int index[])
			throws Exception {

		int result, one = 1, n_one = -1;

		int mask = 0xFFFF, input;

		if (n == 0)
			return 0;

		index[0] -= n;

		if (index[0] >= 0) {

			if (index[0] < marker_index) {

				marker_index = 0;

				return (0xFF00 | marker) << 8;

			}

			result = temp[0] >> index[0];

			temp[0] &= (mask >> (16 - index[0]));

		} else {

			temp[0] <<= 8;

			input = get8(in);

			if (input == 0xFF) {

				marker = get8(in);

				if (marker != 0)
					marker_index = 9;

			}

			temp[0] |= input;

			index[0] += 8;

			if (index[0] < 0) {

				if (marker_index != 0) {

					marker_index = 0;

					return (0xFF00 | marker) << 8;

				}

				temp[0] <<= 8;

				input = get8(in);

				if (input == 0xFF) {

					marker = get8(in);

					if (marker != 0)
						marker_index = 9;

				}

				temp[0] |= input;

				index[0] += 8;

			}

			if (index[0] < 0)
				error("index=" + index[0] + " in getn()");

			if (index[0] < marker_index) {

				marker_index = 0;

				return (0xFF00 | marker) << 8;

			}

			result = temp[0] >> index[0];

			temp[0] &= (mask >> (16 - index[0]));

		}

		if (result < (one << (n - 1)))

			result += (n_one << n) + 1;

		return result;

	}

	/** */
	/******************************************************************
	 * Decode MCU
	 * 
	 * 
	 * 
	 * DU[i][j][8][8] the j-th data unit of component i.
	 ******************************************************************/

	private int YUV_to_BGR(int Y, int u, int v) {

		if (Y < 0)
			Y = 0;

		int tempB, tempG, tempR;

		tempB = Y + ((116130 * u) >> 16);

		if (tempB < 0)
			tempB = 0;

		else if (tempB > 255)
			tempB = 255;

		tempG = Y - ((22554 * u + 46802 * v) >> 16);

		if (tempG < 0)
			tempG = 0;

		else if (tempG > 255)
			tempG = 255;

		tempR = Y + ((91881 * v) >> 16);

		if (tempR < 0)
			tempR = 0;

		else if (tempR > 255)
			tempR = 255;

		return 0xff000000 | ((tempR << 16) + (tempG << 8) + tempB);

	}

	/**//*
		 * output()
		 * 
		 * x, y should be the starting point of MCU when calling output(..)
		 * 
		 * it means output() should set x,y for the next MCU at the end.
		 */

	private void output(PixelArray out) {

		int temp_x, temp_8y, temp;

		int k = 0;

		int DU10[], DU20[];

		DU10 = DU[1][0];
		DU20 = DU[2][0];

		num++;

		for (int i = 0; i < YV; i++) {

			for (int j = 0; j < YH; j++) {

				temp_8y = i * 32;

				temp_x = temp = j * 4;

				for (int l = 0; l < 64; l++) {

					if (x < Xsize && y < Ysize) {

						out.setPixel(
								x,
								y,

								YUV_to_BGR(DU[0][k][l] + 128, DU10[temp_8y
										+ temp_x], DU20[temp_8y + temp_x]));

					}

					x++;

					if ((x % YH) == 0)
						temp_x++;

					if ((x % 8) == 0) {

						y++;
						x -= 8;

						temp_x = temp;

						if ((y % YV) == 0)
							temp_8y += 8;

					}

				}

				k++;

				x += 8;

				y -= 8;

			}

			x -= YH * 8;

			y += 8;

		}

		x += YH * 8;

		y -= YV * 8;

		if (x >= Xsize) {

			y += YV * 8;

			x = 0;

		}

		yp = y;

	}

	private void level_shift(int du[], int P) throws Exception {

		int i;

		if (P == 8) {

			for (i = 0; i < 64; i++)

				du[i] += 128;

		} else if (P == 12) {

			for (i = 0; i < 64; i++)

				du[i] += 2048;

		} else

			error("ERROR: Precision=" + P);

	}

	/**//*
		 * decode_MCU()
		 * 
		 * return 0 if correctly decoded
		 * 
		 * 0xFF?? if it sees a MARKER
		 */

	private int decode_MCU(InputStream in, int PrevDC[],

	int temp[], int index[]) throws Exception

	{

		int value, actab[], dctab[];

		int qtab[], Cs;

		for (Cs = 0; Cs < nComp; Cs++) {

			qtab = qTab[Cs];
			actab = acTab[Cs];
			dctab = dcTab[Cs];

			for (int i = 0; i < nBlock[Cs]; i++) {

				for (int k = 0; k < IDCT_Source.length; k++)

					IDCT_Source[k] = 0;

				value = HuffmanValue(dctab, temp, index, in);

				if (value >= 0xFF00)

					return value;

				PrevDC[Cs] = IDCT_Source[0] = PrevDC[Cs]
						+ getn(in, value, temp, index);

				IDCT_Source[0] *= qtab[0];

				for (int j = 1; j < 64; j++) {

					value = HuffmanValue(actab, temp, index, in);

					if (value >= 0xFF00)

						return value;

					j += (value >> 4);

					if ((value & 0x0F) == 0) {

						if ((value >> 4) == 0)
							break;

					} else {

						IDCT_Source[IDCT_P[j]] =

						getn(in, value & 0x0F, temp, index) * qtab[j];

					}

				}

				ScaleIDCT(DU[Cs][i]);

			}

		}

		return 0;

	}

	// in-place operation

	private void EnhanceQuantizationTable(int qtab[]) {

		int i;

		for (i = 0; i < 8; i++)

		{

			qtab[table[0 * 8 + i]] *= 90;

			qtab[table[4 * 8 + i]] *= 90;

			qtab[table[2 * 8 + i]] *= 118;

			qtab[table[6 * 8 + i]] *= 49;

			qtab[table[5 * 8 + i]] *= 71;

			qtab[table[1 * 8 + i]] *= 126;

			qtab[table[7 * 8 + i]] *= 25;

			qtab[table[3 * 8 + i]] *= 106;

		}

		for (i = 0; i < 8; i++)

		{

			qtab[table[0 + 8 * i]] *= 90;

			qtab[table[4 + 8 * i]] *= 90;

			qtab[table[2 + 8 * i]] *= 118;

			qtab[table[6 + 8 * i]] *= 49;

			qtab[table[5 + 8 * i]] *= 71;

			qtab[table[1 + 8 * i]] *= 126;

			qtab[table[7 + 8 * i]] *= 25;

			qtab[table[3 + 8 * i]] *= 106;

		}

		for (i = 0; i < 64; i++) {

			qtab[i] >>= 6;

		}

	}

	// out-of-place operation

	// input: IDCT_Source

	// output: matrix

	private void ScaleIDCT(int matrix[]) {

		int p[][] = new int[8][8];

		int t0, t1, t2, t3, i;

		int src0, src1, src2, src3, src4, src5, src6, src7;

		int det0, det1, det2, det3, det4, det5, det6, det7;

		int mindex = 0;

		for (i = 0; i < 8; i++)

		{

			src0 = IDCT_Source[0 * 8 + i];

			src1 = IDCT_Source[1 * 8 + i];

			src2 = IDCT_Source[2 * 8 + i] - IDCT_Source[3 * 8 + i];

			src3 = IDCT_Source[3 * 8 + i] + IDCT_Source[2 * 8 + i];

			src4 = IDCT_Source[4 * 8 + i] - IDCT_Source[7 * 8 + i];

			src6 = IDCT_Source[5 * 8 + i] - IDCT_Source[6 * 8 + i];

			t0 = IDCT_Source[5 * 8 + i] + IDCT_Source[6 * 8 + i];

			t1 = IDCT_Source[4 * 8 + i] + IDCT_Source[7 * 8 + i];

			src5 = t0 - t1;

			src7 = t0 + t1;

			//

			det4 = -src4 * 480 - src6 * 192;

			det5 = src5 * 384;

			det6 = src6 * 480 - src4 * 192;

			det7 = src7 * 256;

			t0 = src0 * 256;

			t1 = src1 * 256;

			t2 = src2 * 384;

			t3 = src3 * 256;

			det3 = t3;

			det0 = t0 + t1;

			det1 = t0 - t1;

			det2 = t2 - t3;

			//

			src0 = det0 + det3;

			src1 = det1 + det2;

			src2 = det1 - det2;

			src3 = det0 - det3;

			src4 = det6 - det4 - det5 - det7;

			src5 = det5 - det6 + det7;

			src6 = det6 - det7;

			src7 = det7;

			//

			p[0][i] = (src0 + src7 + (1 << 12)) >> 13;

			p[1][i] = (src1 + src6 + (1 << 12)) >> 13;

			p[2][i] = (src2 + src5 + (1 << 12)) >> 13;

			p[3][i] = (src3 + src4 + (1 << 12)) >> 13;

			p[4][i] = (src3 - src4 + (1 << 12)) >> 13;

			p[5][i] = (src2 - src5 + (1 << 12)) >> 13;

			p[6][i] = (src1 - src6 + (1 << 12)) >> 13;

			p[7][i] = (src0 - src7 + (1 << 12)) >> 13;

		}

		//

		for (i = 0; i < 8; i++)

		{

			src0 = p[i][0];

			src1 = p[i][1];

			src2 = p[i][2] - p[i][3];

			src3 = p[i][3] + p[i][2];

			src4 = p[i][4] - p[i][7];

			src6 = p[i][5] - p[i][6];

			t0 = p[i][5] + p[i][6];

			t1 = p[i][4] + p[i][7];

			src5 = t0 - t1;

			src7 = t0 + t1;

			//

			det4 = -src4 * 480 - src6 * 192;

			det5 = src5 * 384;

			det6 = src6 * 480 - src4 * 192;

			det7 = src7 * 256;

			t0 = src0 * 256;

			t1 = src1 * 256;

			t2 = src2 * 384;

			t3 = src3 * 256;

			det3 = t3;

			det0 = t0 + t1;

			det1 = t0 - t1;

			det2 = t2 - t3;

			//

			src0 = det0 + det3;

			src1 = det1 + det2;

			src2 = det1 - det2;

			src3 = det0 - det3;

			src4 = det6 - det4 - det5 - det7;

			src5 = det5 - det6 + det7;

			src6 = det6 - det7;

			src7 = det7;

			//

			matrix[mindex++] = (src0 + src7 + (1 << 12)) >> 13;

			matrix[mindex++] = (src1 + src6 + (1 << 12)) >> 13;

			matrix[mindex++] = (src2 + src5 + (1 << 12)) >> 13;

			matrix[mindex++] = (src3 + src4 + (1 << 12)) >> 13;

			matrix[mindex++] = (src3 - src4 + (1 << 12)) >> 13;

			matrix[mindex++] = (src2 - src5 + (1 << 12)) >> 13;

			matrix[mindex++] = (src1 - src6 + (1 << 12)) >> 13;

			matrix[mindex++] = (src0 - src7 + (1 << 12)) >> 13;

		}

	}

	public void decode(InputStream in, PixelArray out) throws Exception {

		int current, m, i, scan_num = 0, RST_num;

		int PRED[] = new int[10];

		if (in == null)
			return;

		x = 0;
		y = 0;
		yp = 0;
		num = 0;

		current = get16(in);

		if (current != 0xFFD8) { // SOI

			error("Not a JPEG file");

			return;

		}

		current = get16(in);

		while (current >> 4 != 0x0FFC) { // SOF 0~15

			switch (current) {

			case 0xFFC4: // DHT

				HT.get(in);
				break;

			case 0xFFCC: // DAC

				error("Program doesn't support arithmetic coding. (format error)");

				return;

			case 0xFFDB:// DQT

				// 在该位置读取量化表的信息
				QT.get(in);
				/*
				 * System.out.println("1"); byte[]
				 * data=QT.Q[0].toString().getBytes();
				 * System.out.println(Tool.Bytes2HexString(data)); byte[]
				 * data1=QT.Q[1].toString().getBytes();
				 * System.out.println(Tool.Bytes2HexString(data1));
				 */
				break;

			case 0xFFDD:

				Ri = readNumber(in);
				break;

			case 0xFFE0:
				ReadAPP0(in);
				break;
			case 0xFFE1:
			case 0xFFE2:
			case 0xFFE3:

			case 0xFFE4:
			case 0xFFE5:
			case 0xFFE6:
			case 0xFFE7:

			case 0xFFE8:
			case 0xFFE9:
			case 0xFFEA:
			case 0xFFEB:

			case 0xFFEC:
			case 0xFFED:
			case 0xFFEE:
			case 0xFFEF:

				readApp(in);
				break;

			case 0xFFFE:

				readComment(in);
				break;

			default:

				if (current >> 8 != 0xFF) {

					error("ERROR: format error! (decode)");

				}

			}

			current = get16(in);

		}

		if (current < 0xFFC0 || current > 0xFFC7) {

			error("ERROR: could not handle arithmetic code!");

		}

		FH.get(in, current);

		current = get16(in);

		// pix = new int[FH.X * FH.Y];

		out.setSize(FH.X, FH.Y);

		do {

			while (current != 0x0FFDA) { // SOS

				switch (current) {

				// 再该位置读写霍夫曼表的信息
				case 0xFFC4: // DHT
					HT.get(in);

					break;

				case 0xFFCC: // DAC

					error("Program doesn't support arithmetic coding. (format error)");

				case 0xFFDB:

					QT.get(in);
					break;

				case 0xFFDD:

					Ri = readNumber(in);
					break;

				case 0xFFE0:
					// 读取APP0段的数据
					ReadAPP0(in);
					break;
				case 0xFFE1:
				case 0xFFE2:
				case 0xFFE3:

				case 0xFFE4:
				case 0xFFE5:
				case 0xFFE6:
				case 0xFFE7:

				case 0xFFE8:
				case 0xFFE9:
				case 0xFFEA:
				case 0xFFEB:

				case 0xFFEC:
				case 0xFFED:
				case 0xFFEE:
				case 0xFFEF:

					readApp(in);
					break;

				case 0xFFFE:

					readComment(in);
					break;

				default:

					if (current >> 8 != 0xFF) {

						error("ERROR: format error! (Parser.decode)");

					}

				}

				current = get16(in);

			}

			SH.get(in);

			nComp = (int) SH.Ns;

			for (i = 0; i < nComp; i++) {

				int CompN = SH.Comp[i].Cs;

				qTab[i] = QT.Q[FH.Comp[CompN].Tq];

				nBlock[i] = FH.Comp[CompN].V * FH.Comp[CompN].H;

				dcTab[i] = HuffTab[SH.Comp[i].Td][0];

				acTab[i] = HuffTab[SH.Comp[i].Ta][1];

			}

			YH = FH.Comp[1].H;
			YV = FH.Comp[1].V;

			Xsize = FH.X;
			Ysize = FH.Y;

			scan_num++;

			m = 0;

			for (RST_num = 0;; RST_num++) { // Decode one scan

				int MCU_num;

				int temp[] = new int[1]; // to store remainded bits

				int index[] = new int[1];

				temp[0] = 0;

				index[0] = 0;

				for (i = 0; i < 10; i++)
					PRED[i] = 0;

				if (Ri == 0) {

					current = decode_MCU(in, PRED, temp, index);

					// 0: correctly decoded

					// otherwise: MARKER

					while (current == 0) {

						m++;

						output(out);

						current = decode_MCU(in, PRED, temp, index);

					}

					break; // current=MARKER

				}

				for (MCU_num = 0; MCU_num < Ri; MCU_num++) {

					current = decode_MCU(in, PRED, temp, index);

					output(out);

					// fprintf(show,"%i ",MCU_num);

					if (current != 0)
						break;

				}

				if (current == 0) {

					if (marker_index != 0) {

						current = (0xFF00 | marker);

						marker_index = 0;

					} else
						current = get16(in);

				}

				if (current >= 0xFFD0 && current <= 0xFFD7) {

				} else
					break; // current=MARKER

			}

			if (current == 0xFFDC && scan_num == 1) { // DNL

				readNumber(in);

				current = get16(in);

			}

		} while (current != 0xFFD9);

	}

	/**
	 * 解析jpeg文件主要的关键信息
	 */
	public void parse() {
		parseAPP0();
		parseSOF0();
		parseDQT();
		parseDHT();
		parseSOS();
	}

	/**
	 * 解析DHT段的信息
	 */
	public void parseDHT() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer("\r\n四个霍夫曼位表的信息如下：\r\n");
		for (int k = 0; k < 2; k++) {
			for (int j = 0; j < 2; j++) {
				HT.printHT(HT.L[k][j]);
			}
		}
		buffer.append("霍夫曼码表的内容如下：\r\n");
		int q = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				q++;
				StringBuilder builder = new StringBuilder("第" + q
						+ "个霍夫曼码表的内容:\r\n");
				int p = 0;
				for (int k = 0; k < 16; k++) {
					for (int m = 0; m < HT.L[i][j][k]; m++, p++) {
						if (p % 6 == 0 && p > 0) {
							builder.append("\n");
						}
						builder.append(Tool
								.Bytes2HexString(new byte[] { (byte) HT.V[i][j][k][m] })
								+ "   ");

					}
				}
				buffer.append(builder.toString() + "\r\n");
				// System.out.println(builder);
			}
		}
		params.put("dhtNum", q);
		System.out.println(buffer);
		OutputInfoToFile(buffer);

	}

	/**
	 * 解析SOS段的数据
	 */
	public void parseSOS() {
		StringBuffer buffer = new StringBuffer("\r\n以下是对SOS扫描行段的信息：\r\n");
		ScanHeader sos = SH;
		int componentNum = sos.Ns;
		buffer.append("扫描的组件数为：" + componentNum + "\r\n");
		params.put("scanComponentNum", componentNum);
		ScanComponent list[] = sos.Comp;
		System.out.println(list.length);
		for (int i = 0; i < list.length; i++) {
			ScanComponent component = list[i];
			int id = component.Cs;
			int humTableDC = component.Td;
			int humTableAC = component.Ta;

			switch (id) {
			case 1:
				buffer.append("亮度对应的霍夫曼码AC表号为：" + humTableAC + "\r\n");
				buffer.append("亮度对应的霍夫曼码AC表号为：" + humTableDC + "\r\n");
				break;
			case 2:
				buffer.append("红色分量对应的霍夫曼码AC表号为：" + humTableAC + "\r\n");
				buffer.append("红色分量对应的霍夫曼码AC表号为：" + humTableDC + "\r\n");
				break;

			case 3:
				buffer.append("蓝色分量对应的霍夫曼码AC表号为：" + humTableAC + "\r\n");
				buffer.append("蓝色分量对应的霍夫曼码AC表号为：" + humTableDC + "\r\n");
				break;
			}
		}
		System.out.println(buffer.toString());
		OutputInfoToFile(buffer);
	}

	/**
	 * 解析DQT段的信息
	 */
	public void parseDQT() {
		// TODO Auto-generated method stub
		// StringBuffer buffer=new StringBuffer("两个量化表的信息如下：\r\n");
		for (int i = 0; i < 2; i++) {
			QT.printQT(QT.Q[i]);
		}
		int[] precision = QT.Pq;
		String precStr = "";
		int num = 0;
		for (int i = 0; i < precision.length; i++) {
			if (precision[i] != 0) {
				precStr = precision[i] + "bit";
				num++;
			}
		}
		params.put("precision", precStr);
		params.put("dqtNum", num);
		// 后面执行将数据读写进文件的操作
		// System.out.println(buffer.toString());

	}

	/**
	 * 解析SOF0段的数据
	 */
	public void parseSOF0() {
		StringBuffer buffer = new StringBuffer();
		FrameHeader sof0 = FH;
		buffer.append("\r\n以下是SOF0段的数据信息：\r\n");
		int samplePrecision = sof0.P & 0xff;
		buffer.append("该照片的样本精度为：" + samplePrecision + "bit\r\n");
		// 以下对高度和宽度的高字节进行判断，如果高字节为0则不用将高字节左移8位，否则需要左移然后再做&运算
		int height = sof0.Y;
		int width = sof0.X;
		params.put("width", width);
		params.put("height", height);
		buffer.append("图片的高度为：" + height + "," + "图片的宽度为：" + width + "\r\n");
		ComponentSpec[] list = sof0.Comp;
		// 待处理的代码

		String str1 = "";
		String str2 = "";
		String str3 = "";
		for (int i = 0; i < list.length; i++) {
			// 组件的数量为3时，表示为：YCbCr/YIQ彩色图
			// Y表示量度，Cr表示红色分量，Cb表示蓝色分量
			// 组件的id有三个
			// 1表示Y,2表示Cb，3表示Cr，4表示I,5表示Q
			ComponentSpec com = list[i];
			// 水平采样系数,也可以表示为逐点采样或者每两个点采样一次等，
			int Xfactor = (com.H) & 0xf;
			// 垂直采样系数
			int Yfactor = com.V & 0xf;
			// 量化的表号
			int tableNo = com.Tq & 0xff;
			switch (com.C) {
			case 1:
				buffer.append("亮度的水平采样系数为：" + Xfactor + "\r\n");
				buffer.append("亮度的垂直采样系数为：" + Yfactor + "\r\n");
				buffer.append("亮度使用的是QT=" + tableNo + "的量化表" + "\r\n");
				str1 = Xfactor * Yfactor + ":";
				break;
			case 2:
				buffer.append("红色分量的水平采样系数为：" + Xfactor + "\r\n");
				buffer.append("红色分量的垂直采样系数为：" + Yfactor + "\r\n");
				buffer.append("红色分量使用的是QT=" + tableNo + "的量化表" + "\r\n");
				str2 = Xfactor * Yfactor + ":";
				break;
			case 3:
				buffer.append("蓝色分量的水平采样系数为：" + Xfactor + "\r\n");
				buffer.append("蓝色分量的垂直采样系数为：" + Yfactor + "\r\n");
				buffer.append("蓝色分量使用的是QT=" + tableNo + "的量化表" + "\r\n");
				str3 = Xfactor * Yfactor + "";
				break;
			}
		}
		params.put("samplePrecision", str1 + str2 + str3);
		System.out.println(buffer.toString());
		OutputInfoToFile(buffer);

	}

	/**
	 * 解析app0的数据
	 */
	public static void parseAPP0() {
		params = new HashMap<String, Object>();
		StringBuffer buffer = new StringBuffer("以下是APP0段的信息：\r\n");
		APP0 app0 = JPEGDecoder.app0;
		/*
		 * System.out.println("-----------" +
		 * Tool.Bytes2HexString(app0.changeStyle));
		 */if (Tool.Bytes2HexString(app0.changeStyle).equals("4A46494600"))
			buffer.append("该图片的交换格式为：" + "JFIF 的ASCII码\r\n");
		else {
			buffer.append("该图片的交换格式为：" + "无法辨别\r\n");
		}
		String version = app0.hostVersion + "." + app0.SecondVersion;
		// 将该jpeg文件的版本号放入到map集合中
		params.put("version", version);
		buffer.append("主次版本号：" + version + "\r\n");
		String unit = "";
		switch (app0.densityUnit) {
		case 0:
			unit += "无单位\r\n";
			break;
		case 1:
			unit += "点数/英寸\r\n";
			break;
		case 2:
			unit += "点数/厘米\r\n";
			break;
		}
		buffer.append("密度单位为：" + unit);
		int Xdensity = ((app0.XpxDensity[0] << 8) + app0.XpxDensity[1]) & 0xffff;
		int Ydensity = ((app0.YpxDensity[0] << 8) + app0.YpxDensity[1]) & 0xffff;
		System.out.println(Tool.Bytes2HexString(app0.XpxDensity));
		System.out.println(Tool.Bytes2HexString(app0.YpxDensity));
		buffer.append("水平方向的密度为：" + Xdensity + unit);
		buffer.append("垂直方向的密度为：" + Ydensity + unit);
		if (app0.XZoom == 0)
			buffer.append("水平方向没有缩略图\r\n");
		if (app0.YZoom == 0)
			buffer.append("垂直方向没有缩略图\r\n");
		OutputInfoToFile(buffer);
		System.out.println(buffer.toString());
	}

	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * 将解析出来的信息保存到文件中，默认是追加保存
	 * 
	 * @param buffer
	 */
	private static void OutputInfoToFile(StringBuffer buffer) {
		FileOutputStream os = null;
		try {
			// 构建输出流,如果要进行给原始的文件进行追加数据时。可以指定FileOUtputstream的第二个参数为true
			os = new FileOutputStream(new File(LoadOriginalImage.JPGFILEINFO),
					true);
			// os = new FileOutputStream(new File(JpgFile.JPGFILEINFO));
			os.write(buffer.toString().getBytes());
			os.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// public

	/**
	 * 定义app0段
	 * 
	 * @author Administrator
	 * 
	 */
	public static final class APP0 {
		// 交换格式 4A46494600 JFIF的ASICLL码
		public byte[] changeStyle = new byte[5];
		// 主版本号
		public byte hostVersion;
		// 次版本号
		public byte SecondVersion;
		// 密度单位
		public byte densityUnit;
		// X像素密度
		public byte[] XpxDensity = new byte[2];
		// Y像素密度
		public byte[] YpxDensity = new byte[2];
		// 缩略图X像素
		public byte XZoom;
		// 缩略图Y像素
		public byte YZoom;

	}

	public static APP0 app0 = new APP0();

	/**
	 * 获取每一段的数据长度
	 * 
	 * @param iByteHigh
	 * @param iByteLow
	 * @return
	 */
	public static int GetSectionLen(int iByteHigh, int iByteLow) {
		/*
		 * System.out .println("GetSectionLen(" + iByteHigh + ", " + iByteLow +
		 * ")");
		 */
		int iBlockLen = ((iByteHigh << 8) + iByteLow) & 0xffff;
		// System.out.println("Section length: " + iBlockLen + " bytes");
		return iBlockLen;
	}

	/**
	 * 读入APP0的数据
	 * 
	 * @param bais
	 * @throws IOException
	 */
	public static void ReadAPP0(InputStream bais) throws IOException {
		int blockLen = GetSectionLen(bais.read(), bais.read());
		APP0 app0 = new APP0();
		// 读入app0的数据
		bais.read(app0.changeStyle);
		app0.hostVersion = (byte) bais.read();
		app0.SecondVersion = (byte) bais.read();
		app0.densityUnit = (byte) bais.read();
		bais.read(app0.XpxDensity);
		bais.read(app0.YpxDensity);
		app0.XZoom = (byte) bais.read();
		app0.YZoom = (byte) bais.read();
		JPEGDecoder.app0 = app0;

	}

}
