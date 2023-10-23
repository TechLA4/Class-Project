#include "stdio.h"

//设置K端口的LED灯
#define GPKCON0 (*(volatile unsigned long *)0x7F008800)
#define GPKDATA (*(volatile unsigned long *)0x7F008808)

//设置F端口的蜂鸣器
#define GPFCON 				(*((volatile unsigned long *)0x7F0080A0))
#define GPFDAT				(*((volatile unsigned long *)0x7F0080A4))

//设置N端口的按键输入
#define GPNCON (*(volatile unsigned long *)0x7F008830)
#define GPNDAT (*(volatile unsigned long *)0x7F008834)

#define 		WAIT		0
#define		SET_PWD	1
#define		PWD_RIGHT 	2
#define		PWD_ERROR	3

void timer_init(unsigned long utimer,unsigned long uprescaler,unsigned long udivider,unsigned long utcntb,unsigned long utcmpb,int key_temp);

void buzzer_init()
{
	//将GPF14蜂鸣器配置为输出口(s3c6410手册中将GPFCON的29-28位设置成01)
	GPFCON |= 1<<28;
	GPFCON &= ~(1<<29);
}

void buzzer_on()
{    
	//将GPFDAT第14位置1
    GPFDAT |= 1<<14;
}

void buzzer_off()
{
	//将GPFDAT第14位置0
    GPFDAT &= ~(1<<14);
}

int raise(int a){
	return 0;
}

void Sleep(volatile unsigned int cnt)
{
	volatile unsigned int x = cnt;
	volatile unsigned int n;
	while(x--)
	{
		n = 0x7ff;
		while(n--){}
	}
		
}


