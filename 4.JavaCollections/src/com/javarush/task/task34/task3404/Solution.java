package com.javarush.task.task34.task3404;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Рекурсия для мат. выражения
*/
public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.recursion("-(-22+22*2)", 0); //expected output 0.5 6
    }
    public void recursion(final String expression, int countOperation) {
        int count = 0;
        if (countOperation == 0) {
            for (char c : expression.toCharArray()) {
                if (c == '^' || c == '*' || c == '/' || c == '-' || c == '+' || c == 'i' || c == 'o' || c == 'a')
                    count++;
            }
        } else count = countOperation;
        try {
            double result = Double.parseDouble(expression);
            NumberFormat format = new DecimalFormat("#.##");
            System.out.println(String.format("%s %d", format.format(result), countOperation));
            return;
        } catch (Exception e) { }
        StringBuilder str = new StringBuilder();
        for (char c : expression.toCharArray()) if (c != ' ') str.append(c);
        String s = str.toString();
        Stack<Integer> stack = new Stack<>();
        int startSubExp = 0;
        int endSubExp = expression.length();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') stack.add(i);
            else if (s.charAt(i) == ')') {
                startSubExp = stack.pop() + 1;
                endSubExp = i;
                break;
            }
        }
        String subExp = s.substring(startSubExp, endSubExp);
        char[] signs = {'^', '/', '*', '-', '+'};
        for (char c : signs) {
            String pattern = String.format("-?\\d+\\.?\\d*\\%s-?\\d+\\.?\\d*", c);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(subExp);
            if (m.find()) {
                String exp = m.group();
                double a = Double.parseDouble(exp.substring(0, exp.lastIndexOf(c)));
                double b = Double.parseDouble(exp.substring(exp.lastIndexOf(c)+1, exp.length()));
                double res = 0;
                if (c == '^') {
                    res = new BigDecimal(Math.pow(a, b)).setScale(4, RoundingMode.HALF_UP).doubleValue();
                    if (a < 0) res *= -1;
                } else if (c == '/') {
                    res = new BigDecimal(a / b).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                } else if (c == '*') {
                    res = new BigDecimal(a * b).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                } else if (c == '-') {
                    res = new BigDecimal(a - b).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                } else if (c == '+') {
                    res = new BigDecimal(a + b).setScale(4, RoundingMode.HALF_EVEN).doubleValue();
                } else continue;
                subExp = m.replaceFirst(res + "");
                break;
            }
        }
        s = s.substring(0, startSubExp) + subExp + s.substring(endSubExp, s.length()) ;
        Pattern p = Pattern.compile("(sin|cos|tan)\\((-?\\d+\\.?\\d*\\))");
        Matcher m = p.matcher(s);
        while (m.find()) {
            String exp = m.group();
            String val = exp.substring(exp.indexOf('(') + 1, exp.indexOf(')'));
            if (exp.startsWith("sin")) {
                double res = Math.sin(Math.toRadians(Double.parseDouble(val)));
                s = m.replaceFirst(new BigDecimal(res).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
            } else if (exp.startsWith("cos")) {
                double res = Math.cos(Math.toRadians(Double.parseDouble(val)));
                s = m.replaceFirst(new BigDecimal(res).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
            } else if (exp.startsWith("tan")) {
                double res = Math.tan(Math.toRadians(Double.parseDouble(val)));
                s = m.replaceFirst(new BigDecimal(res).setScale(2, RoundingMode.HALF_UP).doubleValue() + "");
            }
            m = m.reset(s);
        }
        p = Pattern.compile("([-+])\\(((\\1)\\d+\\.?\\d*\\))"); // -(-0.25) || +(+0.25) -> +0.25
        m = p.matcher(s);
        while (m.find()) {
            String exp = m.group();
            String val = exp.substring(exp.indexOf('(') + 2, exp.indexOf(')'));
            s = m.replaceFirst("+" + val);
            m = m.reset(s);
        }
        p = Pattern.compile("(?<!\\d)\\(([-+]?\\d+\\.?\\d*\\))"); // (-10) > -10 || (+10) > +10
        m = p.matcher(s);
        while (m.find()) {
            String exp = m.group();
            String val = exp.substring(exp.indexOf('(') + 1, exp.indexOf(')'));
            s = m.replaceFirst(val);
            m = m.reset(s);
        }
        p = Pattern.compile("\\+-|--|-\\+");
        m = p.matcher(s);
        while (m.find()) {
            s = m.replaceFirst("-");
            m = m.reset(s);
        }
        recursion(s.toString(), count);
    }

    public Solution() {
        //don't delete
    }
}