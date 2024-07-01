package Project_OPG;
import java.util.*;

public class OPG {
    static ArrayList<String> Grammar=new ArrayList<String>();
    static ArrayList<String> V_T=new ArrayList<String>();
    static ArrayList<String> V_N=new ArrayList<String>();
    static HashMap<String,ArrayList<String>> Grammar_New=new HashMap<String,ArrayList<String>>(){};
    static Boolean F[][]=new Boolean[100][100];
    static Boolean L[][]=new Boolean[100][100];
    static HashMap<String,ArrayList<String>> FirstVT=new HashMap<String,ArrayList<String>>(){};
    static HashMap<String,ArrayList<String>> LastVT=new HashMap<String,ArrayList<String>>(){};
    static String Table[][]=new String[100][100];
    static Stack<String> Stack_FirstVT=new Stack<String>();
    static Stack<String> Stack_LastVT=new Stack<String>();
    static ArrayList<String> Stack_Analysis=new ArrayList<String>();
    static ArrayList<String> Stack_Input=new ArrayList<String>();
    static HashMap<String,ArrayList<String>> First_Map=new HashMap<String,ArrayList<String>>() {};
    static HashMap<String,ArrayList<String>> Follow_Map=new HashMap<String,ArrayList<String>>() {};



    static String[][] AnalysisTable;

    //创建文法
    public static void Create_Grammer()
    {
        //教材P90文法
        Grammar.add("E->E+T|T");
        Grammar.add("T->T*F|F");
        Grammar.add("F->P↑F|P");
        Grammar.add("P->(E)|i");
    }


    //展示文法
    public static void Show_Grammer()
    {
        System.out.println("Grammer:");
        Grammar.stream().forEach(d -> System.out.println(d+" "));
        System.out.println();
    }



    //创建终结符和非终结符集合
    public static void Create_VT_VN()
    {
        for(int i=0;i<Grammar.size();i++) {

            String grammer = Grammar.get(i);

            //以->符号来分割左右内容，左为非终结符，右为产生式
            String [] str=grammer.split("->");
            //System.out.println("right:"+str[1]);

            //->左边的非终结符去重后直接添加进Vn中
            if(!V_N.contains(str[0]))
                V_N.add(str[0]);
            //对->右边的产生式进行分析
            for (int j = 0; j < str[1].length(); j++)
            {
                char char_test=str[1].charAt(j);
                //System.out.println("char:"+char_test);
                //跳过产生式的  |  ε
                if(char_test=='|'||char_test=='ε')
                    continue;
                //如果是大写字母，说明是非终结符
                if (char_test>='A'&&char_test<='Z')
                {
                    //如果VN集合中，没有这个非终结符，就添加进去
                    if (!V_N.contains(char_test + ""))
                        V_N.add(char_test + "");
                }
                //如果是非大写字母，说明是终结符
                else
                {
                    //如果VT集合中，没有这个终结符，就添加进去
                    if (!V_T.contains(char_test + ""))
                        V_T.add(char_test + "");
                }
            }
        }
    }

    //VN和VT集合展示函数
    public static void Show_VN_VT()
    {
        System.out.println("VT:");
        V_T.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();

        System.out.println("VN:");
        V_N.stream().forEach(d -> System.out.print(d+" "));
        System.out.println();
        System.out.println();
    }

    public static void Grammar_New()
    {
        //先构建所有产生式，右部为空,在使用中取出，进行添加，防止循环中后面将前面覆盖
        for(int i=0;i<V_N.size();i++)
            Grammar_New.put(V_N.get(i),new ArrayList<String>());

        for(int i=0;i<Grammar.size();i++)
        {
            //获得原来文法的每行产生式
            String str=Grammar.get(i);
            //截取每个产生式左边首字母
            String [] str_test=str.split("->");
            String key_left=str_test[0];
            //取出对应左字母在Grammar_New中的空value
            ArrayList<String> value=Grammar_New.get(key_left);

            //截取产生式右边内容
            String value_right=str_test[1];
            //以|符号来分割右边内容
            String [] str_values=value_right.split("\\|");
            //每个分段添加进value
            for(int j=0;j<str_values.length;j++)
                value.add(str_values[j]);
            //处理完后，放入哈希表中
            Grammar_New.put(key_left,value);
        }
    }

