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
        //���봴���ؼ���k��ͷָ���s��
        creat_k();
        creat_s();
        creat_a();
        creat_r();



        //�������

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



    //�����봮�ķ�������
    public static void Analyse_string(String s){
        //ArrayList<String> ss=new ArrayList<>();
        String[] ss=new String[20];
        ss=s.split(" ");

        for(int i=0;i<ss.length;i++)
        {
            String test="";
            //�ж����ַ�
            char first=ss[i].charAt(0);
            //������ַ�����ĸ����û��error���������search�������йؼ��ֻ��ʶ�����ж�
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
                //������ֿ�ͷ��������ж�
                char a;
                //������֮����ַ������ֽ���������ʽƥ��
                boolean flag=ss[i].matches("[A-Z]*[a-z]+");
                //������ֺ���ƥ�䵽һ����ĸ����˵���Ǳ�ʶ��error
                if(flag)
                    System.out.println("error");
                //���û��ƥ�䣬��˵�������ִ�������search����
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


    //�Ե��ʵ���������
    public static void search(String s,String judge){
        String s1;
        char first=s.charAt(0);

        //������ַ�����ĸ����>���ҹؼ��֡���ʶ��
        if(judge.equals("alpha"))
        {
            //����ǹؼ���
            if(k.contains(s)==true){
                System.out.println(s+"�ؼ���");
            }
            else //������Ǳ�ʶ��
            {
                //�����ʶ�����ڡ���>ֱ�����
                if(id.contains(s)==true){
                    System.out.println(s+"��ʶ��");
                }
                else//�����ʶ�������ڡ���>��ӽ�id����
                {
                    id.add(s);
                }
            }
        }

        //������ַ������֡���>���ҳ���
        else if(judge.equals("number")){
            //������ִ��ڡ���>ֱ�����
            if(ci.contains(s)==true){
                System.out.println(s+"��ʶ��");
            }
            else//������ֲ����ڡ���>��ӽ�ci����
            {
                ci.add(s);
            }
        }
        //������ַ��������ַ�
        else{
            //�Ƿָ���
            if(s.contains(s)==true){
                System.out.println(s+"�ֽ��");
            }
            //�����������
            else if(a.contains(s)==true){
                System.out.println(s+"���������");
            }
            //�ǹ�ϵ�����
            else if(r.contains(s)==true){
                System.out.println(s+"��ϵ�����");
            }
            else{
                System.out.println("error");
            }
        }
    }


}

