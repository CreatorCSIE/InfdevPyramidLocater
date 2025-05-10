#include <iostream>
#include <cmath>
#include <limits>
#include <cstring>
#include <fstream>
#include <sstream>
#include <ctime>
#include <cstdlib>

namespace java_random {
    const long long multiplier = 0x5DEECE66DLL;
    const long long addend = 0xB;
    const long long mask = (1LL << 48) - 1;

    class Random {
    private:
        long long seed;

    public:
        Random(long long seed) {
            this->seed = (seed ^ multiplier) & mask;
        }

        int next(int bits) {
            seed = (seed * multiplier + addend) & mask;
            return (int)(seed >> (48 - bits));
        }

        int nextInt(int bound) {
            if ((bound & -bound) == bound) { // power of two
                return (int)((bound * (long long)next(31)) >> 31);
            }

            int bits, val;
            do {
                bits = next(31);
                val = bits % bound;
            } while (bits - val + (bound - 1) < 0);
            return val;
        }
    };
}

bool isValidInput(const std::string& input) {
    std::stringstream ss(input);
    int num;
    ss >> num;
    return !ss.fail() && ss.eof() && num <= 33554432 && num >= 0;
}

void saveCoordinates(int x, int z) {
    std::ofstream file;
    std::ifstream checkFile("PyramidPosition.txt");
    bool needHeader = false;

    // 检查文件头
    if (!checkFile) {
        needHeader = true;
    } else {
        std::string firstLine;
        std::getline(checkFile, firstLine);
        if (firstLine != "Minecraft Infdev砖块金字塔坐标已保存列表 坐标格式为 (X,Y,Z)") {
            needHeader = true;
        }
        checkFile.close();
    }

    // 打开文件
    file.open("PyramidPosition.txt", needHeader ? std::ios::out : std::ios::app);

    if (!file.is_open()) {
        std::cout << "无法打开文件进行保存！" << std::endl;
        return;
    }

    // 写入文件头（如果需要）
    if (needHeader) {
        file << "Minecraft Infdev砖块金字塔坐标已保存列表 坐标格式为 (X,Y,Z)\n\n";
    }

	// 获取当前时间（兼容旧版本C++）
    time_t now = time(NULL); // 使用NULL代替nullptr
    tm *ltm = localtime(&now); // 传统时间函数
    
    // 时间有效性检查
    if (ltm == NULL) {
        std::cout << "时间获取失败！" << std::endl;
        file.close();
        return;
    }

    // 时间格式处理
    char timeStr[32];
    strftime(timeStr, sizeof(timeStr), "%Y年%m月%d日 %H:%M", ltm);

    // 写入坐标信息
    file << "(" << x << ",128," << z << ")\n";
    file << "记录于" << timeStr << "\n\n";

    file.close();
    std::cout << "\n坐标已成功保存至PyramidPosition.txt\n";
}

int main() {
    using namespace std;
    using namespace java_random;
    
    string version="v1.4";
    
    system("title 砖块金字塔中心坐标查找器");
    
    cout<<"Minecraft Infdev砖块金字塔中心坐标查找器 "<<version<<endl;
    cout<<"Made by CreatorCSIE"<<endl<<endl;
	
    char continueChoice;
    do {
        string inputX_str, inputZ_str;
        
        // 输入坐标
        cout << "请输入 X 坐标: ";
        cin >> inputX_str;
        cout << "请输入 Z 坐标: ";
        cin >> inputZ_str;

        // 验证输入
        if (!isValidInput(inputX_str) || !isValidInput(inputZ_str)) {
            cout << "输入无效！请输入0~33554432之间的整数\n";
            cin.clear();
            cin.ignore(numeric_limits<streamsize>::max(), '\n');
            continue;
        }

        // 转换输入
        stringstream ssX(inputX_str), ssZ(inputZ_str);
        int inputX, inputZ;
        ssX >> inputX;
        ssZ >> inputZ;

        // 计算坐标
        int nearestX = 0, nearestZ = 0;
        double minDistance = numeric_limits<double>::max();
        int baseX = inputX / 1024;
        int baseZ = inputZ / 1024;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int i7 = baseX + dx;
                int i8 = baseZ + dz;

                long long seed = (long long)(i7 + i8 * 13871);
                Random rand(seed);
                int centerX = (i7 << 10) + 128 + rand.nextInt(512);
                int centerZ = (i8 << 10) + 128 + rand.nextInt(512);

                if (centerX < 0 || centerZ < 0 || centerX > 33554432 || centerZ > 33554432)
                    continue;

                double distance = hypot(centerX - inputX, centerZ - inputZ);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestX = centerX;
                    nearestZ = centerZ;
                }
            }
        }

        // 显示结果
        if (minDistance < numeric_limits<double>::max()) {
            cout << "\n最近的砖块金字塔中心坐标是: (" << nearestX << ", " << nearestZ << ")\n";
            
            // 保存提示
            char saveChoice;
            cout << "是否保存坐标？(Y/N): ";
            cin >> saveChoice;
            if (toupper(saveChoice) == 'Y') {
                saveCoordinates(nearestX, nearestZ);
            }
        } else {
            cout << "\n未找到符合条件的金字塔中心\n";
        }

        // 继续提示
        cout << "是否继续计算？(Y/N): ";
        cin >> continueChoice;
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << endl;

    } while (toupper(continueChoice) == 'Y');

	system("pause");
	
    return 0;
}
