package Experiment_2;

import java.io.*;
import java.util.Vector;

class Inventory     //创建Inventory类
{
    String Item_number;
    int Quantity;
    String Supplier;
    String Description;
}

class Transaction_A //创建A类
{
    String Tran_Item_Number;
    Integer Tran_Quantity;
    String Tran_Custom;
    String Tran_Description;
}

class Transaction_R //创建R类
{
    String Tran_Item_Number;
    Integer Tran_Quantity;
}

class Transaction_O //创建O类
{
    String Tran_Item_Number;
    Integer Tran_Quantity;
    String Tran_Custom;
}

class Transaction_D //创建D类
{
    String Tran_Item_Number;
    Integer Tran_Quantity;
}

public class EX_2 {
    public static void main(String[] args)throws Exception {
        Vector<Inventory> inventory=new Vector<>();             //使用Vector分别创造Inventory，A，R，O，D类的数组
        Vector<Transaction_A> transaction_a=new Vector<>();
        Vector<Transaction_R> transaction_r=new Vector<>();
        Vector<Transaction_O> transaction_o=new Vector<>();
        Vector<Transaction_D> transaction_d=new Vector<>();
        Vector<Transaction_O> shipping =new Vector<>();         //创建shipping类的数组方便对shipping中数据进行合并

        FileReader fileInventory=new FileReader("src\\Experiment_2\\Inventory.txt");
        BufferedReader buff_inventory=new BufferedReader(fileInventory);

        //读入Inventory.txt中内容，分别将其存放到对应容器中
        try
        {
            String s;
            s=buff_inventory.readLine();
            while(s!=null)
            {
                Inventory i=new Inventory();
                String []ss=s.split("\t");
                if(ss[0]!=null)
                {
                    i.Item_number=ss[0];
                    i.Quantity=Integer.parseInt(ss[1]);
                    i.Supplier=ss[2];
                    i.Description=ss[3];
                    inventory.add(i);
                }
                s=buff_inventory.readLine();
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        fileInventory.close();
        System.out.println("Inventory read Already");

        FileReader fileTransaction=new FileReader("src\\Experiment_2\\Transactions.txt");
        BufferedReader buff_tran=new BufferedReader(fileTransaction);

        //读入Transactions.txt中内容，分别将其存放到对应容器中
        try
        {
            String s;
            s=buff_tran.readLine();
            while(s!=null)
            {
                Transaction_A t_a=new Transaction_A();
                Transaction_R t_r=new Transaction_R();
                Transaction_O t_o=new Transaction_O();
                Transaction_D t_d=new Transaction_D();

                String []ss=s.split("\t");
                String type=ss[0];
                if(type!=null)
                {
                    switch (type)
                    {
                        case "O":
                            t_o.Tran_Item_Number=ss[1];
                            t_o.Tran_Quantity=Integer.parseInt(ss[2]);
                            t_o.Tran_Custom=ss[3];
                            transaction_o.add(t_o);
                            break;
                        case "D":
                            t_d.Tran_Item_Number=ss[1];
                            t_d.Tran_Quantity=-1;
                            transaction_d.add(t_d);
                            break;
                        case "A":
                            t_a.Tran_Item_Number=ss[1];
                            t_a.Tran_Quantity=0;
                            t_a.Tran_Custom=ss[2];
                            t_a.Tran_Description=ss[3];
                            transaction_a.add(t_a);
                            break;
                        case "R":
                            t_r.Tran_Item_Number=ss[1];
                            t_r.Tran_Quantity=Integer.parseInt(ss[2]);
                            transaction_r.add(t_r);
                            break;
                    }
                }
                s=buff_tran.readLine();
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
        fileTransaction.close();
        System.out.println("Transaction read Already");

        //新建NewInventory.txt
        File file_New_Inventory=new File("src\\Experiment_2\\NewInventory.txt");
        if(!file_New_Inventory.exists())
        {
            try {
                file_New_Inventory.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }

        //新建Errors.txt
        File file_Errors=new File("src\\Experiment_2\\Errors.txt");
        if(!file_Errors.exists())
        {
            try {
                file_Errors.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }
        //新建Shipping.txt
        File file_Shipping=new File("src\\Experiment_2\\Shipping.txt");
        if(!file_Shipping.exists())
        {
            try {
                file_Shipping.createNewFile();
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }

        //读入A的数据，将新增的货物种类写进New_Inventory
        A(transaction_a,file_New_Inventory);

        //读入R的数据，将添加的货单数量写进inventory
        R(transaction_r,inventory);

        //读入O的数据，将要发送的货物按照发送数量从小到大排序
        O_Sort(transaction_o);

        //读入O的数据，开始发送货物，成功就将发送信息写入shipping，失败就将失败信息写入errors
        O(transaction_o,inventory,file_Errors,shipping);

        //读入Shipping数据，将发送的同种货物数量信息进行合并
        Shipping_Combine(transaction_o);

        //读入inventory数据，删除货物信息,失败就将失败信息写入errors
        D(transaction_d,inventory,file_Errors);

        //读入Shipping数据,写入Shipping文件
        Write_Shipping(shipping,file_Shipping);

        //读入已经更改过的inventory数据，写入New_Inventory文件
        Write_New_Inventory(inventory,file_New_Inventory);

    }

    public static void A (Vector<Transaction_A> v,File f)throws Exception
    {
        //读入A的数据，将新增的货物种类写进New_Inventory
        FileWriter fileWriter=new FileWriter(f,true);
        for(int i=0;i<v.size();i++)
        {
            fileWriter.write(v.elementAt(i).Tran_Item_Number+"\t");
            fileWriter.write(v.elementAt(i).Tran_Quantity+"\t");
            fileWriter.write(v.elementAt(i).Tran_Custom+"\t");
            fileWriter.write(v.elementAt(i).Tran_Description+"\n");
        }
        fileWriter.close();
        System.out.println("A is Done");
    }

    public static void R (Vector<Transaction_R> v_t,Vector<Inventory> v_i)throws Exception
    {
        //读入R的数据，将添加的货单数量写进inventory
       for(int i=0;i<v_t.size();i++)
        {
            for(int j=0;j<v_i.size();j++)
            {
                if(v_t.elementAt(i).Tran_Item_Number.equals(v_i.elementAt(j).Item_number))
                {
                    v_i.elementAt(j).Quantity+=v_t.elementAt(i).Tran_Quantity;
                }
            }
        }
        System.out.println("R is Done");
    }

    public static void O_Sort(Vector<Transaction_O> v_o)
    {
        //读入O的数据，将要发送的货物按照发送数量从小到大排序
        Transaction_O m,n;
        for(int i=0;i<v_o.size();i++)
        {
            for(int j=i+1;j<v_o.size();j++)
            {
                if(v_o.elementAt(i).Tran_Quantity>v_o.elementAt(j).Tran_Quantity)
                {
                    m=v_o.elementAt(i);
                    n=v_o.elementAt(j);
                    v_o.remove(i);          //VectorelementAt（）为常量，不能赋值，所以使用两个变量，对交换的双方进行删除再添加的方法
                    v_o.add(i,n);
                    v_o.remove(j);
                    v_o.add(j,m);
                }
            }
        }
        System.out.println("O is sorted");
    }

    public static void O (Vector<Transaction_O> v_o,Vector<Inventory> v_i,File file_errors,Vector<Transaction_O> v_s)throws Exception
    {
        //读入O的数据，开始发送货物，成功就将发送信息写入shipping，失败就将失败信息写入errors
        FileWriter fileWriter=new FileWriter(file_errors,true);
        for(int i=0;i<v_o.size();i++)
        {
            for(int j=0;j<v_i.size();j++)
            {
                if(v_o.elementAt(i).Tran_Item_Number.equals(v_i.elementAt(j).Item_number))
                {
                    if(v_o.elementAt(i).Tran_Quantity<=v_i.elementAt(j).Quantity)
                    {
                        v_i.elementAt(j).Quantity-= v_o.elementAt(i).Tran_Quantity;
                        v_s.add(v_o.elementAt(i));
                    }
                    else
                    {
                        fileWriter.write("O Error:   "+v_i.elementAt(j).Supplier+"\t"+v_i.elementAt(j).Item_number+"\t"+v_i.elementAt(j).Quantity+"\n");
                    }
                }
            }
        }
        fileWriter.close();
        System.out.println("O is Done");
    }

    public static void D (Vector<Transaction_D> v_d,Vector<Inventory> v_i,File file_errors)throws Exception
    {
        //读入inventory数据，删除货物信息,失败就将失败信息写入errors
        FileWriter fileWriter=new FileWriter(file_errors,true);
        for(int i=0;i<v_d.size();i++)
        {
            for(int j=0;j<v_i.size();j++)
            {
                if(v_d.elementAt(i).Tran_Item_Number.equals(v_i.elementAt(j).Item_number))
                {
                    if(v_i.elementAt(j).Quantity==0)
                    {
                        v_i.remove(j);
                    }
                    else
                    {
                        fileWriter.write("D Error: "+v_i.elementAt(j).Quantity);
                    }
                }
            }
        }
        fileWriter.flush();
        fileWriter.close();
        System.out.println("D is Done");
    }

    public static void Shipping_Combine(Vector<Transaction_O> v)
    {
        for(int i=0;i<v.size();i++)
        {
            for(int j=i+1;j<v.size();j++)
            {
                if(v.elementAt(i).Tran_Item_Number.equals(v.elementAt(j).Tran_Item_Number))
                {
                    v.elementAt(i).Tran_Item_Number+=v.elementAt(j).Tran_Item_Number;
                    v.remove(j);
                }
            }
        }
        System.out.println("Shipping is combined");
    }

    public static void Write_Shipping(Vector<Transaction_O> s,File file)throws Exception
    {
        FileWriter fileWriter=new FileWriter(file,true);
        for(int i=0;i<s.size();i++)
        {
            fileWriter.write(s.elementAt(i).Tran_Item_Number+"\t");
            fileWriter.write(s.elementAt(i).Tran_Quantity+"\t");
            fileWriter.write(s.elementAt(i).Tran_Custom+"\n");
        }
        fileWriter.flush();
        fileWriter.close();
        System.out.println("Shipping is Done");
    }
    public static void Write_New_Inventory(Vector<Inventory> I,File file)throws Exception
    {
        FileWriter fileWriter=new FileWriter(file,true);
        for(int i=0;i<I.size();i++)
        {
            fileWriter.write(I.elementAt(i).Item_number+"\t");
            fileWriter.write(I.elementAt(i).Quantity+"\t");
            fileWriter.write(I.elementAt(i).Supplier+"\t");
            fileWriter.write(I.elementAt(i).Description+"\n");
        }
        fileWriter.flush();
        fileWriter.close();
        System.out.println("New Inventory is Done");
    }


}
   /* public static void ShowI(Vector<Inventory> in)
    {
        for(int i=0;i<in.size();i++)
        {
            System.out.println(in.elementAt(i).Item_number);
            System.out.println(in.elementAt(i).Quantity);
            System.out.println(in.elementAt(i).Supplier);
            System.out.println(in.elementAt(i).Description);
        }
    }
    public static void ShowA(Vector<Transaction_A> a)
    {
        for(int i=0;i<a.size();i++)
        {
            System.out.println(a.elementAt(i).Tran_Item_Number);
            System.out.println(a.elementAt(i).Tran_Quantity);
            System.out.println(a.elementAt(i).Tran_Custom);
            System.out.println(a.elementAt(i).Tran_Description);
        }
    }
    public static void ShowS(Vector<Transaction_O> s)
    {
        for(int i=0;i<s.size();i++)
        {
            System.out.println(s.elementAt(i).Tran_Item_Number);
            System.out.println(s.elementAt(i).Tran_Quantity);
            System.out.println(s.elementAt(i).Tran_Custom);
        }
    }
    public static void ShowD(Vector<Transaction_D> s)
    {
        System.out.println(s.size());
        for(int i=0;i<s.size();i++)
        {
            System.out.println(s.elementAt(i).Tran_Item_Number);
            System.out.println(s.elementAt(i).Tran_Quantity);
        }
    }
    public static void ShowO(Vector<Transaction_O> s)
    {
        for(int i=0;i<s.size();i++)
        {
            System.out.println(s.elementAt(i).Tran_Item_Number);
            System.out.println(s.elementAt(i).Tran_Quantity);
            System.out.println(s.elementAt(i).Tran_Custom);
        }
    }*/