//LR(1)���������н��棩

package Experiment_3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/*class project{
	String produceFormula_2; //����ʽ
	ArrayList<String> searchChar=new ArrayList<String>(); //��ǰ������
}*/

public class work3_1 {
	static ArrayList<String> produceFormula=new ArrayList<String>(Arrays.asList("E->E+T","E->T","T->T*F","T->F","F->(E)","F->i")); //����ʽ,%��ʾ��
	//static ArrayList<String> produceFormula=new ArrayList<String>(Arrays.asList("S->BB","B->aB","B->b"));
	static ArrayList<String> vt=new ArrayList<>(); //�ս����
	static ArrayList<String> vn=new ArrayList<>(); //���ս����
	static HashMap<String,ArrayList<String>> first=new HashMap<>(); //first��
	static HashMap<String,ArrayList<String>> produceFormula_1=new HashMap<>(); //����ʽ����һ����ʽ
	static String [][] action; //action��
	static String [][] GOTO; //goto��
	static String projectSet[][]=new String[50][50];//��Ŀ���壬ÿ�еĵ�һ��Ԫ�ش���Ŀ��
	static int ii=0;// prjectSet��������Ҳ������Ŀ����-1
	static String projectSetLink[][]=new String[50][50]; //��ʱ�����Ŀ��֮�����ӹ�ϵ
	static String stepStr=""; //�������������
	
	//����ʽ��ʽת��
	static void produceFormulaHandle() {
		//�ȹ������в���ʽ���Ҳ�Ϊ��
		for(int i=0;i<vn.size();i++) 
			produceFormula_1.put(vn.get(i),new ArrayList<String>());
		
		for(int i=0;i<produceFormula.size();i++) {
			String str=produceFormula.get(i);
			String key=str.substring(0,1); //ȡ������ʽ���
			ArrayList<String> valueTemp=produceFormula_1.get(key); //ȡ����Ӧkey��value
			str=str.substring(3); //��ȡ����ʽ�ұ�
			//�����ұߵ�ʽ�ӣ���|�ָ�
			String [] str1=str.split("\\|");
			Collections.addAll(valueTemp,str1);
			produceFormula_1.put(key,valueTemp); //������󣬷����ϣ����
		}
	}
	
