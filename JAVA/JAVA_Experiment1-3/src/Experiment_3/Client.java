package Experiment_3;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class MyClient_JFrame extends JFrame implements ActionListener
{
    JPanel jp;
    JLabel jl_IP,jl_Port,jl_Say;
    JButton jb_Connect,jb_Say;
    JTextField jtf_IP,jtf_Port,jtf_Say;
    JTextArea jta;
    Border border=BorderFactory.createTitledBorder("客户机设置: ");

    String IP;
    static Socket client;
    int Port;

    public MyClient_JFrame()
    {
        super("客户端");
        this.setBounds(200, 200, 600, 600);
        init();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void init()
    {
        jp=new JPanel();
        jp.setBorder(border);
        jl_IP=new JLabel("Server IP:");
        jtf_IP=new JTextField(15);
        jl_Port=new JLabel("Server Port:");
        jtf_Port=new JTextField(10);
        jb_Connect=new JButton("Connect");
        jta=new JTextArea(15,55);
        jta.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        jl_Say=new JLabel("Say:  ");
        jtf_Say=new JTextField(40);
        jb_Say=new JButton("Say ");

        jp.add(jl_IP);
        jp.add(jtf_IP);
        jp.add(jl_Port);
        jp.add(jtf_Port);
        jp.add(jb_Connect);
        jp.add(jta);
        jp.add(jl_Say);
        jp.add(jtf_Say);
        jp.add(jb_Say);
        setContentPane(jp);
        setLayout(new FlowLayout(FlowLayout.LEFT,15,40));

        jta.append("Please Input IP and Port First ! \n");

        jb_Connect.addActionListener(this);
        jb_Say.addActionListener(this);
    }


    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==jb_Connect)
        {
            try
            {
                Connect();      //client 与 server 连接
            }
            catch (IOException i) {System.out.println("Failed "+i);}
        }
        else if(e.getSource()==jb_Say)
        {
            try
            {
               Sends();         //client 向 server 发送消息
            }
            catch (IOException o) {System.out.println("Failed"+o);}
        }

        try{
            Reads();            //client 接受 server 发送的消息
        }
        catch (IOException O){System.out.println("Reads Failed"+O);}
    }

    public void Connect()throws IOException
    {
        jta.append("Connecting to server  …… \n");
        IP=jtf_IP.getText();
        Port=Integer.parseInt(jtf_Port.getText());
        client=new Socket(IP,Port);
        jta.append("Please Input : \n");
    }

    public void Sends() throws IOException
    {
        PrintWriter pw=new PrintWriter(client.getOutputStream(),true);//需要true来自动刷新缓存区
        String s;
        s=jtf_Say.getText();
        pw.println(s);
        jta.append("Client sends out : "+s+"\n");
    }


    public void Reads() throws IOException
    {
        //开启多线程读取server发送的消息，防止TextArea中堵塞
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true)
                    {
                        BufferedReader buff=new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String s;
                        s=buff.readLine();
                        jta.append("Server reads in : "+s+"\n");
                    }
                }
                catch (IOException IO){System.out.println("Buff Failed"+IO);}
            }
        });
        thread.start();
    }
}

public class Client {
    public static void main(String[] args) {
        MyClient_JFrame client=new MyClient_JFrame();
    }
}
