#include <iostream>
#include <cmath>
#include <limits>
#include <cstring>
#include <sstream>
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

int main() {
    using namespace std;
    using namespace java_random;

    string inputX_str, inputZ_str;

    // 获取用户输入
    cout << "请输入 X 坐标: ";
    cin >> inputX_str;
    
    cout << "请输入 Z 坐标: ";
    cin >> inputZ_str;
    if (!isValidInput(inputX_str) || !isValidInput(inputZ_str)) {
        cout << "输入无效！请输入一个合法的整数！\n";
        return 0;
    }

    // 使用 stringstream 转换字符串到整数
    std::stringstream ssX(inputX_str), ssZ(inputZ_str);
    int inputX, inputZ;
    ssX >> inputX;
    ssZ >> inputZ;

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

    if (minDistance < numeric_limits<double>::max()) {
        cout << "最近的砖块金字塔中心坐标是: (" << nearestX << ", " << nearestZ << ")\n";
    } else {
        cout << "未找到符合条件的金字塔中心。\n";
    }

	system("pause");
	
    return 0;
}
