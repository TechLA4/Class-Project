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

    //�����ķ�
    public static void Create_Grammer()
    {
        //�̲�P90�ķ�
        Grammar.add("E->E+T|T");
        Grammar.add("T->T*F|F");
        Grammar.add("F->P��F|P");
        Grammar.add("P->(E)|i");
    }


    //չʾ�ķ�
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
                //��������ʽ��  |  ��
                if(char_test=='|'||char_test=='��')
                    continue;
                //����Ǵ�д��ĸ��˵���Ƿ��ս��
                if (char_test>='A'&&char_test<='Z')
                {
                    //���VN�����У�û��������ս��������ӽ�ȥ
                    if (!V_N.contains(char_test + ""))
                        V_N.add(char_test + "");
                }
                //����ǷǴ�д��ĸ��˵�����ս��
                else
                {
                    //���VT�����У�û������ս��������ӽ�ȥ
                    if (!V_T.contains(char_test + ""))
                        V_T.add(char_test + "");
                }
            }
        }
    }

    //VN��VT����չʾ����
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

    //�ж��Ƿ�������ķ�
    public static void Judge_Operator_Grammer()
    {
        for(int i=0;i< V_N.size();i++)
        {
            //��ȡÿһ��VN�Ĳ���ʽ�Ҳ������з���
            ArrayList<String> right=Grammar_New.get(V_N.get(i));
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //strΪÿһ������ʽ
                String str=right.get(j);
                //System.out.println(str);
                //��ÿһ������ʽ����VN�������VN���Ƿ�����һ��VN
                for(int p=0;p<str.length()-1;p++)
                {
                    //�������ʽ�Ҷ���VN
                    if(V_N.contains(str.charAt(p)+""))
                    {
                        //System.out.println("VN:"+str.charAt(p));
                        //System.out.println("VN_next:"+str.charAt(p+1));
                        //���VN���Ƿ�����һ��VN,��������ķ�
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


    //����FirstVT����
    public static void Create_FirstVT()
    {
        //��ʼ��F����
        for(int i=0;i< V_N.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                F[i][j]=false;
        }

        //F���鸳��ֵ
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //��ȡÿһ��VN�Ĳ���ʽ�Ҳ������з���
            ArrayList<String> right=Grammar_New.get(Vn);
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //strΪÿһ������ʽ
                String str=right.get(j);
                //System.out.println(str);
                //���չ���1�������ҵ�һ��VT
                //1.�����P->a�����
                if(V_T.contains((str.charAt(0)+"")))
                {
                    //��Ӧλ�õ�F����Ϊtrue
                    F[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(0)+"")]=true;
                    Stack_FirstVT.push(Vn+"->"+str.charAt(0));
                    //System.out.println(Vn+"->"+str.charAt(0));
                }
                //2.�����P->Qa�����
                else if(str.length()>1&&V_N.contains(str.charAt(0)+"")&&V_T.contains(str.charAt(1)+""))
                {
                    F[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(1)+"")]=true;
                    Stack_FirstVT.push(Vn+"->"+str.charAt(1));
                    //System.out.println(Vn+"->"+str.charAt(1));
                }
            }
        }

        //ջ����
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
                //����P=Q�����
                if(Vn.equals(Q))
                    continue;
                //��ȡÿһ��P�Ĳ���ʽ�Ҳ������з���
                ArrayList<String> right=Grammar_New.get(Vn);
                for(int j=0;j<right.size();j++)
                {
                    String P_Q=right.get(j).charAt(0)+"";
                    //���P->Q && Q->a
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

    //չʾFirstVT
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

    //����LastVT����
    public static void Create_Last_VT()
    {
        //��ʼ��L����
        for(int i=0;i< V_N.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                L[i][j]=false;
        }

        //L���鸳��ֵ
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //��ȡÿһ��VN�Ĳ���ʽ�Ҳ������з���
            ArrayList<String> right=Grammar_New.get(Vn);
            //System.out.println(right);
            for(int j=0;j<right.size();j++)
            {
                //strΪÿһ������ʽ
                String str=right.get(j);
                //System.out.println(str);
                //���չ���1�����������һ��VT
                //1.�����P->����a�����
                if(V_T.contains((str.charAt(str.length()-1)+"")))
                {
                    //��Ӧλ�õ�L����Ϊtrue
                    L[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(str.length()-1)+"")]=true;
                    Stack_LastVT.push(Vn+"->"+str.charAt(str.length()-1));
                    //System.out.println(Vn+"->"+str.charAt(str.length()-1));
                }
                //2.�����P->����aQ�����
                else if(str.length()>1&&V_N.contains(str.charAt(str.length()-1)+"")&&V_T.contains(str.charAt(str.length()-2)+""))
                {
                    L[V_N.indexOf(Vn)][V_T.indexOf(str.charAt(str.length()-2)+"")]=true;
                    Stack_LastVT.push(Vn+"->"+str.charAt(str.length()-2));
                    //System.out.println(Vn+"->"+str.charAt(str.length()-2));
                }
            }
        }

        //ջ����
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
                //����P=Q�����
                if (Vn.equals(Q))
                    continue;
                //��ȡÿһ��P�Ĳ���ʽ�Ҳ������з���
                ArrayList<String> right = Grammar_New.get(Vn);
                for (int j = 0; j < right.size(); j++) {
                    String P_Q = right.get(j).charAt(right.get(j).length() - 1) + "";

                    //���P->����Q && Q->a
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

    //����������ȹ�ϵ��
    public static void Create_Operator_Table()
    {
        //���#
        V_T.add("#");


        //��ʼ����ϵ��
        //=��������   <����С��   >��������   �ա���û�й�ϵ
        for(int i=0;i< V_T.size();i++)
        {
            for (int j=0;j< V_T.size();j++)
                Table[i][j]=" ";
        }

        //����ÿ������ʽ���з���,���
        for(int i=0;i< V_N.size();i++)
        {
            String Vn=V_N.get(i);
            //��ȡÿһ��VN�Ĳ���ʽ�Ҳ������з���
            ArrayList<String> right=Grammar_New.get(Vn);
            for(int j=0;j<right.size();j++)
            {
                //strΪ�����ÿһ������ʽ
                String str=right.get(j);
                //����1) P->����ab����   a=b
                for(int p=0;p<str.length()-1;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_T.contains(str.charAt(p+1)+""))
                        Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(str.charAt(p+1)+"")]="=";
                }

                //����1�� P->����aQb����   a=b
                for(int p=0;p<str.length()-2;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_N.contains(str.charAt(p+1)+"")&&V_T.contains(str.charAt(p+2)+""))
                        Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(str.charAt(p+2)+"")]="=";
                }

                //����2�� P->����aR������R->b  a<b
                for(int p=0;p<str.length()-1;p++)
                {
                    if(V_T.contains(str.charAt(p)+"")&&V_N.contains(str.charAt(p+1)+""))
                    {
                        ArrayList<String> firstvt_R=FirstVT.get(str.charAt(p+1)+"");
                        for(int q=0;q<firstvt_R.size();q++)
                            Table[V_T.indexOf(str.charAt(p)+"")][V_T.indexOf(firstvt_R.get(q)+"")]="<";
                    }
                }

                //����3��P->����Rb������R->a  a>b
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

        //��Ӷ�#�ŵĴ���
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

    //չʾ������ȹ�ϵ��
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

    //��������
    public static void Analysis(String String_Analysis)
    {
        //���봮ת��Arraylist
        for(int i=0;i<String_Analysis.length();i++)
            Stack_Input.add(String_Analysis.charAt(i)+"");

        //������ѹ��#
        Stack_Analysis.add("#");

        //ջ������
        String Top_Vt="";
        //�������
        String Read="Begin";
        //������ȷ�����ĺ�������
        int x,y=0;


        System.out.println("��������");
        System.out.printf("%-5s%-17s%-17s%-8s%-8s%-8s%-10s","����","����ջ","ʣ�����봮","ջ���ս����","��ǰ�������","���ȼ���ϵ","����");
        System.out.println();
        int step=1;
        while(Stack_Analysis.size()>3||Stack_Input.size()>0)
        {


            //�ҵ�ջ�����Vt:���
            for(int i=Stack_Analysis.size()-1;i>=0;i--)
            {
                if(V_T.contains(Stack_Analysis.get(i))){
                    //ȡ�����vt
                    Top_Vt=Stack_Analysis.get(i);
                    //System.out.println("TOP:"+Top_Vt);
                    break;
                }
            }


            //�������
            Read=Stack_Input.get(0);


            x=V_T.indexOf(Top_Vt);
            y=V_T.indexOf(Read);
            //�ҵ��������ŵ����ȼ���ϵ
            String Priority=Table[x][y];

            //���ȼ���ϵ:��   ->��Լʧ��
            if(Priority.equals(" "))
            {
                System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"��Լʧ�ܣ�");
                System.exit(0);
            }
            //���ȼ���ϵ:<   ->�ƽ�
            else if(Priority.equals("<"))
            {
                System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"�ƽ�"+Read);
                System.out.println();
                //��������ƽ�����ջ
                Stack_Analysis.add(Read);
                //���봮ɾ�����ַ�
                Stack_Input.remove(0);
            }
            //���ȼ���ϵ:>   ->��ʼ��Լ
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

                //Ѱ�������ض���
                for(int i=Stack_Analysis.size()-1;i>=0;i--)
                {
                    //�ҵ����
                    handle=str_stack_analysis.substring(i,str_stack_analysis.length());
                    //System.out.println("handle:"+handle);
                    //���������ֻ��һ��VN�������Ҫ�ҵ��ս��
                    if(handle.length()==1&&handle.charAt(0)>='A'&&handle.charAt(0)<='Z')
                        continue;
                    //�ҹ�Լ�Ĳ���ʽ
                    for(int j=0;j<V_N.size();j++)
                    {
                        String Vn=V_N.get(j);
                        //System.out.println("Vn:"+Vn);
                        //��ȡÿһ��VN�Ĳ���ʽ�Ҳ������з���
                        ArrayList<String> right=Grammar_New.get(Vn);
                        for(int p=0;p<right.size();p++)
                        {
                            //����ÿһ������ʽ
                            String str=right.get(p);
                            //System.out.println("str:"+str);

                            //��ʼ�ж�

                            //���ȳ������
                            if(handle.length()==str.length())
                            {
                                //��ÿһ���ַ����б����ж�
                                for(int k=0;k<handle.length();k++)
                                {
                                    //����Ͳ���ʽ���ַ�ͬΪVN�����ս��û��Ӱ��
                                    if(handle.charAt(k)>='A'&&handle.charAt(k)<='Z'&&str.charAt(k)>='A'&&str.charAt(k)<='Z')
                                    {
                                        /**/
                                        //���������ĩβ���������VN����ƥ����
                                        if(k==handle.length()-1)
                                        {
                                            //System.out.println("yse");
                                            flag=true;
                                            str_vn=Vn;
                                            str_vn_right=str;
                                        }


                                        continue;
                                    }

                                    //����Ͳ���ʽ���ַ�ͬΪVT����VT������ͬ,��ͬ������ƥ���������ʽ
                                    if(handle.charAt(k)!=str.charAt(k)){
                                        flag=false;
                                        break;
                                    }
                                    //���������ĩβ����ƥ����
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
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"��Լ"+str_vn+"->"+str_vn_right);
                    System.out.println();
                    //����ջ�������ض����ջ
                    for(int q=0;q<handle.length();q++)
                        Stack_Analysis.remove(Stack_Analysis.size()-1);
                    //����ʽ��VN��ջ
                    Stack_Analysis.add(str_vn);
                }
                else
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"��Լʧ��");
                    System.exit(0);
                }


            }
            //���ȼ���ϵ:=   (1)�ƽ�  (2)����
            else if(Priority.equals("="))
            {
                if(Top_Vt.equals(Read)&&Top_Vt.equals("#"))
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"����");
                    System.exit(0);
                }
                else
                {
                    System.out.printf("%-5s%-20s%-20s%-12s%-12s%-12s%-10s",step,Stack_Analysis,Stack_Input,Top_Vt,Read,Top_Vt+Priority+Read,"�ƽ�"+Read);
                    System.out.println();
                    //��������ƽ�����ջ
                    Stack_Analysis.add(Read);
                    //���봮ɾ�����ַ�
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















    //���غ���
    public static void main(String[] args) {
        //�����﷨
        Create_Grammer();
        Show_Grammer();

        //�����ս���ͷ��ս������
        Create_VT_VN();
        Show_VN_VT();

        //�������Ƶ�ʽ����
        Grammar_New();
        Show_Grammar_New();

        //�ж��Ƿ�������ķ�
        Judge_Operator_Grammer();

        //����FirstVT����
        Create_FirstVT();
        Show_First_VT();

        //����LastVT����
        Create_Last_VT();
        Show_Last_VT();

        //����������ȹ�ϵ��
        Create_Operator_Table();
        Show_Operator_Table();

        //��ʼ������ȷ���
        Begin_Analysis();

    }

}
