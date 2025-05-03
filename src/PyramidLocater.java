import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class PyramidLocator extends JFrame {

    private JTextField inputXField;
    private JTextField inputZField;
    private JTextArea resultArea;

    public PyramidLocator() {
        setTitle("Minecraft Infdev砖块金字塔中心坐标查找器");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示

        initComponents();
    }

    private void initComponents() {
        JLabel xLabel = new JLabel("请输入 X 坐标:");
        inputXField = new JTextField(10);

        JLabel zLabel = new JLabel("请输入 Z 坐标:");
        inputZField = new JTextField(10);

        JButton computeButton = new JButton("计算最近金字塔");

        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        computeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                computeNearestPyramid();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 5, 5));
        inputPanel.add(xLabel);
        inputPanel.add(inputXField);
        inputPanel.add(zLabel);
        inputPanel.add(inputZField);
        inputPanel.add(new JLabel()); // 占位
        inputPanel.add(computeButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PyramidLocator().setVisible(true);
        });
    }
}