    public static void Show_Grammar_New()
    {
        System.out.println("Grammer_New: ");

        for (int i=0 ;i<V_N.size();i++)
        {
            System.out.print(V_N.get(i)+" : ");
            System.out.println(Grammar_New.get(V_N.get(i))+" ");
        }
        System.out.println();
    }

    //判断是否是算符文法
    public static void Judge_Operator_Grammer()
    {
        for(int i=0;i< V_N.size();i++)
        {
            //截取每一个VN的产生式右部，进行分析
            ArrayList<String> right=Grammar_New.get(V_N.get(i));
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //str为每一个产生式
                String str=right.get(j);
                //System.out.println(str);
                //对每一个产生式查找VN，并检查VN后是否仍是一个VN
                for(int p=0;p<str.length()-1;p++)
                {
                    //如果产生式右端有VN
                    if(V_N.contains(str.charAt(p)+""))
                    {
                        //System.out.println("VN:"+str.charAt(p));
                        //System.out.println("VN_next:"+str.charAt(p+1));
                        //如果VN后是否仍是一个VN,则不是算符文法
                        if(V_N.contains(str.charAt(p+1)+""))
                        {
                            System.out.println("Not Operator Grammer ! ");
                            System.exit(1);
                        }
                    }
                }
            }
        }

