import tkinter as tk
from tkinter import messagebox, ttk
from datetime import datetime
import os

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

# 版本信息常量
VERSION = "v1.4"
FILE_HEADER = "Minecraft Infdev砖块金字塔坐标已保存列表 坐标格式为 (X,Y,Z)"
FILENAME = "PyramidPosition.txt"

def is_valid_input(input_str):
    try:
        num = int(input_str)
        return 0 <= num <= 33554432
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
        return (nearestX, nearestZ)
    else:
        return (None, None)

def save_coordinates(x, z):
    timestamp = datetime.now().strftime("%Y年%m月%d日 %H:%M")
    
    # 检查文件头
    need_header = not os.path.exists(FILENAME)
    if not need_header:
        try:
            with open(FILENAME, "r", encoding="utf-8") as f:
                first_line = f.readline().strip()
                need_header = first_line != FILE_HEADER
        except:
            need_header = True

    try:
        mode = "w" if need_header else "a"
        with open(FILENAME, mode, encoding="utf-8") as f:
            if need_header:
                f.write(FILE_HEADER + "\n\n")
            f.write(f"({x},128,{z})\n")
            f.write(f"记录于{timestamp}\n\n")
        return True
    except Exception as e:
        messagebox.showerror("保存失败", f"文件保存失败：{str(e)}")
        return False

def on_calculate():
    global current_coords
    x = entry_x.get().strip()
    z = entry_z.get().strip()

    if not is_valid_input(x) or not is_valid_input(z):
        messagebox.showerror("错误", "请输入合法的整数坐标！")
        return

    x_val = int(x)
    z_val = int(z)
    nearestX, nearestZ = find_nearest_pyramid(x_val, z_val)
    
    if nearestX is None:
        result = "未找到符合条件的金字塔中心。"
        current_coords = None
        btn_save.config(state=tk.DISABLED)
    else:
        result = f"最近的砖块金字塔中心坐标是: ({nearestX}, {nearestZ})"
        # 更新当前坐标
        current_coords = (nearestX, nearestZ)
        btn_save.config(state=tk.NORMAL)

    result_area.config(state=tk.NORMAL)
    result_area.delete(1.0, tk.END)
    result_area.insert(tk.END, result + "\n")
    result_area.config(state=tk.DISABLED)

def on_save():
    global current_coords
    if current_coords is None:
        messagebox.showerror("错误", "请先进行计算再保存！")
        return
    
    x, z = current_coords
    try:
        if save_coordinates(x, z):
            # 显示明确的成功提示
            messagebox.showinfo("保存成功", "坐标已成功保存至PyramidPosition.txt")
        else:
            messagebox.showerror("保存失败", "未知原因导致保存失败")
    except Exception as e:
        messagebox.showerror("保存错误", f"发生意外错误：{str(e)}")

# 初始化GUI
root = tk.Tk()
root.title(f"Minecraft Infdev砖块金字塔中心坐标查找器")
root.geometry("500x400")
root.resizable(False, False)

# 输入框架
frame_input = ttk.Frame(root, padding=10)
frame_input.pack(fill=tk.X)

ttk.Label(frame_input, text="请输入 X 坐标:").grid(row=0, column=0, padx=5, pady=5)
entry_x = ttk.Entry(frame_input)
entry_x.grid(row=0, column=1, padx=5)

ttk.Label(frame_input, text="请输入 Z 坐标:").grid(row=1, column=0, padx=5, pady=5)
entry_z = ttk.Entry(frame_input)
entry_z.grid(row=1, column=1, padx=5)

# 按钮框架
frame_buttons = ttk.Frame(root, padding=10)
frame_buttons.pack(fill=tk.X)

btn_calc = ttk.Button(frame_buttons, text="计算", command=on_calculate)
btn_calc.pack(side=tk.LEFT, padx=5)

btn_save = ttk.Button(frame_buttons, text="保存坐标", command=on_save, state=tk.DISABLED)
btn_save.pack(side=tk.LEFT, padx=5)

# 结果区域
frame_result = ttk.Frame(root, padding=10)
frame_result.pack(fill=tk.BOTH, expand=True)

result_area = tk.Text(frame_result, height=8, width=45, state=tk.DISABLED)
result_area.pack(fill=tk.BOTH, expand=True)

# 版本信息
frame_footer = ttk.Frame(root, padding=10)
frame_footer.pack(fill=tk.X, side=tk.BOTTOM)

info_panel = ttk.Frame(frame_footer)
info_panel.pack(side=tk.RIGHT, anchor=tk.E)

lbl_version = ttk.Label(info_panel, text=f"Made by CreatorCSIE", anchor=tk.E)
lbl_version.pack(fill=tk.X, anchor=tk.E)

lbl_author = ttk.Label(info_panel, text=f"版本：{VERSION}", anchor=tk.E)
lbl_author.pack(fill=tk.X, anchor=tk.E)

# 初始化坐标存储
current_coords = None

root.mainloop()
