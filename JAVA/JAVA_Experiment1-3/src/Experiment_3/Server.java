package Experiment_3;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class MyServer_JFrame extends JFrame implements ActionListener
{
    JPanel jp;
    JLabel jl_Port,jl_Say;
    JButton jb_Start,jb_Say;
    JTextField jtf_Port,jtf_Say;
    JTextArea jta;
    Border border=BorderFactory.createTitledBorder("服务器设置: ");

    static ServerSocket serverSocket;
    static Socket server;
    int Port;

    public MyServer_JFrame()
    {
        super("服务器端");
        this.setBounds(200, 200, 600, 600);
        init();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void init()
    {
        jp=new JPanel();
        jp.setBorder(border);
        jl_Port=new JLabel("Port: ");
        jtf_Port=new JTextField(40);
        jb_Start=new JButton("Start");
        jta=new JTextArea(18,55);
        jta.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        jl_Say=new JLabel("Say:  ");
        jtf_Say=new JTextField(40);
        jb_Say=new JButton("Say ");

        jp.add(jl_Port);
        jp.add(jtf_Port);
        jp.add(jb_Start);
        jp.add(jta);
        jp.add(jl_Say);
        jp.add(jtf_Say);
        jp.add(jb_Say);
        setContentPane(jp);
        setLayout(new FlowLayout(FlowLayout.LEFT,15,40));

        jta.append("Please input Port First ! \n");

        jb_Start.addActionListener(this);
        jb_Say.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource()==jb_Start)
        {
            try{
                    Reads();        // server 接受 client 发送的消息
            }
            catch (IOException i) {System.out.println("Reads Failed"+i);}
        }
        else if(e.getSource()==jb_Say)
        {
            try
            {
                    Sends();        //server 向 client 发送消息
            }
            catch (IOException I){System.out.println("PrintWriter Failed"+I);}
        }
    }

    public void Reads() throws IOException
    {
        jta.append("Server is waiting …… \n");

        Port=Integer.parseInt(jtf_Port.getText());
        serverSocket=new ServerSocket(Port);
        server=serverSocket.accept();

        //开启多线程读取client发送的消息，防止TextArea中堵塞
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(true)
                    {
                        BufferedReader buff=new BufferedReader(new InputStreamReader(server.getInputStream()));
                        String s;
                        s=buff.readLine();
                        jta.append("Server reads in : "+s+"\n");
                    }
                }
                catch (IOException o){System.out.println("Buff Failed"+o);}
            }
        });
        thread.start();
    }

    public void Sends() throws IOException
    {
        PrintWriter pw=new PrintWriter(server.getOutputStream(),true);
        String s;
        s=jtf_Say.getText();
        pw.println(s);
        jta.append("Server sends out : "+s+"\n");
    }
}


public class Server {
    public static void main(String[] args) {
        MyServer_JFrame server =new MyServer_JFrame();
    }
}

