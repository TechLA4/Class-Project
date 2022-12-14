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



//ͼ�Ĳ���

#define INF 65533          //���������
#define MaxCity 100     //������������
#define City 30
#define AntMax 50		//������������
#define LenMax 500		//�������·��
#define TimeMax 200     //�����������

//��Ϣ����Ҫ��
#define message_alpha 5.0
//������Ϣ��Ҫ��
#define distance_beta 3.0
//��Ϣ�ز�����
#define message_decline 0.98
//�����ͷŵ���Ϣ��Ũ��
#define message_ant 200

typedef struct Map
{
	double Map_Distance[MaxCity + 1][MaxCity + 1];   //��ͼ�ľ�������
	double Map_Message[MaxCity + 1][MaxCity + 1];	 //��ͼ����Ϣ������
} Graph;



class Ant;
void showlength(int b[],Graph G,Ant a);
Ant final_length(Ant a[]);
Ant shortest_length(Ant a[]);

void showpath(int a[]);
void DrawVertex(Vertex V[]);
void DrawInitialEdge(Vertex V[]);
void DrawByMessage(Graph& G, Vertex V[], int times, Ant a);
//��ͼ���в���

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



//������
class Ant
{
public:
	int startCity;
	int currentCity;
	bool visited[City];
	//TSP����Ҫ�ص������ĳ���,����·����City+1
	int path[City+1];
	int city_cnt;
	double length;

	Ant(){}

	void initial()
	{
		//��ʼ���߹��ĳ�������
		city_cnt=0;
		//��ʼ���߹���·������
		length=0;

		//��ʼ�����Ϸ��ʱ��
		for (int i = 0; i <City; i++)
		{
			visited[i] = false;
			path[i] = 1;
		}
		path[City] = 1;
			

		//������ó�������

		//ʱ�����ӵ���clock����Ϊһ�����ж�ε�������time����ͬһ�뼸�����������ϵĳ�ʼ���ж���ͬ
		srand(clock());
		startCity = rand() %City;
		Sleep(0.01);
		//��ǳ�������
		visited[startCity] = 1;
		//�������м���·��
		path[city_cnt] = startCity;


		currentCity = startCity;
		//cout << "StartCity:" <<startCity<<endl;
	}

	int Ant_Choose(Graph G)
	{
		double message[City];			//ÿ����������ȡ����Ϣ����
		double message_sum=0;			//���г�����Ϣ�����ܺ�
		double possiblity[City];		//ÿ�����еķ��ʸ�������
		double possiblity_t=0;			//
		int nextcity=-1;
		
		
		//����ӵ�ǰ���е�ÿ��û�з��ʹ��ĳ��е���Ϣ����
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

		//����ӵ�ǰ���е�ÿ��û�з��ʹ��ĳ��еĸ���
		for (int i = 0; i < City; i++)
		{
			if (!visited[i])
				possiblity[i] = message[i] / message_sum;
			else
				possiblity[i] = 0;
			//cout << "possiblity: " << i <<" " << possiblity[i] << endl;
		}


		//����һ�����и��ʣ�������ѡ������������Ǹ����У������ᵼ�µ�������֮�����е����϶���ѡ��ͬ����·�����Ӷ�ʧȥ�㷨�������
		//Ϊ�˱���ʧȥ����ԣ�ʹ�����̶ķ�
		//Բ�̸��ݸ����еĸ��ʻ������������ͣ���Ŀ������ȥ�ĸ�����
		//������֤�˸��ʴ������ѡ����ͬʱҲ��֤�˸���С���ܱ�ѡ��

		//srand(clock());
		int r = rand() % 100;
		r =r*5%100;//�������5���������С���ļ��
		double pos = (double)r/100;
		
		//cout << "random pos = " << pos << endl;

		for (int i = 0; i < City; i++)
		{
			if (!visited[i])
			{
				possiblity_t += message[i]/message_sum;
				//cout << "possiblity_t = " << possiblity_t << endl;
				//��ȥ���ĳ���������Ϣ�ص��е�ռ�ȣ����ڵó��������ܺͼ�һ�鲻��1��������nextcity��Ϊ-1��ʹ����Խ��
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
		//�������ֵֵ��-1�������г��и��ʶ�Ϊ0�������г��ж���������
		/*
		if (next == -1)
			cout << "All City Travelled Already" << endl;
		*/

		//��δ���������ʱ
		if (city_cnt < City)
		{
			//������·������
			length+=G.Map_Distance[currentCity][next];
			//�ֲ�������Ϣ��
			//G.Map_Message[currentCity][next]= G.Map_Message[next][currentCity] = G.Map_Message[currentCity][next]*message_decline+ message_ant / G.Map_Distance[currentCity][next];
			G.Map_Message[currentCity][next]= G.Map_Message[next][currentCity] += message_ant / G.Map_Distance[currentCity][next];
			//����һ�����и������ڵĳ���
			currentCity=next;
			//��Ƿ��ʳ���
			visited[currentCity]=true;
			//����·��
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
		//��������·����ʼ��ΪMAX
		Shortest.length = LenMax;
		//ÿһ������·����ʼ��ΪMAX
		for (int i = 0; i < TimeMax; i++)
			time_final[i].length=LenMax;
	}

	//����ȫ����Ϣ��
	void Update_final_message(Graph& G,Ant a)
	{
		/**/
		//��ÿһ�ֵ�����·�����и���
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
		
		//��Ϣ�ػӷ�
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
			

			/*չʾ��Ϣ��
			
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



//��ͼ�Ĳ���
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
