package Experiment_1;

import java.util.*;

public class experiment1 {
    static ArrayList<String> k=new ArrayList<String>();
    static ArrayList<String> s=new ArrayList<String>();
    static ArrayList<String> a=new ArrayList<String>();
    static ArrayList<String> r=new ArrayList<String>();
    static ArrayList<String> ci=new ArrayList<>();
    static ArrayList<String> id=new ArrayList<>();

    static int RowX=1; //行
    static int lieY=1; //列

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



    //查找识别方法
    static void search(String str) {
        int i=0; //区分String的首符，1为字母，2为数字，3为其他分隔符
        char ch=str.charAt(0);
        if ((ch>=65&&ch<=90)||(ch>=97&&ch<=122))
            i=1;
        else if(ch>=48&&ch<=57)
            i=2;
        else
            i=3;

        //开始查找识别
        //如果首字符是字母——>查找关键字、标识符
        if (i==1) {
            boolean find=k.contains(str);
            if(find==true) {
                //System.out.println(str+'\t'+'\t'+"(1,"+str+")"+'\t'+'\t'+"关键字"+'\t'+'\t'+"("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(1,"+str+")","关键字","("+RowX+","+lieY+")");
                System.out.println();
            }
            //否则就是标识符
            else {
                find=id.contains(str);
                if(find==true) {
                    //System.out.println(str+"  (6,"+str+")  "+"标识符"+"  ("+RowX+","+lieY+")");
                    System.out.printf("%-10s%-10s%-10s%-10s",str,"(6,"+str+")","标识符","("+RowX+","+lieY+")");
                    System.out.println();
                }
                //如果标识符不存在——>添加进id数组
                else {
                    //System.out.println(str+"  (6,"+str+")  "+"标识符"+"  ("+RowX+","+lieY+")");
                    System.out.printf("%-10s%-10s%-10s%-10s",str,"(6,"+str+")","标识符","("+RowX+","+lieY+")");
                    System.out.println();
                    id.add(str);
                }
            }
        }
        //如果首字符是数字——>查找常数
        else if(i==2) {
            boolean find=ci.contains(str);
            if(find==true) {
                //System.out.println(str+"  (5,"+str+")  "+"常数"+"  ("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(5,"+str+")","常数","("+RowX+","+lieY+")");
                System.out.println();
            }
            //如果数字不存在——>添加进ci数组
            else {
                //System.out.println(str+"  (5,"+str+")  "+"常数"+"  ("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(5,"+str+")","常数","("+RowX+","+lieY+")");
                System.out.println();
                ci.add(str);
            }
        }
        //查找其他字符
        else {
            //分隔符
            if(s.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(2,"+str+")","分界符","("+RowX+","+lieY+")");
                System.out.println();
            }
            //算术运算符
            else if(a.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(3,"+str+")","算术运算符","("+RowX+","+lieY+")");
                System.out.println();
            }
            //关系运算符
            else if(r.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(4,"+str+")","关系运算符","("+RowX+","+lieY+")");
                System.out.println();
            }
            //非法字符
            else {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"Error","Error","("+RowX+","+lieY+")");
                System.out.println();
            }

        }

    }

    //对输入串的分析函数
    static void analysisString(String str) {
        String strTest=""; //当前识别的串
        char char_first;
        for(int i=0;i<str.length();i++) {
            //判断首字符
            char_first=str.charAt(i);
            if(char_first==' ')
                continue;
            //如果首字符是字母
            if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)) {
                strTest+=char_first;
                i++;
                //对后面的字符进行判断，如果后续是字母或者数字，说明是正确的输入，进行搜索
                do {
                    char_first=str.charAt(i);
                    if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)||(char_first>=48&&char_first<=57)) {
                        strTest+=char_first;
                        i++;
                    }
                    else
                        break;
                }while(i<str.length());
                //指针复位
                i--;
                search(strTest);
                strTest="";
                lieY++;
                continue;
            }

            else if(char_first>=48&&char_first<=57) { //识别数字
                strTest+=char_first;
                i++;
                //数字后如果是字母或者是数字或者小数点先全部加进test字符串
                while(i<str.length()){
                    char_first=str.charAt(i);
                    if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)||(char_first>=48&&char_first<=57)||(char_first=='.')) {
                        strTest+=char_first;
                        i++;
                    }
                    else
                        break;
                }


                //指针复位
                i--;

                boolean flag=false;

                //先判断是否是浮点数
                boolean char_float=false;
                for(int j=0;j<strTest.length();j++) {
                    char ch_test=strTest.charAt(j);
                    if(ch_test=='.')
                        char_float=true;
                    else
                        char_float=false;
                }

                //如果不是浮点数
                if(!char_float)
                {
                    //在test字符串中判断数字后是否有字母，有，输出为标识符error
                    int j;
                    for(j=0;j<strTest.length();j++) {
                        char ch_test=strTest.charAt(j);
                        if((ch_test>=65&&ch_test<=90)||(ch_test>=97&&ch_test<=122)) {
                            System.out.printf("%-10s%-10s%-10s%-10s",strTest,"Error","Error","("+RowX+","+lieY+")");
                            System.out.println();
                            flag=true;
                            break;
                        }
                    }
                }
                //如果是浮点数
                else
                {
                    char ch_test=strTest.charAt(0);
                    //在test字符串中判断浮点数
                    int j=0;
                    //先遍历到小数点. 对小数点之前进行字母判断
                    for(j=0;ch_test!='.';j++) {
                        ch_test = strTest.charAt(j);
                        //如果一直都是数字，则正确
                        if (ch_test >= 48 && ch_test <= 57)
                            continue;
                            //如果出现了非数字串，则error
                        else {
                            System.out.printf("%-10s%-10s%-10s%-10s", strTest, "Error", "Error", "(" + RowX + "," + lieY + ")");
                            System.out.println();
                            flag = true;
                            break;
                        }
                    }
                    //对于小数点后的字符进行判断
                    for(int q=j;q<strTest.length();q++)
                    {
                        char ch_test2=strTest.charAt(q);
                        //如果一直都是数字，则正确
                        if(char_first>=48&&char_first<=57)
                            continue;
                            //如果出现了非数字串，则error
                        else
                        {
                            System.out.printf("%-10s%-10s%-10s%-10s",strTest,"Error","Error","("+RowX+","+lieY+")");
                            System.out.println();
                            flag=true;
                            break;
                        }
                    }
                }



                //搜索Test字符串
                if(!flag)
                    search(strTest);
                strTest="";
                lieY++;
                continue;
            }

            //识别其他字符
            else {
                strTest+=char_first;

                //识别是分隔符
                if(s.contains(strTest)) {
                    search(strTest);
                    strTest="";
                    lieY++;
                    continue;
                }
                //识别为算术运算符
                else if(a.contains(strTest)) { //识别为算术运算符
                    i++;
                    //判断运算符后面是否是算术运算符
                    while(i<str.length()) {
                        char_first=str.charAt(i);
                        if(a.contains(char_first+"")) {
                            strTest+=char_first;
                            i++;
                        }
                        else
                            break;
                    }
                    search(strTest);
                    i--; //复位
                    strTest="";
                    lieY++;
                    continue;
                }
                //识别为关系运算符
                else if(r.contains(strTest)) {
                    i++;
                    //判断后面是否是关系运算符
                    while(i<str.length()) {
                        char_first=str.charAt(i);
                        if(r.contains(char_first+"")) {
                            strTest+=char_first;
                            i++;
                        }
                        else
                            break;
                    }
                    search(strTest);
                    i--; //复位
                    strTest="";
                    lieY++;
                    continue;
                }
                //识别非法字符
                else {
                    search(strTest);
                    strTest="";
                    lieY++;
                    continue;
                }
            }
        }
    }
    //主函数
    public static void main(String[] args) {
        //输入创建关键字k表和分隔符s表
        creat_k();
        creat_s();
        creat_a();
        creat_r();


        System.out.println("Please input: ");

        Scanner scan=new Scanner(System.in);
        String  input=scan.nextLine();

        //一行一行的读取
        while(!input.equals("") && scan.hasNextLine())
        {
            //删除注释
            for(int i=0;i<input.length()-1;i++) {
                if(input.charAt(i)=='/'&&input.charAt(i+1)=='/') {
                    input=input.substring(0, i);
                    break;
                }
            }

            //调用分析函数
            analysisString(input);
            RowX++; //行加1
            lieY=1; //列置1
            input=scan.nextLine();
        }
    }
}
