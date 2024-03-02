package edu.utexas.ece.flakytracker.agent;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static String[] getArgumentsByDescriptor(String descriptor){
        Pattern pattern = Pattern.compile("\\((.*?)\\)");
        Matcher matcher = pattern.matcher(descriptor);
        String[] tempArguments = null;
        while (matcher.find()){
            tempArguments = matcher.group(1).split(";");
        }
        ArrayList<String> types = new ArrayList<>();
        for (String tempArgument : tempArguments) {
            if (tempArgument.indexOf('/')<0)
                types.addAll(Arrays.asList(tempArgument.split("")));
            else
                types.add(tempArgument);
        }
        return types.toArray(new String[0]);
    }


    public static String getAssertType(String descriptor){
        String[] types = getArgumentsByDescriptor(descriptor);
        if (types.length > 0){
            return types[types.length-1];
        }
        return null;
    }

    public static boolean isDoubleSlot(String descriptor){
        String assertType = getAssertType(descriptor);
        if (assertType == null)
            return false;
        if (assertType.equals("D") || assertType.equals("J")){
            return true;
        }
        return false;
    }

    public static String getReturnTypeByDescriptor(String descriptor){
        return descriptor.substring(descriptor.indexOf(")")+1);
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
}
