package com.example.calculator;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText displayText;
    private TextView historyText;
    private TextView resultPreview;
    private StringBuilder currentInput;
    private List<String> calculationHistory;
    private DecimalFormat formatter;
    private boolean needCloseBracket = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        displayText = findViewById(R.id.display_text);
        historyText = findViewById(R.id.history_text);
        resultPreview = findViewById(R.id.result_preview);

        // 初始化变量
        currentInput = new StringBuilder();
        calculationHistory = new ArrayList<>();
        formatter = new DecimalFormat("#.##########");
        formatter.setMaximumFractionDigits(10);

        // 设置显示文本不可编辑
        displayText.setShowSoftInputOnFocus(false);
        displayText.setText("");

        // 设置按钮点击事件
        setupNumberButtons();
        setupOperatorButtons();
        setupFunctionButtons();
        setupControlButtons();
    }

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

    private void setupOperatorButtons() {
        int[] operatorIds = {
            R.id.btn_add, R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide
        };

        for (int id : operatorIds) {
            MaterialButton button = findViewById(id);
            button.setOnClickListener(v -> {
                // 如果需要闭合括号且最后一个字符不是右括号，自动补充右括号
                if (needCloseBracket && currentInput.length() > 0 && 
                    currentInput.charAt(currentInput.length() - 1) != ')') {
                    appendToDisplay(")");
                    needCloseBracket = false;
                }
                
                String operator = button.getText().toString();
                if (operator.equals("×")) operator = "*";
                if (operator.equals("÷")) operator = "/";
                
                // 如果当前输入为空且是减号，允许输入负数
                if (operator.equals("-") && (currentInput.length() == 0 || 
                    !Character.isDigit(currentInput.charAt(currentInput.length() - 1)))) {
                    appendToDisplay(operator);
                    return;
                }
                
                // 如果最后一个字符是运算符，替换它
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

    private void setupFunctionButtons() {
        MaterialButton sinButton = findViewById(R.id.btn_sin);
        MaterialButton cosButton = findViewById(R.id.btn_cos);

        View.OnClickListener trigFunction = v -> {
            MaterialButton button = (MaterialButton) v;
            String func = button.getText().toString().toLowerCase();
            
            // 如果前一个字符是数字，自动添加乘号
            if (currentInput.length() > 0 && Character.isDigit(currentInput.charAt(currentInput.length() - 1))) {
                appendToDisplay("*");
            }
            
            appendToDisplay(func + "(");
            needCloseBracket = true;
        };

        sinButton.setOnClickListener(trigFunction);
        cosButton.setOnClickListener(trigFunction);
    }

    private void setupControlButtons() {
        MaterialButton clearButton = findViewById(R.id.btn_clear);
        MaterialButton deleteButton = findViewById(R.id.btn_delete);
        MaterialButton equalsButton = findViewById(R.id.btn_equals);

        clearButton.setOnClickListener(v -> {
            currentInput.setLength(0);
            displayText.setText("");
            resultPreview.setText("");
            needCloseBracket = false;
        });

        deleteButton.setOnClickListener(v -> {
            if (currentInput.length() > 0) {
                // 如果删除的是左括号，取消需要闭合括号的标记
                if (currentInput.charAt(currentInput.length() - 1) == '(') {
                    needCloseBracket = false;
                }
                currentInput.deleteCharAt(currentInput.length() - 1);
                displayText.setText(currentInput.toString());
                updateResultPreview();
            }
        });

        equalsButton.setOnClickListener(v -> {
            // 如果需要闭合括号，自动补充
            if (needCloseBracket && currentInput.length() > 0 && 
                currentInput.charAt(currentInput.length() - 1) != ')') {
                appendToDisplay(")");
                needCloseBracket = false;
            }
            calculateResult();
        });
    }

    private void appendToDisplay(String text) {
        currentInput.append(text);
        displayText.setText(currentInput.toString());
    }

    private void updateResultPreview() {
        try {
            String expression = currentInput.toString();
            if (!expression.isEmpty() && isValidExpression(expression)) {
                // 如果需要闭合括号，临时添加右括号进行计算
                if (needCloseBracket && expression.charAt(expression.length() - 1) != ')') {
                    expression += ")";
                }
                double result = evaluateExpression(expression);
                resultPreview.setText("= " + formatter.format(result));
            } else {
                resultPreview.setText("");
            }
        } catch (Exception e) {
            resultPreview.setText("");
        }
    }

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

    private boolean isValidExpression(String expression) {
        // 检查括号匹配
        int brackets = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') brackets++;
            if (c == ')') brackets--;
            if (brackets < 0) return false;
        }
        
        // 如果设置了需要闭合括号标记，允许少一个右括号
        if (needCloseBracket && brackets == 1) return true;
        if (!needCloseBracket && brackets != 0) return false;

        // 检查基本语法
        if (expression.matches(".*[+\\-*/]{2,}.*")) return false;
        if (expression.matches("^[*/].*")) return false;
        if (expression.matches(".*[+\\-*/]$")) return false;

        // 检查函数语法
        Pattern funcPattern = Pattern.compile("(sin|cos)\\s*\\(");
        if (!funcPattern.matcher(expression).find()) {
            // 如果没有三角函数，检查括号内是否为空
            if (expression.contains("()")) return false;
        }

        return true;
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
} 
