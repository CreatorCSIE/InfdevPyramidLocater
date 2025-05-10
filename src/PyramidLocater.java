import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PyramidLocator extends JFrame {

    private JTextField inputXField;
    private JTextField inputZField;
    private JTextArea resultArea;
    private int currentX = -1;
    private int currentZ = -1;
    
    private static final String HEADER = "Minecraft Infdev砖块金字塔坐标已保存列表 坐标格式为 (X,Y,Z) ";
    private static final String FILENAME = "PyramidPosition.txt";

    public PyramidLocator() {
        setTitle("Minecraft Infdev砖块金字塔中心坐标查找器");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示
        
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JLabel xLabel = new JLabel("请输入 X 坐标:");
        inputXField = new JTextField(10);

        JLabel zLabel = new JLabel("请输入 Z 坐标:");
        inputZField = new JTextField(10);

        JButton computeButton = new JButton("计算最近金字塔");
        JButton saveButton = new JButton("保存坐标");

        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        computeButton.addActionListener(e -> computeNearestPyramid());
        saveButton.addActionListener(e -> saveCoordinates(e));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 5, 5));
        inputPanel.add(xLabel);
        inputPanel.add(inputXField);
        inputPanel.add(zLabel);
        inputPanel.add(inputZField);
        inputPanel.add(new JLabel()); // 占位
        inputPanel.add(computeButton);
        
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 8, 0); // 底部留出信息显示空间
        bottomPanel.add(saveButton, gbc);
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel authorLabel = new JLabel("Made by CreatorCSIE");
        authorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        authorLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        infoPanel.add(authorLabel);
        JLabel versionLabel = new JLabel("版本：1.4");
        versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        versionLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        infoPanel.add(versionLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(0, 0, 0, 10); // 右侧留10像素边距
        bottomPanel.add(infoPanel, gbc);

        getContentPane().setLayout(new BorderLayout(5, 5));
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void computeNearestPyramid() {
        String inputX = inputXField.getText().trim();
        String inputZ = inputZField.getText().trim();

        if (!isValidInput(inputX) || !isValidInput(inputZ)) {
            resultArea.setText("输入无效！请输入合法的整数！");
            return;
        }

        int inputXVal = Integer.parseInt(inputX);
        int inputZVal = Integer.parseInt(inputZ);

        int nearestX = 0;
        int nearestZ = 0;
        double minDistance = Double.MAX_VALUE;

        int baseX = inputXVal / 1024;
        int baseZ = inputZVal / 1024;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int i7 = baseX + dx;
                int i8 = baseZ + dz;

                Random rand = new Random((long) (i7 + i8 * 13871));
                int centerX = (i7 << 10) + 128 + rand.nextInt(512);
                int centerZ = (i8 << 10) + 128 + rand.nextInt(512);

                double distance = Math.hypot(centerX - inputXVal, centerZ - inputZVal);
                if (distance < minDistance) {
                    if (centerX >= 0 && centerZ >= 0 && centerX <= 33554432 && centerZ <= 33554432) {
                        minDistance = distance;
                        nearestX = centerX;
                        nearestZ = centerZ;
                    }
                }
            }
        }
        this.currentX = nearestX;
        this.currentZ = nearestZ;
        resultArea.setText(String.format("最近的砖块金字塔中心坐标是: (%d, %d)", nearestX, nearestZ));
    }

    private boolean isValidInput(String input) {
        try {
            int num = Integer.parseInt(input);
            return num >= 0 && num <= 33554432;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private void saveCoordinates(ActionEvent e) {
        if (currentX == -1 || currentZ == -1) {
            JOptionPane.showMessageDialog(this, "请先进行计算再保存！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(FILENAME);
        boolean needHeader = !file.exists();

        // 如果文件已存在，检查首行内容
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String firstLine = br.readLine();
                needHeader = !HEADER.equals(firstLine);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "文件读取失败：" + ex.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try (PrintWriter out = new PrintWriter(new FileWriter(file, !needHeader))) {
            // 需要写入文件头时
            if (needHeader) {
                out.println(HEADER);
                out.println();
            }
            // 写入坐标信息
            out.printf("(%d,128,%d)%n", currentX, currentZ);
            out.printf("记录于%s%n%n", new SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(new Date()));
            JOptionPane.showMessageDialog(this, "坐标已保存至程序同目录下的PyramidPosition.txt", "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "文件保存失败：" + ex.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PyramidLocator().setVisible(true);
        });
    }
}
