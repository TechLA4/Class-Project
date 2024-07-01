#include<iostream>
#include <conio.h>
#include <graphics.h>
#include<stdlib.h>
#include <windows.h>
#include<time.h>
#include<math.h>
#include <MMSystem.h>
#pragma comment(lib,"winmm.lib")
using namespace std;
#define PI 3.1415626359
#define Len 40		//���鵥λ����
#define EASY 380	//���ٶ�
#define NORMAL 310	//��ͨ�ٶ�
#define FAST 200	//�����ٶ�

struct square		//����ṹ��
{
	int location[4][4][2];	//����ࣩ������ת��̬���ĸ�������
};

struct square shape[7] =	//7�ַ���
{
	{
		2,0 , 2,1 , 2,2 , 2,3,
		1,0 , 2,0 , 3,0 , 4,0,
		2,0 , 2,1 , 2,2 , 2,3,//һ����
		1,0 , 2,0 , 3,0 , 4,0
	},
	{
		0,0 , 0,1 , 1,0 , 1,1 ,
		0,0 , 0,1 , 1,0 , 1,1 ,
		0,0 , 0,1 , 1,0 , 1,1 ,//������
		0,0 , 0,1 , 1,0 , 1,1
	},
	{
		0,0 , 1,0 , 1,1 , 1,2,
		0,0 , 0,1 , 0,2 , 1,2,
		0,1 , 1,1 , 2,1 , 2,0,
		0,0 , 1,0 , 2,0 , 0,1//L��
	},
	{
		0,0 , 0,1 , 0,2 , 1,0,
		1,0 , 1,1 , 1,2 , 0,2,
		0,0 , 0,1 , 1,1 , 2,1,
		0,0 , 1,0 , 2,0 , 2,1//��L��
	},
	{
		1,0 , 0,1 , 1,1 , 0,2,
		0,1 , 1,1 , 1,2 , 2,2,
		1,0 , 0,1 , 1,1 , 0,2,//Z��
		0,1 , 1,1 , 1,2 , 2,2
	},
	{
		0,0 , 0,1 , 1,1 , 1,2,
		0,2 , 1,2 , 1,1 , 2,1,
		0,0 , 0,1 , 1,1 , 1,2,//��Z��
		0,2 , 1,2 , 1,1 , 2,1
	},
	{
		0,1 , 1,0 , 1,1 , 2,1,
		1,0 , 1,1 , 1,2 , 2,1,
		0,1 , 1,1 , 2,1 , 1,2,
		1,0 , 1,1 , 1,2 , 0,1//������
		
	}
};

void SetBeginPage();		//��ʼ����
void SetGameBackground();	//��Ϸ����
int  Begin();				//��ʼ��Ϸ
void SetDifficulty();		//�����Ѷ�
void SetText();				//������ʾ
void setbkmode(int mode);	//����͸��
void Set_Data_PerRound();	//����ÿһ�ֵ�����
void Set_Data_PerTime();	//����ÿһ��������·��������
void Draw_Square_Item(int x, int y);	//�������һ����λ
void Draw_Square();			//�������ķ���
void Draw_Square_Next();	//����һ������
void Control();		//����
void MoveLeft();	//����
void MoveRight();	//����
void MoveRound();	//��ת
void MoveDown();	//����
void Pause();		//��ͣ
void Record_map_position();			//��¼��Ϸ���򷽿�λ��
void Draw_Record_map_position();	//���Ѿ����µķ���
void Check_Row();					//���һ���Ƿ������
void Clear_Row(int i,int j);		//���һ��
void Down_Record_map_position(int i,int j);		//��������Ϸ��ķ�����������
void Check_Over();		//�����Ϸ�Ƿ����
int  End();				//����
void SetEndPage();		//��������

int now_x ;	//���ڷ��������
int now_y ;	//���ڷ���������
int square_shape_i_next ;	//�¸�����
int square_shape_j_next ;	//�¸��������ת��״
int square_shape_i_now;	//���ڷ���
int square_shape_j_now;	//���ڷ������ת��״
int time_now;	//���ڵ�ʱ��
int map_position[400][800];	//��Ϸ���򷽿�λ������
int flag_round=1;	//ÿһ����Ϸ�ı�־
int flag_next;	//ÿһ���·�������ı�־
int flag_again=1;	//������Ϸ�ı�־
int score;	//�÷�
int SLEEP;	//�����ٶ�


