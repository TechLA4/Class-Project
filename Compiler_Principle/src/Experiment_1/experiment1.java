package Experiment_1;

import java.util.*;

public class experiment1 {
    static ArrayList<String> k=new ArrayList<String>();
    static ArrayList<String> s=new ArrayList<String>();
    static ArrayList<String> a=new ArrayList<String>();
    static ArrayList<String> r=new ArrayList<String>();
    static ArrayList<String> ci=new ArrayList<>();
    static ArrayList<String> id=new ArrayList<>();

    static int RowX=1; //��
    static int lieY=1; //��

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



    //����ʶ�𷽷�
    static void search(String str) {
        int i=0; //����String���׷���1Ϊ��ĸ��2Ϊ���֣�3Ϊ�����ָ���
        char ch=str.charAt(0);
        if ((ch>=65&&ch<=90)||(ch>=97&&ch<=122))
            i=1;
        else if(ch>=48&&ch<=57)
            i=2;
        else
            i=3;

        //��ʼ����ʶ��
        //������ַ�����ĸ����>���ҹؼ��֡���ʶ��
        if (i==1) {
            boolean find=k.contains(str);
            if(find==true) {
                //System.out.println(str+'\t'+'\t'+"(1,"+str+")"+'\t'+'\t'+"�ؼ���"+'\t'+'\t'+"("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(1,"+str+")","�ؼ���","("+RowX+","+lieY+")");
                System.out.println();
            }
            //������Ǳ�ʶ��
            else {
                find=id.contains(str);
                if(find==true) {
                    //System.out.println(str+"  (6,"+str+")  "+"��ʶ��"+"  ("+RowX+","+lieY+")");
                    System.out.printf("%-10s%-10s%-10s%-10s",str,"(6,"+str+")","��ʶ��","("+RowX+","+lieY+")");
                    System.out.println();
                }
                //�����ʶ�������ڡ���>��ӽ�id����
                else {
                    //System.out.println(str+"  (6,"+str+")  "+"��ʶ��"+"  ("+RowX+","+lieY+")");
                    System.out.printf("%-10s%-10s%-10s%-10s",str,"(6,"+str+")","��ʶ��","("+RowX+","+lieY+")");
                    System.out.println();
                    id.add(str);
                }
            }
        }
        //������ַ������֡���>���ҳ���
        else if(i==2) {
            boolean find=ci.contains(str);
            if(find==true) {
                //System.out.println(str+"  (5,"+str+")  "+"����"+"  ("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(5,"+str+")","����","("+RowX+","+lieY+")");
                System.out.println();
            }
            //������ֲ����ڡ���>��ӽ�ci����
            else {
                //System.out.println(str+"  (5,"+str+")  "+"����"+"  ("+RowX+","+lieY+")");
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(5,"+str+")","����","("+RowX+","+lieY+")");
                System.out.println();
                ci.add(str);
            }
        }
        //���������ַ�
        else {
            //�ָ���
            if(s.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(2,"+str+")","�ֽ��","("+RowX+","+lieY+")");
                System.out.println();
            }
            //���������
            else if(a.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(3,"+str+")","���������","("+RowX+","+lieY+")");
                System.out.println();
            }
            //��ϵ�����
            else if(r.contains(str)) {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"(4,"+str+")","��ϵ�����","("+RowX+","+lieY+")");
                System.out.println();
            }
            //�Ƿ��ַ�
            else {
                System.out.printf("%-10s%-10s%-10s%-10s",str,"Error","Error","("+RowX+","+lieY+")");
                System.out.println();
            }

        }

    }

    //�����봮�ķ�������
    static void analysisString(String str) {
        String strTest=""; //��ǰʶ��Ĵ�
        char char_first;
        for(int i=0;i<str.length();i++) {
            //�ж����ַ�
            char_first=str.charAt(i);
            if(char_first==' ')
                continue;
            //������ַ�����ĸ
            if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)) {
                strTest+=char_first;
                i++;
                //�Ժ�����ַ������жϣ������������ĸ�������֣�˵������ȷ�����룬��������
                do {
                    char_first=str.charAt(i);
                    if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)||(char_first>=48&&char_first<=57)) {
                        strTest+=char_first;
                        i++;
                    }
                    else
                        break;
                }while(i<str.length());
                //ָ�븴λ
                i--;
                search(strTest);
                strTest="";
                lieY++;
                continue;
            }

            else if(char_first>=48&&char_first<=57) { //ʶ������
                strTest+=char_first;
                i++;
                //���ֺ��������ĸ���������ֻ���С������ȫ���ӽ�test�ַ���
                while(i<str.length()){
                    char_first=str.charAt(i);
                    if((char_first>=65&&char_first<=90)||(char_first>=97&&char_first<=122)||(char_first>=48&&char_first<=57)||(char_first=='.')) {
                        strTest+=char_first;
                        i++;
                    }
                    else
                        break;
                }


                //ָ�븴λ
                i--;

                boolean flag=false;

                //���ж��Ƿ��Ǹ�����
                boolean char_float=false;
                for(int j=0;j<strTest.length();j++) {
                    char ch_test=strTest.charAt(j);
                    if(ch_test=='.')
                        char_float=true;
                    else
                        char_float=false;
                }

                //������Ǹ�����
                if(!char_float)
                {
                    //��test�ַ������ж����ֺ��Ƿ�����ĸ���У����Ϊ��ʶ��error
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
                //����Ǹ�����
                else
                {
                    char ch_test=strTest.charAt(0);
                    //��test�ַ������жϸ�����
                    int j=0;
                    //�ȱ�����С����. ��С����֮ǰ������ĸ�ж�
                    for(j=0;ch_test!='.';j++) {
                        ch_test = strTest.charAt(j);
                        //���һֱ�������֣�����ȷ
                        if (ch_test >= 48 && ch_test <= 57)
                            continue;
                            //��������˷����ִ�����error
                        else {
                            System.out.printf("%-10s%-10s%-10s%-10s", strTest, "Error", "Error", "(" + RowX + "," + lieY + ")");
                            System.out.println();
                            flag = true;
                            break;
                        }
                    }
                    //����С�������ַ������ж�
                    for(int q=j;q<strTest.length();q++)
                    {
                        char ch_test2=strTest.charAt(q);
                        //���һֱ�������֣�����ȷ
                        if(char_first>=48&&char_first<=57)
                            continue;
                            //��������˷����ִ�����error
                        else
                        {
                            System.out.printf("%-10s%-10s%-10s%-10s",strTest,"Error","Error","("+RowX+","+lieY+")");
                            System.out.println();
                            flag=true;
                            break;
                        }
                    }
                }



                //����Test�ַ���
                if(!flag)
                    search(strTest);
                strTest="";
                lieY++;
                continue;
            }

            //ʶ�������ַ�
            else {
                strTest+=char_first;

                //ʶ���Ƿָ���
                if(s.contains(strTest)) {
                    search(strTest);
                    strTest="";
                    lieY++;
                    continue;
                }
                //ʶ��Ϊ���������
                else if(a.contains(strTest)) { //ʶ��Ϊ���������
                    i++;
                    //�ж�����������Ƿ������������
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
                    i--; //��λ
                    strTest="";
                    lieY++;
                    continue;
                }
                //ʶ��Ϊ��ϵ�����
                else if(r.contains(strTest)) {
                    i++;
                    //�жϺ����Ƿ��ǹ�ϵ�����
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
                    i--; //��λ
                    strTest="";
                    lieY++;
                    continue;
                }
                //ʶ��Ƿ��ַ�
                else {
                    search(strTest);
                    strTest="";
                    lieY++;
                    continue;
                }
            }
        }
    }
    //������
    public static void main(String[] args) {
        //���봴���ؼ���k��ͷָ���s��
        creat_k();
        creat_s();
        creat_a();
        creat_r();


        System.out.println("Please input: ");

        Scanner scan=new Scanner(System.in);
        String  input=scan.nextLine();

        //һ��һ�еĶ�ȡ
        while(!input.equals("") && scan.hasNextLine())
        {
            //ɾ��ע��
            for(int i=0;i<input.length()-1;i++) {
                if(input.charAt(i)=='/'&&input.charAt(i+1)=='/') {
                    input=input.substring(0, i);
                    break;
                }
            }

            //���÷�������
            analysisString(input);
            RowX++; //�м�1
            lieY=1; //����1
            input=scan.nextLine();
        }
    }
}
