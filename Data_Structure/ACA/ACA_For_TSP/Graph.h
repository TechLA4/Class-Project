#include<stdlib.h>
#include<iostream>
#include <iomanip> 
#include<string>
#include<string.h>
#include<cmath>
#include<ctime>
#include <graphics.h>		
#include <conio.h>

using namespace std;

struct Vertex
{
	int x;
	int y;
};



//图的部分

#define INF 65533          //定义无穷大
#define MaxCity 100     //定义最多城市数
#define City 30
#define AntMax 50		//定义蚂蚁数量
#define LenMax 500		//定义最大路径
#define TimeMax 200     //定义迭代次数

//信息素重要性
#define message_alpha 5.0
//距离信息重要性
#define distance_beta 3.0
//信息素残留率
#define message_decline 0.98
//蚂蚁释放的信息素浓度
#define message_ant 200

typedef struct Map
{
	double Map_Distance[MaxCity + 1][MaxCity + 1];   //地图的距离数组
	double Map_Message[MaxCity + 1][MaxCity + 1];	 //地图的信息素数组
} Graph;



class Ant;
void showlength(int b[],Graph G,Ant a);
Ant final_length(Ant a[]);
Ant shortest_length(Ant a[]);

void showpath(int a[]);
void DrawVertex(Vertex V[]);
void DrawInitialEdge(Vertex V[]);
void DrawByMessage(Graph& G, Vertex V[], int times, Ant a);
//对图进行操作

void initial_distance(Graph& G,double Map[City][City])
{
	for (int i = 0; i < City; i++)
	{
		for (int j = 0; j < City; j++)
			G.Map_Distance[i][j] = Map[i][j];
	}
}

void Initial_distance(Graph& G)
{
	srand((unsigned)time(NULL));
	for (int i = 0; i < City; i++)
	{
		for (int j = 0; j < City; j++)
		{
			if (i == j)
				G.Map_Distance[i][j] = INF;
			else
			{
				int d= rand() % 100+1;
				double D = (double)d;
				G.Map_Distance[i][j] = D;
			}
			
		}
	}
}

void initial_message(Graph &G)
{
	for (int i = 0; i < City; i++)
	{
		for (int j = 0; j < City; j++)
			G.Map_Message[i][j] = 2;
	}
}

void show_graph(Graph& G)
{
	for (int i = 0; i < City; i++)
	{
		for (int j = 0; j < City; j++)
			cout << G.Map_Distance[i][j]<<" ";
		cout << endl;
	}
	cout << endl;
}



//蚂蚁类
class Ant
{
public:
	int startCity;
	int currentCity;
	bool visited[City];
	//TSP问题要回到出发的城市,所以路径是City+1
	int path[City+1];
	int city_cnt;
	double length;

	Ant(){}

	void initial()
	{
		//初始化走过的城市数量
		city_cnt=0;
		//初始化走过的路径长度
		length=0;

		//初始化蚂蚁访问标记
		for (int i = 0; i <City; i++)
		{
			visited[i] = false;
			path[i] = 1;
		}
		path[City] = 1;
			

		//随机设置出发城市

		//时间种子得用clock，因为一秒内有多次迭代，用time导致同一秒几代的所有蚂蚁的初始城市都相同
		srand(clock());
		startCity = rand() %City;
		Sleep(0.01);
		//标记出发城市
		visited[startCity] = 1;
		//出发城市加入路径
		path[city_cnt] = startCity;


		currentCity = startCity;
		//cout << "StartCity:" <<startCity<<endl;
	}