int main()
{
	
		while (flag_again)
		{
			
			SetBeginPage();
			Set_Data_PerRound();
			PlaySound("background.wav", NULL, SND_FILENAME | SND_ASYNC);
			while (flag_round)
			{
				
				Set_Data_PerTime();
				while (!flag_next)
				{
					
					BeginBatchDraw();
					SetGameBackground();
					Draw_Square();
					FlushBatchDraw();
					Draw_Record_map_position();
					Control();
					EndBatchDraw();
				}
				Check_Over();
			}
			SetEndPage();
			End();
		}
	_getch();
	closegraph();
	return 0;

}



void SetBeginPage()
{
	initgraph(700, 800);
	for (int i = 0; i < 800; i++)
	{
		setlinecolor(RGB(0, 255, 255 - i / 10));
		line(0, i, 700, i);
	}
	setbkmode(TRANSPARENT);
	settextcolor(WHITE);
	settextstyle(100, 0, "����");
	char str1[] = "����˹����";
	char str2[] = "�ƿ�-5 ������";
	char str3[] = "��ʼ��Ϸ";
	outtextxy(110, 150, str1);
	settextstyle(60, 30, "����");
	outtextxy(160, 300, str2);
	setfillcolor(RGB(142, 229,213));
	fillrectangle(150, 400, 550, 600);
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(80, 40, "����");
	outtextxy(195, 455, str3);
	Begin();
};
int  Begin()
{
	ExMessage m;
	do
	{
		m = getmessage(EM_MOUSE);
		if (m.message == WM_LBUTTONDOWN && m.x >= 150 && m.x <= 550 && m.y >= 400 && m.y <= 600)
		{
			cleardevice();
			SetDifficulty();
		}
	} while (!(m.message == WM_LBUTTONDOWN && m.x >= 150 && m.x <= 550 && m.y >= 400 && m.y <= 600));

	return 0;
}

void SetDifficulty()
{
	for (int i = 0; i < 800; i++)
	{
		setlinecolor(RGB(0, 255, 255 - i / 10));
		line(0, i, 700, i);
	}
	char str1[] = "��ѡ���Ѷ�";
	char str2[] = "��";
	char str3[] = "��ͨ";
	char str4[] = "����";
	setbkmode(TRANSPARENT);
	settextcolor(WHITE);
	settextstyle(100, 0, "����");
	outtextxy(110, 40, str1);
	setfillcolor(RGB(249, 190, 183));
	fillrectangle(230, 220, 470, 320);
	settextstyle(70, 0, "����");
	outtextxy(280, 235, str2);
	setfillcolor(RGB(252, 120, 120));
	fillrectangle(230, 400, 470, 500);
	settextstyle(70, 0, "����");
	outtextxy(280, 415, str3);
	setfillcolor(RGB(254, 67, 60));
	fillrectangle(230, 580, 470, 680);
	settextstyle(70, 0, "����");
	outtextxy(280, 595, str4);
	ExMessage m;
	while (1)
	{
		m = getmessage(EM_MOUSE);
		if (m.message == WM_LBUTTONDOWN && m.x >= 230 && m.x <= 470 && m.y >= 220 && m.y <= 320)
		{
			SLEEP = EASY;
			cleardevice();
			SetGameBackground();
			break;
		}
		else if (m.message == WM_LBUTTONDOWN && m.x >= 230 && m.x <= 470 && m.y >= 400 && m.y <= 500)
		{
			SLEEP = NORMAL;
			cleardevice();
			SetGameBackground();
			break;
		}
		else if (m.message == WM_LBUTTONDOWN && m.x >= 230 && m.x <= 470 && m.y >= 580 && m.y <= 680)
		{
			SLEEP = FAST;
		
			SetGameBackground();
			break;
		}
	}
}

void SetGameBackground()
{
	for (int i = 0; i < 800; i++)
	{
		setlinecolor(RGB(102, 175, 208 - i / 10));
		line(0, i, 400, i);
	}
	for (int i = 0; i < 800; i++)
	{
		setlinecolor(RGB(249, 205, 173 - i / 10));
		line(400, i, 700, i);
	}
	SetText();
}

void SetText()
{
	char str[80] ;
	sprintf(str, "�÷� : %d", score);
	char str1[] = "�����ƶ� ��";
	char str2[] = "�����ƶ� ��";
	char str3[] = "���¼��� ��";
	char str4[] = "�任��״ ��";
	char str5[] = "��ͣ SPACE";
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 3);
	rectangle(430, 40, 670, 280);
	setbkmode(TRANSPARENT);
	settextcolor(RGB(255,48,48));
	settextstyle(50, 0, "����");
	outtextxy(420, 400, str);
	settextstyle(35, 0, "����");
	outtextxy(460, 500, str1);
	outtextxy(460, 550, str2);
	outtextxy(460, 600, str3);
	outtextxy(460, 650, str4);
	outtextxy(460, 700, str5);
}

