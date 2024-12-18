# Android计算器代码实现说明

## 1. 数字输入的实现
数字以字符形式输入，使用getText()函数读取数据：

```java
private void setupNumberButtons() {
    int[] numberIds = {
        R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
        R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9,
        R.id.btn_decimal
    };

    for (int id : numberIds) {
        MaterialButton button = findViewById(id);
        button.setOnClickListener(v -> {
            String number = button.getText().toString();
            // 如果当前输入为空且输入小数点，自动补0
            if (number.equals(".") && (currentInput.length() == 0 || 
                !Character.isDigit(currentInput.charAt(currentInput.length() - 1)))) {
                appendToDisplay("0");
            }
            appendToDisplay(number);
            updateResultPreview();
        });
    }
}
```

## 2. 双目运算符的实现
获取num1，清空输入框，并设置标志 op 标明是哪种运算。对于除法运算需要判断，除数是否为零并提醒。：

```java
private void setupOperatorButtons() {
    int[] operatorIds = {
        R.id.btn_add, R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide
    };

    for (int id : operatorIds) {
        MaterialButton button = findViewById(id);
        button.setOnClickListener(v -> {
            // 自动补充右括号
            if (needCloseBracket && currentInput.length() > 0 && 
                currentInput.charAt(currentInput.length() - 1) != ')') {
                appendToDisplay(")");
                needCloseBracket = false;
            }
            
            String operator = button.getText().toString();
            if (operator.equals("×")) operator = "*";
            if (operator.equals("÷")) operator = "/";
            
            // 支持负数输入
            if (operator.equals("-") && (currentInput.length() == 0 || 
                !Character.isDigit(currentInput.charAt(currentInput.length() - 1)))) {
                appendToDisplay(operator);
                return;
            }
            
            // 运算符替换
            if (currentInput.length() > 0 && isOperator(currentInput.charAt(currentInput.length() - 1))) {
                currentInput.setLength(currentInput.length() - 1);
            }
            
            appendToDisplay(operator);
        });
    }
}

private boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '×' || c == '÷';
}
```

## 3. 三角函数的实现
使用 exp4j 的 Function 类实现三角函数，自动处理角度到弧度的转换：

```java
// 定义三角函数
private final Function sinFunction = new Function("sin", 1) {
    @Override
    public double apply(double... args) {
        return Math.sin(Math.toRadians(args[0]));
    }
};

private final Function cosFunction = new Function("cos", 1) {
    @Override
    public double apply(double... args) {
        return Math.cos(Math.toRadians(args[0]));
    }
};

private void setupFunctionButtons() {
    MaterialButton sinButton = findViewById(R.id.btn_sin);
    MaterialButton cosButton = findViewById(R.id.btn_cos);

    View.OnClickListener trigFunction = v -> {
        MaterialButton button = (MaterialButton) v;
        String func = button.getText().toString().toLowerCase();
        
        // 自动添加乘号
        if (currentInput.length() > 0 && Character.isDigit(currentInput.charAt(currentInput.length() - 1))) {
            appendToDisplay("*");
        }
        
        appendToDisplay(func + "(");
        needCloseBracket = true;
    };

    sinButton.setOnClickListener(trigFunction);
    cosButton.setOnClickListener(trigFunction);
}
```

## 4. 清空和删除功能的实现
在清空时，需要将输入框置空。清除一位数据时，判断输入框中是否 只有一个数据，如是，直接清空，如不是输入框中置前 n-1 位。
清空和删除一位数据的实现：

```java
private void setupControlButtons() {
    MaterialButton clearButton = findViewById(R.id.btn_clear);
    MaterialButton deleteButton = findViewById(R.id.btn_delete);

    clearButton.setOnClickListener(v -> {
        currentInput.setLength(0);
        displayText.setText("");
        resultPreview.setText("");
        needCloseBracket = false;
    });

    deleteButton.setOnClickListener(v -> {
        if (currentInput.length() > 0) {
            // 处理括号删除
            if (currentInput.charAt(currentInput.length() - 1) == '(') {
                needCloseBracket = false;
            }
            currentInput.deleteCharAt(currentInput.length() - 1);
            displayText.setText(currentInput.toString());
            updateResultPreview();
        }
    });
}
```

## 5. 等号运算的实现
在点击等于符号时，得到输入框里的数据 num2，根据前面设置的标
志位，判断点击的是单目运算符还是双目运算符，若是单目运算符就将 num2 进
行单目运算。若是双目运算，num2 不变。再进行 Result 运算。

根据运算符类型进行相应计算：

```java
private void calculateResult() {
    try {
        String expression = currentInput.toString();
        if (!expression.isEmpty() && isValidExpression(expression)) {
            double result = evaluateExpression(expression);
            String formattedResult = formatter.format(result);
            
            // 添加到历史记录
            String historyEntry = expression + " = " + formattedResult;
            calculationHistory.add(historyEntry);
            
            // 更新历史显示
            StringBuilder historyBuilder = new StringBuilder();
            for (int i = Math.max(0, calculationHistory.size() - 5); i < calculationHistory.size(); i++) {
                historyBuilder.append(calculationHistory.get(i)).append("\n");
            }
            historyText.setText(historyBuilder.toString());

            // 更新当前输入
            currentInput.setLength(0);
            currentInput.append(formattedResult);
            displayText.setText(formattedResult);
            resultPreview.setText("");
            needCloseBracket = false;
        }
    } catch (Exception e) {
        resultPreview.setText("Error");
    }
}

private double evaluateExpression(String expression) {
    try {
        // 将显示符号转换为实际运算符
        expression = expression.replace("×", "*").replace("÷", "/");
        
        // 构建表达式并计算
        Expression e = new ExpressionBuilder(expression)
            .function(sinFunction)
            .function(cosFunction)
            .build();
        
        return e.evaluate();
    } catch (Exception e) {
        throw new IllegalArgumentException("Invalid expression");
    }
}
```

## 6. 表达式验证
实现完整的表达式验证逻辑：

```java
private boolean isValidExpression(String expression) {
    // 检查括号匹配
    int brackets = 0;
    for (char c : expression.toCharArray()) {
        if (c == '(') brackets++;
        if (c == ')') brackets--;
        if (brackets < 0) return false;
    }
    
    // 处理未闭合的括号
    if (needCloseBracket && brackets == 1) return true;
    if (!needCloseBracket && brackets != 0) return false;

    // 检查基本语法
    if (expression.matches(".*[+\\-*/]{2,}.*")) return false;
    if (expression.matches("^[*/].*")) return false;
    if (expression.matches(".*[+\\-*/]$")) return false;

    // 检查函数语法
    Pattern funcPattern = Pattern.compile("(sin|cos)\\s*\\(");
    if (!funcPattern.matcher(expression).find()) {
        if (expression.contains("()")) return false;
    }

    return true;
}
``` 
