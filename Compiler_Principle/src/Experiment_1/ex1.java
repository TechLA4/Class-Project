package Experiment_1;

import java.util.*;
import java.lang.*;


public class ex1 {
    public static ArrayList<String> k=new ArrayList<String>();
    public static ArrayList<String> s=new ArrayList<String>();
    public static ArrayList<String> a=new ArrayList<String>();
    public static ArrayList<String> r=new ArrayList<String>();
    public static ArrayList<String> id=new ArrayList<String>();
    public static ArrayList<String> ci=new ArrayList<String>();
    public static ArrayList<String> instrings=new ArrayList<String>();
    public static ArrayList<String> outtoken=new ArrayList<String>();



    public static void main(String[] args) {
        //输入创建关键字k表和分隔符s表
        creat_k();
        creat_s();
        creat_a();
        creat_r();



        //输入测试

        Scanner scan=new Scanner(System.in);

        String  input=scan.nextLine();
        while(!input.equals("") && scan.hasNextLine())
        {
            Analyse_string(input);
            input=scan.nextLine();
        }

    }


    public static void creat_k(){
        System.out.println("Creating  keywords:");
        String[] keywords={"do","end","for","if","printf","scanf","then","while"};
        for (String str : keywords) {
            k.add(str);
        }

        k.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();
    }

    public static void creat_s() {
        System.out.println("Creating  Split:");
        String[] split = {",", ";", "(", ")", "[", "]"};
        for (String str : split) {
            s.add(str);
        }

        s.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();
    }

    public static void creat_a() {
        System.out.println("Creating  Arithmetic operation:");
        String[] a_o = {"+", "-", "*", "/"};
        for (String str : a_o) {
            a.add(str);
        }

        a.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();
    }

    public static void creat_r() {
        System.out.println("Creating  Relation:");
        String[] a_o = {"<", "<=", "=", ">", ">=", "<>"};
        for (String str : a_o) {
            r.add(str);
        }

        r.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();
    }



    //对输入串的分析函数
    public static void Analyse_string(String s){
        //ArrayList<String> ss=new ArrayList<>();
        String[] ss=new String[20];
        ss=s.split(" ");

        for(int i=0;i<ss.length;i++)
        {
            String test="";
            //判断首字符
            char first=ss[i].charAt(0);
            //如果首字符是字母，则没有error情况，进入search函数进行关键字或标识符的判断
            if((first>=65&&first<=90)||(first>=97&&first<=122))
            {
                for(int j=1;j<ss[i].length();j++)
                {
                    if(a.contains(ss[i].charAt(j)))
                    {

                    }
                }

                search(ss[i],"alpha");
            }

            else if(first>=48&&first<=57)
            {
                //如果数字开头，则进行判断
                char a;
                //对数字之后的字符串部分进行正则表达式匹配
                boolean flag=ss[i].matches("[A-Z]*[a-z]+");
                //如果数字后能匹配到一个字母，则说明是标识符error
                if(flag)
                    System.out.println("error");
                //如果没有匹配，则说明是数字串，进行search函数
                else
                    search(ss[i],"number");
            }
            //
            else
            {

            }
        }



        for(int i=0;i<s.length();i++)
        {

        }
    }


    //对单词的搜索函数
    public static void search(String s,String judge){
        String s1;
        char first=s.charAt(0);

        //如果首字符是字母——>查找关键字、标识符
        if(judge.equals("alpha"))
        {
            //如果是关键字
            if(k.contains(s)==true){
                System.out.println(s+"关键字");
            }
            else //否则就是标识符
            {
                //如果标识符存在——>直接输出
                if(id.contains(s)==true){
                    System.out.println(s+"标识符");
                }
                else//如果标识符不存在——>添加进id数组
                {
                    id.add(s);
                }
            }
        }

        //如果首字符是数字——>查找常数
        else if(judge.equals("number")){
            //如果数字存在——>直接输出
            if(ci.contains(s)==true){
                System.out.println(s+"标识符");
            }
            else//如果数字不存在——>添加进ci数组
            {
                ci.add(s);
            }
        }
        //如果首字符是其他字符
        else{
            //是分隔符
            if(s.contains(s)==true){
                System.out.println(s+"分解符");
            }
            //是算术运算符
            else if(a.contains(s)==true){
                System.out.println(s+"算术运算符");
            }
            //是关系运算符
            else if(r.contains(s)==true){
                System.out.println(s+"关系运算符");
            }
            else{
                System.out.println("error");
            }
        }
    }


}