        System.out.println("IS Operator Grammer ! ");
        System.out.println("");
    }


    //创建FirstVT集合
    public static void Create_FirstVT()
    {
        //初始化F数组
        for(int i=0;i< V_N.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                F[i][j]=false;
        }

        //F数组赋初值
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //截取每一个VN的产生式右部，进行分析
            ArrayList<String> right=Grammar_New.get(Vn);
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //str为每一个产生式
                String str=right.get(j);
                //System.out.println(str);
                //按照规则（1），查找第一个VT
                //1.如果是P->a的情况
                if(V_T.contains((str.charAt(0)+"")))
                {
                    //对应位置的F设置为true
                    F[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(0)+"")]=true;
                    Stack_FirstVT.push(Vn+"->"+str.charAt(0));
                    //System.out.println(Vn+"->"+str.charAt(0));
                }
                //2.如果是P->Qa的情况
                else if(str.length()>1&&V_N.contains(str.charAt(0)+"")&&V_T.contains(str.charAt(1)+""))
                {
                    F[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(1)+"")]=true;
                    Stack_FirstVT.push(Vn+"->"+str.charAt(1));
                    //System.out.println(Vn+"->"+str.charAt(1));
                }
            }
        }

        //栈操作
        while (!Stack_FirstVT.empty())
        {
            //Q->a
            String top=Stack_FirstVT.pop();
            //System.out.println("top:"+top);
            String [] Top=top.split("->");
            //Q
            String Q=Top[0];
            //a
            String a=Top[1];

            for(int i=0;i<V_N.size();i++)
            {
                //P
                String Vn=V_N.get(i);
                //跳过P=Q的情况
                if(Vn.equals(Q))
                    continue;
                //截取每一个P的产生式右部，进行分析
                ArrayList<String> right=Grammar_New.get(Vn);
                for(int j=0;j<right.size();j++)
                {
                    String P_Q=right.get(j).charAt(0)+"";
                    //如果P->Q && Q->a
                    if(P_Q.equals(Q))
                    {
                        F[V_N.indexOf(Vn)][V_T.indexOf(a)]=true;
                        Stack_FirstVT.push(Vn+"->"+a);
                        //System.out.println(Vn+"->"+a);
                    }
                }
            }
        }


        for (int i=0;i<V_N.size();i++)
        {
            ArrayList<String> firstvt=new ArrayList<>();
            for(int j=0;j< V_T.size();j++)
                if(F[i][j])
                    firstvt.add(V_T.get(j));
            FirstVT.put(V_N.get(i),firstvt);
        }
    }

    //展示FirstVT
    public static void Show_First_VT()
    {
        System.out.printf("%-10s","F[]:");
        for(int i=0;i< V_T.size();i++)
            System.out.printf("%-10s",V_T.get(i));
        System.out.println();
        for(int i=0;i< V_N.size();i++)
        {
            System.out.printf("%-10s",V_N.get(i));
            for (int j=0;j< V_T.size();j++)
                System.out.printf("%-10s",F[i][j]);
            System.out.println();
        }

        System.out.println("FirstVT:");
        for(int i=0;i< V_N.size();i++)
        {
            System.out.print(V_N.get(i)+" : ");
            System.out.println(FirstVT.get(V_N.get(i))+" ");
        }
        System.out.println();
    }

    //创建LastVT集合
    public static void Create_Last_VT()
    {
        //初始化L数组
        for(int i=0;i< V_N.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                L[i][j]=false;
        }

        //L数组赋初值
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //截取每一个VN的产生式右部，进行分析
            ArrayList<String> right=Grammar_New.get(Vn);
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //str为每一个产生式
                String str=right.get(j);
                //System.out.println(str);
                //按照规则（1），查找最后一个VT
                //1.如果是P->……a的情况
                if(V_T.contains((str.charAt(str.length()-1)+"")))
                {
                    //对应位置的L设置为true
                    L[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(str.length()-1)+"")]=true;
                    Stack_LastVT.push(Vn+"->"+str.charAt(str.length()-1));
                    //System.out.println(Vn+"->"+str.charAt(str.length()-1));
                }
                //2.如果是P->……aQ的情况
                else if(str.length()>1&&V_N.contains(str.charAt(str.length()-1)+"")&&V_T.contains(str.charAt(str.length()-2)+""))
                {
                    L[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(str.length()-2)+"")]=true;
                    Stack_LastVT.push(Vn+"->"+str.charAt(str.length()-2));
                    //System.out.println(Vn+"->"+str.charAt(str.length()-2));
                }
            }
        }

        //栈操作
        while (!Stack_LastVT.empty()) {
            //Q->a
            String top = Stack_LastVT.pop();
            //System.out.println("top:" + top);
            String[] Top = top.split("->");
            //Q
            String Q = Top[0];
            //a
            String a = Top[1];

            for (int i = 0; i < V_N.size(); i++) {
                //P
                String Vn = V_N.get(i);
                //跳过P=Q的情况
                if (Vn.equals(Q))
                    continue;
                //截取每一个P的产生式右部，进行分析
                ArrayList<String> right = Grammar_New.get(Vn);
                for (int j = 0; j < right.size(); j++) {
                    String P_Q = right.get(j).charAt(right.get(j).length() - 1) + "";

                    //如果P->……Q && Q->a
                    if (P_Q.equals(Q)) {
                        L[V_N.indexOf(Vn)][V_T.indexOf(a)] = true;
                        Stack_LastVT.push(Vn + "->" + a);
                        //System.out.println(Vn + "->" + a);
                    }

                }
            }
        }

        for (int i=0;i<V_N.size();i++)
        {
            ArrayList<String> lastvt=new ArrayList<>();
            for(int j=0;j< V_T.size();j++)
                if(L[i][j])
                    lastvt.add(V_T.get(j));
            LastVT.put(V_N.get(i),lastvt);
        }
    }

    public static void Show_Last_VT()
    {
        System.out.printf("%-10s","L[]:");
        for(int i=0;i< V_T.size();i++)
            System.out.printf("%-10s",V_T.get(i));
        System.out.println();
        for(int i=0;i< V_N.size();i++)
        {
            System.out.printf("%-10s",V_N.get(i));
            for (int j=0;j< V_T.size();j++)
                System.out.printf("%-10s",L[i][j]);
            System.out.println();
        }

        System.out.println("LastVT:");
        for(int i=0;i< V_N.size();i++)
        {
            System.out.print(V_N.get(i)+" : ");
            System.out.println(LastVT.get(V_N.get(i))+" ");
        }
        System.out.println();
    }

    //构建算符优先关系表
    public static void Create_Operator_Table()
    {
        //添加#
        V_T.add("#");


        //初始化关系表
        //=——等于   <——小于   >——大于   空——没有关系
        for(int i=0;i< V_T.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                Table[i][j]=" ";
        }

        //对于每条产生式进行分析,填表
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //截取每一个VN的产生式右部，进行分析
            ArrayList<String> right=Grammar_New.get(Vn);
            for(int j=0;j<right.size();j++)
            {
                //str为具体的每一条产生式
                String str=right.get(j);
                //规则（1) P->……ab……   a=b
                for(int p=0;p<str.length()-1;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_T.contains(str.charAt(p+1)+""))
                        Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(str.charAt(p+1)+"")]="=";
                }

                //规则（1） P->……aQb……   a=b
                for(int p=0;p<str.length()-2;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_N.contains(str.charAt(p+1)+"")&&V_T.contains(str.charAt(p+2)+""))
                        Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(str.charAt(p+2)+"")]="=";
                }

                //规则（2） P->……aR……且R->b  a<b
                for(int p=0;p<str.length()-1;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_N.contains(str.charAt(p+1)+""))
                    {
                        ArrayList<String> firstvt_R=FirstVT.get(str.charAt(p+1)+"");
                        for(int q=0;q<firstvt_R.size();q++)
                            Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(firstvt_R.get(q)+"")]="<";
                    }
                }

                //规则（3）P->……Rb……且R->a  a>b
                for(int p=0;p<str.length()-1;p++)
                {
                    if(V_N.contains(str.charAt(p)+"")&&V_T.contains(str.charAt(p+1)+""))
                    {
                        ArrayList<String> lastvt_R=LastVT.get(str.charAt(p)+"");
                        for(int q=0;q<lastvt_R.size();q++)
                            Table[V_T.indexOf(lastvt_R.get(q)+"")][V_T.indexOf(str.charAt(p+1)+"")]=">";
                    }
                }
            }
        }

        //添加对#号的处理
        for(int i=0;i<V_N.size();i++)
        {
            String Vn=V_N.get(i);

            ArrayList<String> firstvt=FirstVT.get(Vn);
            for(int j=0;j<firstvt.size();j++)
                Table[V_T.size()-1][V_T.indexOf(firstvt.get(j))]="<";

            ArrayList<String> lastvt=LastVT.get(Vn);
            for(int j=0;j<lastvt.size();j++)
                Table[V_T.indexOf(lastvt.get(j))][V_T.size()-1]=">";
        }
        Table[V_T.size()-1][V_T.size()-1]="=";

    }

    //展示算符优先关系表
    public static void Show_Operator_Table()
    {
        System.out.printf("%-8s","Table:");
        for(int i=0;i< V_T.size();i++)
            System.out.printf("%-8s",V_T.get(i));
        System.out.println();
        for(int i=0;i< V_T.size();i++)
        {
            System.out.printf("%-8s",V_T.get(i));
            for (int j=0;j< V_T.size();j++)
                System.out.printf("%-8s",Table[i][j]);
            System.out.println();
        }
    }

    //分析过程
    public static void Analysis(String String_Analysis)
    {
        //输入串转成Arraylist
        for(int i=0;i<String_Analysis.length();i++)
            Stack_Input.add(String_Analysis.charAt(i)+"");

        //分析串压入#
        Stack_Analysis.add("#");

        //栈顶符号
        String Top_Vt="";
        //读入符号
        String Read="Begin";
        //算符优先分析表的横纵坐标
        int x,y=0;


        System.out.println("分析过程");
        System.out.printf("%-5s%-17s%-17s%-8s%-8s%-8s%-10s","步骤","分析栈","剩余输入串","栈顶终结符号","当前读入符号","优先级关系","动作");
        System.out.println();
        int step=1;
        while(Stack_Analysis.size()>3||Stack_Input.size()>0)
        {


            //找到栈里最顶的Vt:左边
            for(int i=Stack_Analysis.size()-1;i>=0;i--)
            {
                if(V_T.contains(Stack_Analysis.get(i))){
                    //取出最顶的vt
                    Top_Vt=Stack_Analysis.get(i);
                    //System.out.println("TOP:"+Top_Vt);
                    break;
                }
            }


            //读入符号
            Read=Stack_Input.get(0);


            x=V_T.indexOf(Top_Vt);
            y=V_T.indexOf(Read);
            //找到两个符号的优先级关系
            String Priority=Table[x][y];

            //优先级关系:空   ->规约失败
            if(Priority.equals(" "))
            {
                System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"规约失败！");
                System.exit(0);
            }
            //优先级关系:<   ->移进
            else if(Priority.equals("<"))
            {
                System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"移进"+Read);
                System.out.println();
                //读入符号移进分析栈
                Stack_Analysis.add(Read);
                //输入串删除首字符
                Stack_Input.remove(0);
            }
            //优先级关系:>   ->开始规约
            else if(Priority.equals(">"))
            {
                boolean flag=false;
                String str_vn="";
                String str_vn_right="";
                String str_stack_analysis="";
                String str_temp="";
                String handle="";

                for(int i=0;i<Stack_Analysis.size();i++)
                {
                    str_temp=Stack_Analysis.get(i);
                    str_stack_analysis=str_stack_analysis+str_temp;
                }

                //System.out.println(Stack_Analysis);
                //System.out.println(str_stack_analysis);

                //寻找最左素短语
                for(int i=Stack_Analysis.size()-1;i>=0;i--)
                {
                    //找到句柄
                    handle=str_stack_analysis.substring(i,str_stack_analysis.length());
                    //System.out.println("handle:"+handle);
                    //跳过句柄中只有一个VN的情况，要找到终结符
                    if(handle.length()==1&&handle.charAt(0)>='A'&&handle.charAt(0)<='Z')
                        continue;
                    //找规约的产生式
                    for(int j=0;j<V_N.size();j++)
                    {
                        String Vn=V_N.get(j);
                        //System.out.println("Vn:"+Vn);
                        //截取每一个VN的产生式右部，进行分析
                        ArrayList<String> right=Grammar_New.get(Vn);
                        for(int p=0;p<right.size();p++)
                        {
                            //具体每一个产生式
                            String str=right.get(p);
                            //System.out.println("str:"+str);

                            //开始判断

                            //首先长度相等
                            if(handle.length()==str.length())
                            {
                                //对每一个字符进行遍历判断
                                for(int k=0;k<handle.length();k++)
                                {
                                    //句柄和产生式的字符同为VN，非终结符没有影响
                                    if(handle.charAt(k)>='A'&&handle.charAt(k)<='Z'&&str.charAt(k)>='A'&&str.charAt(k)<='Z')
                                    {
                                        /**/
                                        //如果遍历到末尾，且最后是VN，则匹配上
                                        if(k==handle.length()-1)
                                        {
                                            //System.out.println("yse");
                                            flag=true;
                                            str_vn=Vn;
                                            str_vn_right=str;
                                        }


                                        continue;
                                    }

                                    //句柄和产生式的字符同为VT，且VT必须相同,不同则跳出匹配这个产生式
                                    if(handle.charAt(k)!=str.charAt(k)){
                                        flag=false;
                                        break;
                                    }
                                    //如果遍历到末尾，则匹配上
                                    if(k==handle.length()-1)
                                    {
                                        flag=true;
                                        str_vn=Vn;
                                        str_vn_right=str;
                                        break;
                                    }

                                }
                            }

                        }
                        if(flag)
                            break;
                    }
                    if (flag)
                        break;




                }
                if(flag)
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"规约"+str_vn+"->"+str_vn_right);
                    System.out.println();
                    //符号栈内最左素短语出栈
                    for(int q=0;q<handle.length();q++)
                        Stack_Analysis.remove(Stack_Analysis.size()-1);
                    //产生式的VN入栈
                    Stack_Analysis.add(str_vn);
                }
                else
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"规约失败");
                    System.exit(0);
                }


            }
            //优先级关系:=   (1)移进  (2)结束
            else if(Priority.equals("="))
            {
                if(Top_Vt.equals(Read)&&Top_Vt.equals("#"))
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"接受");
                    System.exit(0);
                }
                else
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"移进"+Read);
                    System.out.println();
                    //读入符号移进分析栈
                    Stack_Analysis.add(Read);
                    //输入串删除首字符
                    Stack_Input.remove(0);
                }
            }
            step++;
        }

    }

    public static void Begin_Analysis()
    {
        System.out.println("Please Input:");
        Scanner scan=new Scanner(System.in);
        String  input=scan.nextLine();
        Analysis(input);
    }















    //主控函数
    public static void main(String[] args) {
        //创建语法
        Create_Grammer();
        Show_Grammer();

        //创建终结符和非终结符集合
        Create_VT_VN();
        Show_VN_VT();

        //创建新推导式集合
        Grammar_New();
        Show_Grammar_New();

        //判断是否是算符文法
        Judge_Operator_Grammer();

        //创建FirstVT集合
        Create_FirstVT();
        Show_First_VT();

        //创建LastVT集合
        Create_Last_VT();
        Show_Last_VT();

        //创建算符优先关系表
        Create_Operator_Table();
        Show_Operator_Table();

        //开始算符优先分析
        Begin_Analysis();

    }

}
