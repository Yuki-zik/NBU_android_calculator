# Android 科学计算器

一个基于 Material Design 的 Android 科学计算器应用，支持基础运算和三角函数计算。

## 功能特点

### 1. 基础运算
- 加减乘除四则运算
- 支持小数点运算
- 支持负数输入
- 实时计算结果预览
- 自动运算符替换

### 2. 科学计算
- 三角函数支持（sin、cos）
- 自动角度转弧度
- 支持复合运算（如 2*sin(30)）
- 智能括号处理

### 3. 用户界面
- Material Design 3 设计风格
- 清晰的显示界面
- 历史记录显示（最近5条）
- 实时结果预览
- 优雅的错误提示

### 4. 智能输入
- 自动补全括号
- 自动添加乘号（如 2sin(30) → 2*sin(30)）
- 自动处理小数点（如 .5 → 0.5）
- 防止无效输入

## 技术特点

### 1. 核心功能
- 使用 exp4j 库进行表达式求值
- 完整的表达式验证机制
- 智能的错误处理
- 优化的数字格式化显示

### 2. 界面实现
- 使用 MaterialButton 组件
- 响应式布局设计
- 优化的触摸反馈
- 合理的空间布局

### 3. 代码架构
- 模块化的功能实现
- 清晰的代码结构
- 完整的注释说明
- 可扩展的设计

## 系统要求

- Android SDK 版本：最低 API 26（Android 8.0）
- 编译 SDK 版本：API 34
- Gradle 版本：最新稳定版
- 依赖项：
  - androidx.appcompat:appcompat:1.6.1
  - com.google.android.material:material:1.11.0
  - net.objecthunter:exp4j:0.4.8

## 使用说明

### 基本运算
1. 点击数字按钮输入数字
2. 使用运算符按钮进行计算
3. 按 = 查看结果
4. 结果会自动成为下一次计算的输入

### 三角函数计算
1. 输入角度值（支持小数）
2. 点击 sin 或 cos 按钮
3. 自动进行角度到弧度的转换
4. 显示计算结果

### 特殊功能
- C：清除所有输入
- ⌫：删除最后一个字符
- 历史记录：自动显示最近5次计算

## 代码结构

项目采用标准的 Android 项目结构，主要文件说明如下：

```
app/                                # 应用程序主目录
├── src/                           # 源代码目录
│   ├── main/                      # 主要代码
│   │   ├── java/                  # Java 源代码
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── calculator/
│   │   │               └── MainActivity.java    # 主活动类，包含所有计算器功能实现
│   │   └── res/                   # 资源文件目录
│   │       ├── layout/            # 布局文件目录
│   │       │   └── activity_main.xml    # 主界面布局，定义计算器的UI结构
│   │       └── values/            # 值资源目录
│   │           ├── colors.xml     # 颜色定义文件，包含应用的配色方案
│   │           └── themes.xml     # 主题定义文件，设置应用的整体样式
└── build.gradle                   # 应用级构建配置，包含依赖项和编译设置
```

### 核心文件说明

1. **MainActivity.java**
   - 计算器的核心逻辑实现
   - 包含数字输入处理
   - 运算符处理逻辑
   - 表达式求值实现
   - 历史记录管理

2. **activity_main.xml**
   - Material Design 风格的UI布局
   - 计算器按钮网格布局
   - 显示区域布局设计
   - 响应式布局适配

3. **colors.xml**
   - Material Design 3 配色方案
   - 主题色定义
   - 按钮和文本颜色定义

4. **themes.xml**
   - 应用主题设置
   - 控件样式定义
   - 全局样式配置

5. **build.gradle**
   - 项目依赖配置
   - SDK 版本设置
   - 编译选项配置

## 实现说明

详细的实现说明请参考 [implementation.md](implementation.md)。

## 未来改进

计划添加的功能：
1. 更多三角函数（tan、arcsin等）
2. 更多数学函数（sqrt、log等）
3. 科学计数法支持
4. 历史记录的持久化存储
5. 横屏布局支持
6. 更多主题选项
7. 计算精度设置 

## 开发环境设置

### 克隆项目
```bash
git clone https://github.com/your-username/NBU_android_calculator.git
cd NBU_android_calculator
```

### 导入项目
1. 打开 Android Studio
2. 选择 "Open an existing Android Studio project"
3. 导航到克隆的项目目录并选择
4. 等待 Gradle 同步完成

### 构建和运行
1. 确保已连接 Android 设备或启动模拟器
2. 点击工具栏中的 "Run" 按钮或使用快捷键 Shift + F10
3. 选择目标设备并等待应用安装和启动

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 版本历史

- 1.0.0
    - 初始发布
    - 基本计算功能
    - 三角函数支持

## 许可证

该项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情
