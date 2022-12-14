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
#define Len 40		//方块单位长度
#define EASY 380	//简单速度
#define NORMAL 310	//普通速度
#define FAST 200	//困难速度

struct square		//方块结构体
{
	int location[4][4][2];	//（最多）四种旋转形态的四个点坐标
};

struct square shape[7] =	//7种方块
{
	{
		2,0 , 2,1 , 2,2 , 2,3,
		1,0 , 2,0 , 3,0 , 4,0,
		2,0 , 2,1 , 2,2 , 2,3,//一字型
		1,0 , 2,0 , 3,0 , 4,0
	},
	{
		0,0 , 0,1 , 1,0 , 1,1 ,
		0,0 , 0,1 , 1,0 , 1,1 ,
		0,0 , 0,1 , 1,0 , 1,1 ,//田字型
		0,0 , 0,1 , 1,0 , 1,1
	},
	{
		0,0 , 1,0 , 1,1 , 1,2,
		0,0 , 0,1 , 0,2 , 1,2,
		0,1 , 1,1 , 2,1 , 2,0,
		0,0 , 1,0 , 2,0 , 0,1//L型
	},
	{
		0,0 , 0,1 , 0,2 , 1,0,
		1,0 , 1,1 , 1,2 , 0,2,
		0,0 , 0,1 , 1,1 , 2,1,
		0,0 , 1,0 , 2,0 , 2,1//反L型
	},
	{
		1,0 , 0,1 , 1,1 , 0,2,
		0,1 , 1,1 , 1,2 , 2,2,
		1,0 , 0,1 , 1,1 , 0,2,//Z型
		0,1 , 1,1 , 1,2 , 2,2
	},
	{
		0,0 , 0,1 , 1,1 , 1,2,
		0,2 , 1,2 , 1,1 , 2,1,
		0,0 , 0,1 , 1,1 , 1,2,//反Z型
		0,2 , 1,2 , 1,1 , 2,1
	},
	{
		0,1 , 1,0 , 1,1 , 2,1,
		1,0 , 1,1 , 1,2 , 2,1,
		0,1 , 1,1 , 2,1 , 1,2,
		1,0 , 1,1 , 1,2 , 0,1//土字型
		
	}
};

void SetBeginPage();		//开始界面
void SetGameBackground();	//游戏背景
int  Begin();				//开始游戏
void SetDifficulty();		//设置难度
void SetText();				//设置提示
void setbkmode(int mode);	//设置透明
void Set_Data_PerRound();	//设置每一轮的数据
void Set_Data_PerTime();	//设置每一次下落的新方块的数据
void Draw_Square_Item(int x, int y);	//画方块的一个单位
void Draw_Square();			//画完整的方块
void Draw_Square_Next();	//画下一个方块
void Control();		//控制
void MoveLeft();	//左移
void MoveRight();	//右移
void MoveRound();	//旋转
void MoveDown();	//下移
void Pause();		//暂停
void Record_map_position();			//记录游戏区域方块位置
void Draw_Record_map_position();	//画已经落下的方块
void Check_Row();					//检查一排是否能清除
void Clear_Row(int i,int j);		//清除一排
void Down_Record_map_position(int i,int j);		//将清除后上方的方块整体下移
void Check_Over();		//检查游戏是否结束
int  End();				//结束
void SetEndPage();		//结束界面

int now_x ;	//现在方块横坐标
int now_y ;	//现在方块纵坐标
int square_shape_i_next ;	//下个方块
int square_shape_j_next ;	//下个方块的旋转形状
int square_shape_i_now;	//现在方块
int square_shape_j_now;	//现在方块的旋转形状
int time_now;	//现在的时间
int map_position[400][800];	//游戏区域方块位置数据
int flag_round=1;	//每一轮游戏的标志
int flag_next;	//每一次新方块下落的标志
int flag_again=1;	//重新游戏的标志
int score;	//得分
int SLEEP;	//下落速度


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
	settextstyle(100, 0, "宋体");
	char str1[] = "俄罗斯方块";
	char str2[] = "计科-5 吴柳航";
	char str3[] = "开始游戏";
	outtextxy(110, 150, str1);
	settextstyle(60, 30, "宋体");
	outtextxy(160, 300, str2);
	setfillcolor(RGB(142, 229,213));
	fillrectangle(150, 400, 550, 600);
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(80, 40, "楷体");
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
	char str1[] = "请选择难度";
	char str2[] = "简单";
	char str3[] = "普通";
	char str4[] = "困难";
	setbkmode(TRANSPARENT);
	settextcolor(WHITE);
	settextstyle(100, 0, "宋体");
	outtextxy(110, 40, str1);
	setfillcolor(RGB(249, 190, 183));
	fillrectangle(230, 220, 470, 320);
	settextstyle(70, 0, "楷体");
	outtextxy(280, 235, str2);
	setfillcolor(RGB(252, 120, 120));
	fillrectangle(230, 400, 470, 500);
	settextstyle(70, 0, "楷体");
	outtextxy(280, 415, str3);
	setfillcolor(RGB(254, 67, 60));
	fillrectangle(230, 580, 470, 680);
	settextstyle(70, 0, "楷体");
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
	sprintf(str, "得分 : %d", score);
	char str1[] = "向左移动 ←";
	char str2[] = "向右移动 →";
	char str3[] = "向下加速 ↓";
	char str4[] = "变换形状 ↑";
	char str5[] = "暂停 SPACE";
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 3);
	rectangle(430, 40, 670, 280);
	setbkmode(TRANSPARENT);
	settextcolor(RGB(255,48,48));
	settextstyle(50, 0, "楷体");
	outtextxy(420, 400, str);
	settextstyle(35, 0, "楷体");
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
		if (Check(now_x, now_y, (square_shape_j_now + i) % 4))//取余！防止形状数组越界！
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
		char str1[] = "暂停中";
		settextstyle(80, 0, "楷体");
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
	char str1[] = "游戏结束";
	char str2[] = "退出游戏";
	char str3[] = "再来亿次";
	char str4[80];
	settextstyle(80, 45, "宋体");
	outtextxy(180, 90, str1);//游戏结束
	setfillcolor(RGB(255, 120, 108));
	fillrectangle(15, 400, 325, 550);
	setfillcolor(RGB(255, 120, 108));
	fillrectangle(375, 400, 675, 550);
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(65, 0, "楷体");
	outtextxy(35, 440, str2);//退出游戏
	setbkmode(TRANSPARENT);
	setlinecolor(WHITE);
	setlinestyle(PS_SOLID | PS_ENDCAP_SQUARE, 5);
	settextstyle(65, 0, "楷体");
	outtextxy(395, 440, str3);//再来亿次
	settextstyle(65, 0, "楷体");
	sprintf(str4, "您的得分 ： %d", score);
	outtextxy(135, 220, str4);//您的得分
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