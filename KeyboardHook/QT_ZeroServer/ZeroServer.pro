#-------------------------------------------------
#
# Project created by QtCreator 2016-12-18T15:52:26
#
#-------------------------------------------------

QT       += core gui network

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

TARGET = ZeroServer
TEMPLATE = app


SOURCES += main.cpp\
    keyboardstop.cpp \
    keyboardtakeover.cpp \
        widget.cpp \
    tcpserver.cpp \
    tcpsocket.cpp \
    zeroserver.cpp \
    zeroclient.cpp \
    keyboardspy.cpp

HEADERS  += widget.h \
    keyboardstop.h \
    keyboardtakeover.h \
    tcpserver.h \
    tcpsocket.h \
    zeroserver.h \
    zeroclient.h \
    keyboardspy.h

FORMS    += widget.ui

RESOURCES += \
    resources.qrc

RC_FILE += winapp.rc
