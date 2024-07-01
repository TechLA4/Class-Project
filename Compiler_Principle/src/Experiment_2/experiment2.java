package Experiment_2;
import java.util.*;

public class experiment2 {

    static ArrayList<String> Grammar=new ArrayList<String>();
    static ArrayList<String> Grammar_left_recursion=new ArrayList<String>();
    static ArrayList<String> V_T=new ArrayList<String>();
    static ArrayList<String> V_N=new ArrayList<String>();
    static HashMap<String,ArrayList<String>> First_Map=new HashMap<String,ArrayList<String>>() {};
    static HashMap<String,ArrayList<String>> Follow_Map=new HashMap<String,ArrayList<String>>() {};
    static HashMap<String,ArrayList<String>> Grammar_New=new HashMap<String,ArrayList<String>>(){};
    static String[][] AnalysisTable;

    //创建文法
    public static void Create_Grammer()
    {


        Grammar.add("E->TG");
        Grammar.add("G->+TG|——TG");
        Grammar.add("G->ε");
        Grammar.add("T->FS");
        Grammar.add("S->*FS|/FS");
        Grammar.add("S->ε");
        Grammar.add("F->(E)");
        Grammar.add("F->i");

       /* Grammar.add("E->TE'");
        Grammar.add("E'->+TE'|ε");
        Grammar.add("T->FT'");
        Grammar.add("T'->*FT'|ε");
        Grammar.add("F->(E)|i");

        Grammar.add("E->E+T|T");
        Grammar.add("T->T*F|F");
        Grammar.add("F->(E)|i");*/
    }

    public static void Left_Recursion()
    {
        for(int i=0;i<V_N.size();i++)
        {
            for(int j=0;j<i-1;j++)
            {
                for(int q=0;q<Grammar.size();q++)
                {
                    String grammer=Grammar.get(q);
                    String grammer_left=grammer.substring(0,4);
                    System.out.println(grammer_left);
                    if(grammer_left.equals(V_N.get(i)+"->"+V_N.get(j)))
                    {
                        String grammer_right=grammer.substring(4);
                        for(int p=0;p<Grammar.size();p++)
                        {
                        }
                    }
                }
            }
        }
    }

    //消除直接左递归
    public static void Left_Direct_Recursion()
    {
        for(int i=0;i<Grammar.size();i++)
        {
            String grammer = Grammar.get(i);
            //以->符号来分割左右内容，左为非终结符，右为产生式
            String [] Str=grammer.split("->");
            String str_left=Str[0];
            String str_right=Str[1];

            //右侧由|分隔的符号集
            List<String> splitByLine = new ArrayList<>(Arrays.asList(str_right.split("\\|")));
            int index = -1;
            //右侧大P所在的首部的字符串
            String oldFlag = null;
            int oldFlagIndex = 0;
            for (String string : splitByLine) {
                index = string.indexOf(str_left);
                //如果是P出现在右边首字符,则判定为直接左递归
                if (index == 0) {
                    oldFlag = string;
                    break;
                }
                oldFlagIndex++;
            }
            //如果有直接左递归，进行消除直接左递归的操作
            if (index == 0) {
                //P'
                String newFlag = str_left + "'";
                //P'——>aP'|ε
                String newFormula1 = newFlag + "->" + oldFlag.replace(str_left, "") + newFlag + "|ε";
                //移除直接左递归项
                splitByLine.remove(oldFlagIndex);
                //P——>bP'形式
                String newFormula2 = str_left + "->";
                for (int j = 0; j < splitByLine.size(); j++) {
                    String str = splitByLine.get(j);
                    //如果|后不是ε，是b，则P—>bP'
                    if (!str.equals("ε")){
                        newFormula2 = newFormula2 + str + newFlag;
                    }else{//如果|后是ε，是b，则P—>P'
                        newFormula2 = newFormula2 + str;
                    }
                    //当有多个|时，则在2式中添加|
                    if (j + 1 < splitByLine.size()) {
                        newFormula2 = newFormula2 + "|";
                    }
                }
                //添加进新grammer
                Grammar_left_recursion.add(newFormula2);
                Grammar_left_recursion.add(newFormula1);
            }
            else
                Grammar_left_recursion.add(grammer);//如果没有直接左递归，则加进newgrammer中
        }
    }

