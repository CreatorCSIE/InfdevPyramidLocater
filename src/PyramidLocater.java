import java.util.Random;
import java.util.Scanner;

public class PyramidLocator {

	public static boolean isValidInput(String input) {
        try {
            int num = Integer.parseInt(input);
            // 检查是否在合法范围内
            return num >= 0 && num <= 33554432;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入 X 坐标: ");
        String inputX = scanner.nextLine();
        if (!isValidInput(inputX)) {
            System.out.println("输入无效！请输入一个合法的整数！");
            scanner.close();
            return; // 停止程序
        }
        int inputXVal = Integer.parseInt(inputX);

        // 获取 Z 坐标并检查输入
        System.out.print("请输入 Z 坐标: ");
        String inputZ = scanner.nextLine();
        if (!isValidInput(inputZ)) {
            System.out.println("输入无效！请输入一个合法的整数！");
            scanner.close();
            return; // 停止程序
        }
        int inputZVal = Integer.parseInt(inputZ);

        int nearestX = 0;
        int nearestZ = 0;
        double minDistance = Double.MAX_VALUE;

        int baseX = inputXVal / 1024;
        int baseZ = inputZVal / 1024;

        // 检查周围 9 个区块
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

        System.out.printf("最近的砖块金字塔中心坐标是: (%d, %d)%n", nearestX, nearestZ);
        scanner.close();
    }
}