int main()
{
	//timer_init(0,65,4,62500,0);

	// 配置GPK4-7为输出功能
    	GPKCON0 = 0x11110000;

	//设置LED全灭
	GPKDATA=0x000000f0;

	// 配置GPN为输入功能
    	GPNCON = 0;

	//蜂鸣器初始化
	buzzer_init();

	//保存设置密码，个位,十位
	int pwd_k1=0;
	int pwd_k2=0;

	//保存输入密码，个位，十位
	int key_k1=0;
	int key_k2=0;

	//设置状态标记，分辨是 设置密码，还是输入密码
	int flag_init_pwd = SET_PWD;

	
	//轮询式查询按键事件
	while (1)
	{
		//GPNDAT 哪一位按下哪一位是0,于是与1<<i位进行比较，结果为0，则为按下

		
		//K1被按下
		if(!(GPNDAT&(1<<0)))
		{

			//设置K1计数器，个位
			int cnt_k1=0;

			//首先延时15
			Sleep(15);
			//如果延时15后仍是K1被按下，就确定是K1按下
			if(!(GPNDAT&(1<<0)))
				cnt_k1=(cnt_k1+1)%10;//计数器+1%10,防止超出个位

			//空转，等待K1按键被抬起，防止K1按键一直被按下，cnt一直在加一
			while (!(GPNDAT&(1<<0)))
			{
				//如果K1按键一直被按下，则一直在死循环中
				//只有K1按键抬起，才能跳出循环，进行下一个间隔判断下一次按下事件
			}

			//抬起消抖
			Sleep(200);

			int i;
			//接着设置定时器，在1500000的间隔内判断是否是有连续按下K1事件
			for(i=1500000;i>0;i--)
			{
				//如果发现在规定的间隔时间内有K1被按下的事件
				if(!(GPNDAT&(1<<0)))
				{
					//延时15
					Sleep(15);
					//如果发现15后仍是K1被按下，就确定是K1按下
					if(!(GPNDAT&(1<<0)))
					{
						cnt_k1=(cnt_k1+1)%10;//计数器+1%10,防止超出个位

						i=1500000;//定时器还原，重新刷新间隔，继续进行判断连续按下K1事件
						
						//空转，等待K1按键被抬起，防止K1按键一直被按下，cnt一直在加一
						while (!(GPNDAT&(1<<0)))
						{
							//如果K1按键一直被按下，则一直在死循环中
							//只有K1按键抬起，才能跳出循环，进行下一个间隔判断下一次按下事件
						}
						//抬起延时消抖
						Sleep(200);
					}
				}
			}
	
			//个位设置完成

			//如果是设置密码
			if(flag_init_pwd == SET_PWD)
				pwd_k1=cnt_k1;
			else//如果是输入密码
				key_k1=cnt_k1;


			//蜂鸣器鸣叫一次
			buzzer_on();
			GPKDATA=0x0f;
			Sleep(1000);
			buzzer_off();
			GPKDATA = 0xff;
		}
		//K2被按下
		if(!(GPNDAT&(1<<1)))
		{
			//设置K2计数器，十位
			int cnt_k2=0;

			//首先延时15
			Sleep(15);
			//如果发现15后仍是K2被按下，就确定是K2按下
			if(!(GPNDAT&(1<<1)))
				cnt_k2=(cnt_k2+1)%10;//计数器+1

			//空转，等待K2按键被抬起，防止K2按键一直被按下，cnt一直在加一
						while (!(GPNDAT&(1<<1)))
						{
							//如果K2按键一直被按下，则一直在死循环中
							//只有K2按键抬起，才能跳出循环，进行下一个间隔判断下一次按下事件
						}
			//抬起延时消抖
			Sleep(200);	
		
				int i;
			//接着设置定时器，在1500000的间隔内判断是否是有连续按下K2事件
			for(i=1500000;i>0;i--)
			{
				//如果发现在规定的间隔时间内有K2被按下的事件
				if(!(GPNDAT&(1<<1)))
				{
					//延时15
					Sleep(15);
					//如果发现10ms后仍是K2被按下，就确定是K2按下
					if(!(GPNDAT&(1<<1)))
					{
						cnt_k2=(cnt_k2+1)%10;//计数器+1%10,防止超出十位

						i=1500000;//定时器还原，重新刷新间隔，继续进行判断连续按下K2事件
						
						//空转，等待K2按键被抬起，防止K2按键一直被按下，cnt一直在加一
						while (!(GPNDAT&(1<<1)))
						{
							//如果K2按键一直被按下，则一直在死循环中
							//只有K2按键抬起，才能跳出循环，进行下一个间隔判断下一次按下事件
						}
						//抬起延时消抖
						Sleep(200);	
					}
				}

			}
				
			//十位设置完成

			//如果是设置密码
			if(flag_init_pwd == SET_PWD)
				pwd_k2 = cnt_k2;
			else//如果是输入密码
				key_k2 = cnt_k2;

			
			//蜂鸣器鸣叫一次
			buzzer_on();
			GPKDATA=0x0f;
			Sleep(1000);
			buzzer_off();
			GPKDATA = 0xff;

		}
		//K3被按下
		if(!(GPNDAT&(1<<2)))
		{
			//首先延时15
			Sleep(15);
			//如果发现10ms后仍是K3被按下，就确定是K3按下
			if(!(GPNDAT&(1<<2)))
			{
				Sleep(200);
				//如果是设置密码模式，蜂鸣器鸣叫设置的密码
				
				if(flag_init_pwd == SET_PWD)
				{
					int i;
					for(i=0;i<pwd_k1;i++)
					{
						buzzer_on();
						Sleep(500);
						buzzer_off();
						Sleep(500);
					}

					Sleep(3000);
					for(i=0;i<pwd_k2;i++)
					{
						buzzer_on();
						Sleep(500);
						buzzer_off();
						Sleep(500);
					}
				}
				
				
				
				//改变设置密码状态->输入密码状态
				flag_init_pwd = WAIT;
				key_k1 = 0;
				key_k2 = 0;

				//启动密码锁，进入1s一次的中断，等待输入
				timer_init(0,65,4,62500,0,WAIT);

			}	

		}
		//K4被按下
		if(!(GPNDAT & ( 1 << 3 )))
		{
			//首先延时10ms
			Sleep(15);
			//如果发现10ms后仍是K4被按下，就确定是K4按下
			if(!(GPNDAT & ( 1 << 3 )))
			{	
	/*				//如果是输入密码模式，输入完密码，蜂鸣器鸣叫响应次数
		        if(flag_init_pwd == WAIT)
			{
				
				int i;
				for(i=0;i<key_k1;i++)
				{
					buzzer_on();
					Sleep(500);
					buzzer_off();
					Sleep(500);
				}

				Sleep(3000);
				for(i=0;i<key_k2;i++)
				{
					buzzer_on();
					Sleep(500);
					buzzer_off();
					Sleep(500);
				}
			}
*/
			//判断密码输入情况，正确
			if(key_k1 == pwd_k1 && key_k2 == pwd_k2)
				//进入0.5s的中断，在中断中进行输入正确的状态输出
				timer_init(0,65,4,31250,0,PWD_RIGHT);
				
			else
				//进入0.5s的中断,在中断中进行输入错误的状态输出
				timer_init(0,65,4,31250,0,PWD_ERROR);
			}
			
		}
	}
	
	return 0;
}



