#ifndef KEYBOARDSTOP_H
#define KEYBOARDSTOP_H


#include <QWidget>
#include "tcpsocket.h"
#include <tcpserver.h>
#include <QApplication>
#include <QDesktopWidget>
#include <QTextEdit>


class KeyboardStop : public QWidget
{
    Q_OBJECT
public:
    explicit KeyboardStop(QWidget *parent = 0);

    // 开始监控服务器，然后返回新的端口号
    int startKeyboardStopServer(QString userName);

private:
    TcpServer *mServer;
    TcpSocket *mSock;
    QTextEdit *mEdit;    // 用来显示接收的键盘数据

signals:

public slots:
    // 有新客户连接
    void newConnection(QTcpSocket *s);

    // 处理新数据
    void processBuffer();

protected:
    // 大小重置
    void resizeEvent(QResizeEvent *);

    // 关闭
    void closeEvent(QCloseEvent *);
};


#endif // KEYBOARDSTOP_H