	//��ӡ����ʽ
	static void printpro() {
		System.out.println("����ʽ");
		Iterator iter = produceFormula_1.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key1 = entry.getKey();
			Object value1 = entry.getValue();
			System.out.println(key1 + ":" + value1);
		}
	}
	
	//������ս�������ս����
	static void vtAndvn() {
		String str;
		char ch;
		for(int i=0;i<produceFormula.size();i++) {
			str=produceFormula.get(i);
			for(int j=0;j<str.length();j++) {
				ch=str.charAt(j);
				if(ch>='A'&&ch<='Z') {
					if(!vn.contains(ch+""))//������ս������û�оͼ���
						vn.add(ch+"");
				}
				else if(j!=1&&ch!='>'&&ch!='|'&&ch!='%') { //����->��|�Ϳմ���Ӱ��
					if(!vt.contains(ch+""))//����ս������û�оͼ���
						vt.add(ch+"");
				}
			}
		}
	}
	
	//��ӡvn��vt
	static void printVnAndVt() {
		System.out.println("vn");
		for(int i=0;i<vn.size();i++) {
			System.out.println(vn.get(i));
		}
		System.out.println("vt");
		for(int i=0;i<vt.size();i++) {
			System.out.println(vt.get(i));
		}
	}
	
	//����first��
	static void firstCreate(String str) {
		
		if(first.containsKey(str)) //�ж��Ƿ����str��first
			return;
		
		if(vt.contains(str)) { //�ս��
			ArrayList<String> arrayListTemp=new ArrayList<String>();
			Collections.addAll(arrayListTemp,str);
			first.put(str, arrayListTemp);
		}
		else { //���ս��
			ArrayList<String> firstValue=new ArrayList<String>(); //�������str��first��
			ArrayList<String> arrayListTemp=produceFormula_1.get(str); //ȡ��str�������Ҳ���ѡʽ
			for(int i=0;i<arrayListTemp.size();i++) { //�Է��ս��ÿ���Ҳ���ѡʽ����
				String str1=arrayListTemp.get(i);
				if(vt.contains(str1.substring(0, 1))||str1.charAt(0)=='%') //�����ѡʽ���ַ�Ϊ�ս�������ǿմ���ֱ�ӽ������first
					firstValue.add(str1.substring(0, 1));
				else { //�����ѡʽ���ַ�Ϊ���ս����������Ҷ�ÿһ���ַ������ж�
					int j;
					for(j=0;j<str1.length();j++) {
						String str2=str1.charAt(j)+"";
						if(str2.equals(str)) break;
						ArrayList<String> arrayListTemp1=produceFormula_1.get(str2); //ȡ��str2�������Ҳ���ѡʽ
						if(arrayListTemp1.contains("%")) //���str2�����Ƴ���
							continue;
						else {
							firstCreate(str2);
							ArrayList<String> arrayListTemp2=first.get(str2); //ȡ��str2��first��
							firstValue.addAll(arrayListTemp2); //��str2��first���ӵ�str��first��
							break;
						}
					}
					if(j==str1.length()) //��������Ƴ���
						firstValue.add("%"); //���ռ���
				}
			}
			first.put(str,firstValue); //����hashmap��
		}
	}
	
	//��ӡfirst��
	static void printFirst() {
		System.out.println("first");
    	Iterator iter = first.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			System.out.println(key + ":" + value);
		}
	}
	
	
	
	//Closure(I)
	static void closure(String projectset[]) {
		int j=0; //��Ŀ�������ڴ���ĵط�
		int jj=Integer.parseInt(projectset[0]); //ȡ����Ŀ������Ŀ�ĸ���
		
		if(jj==0) return; //���Ϊ0��ֱ�ӽ���
		
		while(true) { //������Ŀ��
			j++;
			String str1=projectset[j]; //ȡ��Ҫ����Ĵ�
			for(int n=0;n<str1.length()-1;n++) { //������
				if(str1.charAt(n)=='.'&&vn.contains(str1.charAt(n+1)+"")) { //�ҵ�������.vn�Ľṹ
					String str2=str1.substring(n+2); //ȡ���ṹ.vn..����Ĳ���
					str2=str2.replaceAll(",",""); //��ȥ,
					ArrayList<String> arraylist1=produceFormula_1.get(str1.charAt(n+1)+""); //ȡ��vn�Ĳ���ʽ�Ҳ�
					for(int m=0;m<arraylist1.size();m++) {
						
						ArrayList<String> arrayList2=first.get(str2.charAt(0)+"");
						for(int k=0;k<arrayList2.size();k++) {
							
							String temp=str1.charAt(n+1)+"->."+arraylist1.get(m)+","+arrayList2.get(k); //�γ���Ŀ
							boolean has=hasProject(projectset,jj,temp); //�ж���Ŀ�Ƿ����
							
							if(has==false) { //��Ŀ�����ڣ�����
								jj++;
								projectset[jj]=temp;
							}
								
						}
						
					}
				break;
				}
				
			}
			if(j==jj) { //�жϱհ��������
				String str3=projectset[j];//ȡ�����һ����
				boolean flag=false; //������־
				for(int n=0;n<str3.length();n++) {
					if(str3.charAt(n)=='.'&&!vn.contains(str3.charAt(n+1)+""))
						flag=true;
				}
				if(flag) break;
			}
		}
		
		projectset[0]=jj+"";
		
		//projectSet[i][jj[i]+1]="0";
	}
	
	//�ж���Ŀ�����Ƿ��Ѿ��������Ŀ
		static boolean hasProject(String projectset[],int jj,String temp) {
			for(int kk=1;kk<=jj;kk++) {
				if(temp.equals(projectset[kk]))
					return true;
			}
			return false;
		}	
	
	//goto����
	static String[] Goto(String projectset[],String x) {
		
		String projectsetTemp[]=new String[50]; //�����Ŀ��
		int num=0;// ��Ŀ������Ŀ������
		int jj=Integer.parseInt(projectset[0]);
		for(int m=1;m<=jj;m++) {
			
			String str2=projectset[m];
			int pointIndex=str2.indexOf('.');
			if((str2.charAt(pointIndex+1)+"").equals(x)) {
				str2=str2.substring(0,pointIndex)+x+"."+str2.substring(pointIndex+2);
				num++;
				projectsetTemp[num]=str2;
			}
			
		}
		projectsetTemp[0]=num+"";
		
		closure(projectsetTemp); //�հ�
		
		return projectsetTemp;
		
	}
	
	//�ж���Ŀ�������Ƿ��������Ŀ��
	static int isExistence(String projectset[]) {
		int a=Integer.parseInt(projectset[0]);
		int b;//
		String projectsetTemp[];
		for(int ip=0;ip<=ii;ip++) {
			projectsetTemp=projectSet[ip];
			b=Integer.parseInt(projectsetTemp[0]);
			if(a!=b) continue;
			int tag=0; //ÿ�ҵ�һ���ظ���+1
			for(int m=1;m<=a;m++) {
				for(int n=1;n<=b;n++) {
					if(projectset[m].equals(projectsetTemp[n])) {
						tag++;
						break;
					}
				}
			}
			if(tag==a) {
				return ip; //�ظ����ǵ�ip����Ŀ��
			}
			
		}
		return -1; //û���ظ�
	}
	
	//������Ŀ��
	static void projectSetCreate() {
		first.put("#", new ArrayList<String>(Arrays.asList("#"))); //��first�������<#,#>
		String str=vn.get(0); //ȡ�ÿ�ʼ��(�ķ���Ҫ�ع�,��A���濪ʼ��',)
		String str1="A->."+str+",#";
		projectSet[0][1]=str1;
		projectSet[0][0]="1";
		
		int i=0; //���ڴ������Ŀ��������
		closure(projectSet[0]); //����I0�հ�
		while(true) {
			
			for(int m=0;m<vn.size();m++) {
				String x=vn.get(m);
				String projectset[]=Goto(projectSet[i],x);
				int num=Integer.parseInt(projectset[0]);
				if(num==0) {
					continue;
				}
				else {
					int ip=isExistence(projectset);
					if(ip==-1) { //�������ظ�
						ii++;
						projectSet[ii]=projectset;
						projectSetLink[i][ii]=x;
					}
					else {
						projectSetLink[i][ip]=x;
					}
				}
				
			}
			
			for(int m=0;m<vt.size();m++) {
				String x=vt.get(m);
				String projectset[]=Goto(projectSet[i],x);
				int num=Integer.parseInt(projectset[0]);
				if(num==0) {
					continue;
				}
				else {
					int ip=isExistence(projectset);
					if(ip==-1) { //�������ظ�
						ii++;
						projectSet[ii]=projectset;
						projectSetLink[i][ii]=x;
					}
					else {
						projectSetLink[i][ip]=x;
					}
				}
				
			}
			
			if(i==ii) {
				break;
			}
			i++;
		}
		
	}
	
	
	
	
	
	//��ӡ��Ŀ��
	static void printProjectSet() {
		for(int i=0;i<=ii;i++) {
			System.out.println(i);
			int num=Integer.parseInt(projectSet[i][0]);
			for(int j=1;j<=num;j++) {
				if(j==10)
					System.out.println();
				System.out.print(projectSet[i][j]+" ");
			}
			System.out.println();
		}
			
	}
	
	//��ӡprojectSetLink
	static void printprojectSetLink() {
		for(int i=0;i<=ii;i++) {
			System.out.println();
			for(int j=0;j<=ii;j++) {
				System.out.print(projectSetLink[i][j]+'\t');
			}
		}
	}
	
	//���������
	static void createAnalysisTable() {
		vt.add("#"); //�ս���м���#
		action=new String[ii+1][vt.size()]; //��ʼ��action
		GOTO=new String[ii+1][vn.size()]; //��ʼ��goto
		
		int tag[]=new int[ii+1]; //��¼���״̬�ǲ���ĩβ״̬��1���ǣ�0��
		//����
		for(int i=0;i<=ii;i++) {
			for(int j=0;j<=ii;j++) {
				if(projectSetLink[i][j]!=null) {
					tag[i]=1;
					String str=projectSetLink[i][j];
					if(vn.contains(str)) { //���ս��
						GOTO[i][vn.indexOf(str)]=j+"";
					}
					else { //�ս��
						action[i][vt.indexOf(str)]="s"+j;
					}
				}
			}
		}
		
		//��Լ����
		for(int i=0;i<=ii;i++) {
			if(tag[i]==0) {
				String projectset[]=projectSet[i]; //ȡ��ĩβ��Ŀ��
				int num=Integer.parseInt(projectset[0]);
				for(int j=1;j<=num;j++) {
					String str=projectset[j];
					int pointIndex=str.indexOf('.');
					String strTail=str.substring(str.length()-1);
					str=str.substring(0,pointIndex);
					
					int produceIndex=produceFormula.indexOf(str)+1;
					action[i][vt.indexOf(strTail)]="r"+produceIndex;
				
				}
			}
			else {
				String projectset[]=projectSet[i]; //ȡ�����пɹ�Լ����Ŀ����Ŀ��������ȫ���ǣ�
				int num=Integer.parseInt(projectset[0]);
				for(int j=1;j<=num;j++) {
					String str=projectset[j];
					int pointIndex=str.indexOf('.');
					int douIndex=str.indexOf(',');
					if(douIndex==pointIndex+1) { //A->a.,?���ֽṹ
						if(str.charAt(0)=='A') {
							action[i][vt.indexOf("#")]="acc";
						}
						else{
							String strTail=str.substring(str.length()-1);
							str=str.substring(0,pointIndex);
							
							int produceIndex=produceFormula.indexOf(str)+1;
							action[i][vt.indexOf(strTail)]="r"+produceIndex;
						}
						
					}
				}
			}
		}
		
		//acc
		//action[1][vt.size()-1]="acc";
		
	}
	
	//��ӡ������
	static void printAnalysisTable() {
		System.out.println("ACTION"+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+'\t'+"GOTO");
		System.out.print("״̬"+'\t');
		for(int i=0;i<vt.size();i++)
			System.out.print(vt.get(i)+'\t');
		for(int i=0;i<vn.size();i++)
			System.out.print(vn.get(i)+'\t');
		
		System.out.println();
		for(int i=0;i<=ii;i++) {
			System.out.print(i+""+'\t');
			for(int j=0;j<vt.size();j++)
				System.out.print(action[i][j]+'\t');
			for(int j=0;j<vn.size();j++)
				System.out.print(GOTO[i][j]+'\t');
			System.out.println();
		}
		
	}
	//[start] userxxx
	//ʶ�����
	static void distinguish() {
		int step=0; //����
		Stack<Integer> stateStack=new Stack<Integer>(); //״̬ջ
		Stack<String> symbolStack=new Stack<String>(); //����ջ
		String inStr=work3Window.inputStr;//�����ַ���
		//String inStr="a";
		String a; //�������a
		int aIndex=0; //ָ�����봮��ͷ
		int s; //ջ��״̬s
		String actionStr=""; //����
		
		//�ж����봮�Ƿ�����
		for(int i=0;i<inStr.length();i++) {
			if(!vt.contains(inStr.charAt(i)+"")) {//���봮���в���vt�еķ���
				stepStr+="!-----���봮����-----!";
				return ;
			}
		}
		
		stateStack.push(0);
		symbolStack.push("#");
		
		stepStr+=("���봮:"+inStr+'\n');
		stepStr+=String.format("%-18s", "����")+String.format("%-18s", "״̬ջ")+String.format("%-18s", "����ջ")+String.format("%-18s", "���봮")+"����"+'\n';
		
		while(true) {
			step++;
			s=stateStack.peek();
			a=inStr.charAt(aIndex)+"";
			
			//��ȡջ�е�ֵ
			//״̬ջ
			String strStateTemp="";
			Stack<Integer> stackStateTemp=new Stack<Integer>();
			while(!stateStack.empty()) {
				
				stackStateTemp.push(stateStack.pop());
			}
			while(!stackStateTemp.empty()) { //�ָ�ջ
				int iState=stackStateTemp.pop();
				strStateTemp+=iState+" ";
				stateStack.push(iState);
			}
			//����ջ
			String strSymbolTemp="";
			while(!symbolStack.empty()) {
				strSymbolTemp+=symbolStack.pop();
			}
			String strSymbolTemp1=new StringBuffer(strSymbolTemp).reverse().toString(); //��ת�ַ���
			for(int i=0;i<strSymbolTemp1.length();i++){
				symbolStack.push(strSymbolTemp1.charAt(i)+""); //���»ָ�ջ
			}
			//���봮
			String inStrTemp=inStr.substring(aIndex);
			stepStr+=String.format("%-20s", (step+""))+String.format("%-20s", strStateTemp)+String.format("%-20s", strSymbolTemp1)+String.format("%-20s", inStrTemp);
			
			
			String acStr=action[s][vt.indexOf(a)];
			if(acStr!=null) { //�������action
				if(acStr.charAt(0)=='s') { //acStr=s��
					stateStack.push(Integer.parseInt(acStr.substring(1))); //״̬��ջ
					symbolStack.push(a); //���������ջ
					actionStr="ACTION["+s+","+a+"]="+acStr+",״̬"+Integer.parseInt(acStr.substring(1))+"��ջ";
					aIndex++;
				}
				else if(acStr.charAt(0)=='r') { //acStr=r?
					int produceIndex=Integer.parseInt(acStr.substring(1)); //��ȡ����ʽ������
					String produce=produceFormula.get(produceIndex-1); //����ʽ
					String produceR=produce.substring(3); //����ʽ�Ҳ�
					String produceL=produce.substring(0,1); //����ʽ��
					int R_Length=produceR.length(); //����ʽ�Ҳ��ĳ���
					//��ջ
					for(int i=0;i<R_Length;i++) {
						stateStack.pop();
						symbolStack.pop();
					}
					
					s=stateStack.peek(); //״̬ջͷ
					String goStr=GOTO[s][vn.indexOf(produceL)]; //ȡ��goto=?
					stateStack.push(Integer.parseInt(goStr)); //?��ջ
					symbolStack.push(produceL); //��Լ�󣬲���ʽ����ջ
					actionStr=acStr+":"+produce+"��Լ,GOTO("+s+","+produceL+")="+goStr+"��ջ";
				}
				else if(acStr.equals("acc")){ //acStr=acc,����
					actionStr="ACC,�����ɹ�";
					stepStr+=actionStr+'\n';
					break;
				}
				else { //����
					actionStr="error";
					stepStr+=actionStr+'\n';
					break;
				}
			}
			else { //����
				actionStr="û�ж�Ӧ��action----error------";
				stepStr+=actionStr+'\n';
				break;
			}
			
			stepStr+=actionStr+'\n';
		}
		//System.out.println(stepStr);
	}
	//[end]
	
	
	
    public static void main(String[] args) {
    	
    	vtAndvn();//����vn��vt
    	//printVnAndVt();
    	
    	produceFormulaHandle(); 
    	//printpro();
    	
    	//����first��
    	for(int i=0;i<vn.size();i++)
    		firstCreate(vn.get(i));
    	for(int i=0;i<vt.size();i++)
    		firstCreate(vt.get(i));
    	//printFirst();
    	
    	projectSetCreate();
    	//printProjectSet();
    	//printprojectSetLink();
    	
    	//������
    	createAnalysisTable();
    	printAnalysisTable();

    	//���ô���
    	new work3Window();
    	//distinguish();
    	
    }
}