    //用新Grammer替换旧Grammer
    public static void Change_Grammer()
    {
        System.out.println("Grammer After Clear Left_Direct_Recursion:");
        Grammar.clear();
        Grammar.addAll(Grammar_left_recursion);
    }


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
                //跳过产生式的  '  |  ε
                if(char_test=='|'||char_test=='ε'||char_test=='\'')
                    continue;
                //如果是大写字母，说明是非终结符
                if (char_test>='A'&&char_test<='Z')
                {
                    //如果非终结符后面有',则如S‘形式的一块添加进去
                    if(j!=str[1].length()-1&&str[1].charAt(j+1)=='\'')
                    {
                        //如果非终结符集合没有，就添加进去
                        if(!V_N.contains(char_test + "'"))
                            V_N.add(char_test + "'");
                    }
                    //如果非终结符后面没有',则如S形式的一块添加进去
                    else
                    {
                        //如果非终结符集合没有，就添加进去
                        if (!V_N.contains(char_test + ""))
                            V_N.add(char_test + "");
                    }
                }
                //如果是非大写字母，说明是终结符
                else
                {
                    //如果终结符集合没有，就添加进去
                    if (!V_T.contains(char_test + ""))
                        V_T.add(char_test + "");
                }
            }
        }
    }

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


    //递归创建First集
    public static void First_Map(String str)
    {
        //判断是否构造过str的first
        if(First_Map.containsKey(str))
            return;
        //如果传入的字符是终结符
        if(V_T.contains(str))
        {
            for(int i=0;i<V_T.size();i++)
            {
                ArrayList<String> First_vt=new ArrayList<String>();
                String str_test=V_T.get(i);
                First_vt.add(str_test);
                //终结符直接加入
                First_Map.put(str_test,First_vt);
            }
        }
        //如果传入的字符是非终结符
        else
        {
            //构造这一步递归中非终结符的First集
            ArrayList<String> first_temp_now = new ArrayList<String>();
            //取出所有非终结符的产生式右边可推导项
            ArrayList<String> Str_right = Grammar_New.get(str);
            for (int i = 0; i < Str_right.size(); i++)
            {
                //对右边产生式进行分析
                String str_test = Str_right.get(i);
                //取出首字符
                char char_first = str_test.charAt(0);
                //如果首字符是ε或者终结符，直接加入First集
                if (char_first == 'ε' || V_T.contains(char_first + ""))
                    first_temp_now.add(char_first + "");
                    //如果首字符是非终结符，则对整行进行分析
                else
                {
                    //设立：可推导式首字符全为ε的情况的flag指针
                    int j=0;
                    for (j = 0; j < str_test.length(); j++)
                    {
                        //取出首字符（非终结符）
                        String str_next = str_test.charAt(j) + "";
                        //取出首字符（非终结符）的右边可推导式
                        ArrayList<String> Str_next = Grammar_New.get(str_next);
                        //如果可以推出ε,则指针右移，看下一个字符
                        if (Str_next.contains("ε"))
                            continue;
                        //否则就进行递归，对此终结符进行函数递归查找
                        else
                        {
                            //递归
                            First_Map(str_next);
                            //取出上一步递归的str_test的first集，加入到这一步的first集中
                            ArrayList<String> first_temp_before=First_Map.get(str_next);
                            first_temp_now.addAll(first_temp_before);
                            break;
                        }
                    }
                    //如果j=length，即指针每一个字符都能推出ε，continue,添加ε进First集
                    if(j==str_test.length())
                        first_temp_now.add("ε");
                }
                //将first-temp_now加入First集
                First_Map.put(str,first_temp_now);
            }
        }
    }

    //创建First集
    public static void Create_First_Map()
    {
        for (int i=0;i<V_N.size();i++)
            First_Map(V_N.get(i));
        for(int i=0;i<V_T.size();i++)
            First_Map(V_T.get(i));

    }

    //展示First集
    public static void Show_First_Map()
    {
        System.out.println("First_Map:");
        for (int i=0 ;i<V_N.size();i++)
        {
            System.out.print(V_N.get(i)+" : ");
            System.out.println(First_Map.get(V_N.get(i))+" ");
        }
        System.out.println();
        for (int i=0 ;i<V_T.size();i++)
        {
            System.out.print(V_T.get(i)+" : ");
            System.out.println(First_Map.get(V_T.get(i))+" ");
        }
        System.out.println();
    }

    //递归创建Follow集
    public static void Follow_Map(String str)
    {
        if(Follow_Map.containsKey(str))
            return;
        //创建str的follow
        ArrayList<String> follow_temp=new ArrayList<>();
        //如果str是开始符号，则加入#号
        for(int i=0;i<Grammar.size();i++)
        {
            String [] str_left=Grammar.get(i).split("->");
            if(str_left[0].equals(str))
                follow_temp.add("#");
        }

        for(int i=0;i<V_N.size();i++)
        {
            //对于传入的str，创建follow集需要str在产生式右边,所以在Grammar_New中查找所有str在右边的产生式进行分析
            //即需在循环中跳过str在左边的产生式
            if(V_N.get(i).equals(str))
                continue;
            //取出Vn中的右边产生式集合进行分析
            ArrayList<String> Vn_right_temp=Grammar_New.get(V_N.get(i));

            for(int j=0;j<Vn_right_temp.size();j++)
            {
                //取出每个可能的产生式
                String right_temp=Vn_right_temp.get(j);
                //创建一个去除'符号的产生式，以便判断长度
                String right_temp_remove=right_temp.replace("'","");

                //使用indexof查找字符串str的位置
                int postion=right_temp.indexOf(str);

                //如果未查找到,跳过
                if(postion==-1)
                    continue;
                //如果形如 E查找到了E'中的E，跳过
                if((postion!=right_temp.length()-1)&&(right_temp.charAt(postion+1)=='\'')&&(str.length()==1))
                    continue;

                //如果是A->aB的形式
                //str在length-1，则B是末尾字符
                if(postion==right_temp_remove.length()-1)
                {
                    //递归创建A的Follow集
                    Follow_Map(V_N.get(i));
                    //取出递归上一步的Follow集
                    ArrayList<String> follow_temp_before = Follow_Map.get(V_N.get(i));
                    //Follow(A)添加进Follow（B）
                    follow_temp.addAll(follow_temp_before);
                }
                //若是A->aBb的形式,则进行判断
                else
                {
                    //先取出First(b)
                    ArrayList<String> first_b;
                    String b;
                    //看后面b的形式，如果是S',则get要加入'符号
                    if(postion<right_temp.length()-2&&right_temp.charAt(postion+str.length()+1)=='\'')
                    {
                        b=right_temp.charAt(postion+str.length()) + "'";
                        first_b = First_Map.get(b);
                    }
                    //如果b是S的形式
                    else
                    {
                        b=right_temp.charAt(postion+str.length())+"";
                        first_b = First_Map.get(b);
                    }
                    //如果First(b)里面有ε
                    if (first_b.contains("ε")) {
                        //除去ε
                        first_b.remove("ε");
                        //同上面if内容，添加Follow(A)
                        //递归创建A的Follow集
                        Follow_Map(V_N.get(i));
                        //取出递归上一步的Follow集
                        ArrayList<String> follow_temp_before = Follow_Map.get(V_N.get(i));
                        //Follow(A)添加进Follow（B）
                        follow_temp.addAll(follow_temp_before);
                    }
                    //将First(b)除去ε之后加到Follow(B)
                    follow_temp.addAll(first_b);
                }
            }
        }

        //使用LinkedHashSet对Follow集中的元素去重
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(follow_temp);
        follow_temp = new ArrayList<>(hashSet);
        Follow_Map.put(str, follow_temp);
    }

    //创建FOLLOW集
    public static void Create_Follow_Map()
    {
        for (int i=0;i<V_N.size();i++)
            Follow_Map(V_N.get(i));
    }

    //展示Follow集
    public static void Show_Follow_Map()
    {
        System.out.println("Follow_Map:");
        for (int i=0 ;i<V_N.size();i++)
        {
            System.out.print(V_N.get(i)+" : ");
            System.out.println(Follow_Map.get(V_N.get(i))+" ");
        }
        System.out.println();
    }

    //构建分析表
    public static void Create_AnalysisTable()
    {
        //创建分析表矩阵,注意表中终结符栏还有一个#符号
        AnalysisTable=new String[V_N.size()][V_T.size()+1];
        //对非终结符进行分析
        for(int i=0;i<V_N.size();i++)
        {
            //取出非终结符的右边产生式
            ArrayList<String> Right_temp=Grammar_New.get(V_N.get(i)); //拿出第一个非终结符的产生式右部
            //对右边产生式分析
            for(int j=0;j<Right_temp.size();j++)
            {
                //取出每个产生式
                String str_right=Right_temp.get(j);
                //如果是A->ε的形式
                if(str_right.equals("ε"))
                {
                    //取出follow（A)
                    ArrayList<String> follow_A=Follow_Map.get(V_N.get(i));
                    //查找Follow(A)中的终结符b
                    for(int q=0;q<V_T.size();q++)
                    {
                        //将A->ε添加到M[A,b]中
                        if(follow_A.contains(V_T.get(q)))
                            AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                    }
                    //如果有#号标志
                    if(follow_A.contains("#"))
                        AnalysisTable[i][V_T.size()]=V_N.get(i)+"->"+str_right;
                }
                //如果是A->α的形式
                else
                {
                    //创建右边产生式α首字符的first集
                    ArrayList<String> first_a;
                    //考虑S'的情况
                    if(str_right.length()>1&&str_right.charAt(1)=='\'')
                        first_a=First_Map.get(str_right.charAt(0)+"'");
                    //S的情况
                    else
                        first_a=First_Map.get(str_right.charAt(0)+"");

                    //查找First(α)中的终结符a
                    for(int q=0;q<V_T.size();q++)
                    {
                        //将A->α添加到M[A,a]中   ——第二条
                        if(first_a.contains(V_T.get(q)))
                            AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                    }

                    //如果first集中有ε，则同上A->ε情况  ——第三条
                    if(first_a.contains("ε"))
                    {
                        //取出follow（A)
                        ArrayList<String> follow_A=Follow_Map.get(V_N.get(j));
                        //查找Follow(A)中的终结符b
                        for(int q=0;q<V_T.size();q++)
                        {
                            if(follow_A.contains(V_T.get(q)))
                                AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                        }
                        //如果有#号标志
                        if(follow_A.contains("#"))
                            AnalysisTable[i][V_T.size()]=V_N.get(i)+"->"+str_right;
                    }
                }
            }
        }
    }

    //展示分析表
    public static void Show_AnalysisTable()
    {
        System.out.println("预测分析表");

        //打印横行的终结符
        System.out.printf("%-10s","table");
        for(int i=0;i<V_T.size();i++) {
            System.out.printf("%-10s",V_T.get(i));
        }
        System.out.printf("%-10s","#");
        System.out.println();
        for(int i=0;i<V_N.size();i++)
        {
            System.out.printf("%-10s",V_N.get(i));
            for(int j=0;j<V_T.size()+1;j++)
                System.out.printf("%-10s",AnalysisTable[i][j]);
            System.out.println();
        }
    }

    //分析过程
    public static void Analyse() {
        //将#加入终结符
        V_T.add("#");
        Stack<String> Stack = new Stack<String>();

        System.out.println("Please input: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input+="#";

        String a;
        String x;
        int step = 0;
        String str_right="";

        //判断输入串是否正规
        for (int i = 0; i < input.length(); i++) {
            if (!V_T.contains(input.charAt(i) + "")) {
                System.out.println("!输入串错误,有非法字符");
                return;
            }
        }
        //#入栈
        Stack.push("#");
        Stack.push(V_N.get(0));

        //取出输入串首字符
        a=input.charAt(0)+"";
        while(!Stack.empty())
        {
            //取出栈里的字符串并进行反转，重新压栈
            String str_stack="";
            while(!Stack.empty()) {
                str_stack+=Stack.pop();
            }
            String str_stack_reverse=new StringBuffer(str_stack).reverse().toString();
            for(int i=0;i<str_stack_reverse.length();i++){
                Stack.push(str_stack_reverse.charAt(i)+"");
            }

            System.out.printf("%-10s%-10s%-10s%-10s",step,str_stack_reverse,input,str_right);
            System.out.println();
            str_right="";//所用产生式字符串清空

            //弹出栈顶符号X，处理S'情况，连同'符号一同弹出
            x=Stack.pop();
            if(!Stack.empty())
            {
                String x_next=Stack.pop();
                if(x_next.equals("'"))
                    x+=x_next;
                else
                    Stack.push(x_next);
                //情况分析以及错误处理
                if(!V_N.contains(x))
                { //x不是非终结符
                    if(V_T.contains(x))
                    { //x是终结符
                        if(x.equals(a))
                        { //x=a
                            if(x.equals("#")) //x=# 代表结束，成功
                                break;
                            else
                            { //x!=#开始下一步骤
                                input=input.substring(1);
                                a=input.charAt(0)+""; //取一输入输入符号a
                            }
                        }
                        else break;
                    }
                    else break;
                }
                else //x是非终结符
                {
                    //取得x,a的位置
                    int xIndedx=V_N.indexOf(x);
                    int aIndedx=V_T.indexOf(a);

                    if(AnalysisTable[xIndedx][aIndedx]!=null) {
                        //symbolStack.pop(); //栈顶出栈
                        str_right =AnalysisTable[xIndedx][aIndedx]; //str1=M[x,a]
                        //反转字符串
                        String str_right_reverse=new StringBuffer(str_right).reverse().toString();
                        //若产生式是A->ε的情况,跳过
                        if(str_right_reverse.charAt(0)=='ε')
                        {
                            step++;
                            continue;
                        }
                        //倒序入栈
                        for(int j=0;j<str_right.length();j++) {
                            if(str_right_reverse.charAt(j)=='>')
                                break;
                            Stack.push(str_right_reverse.charAt(j)+"");
                        }
                    }
                }
                step++;
            }
        }
        //移去#，恢复vt
        V_T.remove("#");

    }

    public static void main(String[] args) {
        //创建语法
        Create_Grammer();
        Show_Grammer();

        //消除左递归
        Left_Direct_Recursion();
        Change_Grammer();
        Show_Grammer();

        //创建终结符和非终结符集合
        Create_VT_VN();
        Show_VN_VT();

        //创建新推导式集合
        Grammar_New();
        Show_Grammar_New();

        //创建First集
        Create_First_Map();
        Show_First_Map();

        //创建Follow集
        Create_Follow_Map();
        Show_Follow_Map();

        //创建分析表
        Create_AnalysisTable();
        Show_AnalysisTable();

        //分析程序
        Analyse();
    }
}