void Set_Data_PerRound()
{
	srand((unsigned)time(NULL));
	time_now = GetTickCount();
	flag_round = 1;
	flag_again = 0;
	square_shape_i_next = rand() % 7;
	square_shape_j_next = rand() % 4;
	score=0;
	
	for (int i = 0, j ; i < 400; i++)
	{
		for (j = 0; j < 800; j++)
		{
			map_position[i][j] = 0;
		}
	}
}
void Set_Data_PerTime()
{
	now_x = 120;
	now_y =-120;
	flag_next = 0;
	square_shape_i_now = square_shape_i_next;
	square_shape_j_now = square_shape_j_next;
	square_shape_i_next = rand() % 7;
	square_shape_j_next = rand() % 4;
}

void Draw_Square_Item(int x, int y)
{
	setlinecolor(WHITE);
	setfillcolor(RED);
	fillrectangle(x, y, x + Len, y + Len);
}

void Draw_Square()
{
	Draw_Square_Item(now_x + Len*(shape[square_shape_i_now].location[square_shape_j_now][0][0]), now_y + Len*(shape[square_shape_i_now].location[square_shape_j_now][0][1]));
	Draw_Square_Item(now_x + Len*(shape[square_shape_i_now].location[square_shape_j_now][1][0]), now_y + Len*(shape[square_shape_i_now].location[square_shape_j_now][1][1]));
	Draw_Square_Item(now_x + Len*(shape[square_shape_i_now].location[square_shape_j_now][2][0]), now_y + Len*(shape[square_shape_i_now].location[square_shape_j_now][2][1]));
	Draw_Square_Item(now_x + Len*(shape[square_shape_i_now].location[square_shape_j_now][3][0]), now_y + Len*(shape[square_shape_i_now].location[square_shape_j_now][3][1]));
	Draw_Square_Next();
}

void Draw_Square_Next()
{
	if (square_shape_i_next == 0&&(square_shape_j_next%2) )
	{
		Draw_Square_Item(435 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][0]), 145 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][1]));
		Draw_Square_Item(435 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][0]), 145 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][1]));
		Draw_Square_Item(435 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][0]), 145 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][1]));
		Draw_Square_Item(435 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][0]), 145 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][1]));
	}
	else if (square_shape_i_next == 0 && !(square_shape_j_next % 2))
	{
		Draw_Square_Item(450 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][0]), 80 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][1]));
		Draw_Square_Item(450 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][0]), 80 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][1]));
		Draw_Square_Item(450 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][0]), 80 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][1]));
		Draw_Square_Item(450 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][0]), 80 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][1]));
	}
	else
	{
	Draw_Square_Item(515 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][0]), 115 + Len * (shape[square_shape_i_next].location[square_shape_j_next][0][1]));
	Draw_Square_Item(515 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][0]), 115 + Len * (shape[square_shape_i_next].location[square_shape_j_next][1][1]));
	Draw_Square_Item(515 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][0]), 115 + Len * (shape[square_shape_i_next].location[square_shape_j_next][2][1]));
	Draw_Square_Item(515 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][0]), 115 + Len * (shape[square_shape_i_next].location[square_shape_j_next][3][1]));
	}
}

void Control()
{
	char c;
	int time_tmp = GetTickCount();
	if (time_tmp - time_now >= SLEEP)
	{
		time_now = time_tmp;
		MoveDown();
	}

	if(_kbhit())
	{
		c = _getch();
		switch (c)
		{
		case 75: MoveLeft(); break;
		case 77: MoveRight(); break;
		case 80: MoveDown(); break;
		case 72: MoveRound(); break;
		case 32:Pause(); break;
		}
	}
	Sleep(0);
}

bool Check(int now_x, int now_y, int square_shape_j_now)
{
	int check_x[4];
	int check_y[4];
	for (int i = 0; i < 4; i++)
	{
		check_x[i] = now_x + Len * (shape[square_shape_i_now].location[square_shape_j_now][i][0]);
		check_y[i] = now_y + Len * (shape[square_shape_i_now].location[square_shape_j_now][i][1]);
	}
	for (int i = 0; i < 4; i++)
	{
		if (check_x[i] < 0 || check_x[i]>400 - Len || check_y[i] > 800 - Len)
			return false;
		else if (map_position[check_x[i]][check_y[i]])
			return false;
	}
	return true;
}

void MoveLeft()
{
	if (Check(now_x-Len, now_y, square_shape_j_now))
		now_x -= Len;
}

void MoveRight()
{
	if (Check(now_x + Len, now_y, square_shape_j_now))
		now_x += Len;
}