//����
class work3Window extends JFrame implements ActionListener{

	JButton action;
	JTextField inputText;
	JTextArea outputText;
	static String inputStr=null;
	
	public work3Window() {
		action=new JButton("����");
		action.setFont(new Font("����", Font.PLAIN, 20));
		inputText=new JTextField(50); //�����
		inputText.setFont(new Font("����", Font.PLAIN, 20));
		inputText.setText("i+i*i#");
		outputText=new JTextArea(); //��ʾ��
		outputText.setFont(new Font("����", Font.PLAIN, 20));
		outputText.setEditable(false);
		
		JPanel pNorth=new JPanel();
		pNorth.add(inputText);
		
		JPanel pSouth=new JPanel();
		pSouth.add(action);
		
		add(pNorth,BorderLayout.NORTH);
		add(new JScrollPane(outputText),BorderLayout.CENTER);
		add(pSouth,BorderLayout.SOUTH);
		
		action.addActionListener(this);
		setBounds(200,200,1200,800);
		setVisible(true);
		setTitle("LR(1)����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==action) {
			inputStr=inputText.getText();//ȡ�����봮
			if(inputStr.length()>1)
				work3_1.distinguish();//���÷�������
			
			outputText.setText(work3_1.stepStr); //���
			work3_1.stepStr="";
			//outputText.append(work2_temp.stepStr);
		}
		
	}
	
}
