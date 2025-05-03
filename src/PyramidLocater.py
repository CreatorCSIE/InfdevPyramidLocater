class JavaRandom:
    def __init__(self, seed):
        self.seed = (seed ^ 0x5DEECE66D) & ((1 << 48) - 1)

    def next(self, bits):
        self.seed = (self.seed * 0x5DEECE66D + 0xB) & ((1 << 48) - 1)
        return self.seed >> (48 - bits)

    def nextInt(self, bound):
        if (bound & (bound - 1)) == 0:
            return (bound * self.next(31)) >> 31
        while True:
            bits = self.next(31)
            val = bits % bound
            if bits - val + (bound - 1) >= 0:
                return val

def is_valid_input(input_str):
    try:
        num = int(input_str)
        # 检查是否在合法范围内
        if num > 2147483647 or num < -2147483648:
            return False
        return True
    except ValueError:
        return False

def find_nearest_pyramid(x, z):
    nearestX = nearestZ = 0
    min_distance = float('inf')
    baseX = x // 1024
    baseZ = z // 1024

    for dx in range(-1, 2):
        for dz in range(-1, 2):
            i7 = baseX + dx
            i8 = baseZ + dz
            seed = i7 + i8 * 13871
            rand = JavaRandom(seed)
            centerX = (i7 << 10) + 128 + rand.nextInt(512)
            centerZ = (i8 << 10) + 128 + rand.nextInt(512)

            if centerX < 0 or centerZ < 0 or centerX > 33554432 or centerZ > 33554432:
                continue

            dist = ((centerX - x) ** 2 + (centerZ - z) ** 2) ** 0.5
            if dist < min_distance:
                min_distance = dist
                nearestX, nearestZ = centerX, centerZ

    if min_distance < float('inf'):
        print(f"最近的砖块金字塔中心坐标是: ({nearestX}, {nearestZ})")
    else:
        print("未找到符合条件的金字塔中心。")

# 获取用户输入并验证
x = input("请输入 X 坐标: ")
if not is_valid_input(x):
    print("输入无效！请输入一个合法的整数！")
else:
    z = input("请输入 Z 坐标: ")
    if not is_valid_input(z):
        print("输入无效！请输入一个合法的整数！")
    else:
        find_nearest_pyramid(int(x), int(z))