	int Ant_Choose(Graph G)
	{
		double message[City];			//每个城市所获取的信息素量
		double message_sum=0;			//所有城市信息素量总和
		double possiblity[City];		//每个城市的访问概率数组
		double possiblity_t=0;			//
		int nextcity=-1;
		
		
		//计算从当前城市到每个没有访问过的城市的信息素量
		for (int i = 0; i < City; i++)
		{
			if (!visited[i])
			{
				message[i] = pow(G.Map_Message[currentCity][i], message_alpha) * pow(1 / G.Map_Distance[currentCity][i], distance_beta);
				message_sum += message[i];
				//cout << "message "<<startCity<<" to "<< i << ": " << message[i]<<endl;
				//cout << "message_sum "<< message_sum <<endl;
			}
				
		}

		//计算从当前城市到每个没有访问过的城市的概率
		for (int i = 0; i < City; i++)
		{
			if (!visited[i])
				possiblity[i] = message[i] / message_sum;
			else
				possiblity[i] = 0;
			//cout << "possiblity: " << i <<" " << possiblity[i] << endl;
		}


		//求下一个城市概率，并非是选择可能性最大的那个城市，这样会导致迭代几次之后，所有的蚂蚁都会选择同样的路径，从而失去算法的随机性
		//为了避免失去随机性，使用轮盘赌法
		//圆盘根据各城市的概率划分区域，随机数停在哪块区域就去哪个城市
		//这样保证了概率大的容易选到，同时也保证了概率小的能被选到

		//srand(clock());
		int r = rand() % 100;
		r =r*5%100;//随机数乘5拉开随机数小数的间距
		double pos = (double)r/100;
		
		//cout << "random pos = " << pos << endl;

		for (int i = 0; i < City; i++)
		{
			if (!visited[i])
			{
				possiblity_t += message[i]/message_sum;
				//cout << "possiblity_t = " << possiblity_t << endl;
				//能去到的城市在总信息素的中的占比，现在得出的数据总和加一块不是1，导致了nextcity成为-1，使数组越界
				if (possiblity_t >= pos)
				{
					nextcity = i;
					break;
				}
					
			}
		}
		//cout << "nextcity: " << nextcity << endl;
		return nextcity;
	}

	void Ant_Move_Next_City(Graph &G)
	{
		int next;
		next=Ant_Choose(G);
		city_cnt++;
		//如果返回值值是-1，即所有城市概率都为0，即所有城市都被访问了
		/*
		if (next == -1)
			cout << "All City Travelled Already" << endl;
		*/

		//在未访问完城市时
		if (city_cnt < City)
		{
			//更新总路径长度
			length+=G.Map_Distance[currentCity][next];
			//局部更新信息素
			//G.Map_Message[currentCity][next]= G.Map_Message[next][currentCity] = G.Map_Message[currentCity][next]*message_decline+ message_ant / G.Map_Distance[currentCity][next];
			G.Map_Message[currentCity][next]= G.Map_Message[next][currentCity] += message_ant / G.Map_Distance[currentCity][next];
			//将下一个城市赋给现在的城市
			currentCity=next;
			//标记访问城市
			visited[currentCity]=true;
			//更新路径
			path[city_cnt]=currentCity;
		}	
		else
		{
			path[city_cnt]=startCity;
			length+=G.Map_Distance[currentCity][startCity];
			G.Map_Message[currentCity][startCity]=G.Map_Message[currentCity][next] += message_ant / G.Map_Distance[currentCity][startCity];
			G.Map_Message[next][currentCity] = G.Map_Message[currentCity][next];
		}
			
	}

	void start(Graph G)
	{
		initial();
		while(city_cnt<City)
		{
			Ant_Move_Next_City(G);
		}

		/*
		showpath(path);
		cout << "length: " << length << endl;
		*/
	}
};

class Ant_Group
{
private:
	Ant ant[AntMax];
	Ant time_final[TimeMax];
	Ant Shortest;

public:

	Ant_Group(){}

	void initial(Graph& G)
	{
		//最终蚂蚁路径初始化为MAX
		Shortest.length = LenMax;
		//每一代最优路径初始化为MAX
		for (int i = 0; i < TimeMax; i++)
			time_final[i].length=LenMax;
	}

	//更新全局信息素
	void Update_final_message(Graph& G,Ant a)
	{
		/**/
		//对每一轮的最优路径进行更新
		for (int i = 0; i < City; i++)
		{
			//cout << "path: " << i << " " << i - 1 << endl;
			//cout << a.path[i] << endl;
			int start = a.path[i];
			int end = a.path[i + 1];
			/*
			if (start == -858993460)
				continue;
			*/
			//cout << "start: " << start << " end: " << end << endl;
			
			G.Map_Message[start][end] = G.Map_Message[end][start] = G.Map_Message[start][end] + message_ant *(1.0 / a.length);
		}
		//G.Map_Message[a.path[City]][a.path[City+1]] = G.Map_Message[a.path[City + 1]][a.path[City]] = G.Map_Message[a.path[City]][a.path[City + 1]] + message_ant *(1.0 / a.length);
		
		//信息素挥发
		for (int i = 0; i < City; i++)
		{
			for (int j = 0; j < City; j++)
			{
				G.Map_Message[i][j] = G.Map_Message[j][i] = G.Map_Message[i][j] * message_decline;
			}
		}
			

	}

