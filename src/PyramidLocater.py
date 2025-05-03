import tkinter as tk
from tkinter import messagebox

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
        return -2147483648 <= num <= 2147483647
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
        return f"最近的砖块金字塔中心坐标是: ({nearestX}, {nearestZ})"
    else:
        return "未找到符合条件的金字塔中心。"

def on_calculate():
    x = entry_x.get().strip()
    z = entry_z.get().strip()

    if not is_valid_input(x) or not is_valid_input(z):
        messagebox.showerror("错误", "请输入合法的整数坐标！")
        return

    result = find_nearest_pyramid(int(x), int(z))
    result_area.config(state=tk.NORMAL)
    result_area.delete(1.0, tk.END)
    result_area.insert(tk.END, result + "\n")
    result_area.config(state=tk.DISABLED)

# GUI 构建
root = tk.Tk()
root.title("Minecraft Infdev砖块金字塔中心坐标查找器")
root.geometry("500x300")

# 输入框
frame_input = tk.Frame(root)
tk.Label(frame_input, text="X 坐标:").grid(row=0, column=0, padx=5, pady=5)
entry_x = tk.Entry(frame_input)
entry_x.grid(row=0, column=1, padx=5)

tk.Label(frame_input, text="Z 坐标:").grid(row=1, column=0, padx=5, pady=5)
entry_z = tk.Entry(frame_input)
entry_z.grid(row=1, column=1, padx=5)

frame_input.pack(pady=10)

# 按钮
btn_calc = tk.Button(root, text="计算", command=on_calculate)
btn_calc.pack()

# 结果输出
result_area = tk.Text(root, height=8, width=45, state=tk.DISABLED)
result_area.pack(pady=10)

# 启动主循环
root.mainloop()