void MoveRound()
{
	int i;
	for (i = 1; i < 4; i++)
	{
		if (Check(now_x, now_y, (square_shape_j_now + i) % 4))//ȡ�࣡��ֹ��״����Խ�磡
		{
			square_shape_j_now = (square_shape_j_now + i) % 4;
			break;
		}	
	}
}
  
void MoveDown()
{
	if (Check(now_x, now_y + Len, square_shape_j_now))
	{
		now_y += Len;
	}
	else
	{
		flag_next = 1;
		Record_map_position();
		Check_Row();
	}
}
void Pause()
{
	while (1)
	{
		settextcolor(BLUE);
		char str1[] = "��ͣ��";
		settextstyle(80, 0, "����");
		outtextxy(90, 20, str1);
		char c;
		if (_kbhit())
		{
			c = _getch();
			
			if (c == 32)
			break;
			}
	}
}

void Record_map_position()
{
	int judge_x[4];
	int judge_y[4];
	for (int i = 0; i < 4; i++)
	{
		judge_x[i] = now_x + Len * (shape[square_shape_i_now].location[square_shape_j_now][i][0]);
		judge_y[i] = now_y + Len * (shape[square_shape_i_now].location[square_shape_j_now][i][1]);
	}
	for (int i = 0; i < 4; i++)
	{
		map_position[judge_x[i]][judge_y[i]] = 1;
	}
}

void Draw_Record_map_position()
{
	BeginBatchDraw();
	for (int i = 0, j; i <400; i+=Len)
	{
		for (j = 0; j < 800; j+=Len)
		{
			if (map_position[i][j])
			{
				Draw_Square_Item(i,j);	
			}
		}
	}
	EndBatchDraw(); 
}

void Check_Row()
{
	int cnt = 0;
	int cnt_line = 0;
	int i, j;
	for (j = 0; j < 800; j += Len)
	{
		cnt = 0;
		for (i = 0; i < 400; i += Len)
		{
			if (map_position[i][j])
			{
				cnt++;
			}
			if (cnt == 10)
			{
				cnt_line++;
				Clear_Row(i,j);
				Down_Record_map_position(i,j);
			}
		}	
	}
	score+= cnt_line*cnt_line*10;
}

void Clear_Row(int i,int j)
{
	for ( i = 0; i <400; i += Len)
	{
		map_position[i][j] = 0;
	}
}

void Down_Record_map_position(int i,int j)
{
	int x, y;
	for ( y = j ; y >0; y -= Len)
	{
		for (x = 0; x <400; x += Len)
		{	
			map_position[x][y] = map_position[x][y - Len];

		}
	}
}

void Check_Over()
{
	int i;
	for (i = 0; i < 400; i += Len)
	{
		if (map_position[i][0])
		{
			flag_round = 0;
			break;
		}
	}
	
}

void SetEndPage()
{
	PlaySound("D:\\Tetris\\Tetris\\endbgm.wav", NULL, SND_FILENAME | SND_ASYNC);
	for (int i = 0; i < 800; i++)
	{
		setlinecolor(RGB(245 ,86 ,108- i / 10));   
		line(0, i, 700, i);
	}
	setbkmode(TRANSPARENT);
	settextcolor(WHITE);
	char str1[] = "��Ϸ����";
	char str2[] = "�˳���Ϸ";
	char str3[] = "�����ڴ�";
	char str4[80];
	settextstyle(80, 45, "����");
	outtextxy(180, 90, str1);//��Ϸ����
	setfillcolor(RGB(255, 120, 108));
	fillrectangle(15, 400, 325, 550);
	setfillcolor(RGB(255, 120, 108));
	fillrectangle(375, 400, 675, 550);
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(65, 0, "����");
	outtextxy(35, 440, str2);//�˳���Ϸ
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(65, 0, "����");
	outtextxy(395, 440, str3);//�����ڴ�
	settextstyle(65, 0, "����");
	sprintf(str4, "���ĵ÷� �� %d", score);
	outtextxy(135, 220, str4);//���ĵ÷�
}

int  End()
{
	ExMessage m;
	do
	{
		m = getmessage(EM_MOUSE);
		if (m.message == WM_LBUTTONDOWN && m.x >= 15 && m.x <= 325 && m.y >= 400 && m.y <= 550)
		{
			closegraph();
			return 0;
		}
		else if (m.message == WM_LBUTTONDOWN && m.x >= 375 && m.x <= 675 && m.y >= 400 && m.y <= 550)
		{
			flag_round = 1;
			flag_again = 1;
		}
	} while (!((m.message == WM_LBUTTONDOWN && m.x >= 15 && m.x <= 325 && m.y >= 400 && m.y <= 550)|| (m.message == WM_LBUTTONDOWN && m.x >= 375 && m.x <= 675 && m.y >= 400 && m.y <= 550)));

	return 0;
}