	void start(Graph& G,Vertex V[])
	{
		initial(G);
		double start = clock();
		for (int i = 0; i < TimeMax; i++)
		{
			double s = clock();
			cout <<endl<< "Time: " << i << endl;

			for (int j = 0; j < AntMax; j++)
			{
				//cout << "ant: " << j << " stated" << endl;

				ant[j].start(G);

				//cout << "ant: " << j << "  path:";
				//showpath(ant[j].path);
				//showlength(ant[j].path,G,ant[j]);
				//cout << endl<<endl;

				if (time_final[i].length >= ant[j].length)
					time_final[i] = ant[j];

			}
			double e = clock();
			cout << "Time " << i << " "; showpath(time_final[i].path);
			cout<<endl<< "length:" << time_final[i].length << endl;
			cout << "time: " << (double)(e - s) / CLOCKS_PER_SEC << " s"<< endl;


			Update_final_message(G,time_final[i]);
			
			 DrawByMessage(G, V, i,time_final[i]);
			 FlushBatchDraw();
			

			/*展示信息素
			
			cout<<endl<<"message Graph :"<<endl;
			cout<<setiosflags(ios::fixed)<<setprecision(1);
			
			for(int i=0;i<City;i++)
			{
				for(int j=0;j<City;j++)
					cout<<left<<setw(6)<<G.Map_Message[i][j]<<"  ";
				cout<<endl;
			}
			cout<<endl<<endl;
			*/
		}
		double end = clock();

			Shortest = shortest_length(time_final);
			cout <<endl<<endl << "Time: " << TimeMax << endl;
			cout << "Total Time: " << (double)( end- start) / CLOCKS_PER_SEC << " s" << endl;
			cout << "Shortest length during searching :" << Shortest.length << endl;
			cout << "Shortest "; showpath(Shortest.path); cout << endl<<endl;
	}

};



void showpath(int a[])
{
	cout << "path is: ";
	for (int i = 0; i<=City; i++)
		cout << a[i] << " ";
}
void showlength(int b[],Graph G,Ant a)
{
	cout<<"length: ";
	for(int i=1;i<=City;i++)
	{
		cout<<G.Map_Distance[a.path[i]][a.path[i-1]]<<" + ";
	}
	cout<<" = "<<a.length;
}
Ant shortest_length(Ant a[])
{
	Ant b;
	b.length=LenMax;
	for(int i=0;i<TimeMax;i++)
	{
		if(b.length>=a[i].length)
			b=a[i];
	}
	return b;
}



//绘图的部分
void DrawVertex(Vertex V[])
{
	for (int i = 0; i < City; i++)
	{
		setfillcolor(RED);
		int x = rand() % 600;
		int y = rand() % 800;
		V[i].x = x;
		V[i].y = y;
		fillcircle(x, y, 20);
	}
}

void DrawInitialEdge(Vertex V[])
{

	setlinestyle(PS_SOLID, 1);
	for (int i = 0; i < City; i++)
	{
		for (int j = i + 1; j < City; j++)
		{
			line(V[i].x, V[i].y, V[j].x, V[j].y);
		}
	}
}

void DrawByMessage(Graph& G,Vertex V[],int times,Ant a)
{
	 if(times <=TimeMax-2)
	{
		for (int i = 0; i < City; i++)
		{
			for (int j = i + 1; j < City; j++)
			{
				double width = (int)G.Map_Message[i][j];
				width = width / 1.5;
					setlinestyle(PS_SOLID, width);
				line(V[i].x, V[i].y, V[j].x, V[j].y);
			}
		}
	}
	else
	{
		cleardevice();
		for (int i = 0; i < City; i++)
		{
			setfillcolor(RED);
			fillcircle(V[i].x,V[i].y, 20);
		}
		setlinestyle(PS_SOLID, 7);
		for (int i = 0; i < City; i++)
		{
				line(V[a.path[i]].x, V[a.path[i]].y, V[a.path[i+1]].x, V[a.path[i+1]].y);
		}
	}
	
}
