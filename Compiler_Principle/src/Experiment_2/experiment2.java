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

    //�����ķ�
    public static void Create_Grammer()
    {


        Grammar.add("E->TG");
        Grammar.add("G->+TG|����TG");
        Grammar.add("G->��");
        Grammar.add("T->FS");
        Grammar.add("S->*FS|/FS");
        Grammar.add("S->��");
        Grammar.add("F->(E)");
        Grammar.add("F->i");

       /* Grammar.add("E->TE'");
        Grammar.add("E'->+TE'|��");
        Grammar.add("T->FT'");
        Grammar.add("T'->*FT'|��");
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

    //����ֱ����ݹ�
    public static void Left_Direct_Recursion()
    {
        for(int i=0;i<Grammar.size();i++)
        {
            String grammer = Grammar.get(i);
            //��->�������ָ��������ݣ���Ϊ���ս������Ϊ����ʽ
            String [] Str=grammer.split("->");
            String str_left=Str[0];
            String str_right=Str[1];

            //�Ҳ���|�ָ��ķ��ż�
            List<String> splitByLine = new ArrayList<>(Arrays.asList(str_right.split("\\|")));
            int index = -1;
            //�Ҳ��P���ڵ��ײ����ַ���
            String oldFlag = null;
            int oldFlagIndex = 0;
            for (String string : splitByLine) {
                index = string.indexOf(str_left);
                //�����P�������ұ����ַ�,���ж�Ϊֱ����ݹ�
                if (index == 0) {
                    oldFlag = string;
                    break;
                }
                oldFlagIndex++;
            }
            //�����ֱ����ݹ飬��������ֱ����ݹ�Ĳ���
            if (index == 0) {
                //P'
                String newFlag = str_left + "'";
                //P'����>aP'|��
                String newFormula1 = newFlag + "->" + oldFlag.replace(str_left, "") + newFlag + "|��";
                //�Ƴ�ֱ����ݹ���
                splitByLine.remove(oldFlagIndex);
                //P����>bP'��ʽ
                String newFormula2 = str_left + "->";
                for (int j = 0; j < splitByLine.size(); j++) {
                    String str = splitByLine.get(j);
                    //���|���Ǧţ���b����P��>bP'
                    if (!str.equals("��")){
                        newFormula2 = newFormula2 + str + newFlag;
                    }else{//���|���Ǧţ���b����P��>P'
                        newFormula2 = newFormula2 + str;
                    }
                    //���ж��|ʱ������2ʽ�����|
                    if (j + 1 < splitByLine.size()) {
                        newFormula2 = newFormula2 + "|";
                    }
                }
                //��ӽ���grammer
                Grammar_left_recursion.add(newFormula2);
                Grammar_left_recursion.add(newFormula1);
            }
            else
                Grammar_left_recursion.add(grammer);//���û��ֱ����ݹ飬��ӽ�newgrammer��
        }
    }

    //����Grammer�滻��Grammer
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


    //�����ս���ͷ��ս������
    public static void Create_VT_VN()
    {
        for(int i=0;i<Grammar.size();i++) {

            String grammer = Grammar.get(i);

            //��->�������ָ��������ݣ���Ϊ���ս������Ϊ����ʽ
            String [] str=grammer.split("->");
            //System.out.println("right:"+str[1]);
            //->��ߵķ��ս��ȥ�غ�ֱ����ӽ�Vn��
            if(!V_N.contains(str[0]))
                V_N.add(str[0]);
            //��->�ұߵĲ���ʽ���з���
            for (int j = 0; j < str[1].length(); j++)
            {
                char char_test=str[1].charAt(j);
                //System.out.println("char:"+char_test);
                //��������ʽ��  '  |  ��
                if(char_test=='|'||char_test=='��'||char_test=='\'')
                    continue;
                //����Ǵ�д��ĸ��˵���Ƿ��ս��
                if (char_test>='A'&&char_test<='Z')
                {
                    //������ս��������',����S����ʽ��һ����ӽ�ȥ
                    if(j!=str[1].length()-1&&str[1].charAt(j+1)=='\'')
                    {
                        //������ս������û�У�����ӽ�ȥ
                        if(!V_N.contains(char_test + "'"))
                            V_N.add(char_test + "'");
                    }
                    //������ս������û��',����S��ʽ��һ����ӽ�ȥ
                    else
                    {
                        //������ս������û�У�����ӽ�ȥ
                        if (!V_N.contains(char_test + ""))
                            V_N.add(char_test + "");
                    }
                }
                //����ǷǴ�д��ĸ��˵�����ս��
                else
                {
                    //����ս������û�У�����ӽ�ȥ
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
        //�ȹ������в���ʽ���Ҳ�Ϊ��,��ʹ����ȡ����������ӣ���ֹѭ���к��潫ǰ�渲��
        for(int i=0;i<V_N.size();i++)
            Grammar_New.put(V_N.get(i),new ArrayList<String>());

        for(int i=0;i<Grammar.size();i++)
        {
            //���ԭ���ķ���ÿ�в���ʽ
            String str=Grammar.get(i);
            //��ȡÿ������ʽ�������ĸ
            String [] str_test=str.split("->");
            String key_left=str_test[0];
            //ȡ����Ӧ����ĸ��Grammar_New�еĿ�value
            ArrayList<String> value=Grammar_New.get(key_left);

            //��ȡ����ʽ�ұ�����
            String value_right=str_test[1];
            //��|�������ָ��ұ�����
            String [] str_values=value_right.split("\\|");
            //ÿ���ֶ���ӽ�value
            for(int j=0;j<str_values.length;j++)
                value.add(str_values[j]);
            //������󣬷����ϣ����
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


    //�ݹ鴴��First��
    public static void First_Map(String str)
    {
        //�ж��Ƿ����str��first
        if(First_Map.containsKey(str))
            return;
        //���������ַ����ս��
        if(V_T.contains(str))
        {
            for(int i=0;i<V_T.size();i++)
            {
                ArrayList<String> First_vt=new ArrayList<String>();
                String str_test=V_T.get(i);
                First_vt.add(str_test);
                //�ս��ֱ�Ӽ���
                First_Map.put(str_test,First_vt);
            }
        }
        //���������ַ��Ƿ��ս��
        else
        {
            //������һ���ݹ��з��ս����First��
            ArrayList<String> first_temp_now = new ArrayList<String>();
            //ȡ�����з��ս���Ĳ���ʽ�ұ߿��Ƶ���
            ArrayList<String> Str_right = Grammar_New.get(str);
            for (int i = 0; i < Str_right.size(); i++)
            {
                //���ұ߲���ʽ���з���
                String str_test = Str_right.get(i);
                //ȡ�����ַ�
                char char_first = str_test.charAt(0);
                //������ַ��ǦŻ����ս����ֱ�Ӽ���First��
                if (char_first == '��' || V_T.contains(char_first + ""))
                    first_temp_now.add(char_first + "");
                    //������ַ��Ƿ��ս����������н��з���
                else
                {
                    //���������Ƶ�ʽ���ַ�ȫΪ�ŵ������flagָ��
                    int j=0;
                    for (j = 0; j < str_test.length(); j++)
                    {
                        //ȡ�����ַ������ս����
                        String str_next = str_test.charAt(j) + "";
                        //ȡ�����ַ������ս�������ұ߿��Ƶ�ʽ
                        ArrayList<String> Str_next = Grammar_New.get(str_next);
                        //��������Ƴ���,��ָ�����ƣ�����һ���ַ�
                        if (Str_next.contains("��"))
                            continue;
                        //����ͽ��еݹ飬�Դ��ս�����к����ݹ����
                        else
                        {
                            //�ݹ�
                            First_Map(str_next);
                            //ȡ����һ���ݹ��str_test��first�������뵽��һ����first����
                            ArrayList<String> first_temp_before=First_Map.get(str_next);
                            first_temp_now.addAll(first_temp_before);
                            break;
                        }
                    }
                    //���j=length����ָ��ÿһ���ַ������Ƴ��ţ�continue,��ӦŽ�First��
                    if(j==str_test.length())
                        first_temp_now.add("��");
                }
                //��first-temp_now����First��
                First_Map.put(str,first_temp_now);
            }
        }
    }

    //����First��
    public static void Create_First_Map()
    {
        for (int i=0;i<V_N.size();i++)
            First_Map(V_N.get(i));
        for(int i=0;i<V_T.size();i++)
            First_Map(V_T.get(i));

    }

    //չʾFirst��
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

    //�ݹ鴴��Follow��
    public static void Follow_Map(String str)
    {
        if(Follow_Map.containsKey(str))
            return;
        //����str��follow
        ArrayList<String> follow_temp=new ArrayList<>();
        //���str�ǿ�ʼ���ţ������#��
        for(int i=0;i<Grammar.size();i++)
        {
            String [] str_left=Grammar.get(i).split("->");
            if(str_left[0].equals(str))
                follow_temp.add("#");
        }

        for(int i=0;i<V_N.size();i++)
        {
            //���ڴ����str������follow����Ҫstr�ڲ���ʽ�ұ�,������Grammar_New�в�������str���ұߵĲ���ʽ���з���
            //������ѭ��������str����ߵĲ���ʽ
            if(V_N.get(i).equals(str))
                continue;
            //ȡ��Vn�е��ұ߲���ʽ���Ͻ��з���
            ArrayList<String> Vn_right_temp=Grammar_New.get(V_N.get(i));

            for(int j=0;j<Vn_right_temp.size();j++)
            {
                //ȡ��ÿ�����ܵĲ���ʽ
                String right_temp=Vn_right_temp.get(j);
                //����һ��ȥ��'���ŵĲ���ʽ���Ա��жϳ���
                String right_temp_remove=right_temp.replace("'","");

                //ʹ��indexof�����ַ���str��λ��
                int postion=right_temp.indexOf(str);

                //���δ���ҵ�,����
                if(postion==-1)
                    continue;
                //������� E���ҵ���E'�е�E������
                if((postion!=right_temp.length()-1)&&(right_temp.charAt(postion+1)=='\'')&&(str.length()==1))
                    continue;

                //�����A->aB����ʽ
                //str��length-1����B��ĩβ�ַ�
                if(postion==right_temp_remove.length()-1)
                {
                    //�ݹ鴴��A��Follow��
                    Follow_Map(V_N.get(i));
                    //ȡ���ݹ���һ����Follow��
                    ArrayList<String> follow_temp_before = Follow_Map.get(V_N.get(i));
                    //Follow(A)��ӽ�Follow��B��
                    follow_temp.addAll(follow_temp_before);
                }
                //����A->aBb����ʽ,������ж�
                else
                {
                    //��ȡ��First(b)
                    ArrayList<String> first_b;
                    String b;
                    //������b����ʽ�������S',��getҪ����'����
                    if(postion<right_temp.length()-2&&right_temp.charAt(postion+str.length()+1)=='\'')
                    {
                        b=right_temp.charAt(postion+str.length()) + "'";
                        first_b = First_Map.get(b);
                    }
                    //���b��S����ʽ
                    else
                    {
                        b=right_temp.charAt(postion+str.length())+"";
                        first_b = First_Map.get(b);
                    }
                    //���First(b)�����Ц�
                    if (first_b.contains("��")) {
                        //��ȥ��
                        first_b.remove("��");
                        //ͬ����if���ݣ����Follow(A)
                        //�ݹ鴴��A��Follow��
                        Follow_Map(V_N.get(i));
                        //ȡ���ݹ���һ����Follow��
                        ArrayList<String> follow_temp_before = Follow_Map.get(V_N.get(i));
                        //Follow(A)��ӽ�Follow��B��
                        follow_temp.addAll(follow_temp_before);
                    }
                    //��First(b)��ȥ��֮��ӵ�Follow(B)
                    follow_temp.addAll(first_b);
                }
            }
        }

        //ʹ��LinkedHashSet��Follow���е�Ԫ��ȥ��
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(follow_temp);
        follow_temp = new ArrayList<>(hashSet);
        Follow_Map.put(str, follow_temp);
    }

    //����FOLLOW��
    public static void Create_Follow_Map()
    {
        for (int i=0;i<V_N.size();i++)
            Follow_Map(V_N.get(i));
    }

    //չʾFollow��
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

    //����������
    public static void Create_AnalysisTable()
    {
        //�������������,ע������ս��������һ��#����
        AnalysisTable=new String[V_N.size()][V_T.size()+1];
        //�Է��ս�����з���
        for(int i=0;i<V_N.size();i++)
        {
            //ȡ�����ս�����ұ߲���ʽ
            ArrayList<String> Right_temp=Grammar_New.get(V_N.get(i)); //�ó���һ�����ս���Ĳ���ʽ�Ҳ�
            //���ұ߲���ʽ����
            for(int j=0;j<Right_temp.size();j++)
            {
                //ȡ��ÿ������ʽ
                String str_right=Right_temp.get(j);
                //�����A->�ŵ���ʽ
                if(str_right.equals("��"))
                {
                    //ȡ��follow��A)
                    ArrayList<String> follow_A=Follow_Map.get(V_N.get(i));
                    //����Follow(A)�е��ս��b
                    for(int q=0;q<V_T.size();q++)
                    {
                        //��A->����ӵ�M[A,b]��
                        if(follow_A.contains(V_T.get(q)))
                            AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                    }
                    //�����#�ű�־
                    if(follow_A.contains("#"))
                        AnalysisTable[i][V_T.size()]=V_N.get(i)+"->"+str_right;
                }
                //�����A->������ʽ
                else
                {
                    //�����ұ߲���ʽ�����ַ���first��
                    ArrayList<String> first_a;
                    //����S'�����
                    if(str_right.length()>1&&str_right.charAt(1)=='\'')
                        first_a=First_Map.get(str_right.charAt(0)+"'");
                    //S�����
                    else
                        first_a=First_Map.get(str_right.charAt(0)+"");

                    //����First(��)�е��ս��a
                    for(int q=0;q<V_T.size();q++)
                    {
                        //��A->����ӵ�M[A,a]��   �����ڶ���
                        if(first_a.contains(V_T.get(q)))
                            AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                    }

                    //���first�����Цţ���ͬ��A->�����  ����������
                    if(first_a.contains("��"))
                    {
                        //ȡ��follow��A)
                        ArrayList<String> follow_A=Follow_Map.get(V_N.get(j));
                        //����Follow(A)�е��ս��b
                        for(int q=0;q<V_T.size();q++)
                        {
                            if(follow_A.contains(V_T.get(q)))
                                AnalysisTable[i][q]=V_N.get(i)+"->"+str_right;
                        }
                        //�����#�ű�־
                        if(follow_A.contains("#"))
                            AnalysisTable[i][V_T.size()]=V_N.get(i)+"->"+str_right;
                    }
                }
            }
        }
    }

    //չʾ������
    public static void Show_AnalysisTable()
    {
        System.out.println("Ԥ�������");

        //��ӡ���е��ս��
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

    //��������
    public static void Analyse() {
        //��#�����ս��
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

        //�ж����봮�Ƿ�����
        for (int i = 0; i < input.length(); i++) {
            if (!V_T.contains(input.charAt(i) + "")) {
                System.out.println("!���봮����,�зǷ��ַ�");
                return;
            }
        }
        //#��ջ
        Stack.push("#");
        Stack.push(V_N.get(0));

        //ȡ�����봮���ַ�
        a=input.charAt(0)+"";
        while(!Stack.empty())
        {
            //ȡ��ջ����ַ��������з�ת������ѹջ
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
            str_right="";//���ò���ʽ�ַ������

            //����ջ������X������S'�������ͬ'����һͬ����
            x=Stack.pop();
            if(!Stack.empty())
            {
                String x_next=Stack.pop();
                if(x_next.equals("'"))
                    x+=x_next;
                else
                    Stack.push(x_next);
                //��������Լ�������
                if(!V_N.contains(x))
                { //x���Ƿ��ս��
                    if(V_T.contains(x))
                    { //x���ս��
                        if(x.equals(a))
                        { //x=a
                            if(x.equals("#")) //x=# ����������ɹ�
                                break;
                            else
                            { //x!=#��ʼ��һ����
                                input=input.substring(1);
                                a=input.charAt(0)+""; //ȡһ�����������a
                            }
                        }
                        else break;
                    }
                    else break;
                }
                else //x�Ƿ��ս��
                {
                    //ȡ��x,a��λ��
                    int xIndedx=V_N.indexOf(x);
                    int aIndedx=V_T.indexOf(a);

                    if(AnalysisTable[xIndedx][aIndedx]!=null) {
                        //symbolStack.pop(); //ջ����ջ
                        str_right =AnalysisTable[xIndedx][aIndedx]; //str1=M[x,a]
                        //��ת�ַ���
                        String str_right_reverse=new StringBuffer(str_right).reverse().toString();
                        //������ʽ��A->�ŵ����,����
                        if(str_right_reverse.charAt(0)=='��')
                        {
                            step++;
                            continue;
                        }
                        //������ջ
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
        //��ȥ#���ָ�vt
        V_T.remove("#");

    }

    public static void main(String[] args) {
        //�����﷨
        Create_Grammer();
        Show_Grammer();

        //������ݹ�
        Left_Direct_Recursion();
        Change_Grammer();
        Show_Grammer();

        //�����ս���ͷ��ս������
        Create_VT_VN();
        Show_VN_VT();

        //�������Ƶ�ʽ����
        Grammar_New();
        Show_Grammar_New();

        //����First��
        Create_First_Map();
        Show_First_Map();

        //����Follow��
        Create_Follow_Map();
        Show_Follow_Map();

        //����������
        Create_AnalysisTable();
        Show_AnalysisTable();

        //��������
        Analyse();
    }
}
