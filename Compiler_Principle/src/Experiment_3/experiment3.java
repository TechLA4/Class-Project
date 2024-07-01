package Experiment_3;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class experiment3 {
    protected ArrayList<node> analy = new ArrayList<>();
    protected ArrayList<node_first> FirstSet = new ArrayList<>();//first集
    protected ArrayList<node2> CLOSURE = new ArrayList<>(); //项目的闭包
    protected List<Character> ter_copy = new ArrayList<>(); //去ε终结符
    protected List<Character> ter_colt = new ArrayList<>(); //终结符
    protected List<Character> non_colt = new ArrayList<>(); //非终结符
    protected Vector<Character> char_stack = new Vector<>();  //字符分析栈
    protected Vector<Character> sur_str = new Vector<>(); //剩余输入字符串
    protected Vector<Integer>   int_stack = new Vector<>(); //状态栈
    protected List<Character> colt = new ArrayList<>();    //去ε文法符集合
    protected char[][] Go;    //关系矩阵
    protected String[][] PTable; //分析表
    public static void main(String []arg) throws IOException{
    FileRead fr = new FileRead();
    List<String> list = new ArrayList<>();
    List<Character> list1 = new ArrayList<>();
    List<String> list2 = new ArrayList<>();
    list = fr.Read("src/Experiment_3/LR1.txt");
    Grammer_Handle analyse = new Grammer_Handle();
    analyse.Analyse(list);
    list1 = analyse.GetNon();
    //首先判断有无左递归
    if(analyse.Judge(list)){
        list2=analyse.Remove(list,list1);//如果有消除左递归
        analyse.Analyse(list2);//对新文法进行解析，获取终结符，非终结符
        analyse.Create_First();//创建新表的First集
        analyse.Restore();//去除左递归后
        analyse.Analyse(list);//再获取去除左递归后，新的终结符，非终结符
        analyse.Get_colt();//获取所有的文法符号集合，便于写GO函数
        analyse.Relation();//初始化关系关系矩阵
        analyse.Construc();//构造项目集
        analyse.Parser_Table();//构造分析表
        analyse.show();

        //输入
        System.out.println("Please input string :");
        Scanner scanner=new Scanner(System.in);
        String s=scanner.nextLine();
        //分析
        analyse.AnalyStack(s);
    }else {
        analyse.Create_First();
        analyse.Get_colt();
        analyse.Relation();
        analyse.Construc();
        analyse.Parser_Table();
        analyse.show();
        System.out.println("Please input string :");
        Scanner scanner=new Scanner(System.in);
        String s=scanner.nextLine();
        analyse.AnalyStack(s);
    }
}
}
//结构体
class node{          //用于方便查找产生式的左部与右部
    public char left;
    public String right;
    public node(char left,String right){
        this.left=left;
        this.right=right;
    }
}
class node_first{         //用于存放First集
    public List<Character> Se = new ArrayList<>();
   // public  node_first(){}
    public void add(char ch){
        this.Se.add(ch);
    }
}
class node2{         //用于存放每个项目集
    public List<String> St = new ArrayList<>();
    public void add(String str){this.St.add(str);}
}
//文法处理类
class Grammer_Handle extends experiment3{
    //确定非终结符
    public boolean IsNotsymbols(char ch) {
        if (ch >= 'A' && ch <= 'Z') return true;
        else return false;
    }
    //获取字符在非终结符表的下标
    public int Get_nindex(char temp) {
        for (int i = 0; i < non_colt.size(); i++) {
            if (temp == non_colt.get(i)) return i;
        }
        return -1;
    }
    //获取所有的文法符号集合，便于写GO函数
    public void Get_colt(){

        for(int i = 0;i<non_colt.size();i++){
            colt.add(non_colt.get(i));
        }
        for(int i = 0;i<ter_copy.size();i++){
            colt.add(ter_copy.get(i));
        }

    }
    //初始化关系关系矩阵
    public void Relation(){
        Go = new char[100][100];
        for(int i=0;i<100;i++)
            for(int j=0;j<100;j++){
            Go[i][j] = '!';
            }
    }
    //获取每个非终结符的First集
    public void Get_first(char temp) {
        int tag = 0;
        int flag = 0;
        for (int i = 0; i < analy.size(); i++) {
            if (analy.get(i).left == temp) {   //匹配产生式左部
                if (!IsNotsymbols(analy.get(i).right.charAt(0)))   //如果右部第一个字符是终结符，直接加入first集
                    FirstSet.get(Get_nindex(temp)).add(analy.get(i).right.charAt(0));
                else {
                    for (int j = 0; j < analy.get(i).right.length(); j++) {
                        if (!IsNotsymbols(analy.get(i).right.charAt(j)))  //直到寻找到终结符为止
                        {
                            FirstSet.get(Get_nindex(temp)).add(analy.get(i).right.charAt(j));
                            break;
                        }
                        Get_first(analy.get(i).right.charAt(j));  //右部递归寻找
                        Iterator it2 = FirstSet.get(Get_nindex(analy.get(i).right.charAt(j))).Se.iterator();
                        while (it2.hasNext()) {
                            char next = (char) it2.next();
                            if (next == 'ε') flag = 1;
                            else {
                                FirstSet.get(Get_nindex(temp)).add(next);  //将FIRST(Y)中的非ε就加入FIRST(X)
                            }
                        }
                        if (flag == 0) break;
                        else {
                            tag = tag + flag;
                            flag = 0;
                        }
                    }
                    if (tag == analy.get(i).right.length())
                        FirstSet.get(Get_nindex(temp)).add('ε');  //所有右部first(Y)都有ε,将ε加入FIRST(X)中
                }
            }
        }
    }
    //判定左递归
    public boolean Judge(List<String> list){
         ArrayList<node> analy = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            node aa = new node(list.get(i).charAt(0),list.get(i).substring(3));
            analy.add(aa);
        }
        for(int i=0;i<analy.size();i++){
            if(analy.get(i).left == analy.get(i).right.charAt(0)){   //当产生式的左部等于右部第一个符号，则具有左递归
                return true;
            }
        }
        return false;
    }
    //消除左递归
    public List<String> Remove(List<String> list1,List<Character> list2){
        List<String> temp1 = new ArrayList<>();  //保存消除了左递归的新的List
        ArrayList<node> analy = new ArrayList<>();
        List<Character> ch = new ArrayList<>();  //保存已经使用了的字母
        List<Character> List2 = list2;          //继承参数的内容（非终结符集）
        for (int i = 0; i < list1.size(); i++) {
            node aa = new node(list1.get(i).charAt(0),list1.get(i).substring(3));
            analy.add(aa);
        }
        for(int i=0;i<analy.size();i++){   //遍历所有产生式
            if(analy.get(i).left == analy.get(i).right.charAt(0)){   //当发现左部等于右部第一个符号
                ch.add(analy.get(i).left);                           //对该左部字母进行保存
                for(int j=0;j<analy.size();j++){                     //寻找该字母的另一个产生式，以便寻找P->βP'的β
                    if((analy.get(j).left == analy.get(i).left)){
                        if (analy.get(j).left != analy.get(j).right.charAt(0)){
                        for(char k='A';k<='Z';k++){                 //遍历字母集，寻找可以替代的字母
                            int flag =0;
                            Iterator it = List2.iterator();
                            while (it.hasNext()){
                                char t =(char)it.next();
                                if(k == t){
                                    flag = 1;                       //当该字母在原来的非终结符集有了，置flag=1
                                }
                            }
                            if(flag == 0) {                       //如果该字母没有使用过，就按照消除左递归的方法设定新的产生式
                                String str1 = analy.get(i).left + "->" + analy.get(j).right.charAt(0) + k;
                                String str2 = k + "->" + analy.get(i).right.substring(1) + k;
                                String str3 = k + "->" + 'ε';
                                List2.add(k);          //将该字母加入非终结符集合
                                temp1.add(str1);
                                temp1.add(str2);
                                temp1.add(str3);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
        for (int i =0;i<analy.size();i++){     //对于原来的产生式，将没有左递归的产生式加入新的List
            int flag =0;
            Iterator it = ch.iterator();
            while (it.hasNext()){                //如果原来产生式的左部在ch链表中，则证明该符号具有左递归，置flag=1
                char t =(char)it.next();
                if(analy.get(i).left == t){
                    flag = 1;
                }
            }
            if(flag == 0){                      //如果没有出现，加入新链表
                String str1 = analy.get(i).left+"->"+analy.get(i).right;
                temp1.add(str1);
            }
        }
        return temp1;
    }
    //对输入文法进行解析，获取全部终结符，非终结符
    public void Analyse(List<String> list) {
        analy.clear();      //当具有左递归时，所有的集合要进行清除
        non_colt.clear();
        ter_colt.clear();
        ter_copy.clear();
        for (int i = 0; i < list.size(); i++) {
            node aa = new node(list.get(i).charAt(0),list.get(i).substring(3));
            analy.add(aa);
        }
        for (int index = 0; index < list.size(); index++) {
            String temp = analy.get(index).left + analy.get(index).right;
            for (int i = 0; i < temp.length(); i++) {
                if (IsNotsymbols(temp.charAt(i))) { //如果是非终结符
                    int flag = 0;
                    for (int j = 0; j < non_colt.size(); j++) {   //遍历非终结符链表
                        if (non_colt.get(j) == temp.charAt(i))  //如果已经存在，将flag置为1
                        {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) non_colt.add(temp.charAt(i)); //如果不存在，添加
                } else {                            //如果是终结符
                    int flag = 0;
                    for (int j = 0; j < ter_colt.size(); j++) {
                        if (ter_colt.get(j) == temp.charAt(i)) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) ter_colt.add(temp.charAt(i));
                }
            }
        }
        ter_colt.add('#'); //终结符表最后添加“#”
        for (int i = 0; i<ter_colt.size(); i++)  //构造去ε终结符集合
        {
            if (ter_colt.get(i) != 'ε')
                ter_copy.add(ter_colt.get(i));
        }
    }
    //构造当前文法的First集
    public void Create_First() {
        for (int i = 0; i<non_colt.size(); i++)    //根据非终结符数量确定First集合链表的长度
        {   node_first bb =new node_first();
            FirstSet.add(bb);
        }
        for (int i=0;i<non_colt.size();i++){      //对于每一个非终结符求First集合
            Get_first(non_colt.get(i));
        }
        for (int i = 0; i < non_colt.size(); i++) {      //由于存放每个非终结符的First的是List链表，会有重复，所以采用Set容器，进行一个过滤
            Set<Character> s1 = new TreeSet<>();
            Iterator it = FirstSet.get(i).Se.iterator();
            while (it.hasNext()) {
                s1.add((char) it.next());
            }
            FirstSet.get(i).Se.clear();
            Iterator it1 = s1.iterator();
            while (it1.hasNext()) {
                char t = (char) it1.next();
                FirstSet.get(i).Se.add(t);
            }
        }
    }
    //供外部获取非终结符集合
    public List<Character> GetNon(){
        return non_colt;
    }
    //求CLOSURE函数
    public void Closure(String str) {
        Iterator it = CLOSURE.get(CLOSURE.size() - 1).St.iterator();
        int flag = 0;
        while (it.hasNext()) {                         //对于送进来的产生式进行检测，是否在当前项目集
            String s = String.valueOf(it.next());
            if (s.equals(str)) {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {        //如果不在，添加
                CLOSURE.get(CLOSURE.size() - 1).add(str);   //添加该产生式，并且寻找是否需要根据该产生式对当前项目集进行扩充
                int temp2 = CLOSURE.get(CLOSURE.size() - 1).St.get(CLOSURE.get(CLOSURE.size() - 1).St.size() - 1).indexOf('.');   //确定'.'的位置
                int temp3 = CLOSURE.get(CLOSURE.size() - 1).St.get(CLOSURE.get(CLOSURE.size() - 1).St.size() - 1).indexOf(',');   //确定','的位置
                char ch1 = CLOSURE.get(CLOSURE.size() - 1).St.get(CLOSURE.get(CLOSURE.size() - 1).St.size() - 1).charAt(temp2 + 1); //记录'.’后面的字符
                char ch2 = CLOSURE.get(CLOSURE.size() - 1).St.get(CLOSURE.get(CLOSURE.size() - 1).St.size() - 1).charAt(temp2 + 2); //记录'.'后面第二个的字符
                String temp4 = CLOSURE.get(CLOSURE.size() - 1).St.get(CLOSURE.get(CLOSURE.size() - 1).St.size() - 1).substring(temp3+1); //保存当前产生式的展望符
                if (IsNotsymbols(ch1)) {      //当点后面的是非终结符 A->α.B，a
                    if (ch2 == ',') {          //如果非终结符后面没有字符 A->α.B,a
                        for (int i = 0; i < analy.size(); i++) {
                            if (analy.get(i).left == ch1) {
                                String s1 = analy.get(i).left + "->" + '.' + analy.get(i).right + ',' + temp4;  //新的产生式的展望符为原产生式的展望符a
                                Closure(s1);
                            }
                        }
                    } else if (IsNotsymbols(ch2)) {      //当非终结符B后面为非终结符β  A->α.Bβ
                        int flag1 = 0;
                        Iterator it1 = FirstSet.get(Get_nindex(ch2)).Se.iterator();  //先检测β的First集有没有ε
                        while (it1.hasNext()) {
                            char c = (char) it1.next();
                            if (c == 'ε') {
                                flag1 = 1;
                                break;
                            }
                        }
                        if (flag1 == 0) {            //如果没有ε，对于每一个新发现产生式的展望符设定为first集
                            for (int i = 0; i < analy.size(); i++) {
                                if (analy.get(i).left == ch1) {
                                    Iterator it2 = FirstSet.get(Get_nindex(ch2)).Se.iterator();
                                    while (it2.hasNext()) {
                                        char c = (char) it2.next();
                                        String s1 = analy.get(i).left + "->" + '.' + analy.get(i).right + ',' + c;
                                        Closure(s1);
                                    }
                                }
                            }
                        }
                    }else if(!IsNotsymbols(ch2)){   //当非终结符后面是终结符A->αBb,   直接添加b当展望符
                        for (node node : analy) {
                            if (node.left == ch1) {
                                String s1 = node.left + "->" + '.' + node.right + ',' + ch2;
                                Closure(s1);
                            }
                        }
                      }
                }
            }
        }
//GO函数
 public void GO(node2 no,char X){
      Iterator it = no.St.iterator();
      while (it.hasNext()){//检测是否存在A->α.Xβ
            String te = String.valueOf(it.next());
            if(te.charAt(te.indexOf('.')+1) == X){    //如果存在
                  int flag = 0;
                  char[] cha = te.toCharArray();  //交换点的位置
                  char c =cha[te.indexOf('.')];
                  cha[te.indexOf('.')] = cha[te.indexOf('.')+1];
                  cha[te.indexOf('.')+1] = c;
                  String s1 = te.valueOf(cha);
                  for(int j=CLOSURE.size()-1;j>=0;j--){
                      Iterator it1=CLOSURE.get(j).St.iterator();
                      while (it1.hasNext()){
                          String te1 = String.valueOf(it1.next());
                          if(s1.equals(te1)){       //对于新的产生式，检测是否在其它项目集中已经存在
                              flag = 1;
                              Go[CLOSURE.indexOf(no)][j] = X;  //如果存在，则将当前项目集至含有该产生式的项目集的关系矩阵置为对应的X
                              break;
                          }
                      }
                      if(flag == 1) break;       //停止遍历
                  }
                  if(flag == 0){                //如果没有在其它项目集中存在
                          String s2 = te.substring(0,te.indexOf(','));
                          node2 aa = new node2();
                          CLOSURE.add(aa);
                          int in2 = CLOSURE.size()-1;
                          Go[CLOSURE.indexOf(no)][in2] = X;
                          Iterator tt = no.St.iterator();
                          while (tt.hasNext()){               //对该项目集中所有.后面字符为X的产生式
                              String s = String.valueOf(tt.next());
                              if(s2.charAt(s2.indexOf('.')+1) == s.charAt(s.indexOf('.')+1)) {
                                  char[] cha1 = s.toCharArray();  //交换点的位置
                                  char c1 =cha1[s.indexOf('.')];
                                  cha1[s.indexOf('.')] = cha1[s.indexOf('.')+1];
                                  cha1[s.indexOf('.')+1] = c1;
                                  String sss = s.valueOf(cha1);
                                  Closure(sss);
                              }
                          }
                  }
            }
      }
 }
//构造项目集
    public void Construc(){
     node2 bb =new node2();    //添加初始的初态产生式
     CLOSURE.add(bb);
     String st = 'Z'+"->"+'.'+non_colt.get(0)+','+'#';
     Closure(st);
       for(int i=0;i<CLOSURE.size();i++){    //CLOSURE.size()是动态变大的，当遍历到最后一个，则所有的项目集构造完毕//////
         for(int j=0;j<colt.size();j++){
            // System.out.println(i+" "+j+" 00");
             GO(CLOSURE.get(i),colt.get(j));
         }
       }
    }
    //构造分析表
    public void Parser_Table(){
        PTable =new String[100][100];
        for(int i=0;i<100;i++){
            for(int j=0;j<100;j++){
                PTable[i][j] = "!";               //初始化分析表
            }
        }
        for(int k =0;k<CLOSURE.size();k++){
            for(int h=0;h<colt.size();h++){
                if(IsNotsymbols(colt.get(h)))
                {         //对于非终结符
                    Iterator it = CLOSURE.get(k).St.iterator();
                    while (it.hasNext()){
                        String str = String.valueOf(it.next());
                        char ch = str.charAt(str.indexOf('.')+1);     //记录每个项目集的每个产生式.后面的字符
                        if(colt.get(h) == ch){                            //当.后面的而字符等于该字符
                            for(int j=0;j<CLOSURE.size();j++){        //遍历Go关系矩阵中以该项目集为起点的元素
                                if(Go[k][j] == ch){                   //当发现终点
                                    PTable[k][h] = String.valueOf(j); //令Goto = 终点
                                    break;
                                }
                            }
                            break;   //停止该项目集的产生式遍历
                        }
                    }
                }
                else
                {                                                   //当检测符号是非终结符
                    Iterator it = CLOSURE.get(k).St.iterator();
                    while (it.hasNext()){
                        String str = String.valueOf(it.next());
                        char ch1 = str.charAt(str.indexOf('.')+1);      //记录.后面的字符
                        char ch2 = str.charAt(str.indexOf('.')-1);      //记录.前面的字符
                        char ch3 = str.charAt(str.indexOf(',')+1);      //记录,后面的展望符
                        if((ch2 == colt.get(0)) && (colt.get(h) == '#') && (str.charAt(0) == 'Z')){       //当产生式为S'->S.,#，分析结束，令相应位置为Acc
                                String s ="Acc";
                                PTable[k][h] = s;
                                break;
                        }else {
                            if((ch1 == ',') && (ch3 == colt.get(h))){      //当产生式为A->α.,a，采用规约动作
                                for(int i=0;i<analy.size();i++){
                                    String s = str.substring(str.indexOf('>')+1,str.indexOf('.'));
                                    if((str.charAt(0) == analy.get(i).left) && (s.equals(analy.get(i).right))){   //对产生式进行遍历，寻找对应规约产生式
                                        String st = 'r'+String.valueOf(i+1);
                                        PTable[k][h] = st;
                                        break;
                                    }
                                }
                            }else {                                             //当产生式为A->α.aβ,b
                                if(ch1 == colt.get(h)){
                                    for(int j=0;j<CLOSURE.size();j++){
                                        if(Go[k][j] == ch1){                    //对关系矩阵遍历，寻找转化的状态
                                            String st = 's'+String.valueOf(j);
                                            PTable[k][h] = st;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    //格式化输出函数
    public String format(String s){
        String out = s;
        int i = s.length();
        while(i<=10){
            out += " ";
            i++;
        }
        return out;
    }
    //分析字符串函数
    public void AnalyStack(String s){
        int in = 0;
        int in1 = 0;
        int in2 =0;
        int in3 =0;
        for(int i=s.length()-1;i>=0;i--){
            sur_str.add(s.charAt(i));
        }
        char_stack.add('#');
        int_stack.add(0);
        System.out.print("步骤      状态栈    符号栈    剩余输入串    动作"+"\n");
        while (sur_str.size() > 0){
            System.out.print(format(String.valueOf(in)));
            String temp = "";
             for(int j=0;j<int_stack.size();j++){
                temp =temp+String.valueOf(int_stack.get(j))+"|";
            }
            System.out.print(format(temp));
            temp = "";
            for(int j=0;j<char_stack.size();j++){
                temp += char_stack.get(j);
            }
            System.out.print(format(temp));
            temp = "";
            for(int j=sur_str.size()-1;j>=0;j--){
                temp += sur_str.get(j);
            }
            System.out.print(format(temp));
            int in4 = int_stack.get(int_stack.size()-1);
            char ch = sur_str.get(sur_str.size()-1);
            for (int i=0;i<colt.size();i++){
                if(ch == colt.get(i)) in1 =i;
            }
            String st = PTable[in4][in1];
            if(st.charAt(0) == 's'){
                String tt = String.valueOf(st.charAt(1));
                int_stack.add(Integer.parseInt(tt));
                char_stack.add(sur_str.get(sur_str.size()-1));
                sur_str.remove(sur_str.size()-1);
                System.out.print(format("状态"+int_stack.get(int_stack.size()-1)+"入栈"));
            }
            if(st.charAt(0) == 'r'){
                String tt = String.valueOf(st.charAt(1));
               int in5 = Integer.parseInt(tt)-1;
               for(int i=0;i<analy.get(in5).right.length();i++){
                   char_stack.remove(char_stack.size()-1);
                   int_stack.remove(int_stack.size()-1);
               }
                char_stack.add(analy.get(in5).left);
                for (int i=0;i<colt.size();i++){
                    if(char_stack.get(char_stack.size()-1) == colt.get(i)){
                        in2 = i;
                    }
                }
                int_stack.add(Integer.parseInt(PTable[int_stack.get(int_stack.size()-1)][in2]));
                System.out.print(format(analy.get(in5).left+"->"+analy.get(in5).right+"规约"));
            }
            if(st.equals("!")) {
                System.out.print(format("Error！！！"));
                break;
            }
            for(int i=0;i<colt.size();i++){
                if(colt.get(i) == '#') in3 = i;
            }
            char cc = char_stack.get(char_stack.size()-1);
            char ss = sur_str.get(sur_str.size()-1);
            if(PTable[int_stack.get(int_stack.size()-1)][in3].equals("Acc") && (cc == non_colt.get(0)) && (ss == '#')){
                System.out.println();
                System.out.print(format(String.valueOf(in+1)));
                String temp1 = "";
                for(int j=0;j<int_stack.size();j++){
                    temp1 =temp1+String.valueOf(int_stack.get(j))+"|";
                }
                System.out.print(format(temp1));
                temp1 = "";
                for(int j=0;j<char_stack.size();j++){
                    temp1 += char_stack.get(j);
                }
                System.out.print(format(temp1));
                temp1 = "";
                for(int j=sur_str.size()-1;j>=0;j--){
                    temp1 += sur_str.get(j);
                }
                System.out.print(format(temp1)+format("Success"));
                break;
            }
            in +=1;
            System.out.println();
        }
    }
    public void Restore(){
        ArrayList<node_first> temp = new ArrayList<>();
        for(int i=0;i<FirstSet.size();i++){
            int flag = 0;
            Iterator it = FirstSet.get(i).Se.iterator();
            while (it.hasNext()){
                char ch = (char)it.next();
                if(ch == 'ε'){
                    flag = 1;
                    break;
                }
            }
            if(flag == 0){
                node_first aa = new node_first();
                temp.add(aa);
                Iterator it1 = FirstSet.get(i).Se.iterator();
                while (it1.hasNext()){
                    char ch = (char)it1.next();
                    temp.get(temp.size()-1).add(ch);
                }
            }
        }
        FirstSet.clear();
        System.out.println(temp.size());
        for (int i=0;i<temp.size();i++){
            node_first bb =new node_first();
            FirstSet.add(bb);
            Iterator it = temp.get(i).Se.iterator();
            while (it.hasNext()){
                char ch1 =(char)it.next();
                FirstSet.get(FirstSet.size()-1).Se.add(ch1);
            }
        }
    }
    //显示函数
    public void show(){
        //显示项目集
       for(int i=0;i<CLOSURE.size();i++){
           Iterator it = CLOSURE.get(i).St.iterator();
           while (it.hasNext()){
               String ss = String.valueOf(it.next());
               System.out.println(ss);
           }
           System.out.println();
       }
       //显示分析表
        System.out.println("\t"+"\t"+"Goto"+"\t"+"\t"+"Action");
        System.out.print("状态"+"\t"+"\t");
        for(int i=0;i<colt.size();i++){
            System.out.print(colt.get(i)+"\t");
        }
        System.out.println();
        for(int i=0;i<CLOSURE.size();i++){
            System.out.print(i+":"+"\t");
            for(int j=0;j<colt.size();j++){
                System.out.print("\t"+PTable[i][j]);
            }
            System.out.println();
        }
    }
}
//文件读取类
class FileRead {
    public List<String> Read(String filename) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String s;
        List<String> list = new ArrayList<>();
        while ((s = in.readLine()) != null) list.add(s);
        in.close();
        return list;
    }
}
