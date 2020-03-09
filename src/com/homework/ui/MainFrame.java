package com.homework.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.homework.dao.JPEGDao;
import com.homework.domain.DecodeJPGInfo;
import com.homework.domain.OriImageInfo;
import com.homework.recovery.LoadOriginalImage;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final String FILETYPE = "JPEGͼ��";
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel oriImage;
	private JLabel exeractImage;
	private JTable table;
	private JButton selectBtn;
	private JButton exeractBtn;
	private JFileChooser chooser;
	private File selectFile;
	private String fileName;
	private String fileAbsolutePath = null;
	private String filePath;
	private String fileSizeStr;
	private ImageIcon oriImageIcon;
	private ImageIcon recImageIcon;
	private int imageWidth;
	private int imageHeight;
	private String fileDecimion = "";
	private LoadOriginalImage loadOriginalImage;
	private Image image;
	private JLabel width_value;
	private JLabel height_value;
	private JLabel pricision_value;
	private JLabel version_value;
	private JLabel sampleFactor_value;
	private JLabel scanComponentNum_value;
	private JLabel DQTNum_value;
	private JLabel DHTNum_value;
	private OriImageInfo oriImageInfo;
	private DecodeJPGInfo decodeJPGInfo;
	private JPEGDao jpegDao;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setTitle("ͨ��jpegͼƬ�ָ��ع�ԭͼƬ");
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 534, 664);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel author = new JPanel();
		contentPane.add(author, BorderLayout.SOUTH);
		author.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNewLabel = new JLabel(
				"\u7248\u6743\u5F52\u6E56\u5357\u5927\u5B66\u7535\u6C14\u4E0E\u4FE1\u606F\u5DE5\u7A0B\u5B66\u9662\u6587\u5C0F\u98DE\u6240\u6709");
		author.add(lblNewLabel);

		JPanel panel_title = new JPanel();
		contentPane.add(panel_title, BorderLayout.NORTH);

		JLabel label_2 = new JLabel("          \u539F\u59CB\u56FE\u50CF");
		panel_title.add(label_2);

		JLabel label_4 = new JLabel(
				"                                                                    ");
		panel_title.add(label_4);

		JLabel label_3 = new JLabel(
				"      \u91CD\u6784\u540E\u7684\u56FE\u50CF");
		panel_title.add(label_3);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.2);
		splitPane.setPreferredSize(new Dimension(121, 30));
		splitPane.setDividerSize(2);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		panel.add(splitPane, BorderLayout.CENTER);

		JPanel panel_image = new JPanel();
		splitPane.setLeftComponent(panel_image);

		// ����ǩ����ͼƬ
		// MainFrame.class.getResource("/image/headImage.jpg")ͨ���ô�����ȡ����·���µ��ļ�
		oriImageIcon = new ImageIcon(
				MainFrame.class
						.getResource("/com/homework/image/headImage.jpg"));
		// �����ع����ͼƬ��"C:\\Users\\Administrator\\Desktop\\200px-50.jpg"
		recImageIcon = new ImageIcon(
				MainFrame.class
						.getResource("/com/homework/image/headImage.jpg"));
		panel_image.setLayout(new BorderLayout(0, 0));

		JPanel panel_2 = new JPanel();
		panel_image.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// ��ʾjpeg�ļ�
		oriImage = new JLabel(oriImageIcon);
		oriImage.setPreferredSize(new Dimension(150, 200));
		panel_2.add(oriImage);
		oriImage.setFont(new Font("SimSun", Font.PLAIN, 12));
		oriImage.setBounds(new Rectangle(0, 0, 100, 133));

		JLabel label_5 = new JLabel("                                        ");
		panel_2.add(label_5);
		// ��ʾ�ع����ͼƬ
		exeractImage = new JLabel(recImageIcon);
		panel_2.add(exeractImage);
		exeractImage.setFont(new Font("����", Font.PLAIN, 12));
		exeractImage.setPreferredSize(new Dimension(150, 200));

		JPanel panel_6 = new JPanel();
		panel_image.add(panel_6, BorderLayout.SOUTH);
		panel_6.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// ѡ���ļ��İ�ť
		selectBtn = new JButton("\u9009\u62E9\u56FE\u7247");
		selectBtn.addActionListener(new ButtonClickListener());
		panel_6.add(selectBtn);

		JLabel label_1 = new JLabel(
				"                                                                 ");
		panel_6.add(label_1);

		exeractBtn = new JButton("\u91CD\u6784\u539F\u56FE");
		exeractBtn.addActionListener(new ButtonClickListener());
		panel_6.add(exeractBtn);

		JPanel panel_param = new JPanel();
		splitPane.setRightComponent(panel_param);
		panel_param.setLayout(new GridLayout(1, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		panel_param.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel label = new JLabel(
				"\u9009\u62E9\u7684\u56FE\u7247\u7684\u57FA\u672C\u4FE1\u606F");
		label.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		label.setFont(new Font("Dialog", Font.PLAIN, 15));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		panel_1.add(label, BorderLayout.NORTH);

		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BorderLayout(0, 0));

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(UIManager.getBorder("TextField.border"));
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_3.add(panel_5, BorderLayout.NORTH);

		// �������
		table = new JTable();
		table.setFont(new Font("����", Font.PLAIN, 13));
		// ���½������ñ�������
		table.setSelectionForeground(SystemColor.inactiveCaptionBorder);
		table.setSelectionBackground(SystemColor.activeCaption);
		table.setShowHorizontalLines(false);
		table.setShowGrid(false);
		table.setShowVerticalLines(false);
		table.setBorder(null);
		table.setBackground(UIManager.getColor("Button.background"));
		table.setRowHeight(25);
		table.setRowMargin(10);
		// һ���������ñ���е����ݾ�����ʾ
		table.setPreferredScrollableViewportSize(new Dimension(500, 400));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setModel(new DefaultTableModel(new Object[][] {
				{ "    \u6587\u4EF6\u540D\u79F0\uFF1A", "����ͼƬ��Ϣ" },
				{ "    \u6587\u4EF6\u7C7B\u578B\uFF1A", "����ͼƬ��Ϣ" },
				{ "    \u6587\u4EF6\u5C3A\u5BF8\uFF1A", "����ͼƬ��Ϣ" },
				{ "    \u4F4D\u7F6E\uFF1A", "����ͼƬ��Ϣ" },
				{ "    \u5927\u5C0F\uFF1A", "����ͼƬ��Ϣ" }, }, new String[] {
				"param", "value" }) {
			/**
					 * 
					 */
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(201);
		table.getColumnModel().getColumn(1).setPreferredWidth(285);
		panel_5.add(table);

		JPanel panel_4 = new JPanel();
		panel_4.setPreferredSize(new Dimension(25, 25));
		panel_4.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_3.add(panel_4, BorderLayout.CENTER);
		panel_4.setLayout(new GridLayout(4, 4, 5, 15));

		JLabel width = new JLabel("\u56FE\u7247\u5BBD\u5EA6\uFF1A");
		width.setHorizontalAlignment(SwingConstants.CENTER);
		width.setFont(new Font("����", Font.BOLD, 12));
		panel_4.add(width);

		width_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		width_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(width_value);

		JLabel height = new JLabel("\u56FE\u7247\u9AD8\u5EA6\uFF1A");
		height.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(height);

		height_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		height_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(height_value);

		JLabel samplePricisioin = new JLabel("\u6837\u672C\u7CBE\u5EA6\uFF1A");
		samplePricisioin.setPreferredSize(new Dimension(60, 20));
		samplePricisioin.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(samplePricisioin);

		pricision_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		pricision_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(pricision_value);

		JLabel version = new JLabel("\u7248\u672C\u53F7\uFF1A");
		version.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(version);

		version_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		version_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(version_value);

		JLabel sampleFactor = new JLabel("\u91C7\u6837\u56E0\u5B50\uFF1A");
		sampleFactor.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(sampleFactor);

		sampleFactor_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		sampleFactor_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(sampleFactor_value);

		JLabel scanComponetNum = new JLabel(
				"\u626B\u63CF\u7EC4\u4EF6\u6570\uFF1A");
		scanComponetNum.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(scanComponetNum);

		scanComponentNum_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		scanComponentNum_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(scanComponentNum_value);

		JLabel DQTNum = new JLabel("\u91CF\u5316\u8868\u4E2A\u6570\uFF1A");
		DQTNum.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(DQTNum);

		DQTNum_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		DQTNum_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(DQTNum_value);

		JLabel DHTNum = new JLabel("\u54C8\u5F17\u66FC\u8868\u4E2A\u6570\uFF1A");
		DHTNum.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(DHTNum);

		DHTNum_value = new JLabel("\u6682\u65E0\u4FE1\u606F");
		DHTNum_value.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(DHTNum_value);
		splitPane.setDividerLocation(0.2);
		splitPane.setDividerLocation(230);
		// splitPane.setDividerLocation(300.0);
	}

	/**
	 * ���尴ť�ļ�����
	 * 
	 * @author Administrator
	 * 
	 */
	class ButtonClickListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			// ����dao��Ķ���
			jpegDao = new JPEGDao();
			if (e.getActionCommand().equals("ѡ��ͼƬ")) {
				// �����ѡ���ļ���ťʱ������һ���ļ�ѡ����
				chooser = new JFileChooser("E:");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
				chooser.showDialog(new JLabel(), "ѡ��");
				selectFile = chooser.getSelectedFile();
				if (selectFile != null) {

					// ���»�ȡѡ����ļ������ƣ�·��
					fileName = selectFile.getName();
					if (fileName.substring(fileName.lastIndexOf(".")).equals(
							".jpg")) {
						fileAbsolutePath = selectFile.getPath();
						filePath = fileAbsolutePath.substring(0,
								fileAbsolutePath.lastIndexOf("\\"));
						String unit = "KB";
						double fileSize = (float) selectFile.length()
								/ (float) 1024;
						// ���й����ļ��Ĵ�С��������KBΪ��λ�ģ�����һλ��Ч���֡���MBΪ��λ�ģ�������λ��Ч����
						fileSizeStr = new DecimalFormat("###.0")
								.format(fileSize) + unit;
						if (fileSize > 1024.0) {
							fileSize = fileSize / 1024.0;
							unit = "MB";
							fileSizeStr = new DecimalFormat("###.00")
									.format(fileSize) + unit;
						}
						System.out.println("�ļ����ƣ�" + fileName);
						System.out.println("�ļ�ȫ·����" + fileAbsolutePath);
						System.out.println("�ļ�·����" + filePath);
						System.out.println("�ļ���С��" + fileSizeStr);
						// ����һ���µ��߳���ͬ�����½�����ʾ��ͼƬ
						new Thread() {
							public void run() {
								oriImageIcon = new ImageIcon(fileAbsolutePath);
								// ȡ��ͼƬ�ĵĸ߶ȺͿ��
								imageHeight = oriImageIcon.getIconHeight();
								imageWidth = oriImageIcon.getIconWidth();
								fileDecimion = imageWidth + "*" + imageHeight;
								// ���´�����Ҫ��ʵ������ͼƬ���ﵽͼƬ�ͱ�ǩ����Ӧ��Ч��
								oriImageIcon.setImage(oriImageIcon.getImage()
										.getScaledInstance(150, 200,
												Image.SCALE_DEFAULT));
								oriImage.setIcon(oriImageIcon);
								// ͬ����ʾtable�����ͼƬ������
								table.setModel(new DefaultTableModel(
										new Object[][] {
												{
														"    \u6587\u4EF6\u540D\u79F0\uFF1A",
														fileName },
												{
														"    \u6587\u4EF6\u7C7B\u578B\uFF1A",
														"JPEG\u56FE\u50CF" },
												{
														"    \u6587\u4EF6\u5C3A\u5BF8\uFF1A",
														fileDecimion },
												{ "    \u4F4D\u7F6E\uFF1A",
														filePath },
												{ "    \u5927\u5C0F\uFF1A",
														fileSizeStr }, },
										new String[] { "param", "value" }) {
									private static final long serialVersionUID = 1L;
									boolean[] columnEditables = new boolean[] {
											false, false };

									public boolean isCellEditable(int row,
											int column) {
										return columnEditables[column];
									}
								});
								// ���ñ��Ĳ�ͬ�еĿ�ȡ�
								table.getColumnModel().getColumn(0)
										.setPreferredWidth(201);
								table.getColumnModel().getColumn(1)
										.setPreferredWidth(285);
								// ���½��б��浽���ݿ�Ĳ���:
								// �����ݿ������һ������
								oriImageInfo = new OriImageInfo(fileName,
										FILETYPE, fileDecimion, filePath,
										fileSizeStr);
								jpegDao.saveOriImageInfo(oriImageInfo);
							};

						}.start();

					} else {
						JOptionPane.showMessageDialog(MainFrame.this,
								"��ѡ����ļ���ʽ������jpeg�ļ���׼��������ѡ��", "��Ϣ���ѣ�����",
								JOptionPane.OK_OPTION);

					}

				}
			}
			if (e.getActionCommand().equals("�ع�ԭͼ")) {
				// �������߳���ִ���ع�ԭͼ�Ĳ���
				// ��ʼ����������
				if (fileAbsolutePath != null) {
					new Thread() {
						public void run() {
							loadOriginalImage = new LoadOriginalImage();
							MemoryImageSource mi = loadOriginalImage
									.loadImage(fileAbsolutePath);
							loadOriginalImage.initParams();
							image = createImage(mi);
							// ���¹���ͼ���ͼƬ��
							recImageIcon = new ImageIcon(image);
							// ������ʾ�ı���
							recImageIcon.setImage(recImageIcon.getImage()
									.getScaledInstance(150, 200,
											Image.SCALE_DEFAULT));
							// Ȼ�����ǩ����ͼƬ
							exeractImage.setIcon(recImageIcon);
							// ��̬�ĸ���jpeg�ļ�������
							version_value
									.setText(LoadOriginalImage.VERSION_VALUE);
							width_value.setText(LoadOriginalImage.WIDTH_VALUE
									+ "");
							height_value.setText(LoadOriginalImage.HEIGHT_VALUE
									+ "");
							sampleFactor_value
									.setText(LoadOriginalImage.SAMPLE_PRECISION_VALUE);
							DHTNum_value
									.setText(LoadOriginalImage.DHT_NUM_VALUE
											+ "");
							DQTNum_value
									.setText(LoadOriginalImage.DQT_NUM_VALUE
											+ "");
							scanComponentNum_value
									.setText(LoadOriginalImage.SACN_COMPONENT_NUM_VALUE
											+ "");
							pricision_value
									.setText(LoadOriginalImage.PRECISION_VALUE);

							// �����ϵ����ݱ��浽���ݿ���
							decodeJPGInfo = new DecodeJPGInfo(
									LoadOriginalImage.WIDTH_VALUE,
									LoadOriginalImage.HEIGHT_VALUE,
									LoadOriginalImage.PRECISION_VALUE,
									LoadOriginalImage.VERSION_VALUE,
									LoadOriginalImage.SAMPLE_PRECISION_VALUE,
									LoadOriginalImage.SACN_COMPONENT_NUM_VALUE,
									LoadOriginalImage.DHT_NUM_VALUE,
									LoadOriginalImage.DQT_NUM_VALUE);
							// ����Ϣ���浽���ݿ�
							jpegDao.saveDecodeImageInfo(decodeJPGInfo);
						};

					}.start();
				} else {
					JOptionPane.showMessageDialog(MainFrame.this, "����û��ѡ��ͼƬ",
							"��Ϣ����", JOptionPane.OK_OPTION);
				}
			}
		}
	}
}
