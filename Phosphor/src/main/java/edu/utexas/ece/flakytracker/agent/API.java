package edu.utexas.ece.flakytracker.agent;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class API {
    String owner;

    String name;

    String descriptor;
    String[] arguments;

    String returnType;

    public API(String owner, String name, String descriptor) {
        this.owner = owner;
        this.name = name;
        this.descriptor = descriptor;
    }



    public static String getAssertType(String descriptor){
        String[] types = getParamTypes(descriptor);
        if (types.length > 0){
            return types[types.length-1];
        }
        return null;
    }

    public static boolean isDoubleSlot(String descriptor){
        String assertType = getAssertType(descriptor);
        if (assertType == null)
            return false;
        if (assertType.equals("double") || assertType.equals("long")){
            return true;
        }
        return false;
    }

    public static boolean isPrimitiveType(String typeName) {
        return typeName.equals("int") || typeName.equals("long") || typeName.equals("short") ||
                typeName.equals("byte") || typeName.equals("char") || typeName.equals("float") ||
                typeName.equals("double") || typeName.equals("boolean");
    }



    public API() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getDescriptor(){
        return descriptor;
    }


    public static String[] getParamTypes(String descriptor) {
        // 用于存储参数类型的列表
        List<String> paramTypes = new ArrayList<>();
        // 记录当前解析的位置
        int index = 0;
        // 跳过'('
        index++;
        // 循环解析参数类型
        while(descriptor.charAt(index) != ')') {
            StringBuilder type = new StringBuilder();
            // 如果遇到了数组类型
            while(descriptor.charAt(index) == '[') {
                type.append("[]");
                index++;
            }
            // 根据类型字符添加类型
            switch(descriptor.charAt(index)) {
                case 'B': type.insert(0, "byte"); break;
                case 'C': type.insert(0, "char"); break;
                case 'D': type.insert(0, "double"); break;
                case 'F': type.insert(0, "float"); break;
                case 'I': type.insert(0, "int"); break;
                case 'J': type.insert(0, "long"); break;
                case 'S': type.insert(0, "short"); break;
                case 'Z': type.insert(0, "boolean"); break;
                case 'L':
                    // 对象类型，直到遇到';'
                    index++; // 跳过'L'
                    while(descriptor.charAt(index) != ';') {
                        if(descriptor.charAt(index) == '/') {
                            type.append(".");
                        } else {
                            type.append(descriptor.charAt(index));
                        }
                        index++;
                    }
                    break;
                default: throw new IllegalArgumentException("Unknown type: " + descriptor.charAt(index));
            }
            index++; // 移动到下一个类型符号
            paramTypes.add(type.toString());
        }

        return paramTypes.toArray(new String[0]);
    }

    public static String getReturnType(String descriptor){
        int index = descriptor.indexOf(')') +1;
        String returnType = "";
        StringBuilder type = new StringBuilder();
        // 如果遇到了数组类型
        while(descriptor.charAt(index) == '[') {
            type.append("[]");
            index++;
        }
        // 根据类型字符添加类型
        switch(descriptor.charAt(index)) {
            case 'B': type.insert(0, "byte"); break;
            case 'C': type.insert(0, "char"); break;
            case 'D': type.insert(0, "double"); break;
            case 'F': type.insert(0, "float"); break;
            case 'I': type.insert(0, "int"); break;
            case 'J': type.insert(0, "long"); break;
            case 'S': type.insert(0, "short"); break;
            case 'Z': type.insert(0, "boolean"); break;
            case 'V': type.insert(0, "void"); break;
            case 'L':
                // 对象类型，直到遇到';'
                index++; // 跳过'L'
                while(descriptor.charAt(index) != ';') {
                    if(descriptor.charAt(index) == '/') {
                        type.append(".");
                    } else {
                        type.append(descriptor.charAt(index));
                    }
                    index++;
                }
                break;
            default: throw new IllegalArgumentException("Unknown type: " + descriptor.charAt(index));
        }
        return type.toString();

    }
}
