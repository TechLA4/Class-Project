#include "stdio.h"
#define GPKCON0     		(*((volatile unsigned long *)0x7F008800))
#define GPKDATA     			(*((volatile unsigned long *)0x7F008808))
//设置F端口的蜂鸣器
#define GPFCON 				(*((volatile unsigned long *)0x7F0080A0))
#define GPFDAT				(*((volatile unsigned long *)0x7F0080A4))


#define EINT0CON0  			(*((volatile unsigned long *)0x7F008900))
#define EINT0MASK  			(*((volatile unsigned long *)0x7F008920))
#define EINT0PEND  			(*((volatile unsigned long *)0x7F008924))
#define PRIORITY 	    	(*((volatile unsigned long *)0x7F008280))
#define SERVICE     		(*((volatile unsigned long *)0x7F008284))
#define SERVICEPEND 		(*((volatile unsigned long *)0x7F008288))
#define VIC0IRQSTATUS  		(*((volatile unsigned long *)0x71200000))
#define VIC0FIQSTATUS  		(*((volatile unsigned long *)0x71200004))
#define VIC0RAWINTR    		(*((volatile unsigned long *)0x71200008))
#define VIC0INTSELECT  		(*((volatile unsigned long *)0x7120000c))
#define VIC0INTENABLE  		(*((volatile unsigned long *)0x71200010))
#define VIC0INTENCLEAR 		(*((volatile unsigned long *)0x71200014))
#define VIC0PROTECTION 		(*((volatile unsigned long *)0x71200020))
#define VIC0SWPRIORITYMASK 	(*((volatile unsigned long *)0x71200024))
#define VIC0PRIORITYDAISY  	(*((volatile unsigned long *)0x71200028))
#define VIC0ADDRESS        	(*((volatile unsigned long *)0x71200f00))

#define		PWMTIMER_BASE			(0x7F006000)
#define		TCFG0    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x00)) )
#define		TCFG1    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x04)) )
#define		TCON      	( *((volatile unsigned long *)(PWMTIMER_BASE+0x08)) )
#define		TCNTB0    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x0C)) )
#define		TCMPB0    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x10)) )
#define		TCNTO0    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x14)) )
#define		TCNTB1    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x18)) )
#define		TCMPB1    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x1C)) )
#define		TCNTO1    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x20)) )
#define		TCNTB2    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x24)) )
#define		TCMPB2    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x28)) )
#define		TCNTO2    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x2C)) )
#define		TCNTB3    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x30)) )
#define		TCMPB3    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x34)) )
#define		TCNTO3    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x38)) )
#define		TCNTB4    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x3C)) )
#define		TCNTO4    	( *((volatile unsigned long *)(PWMTIMER_BASE+0x40)) )
#define		TINT_CSTAT 	( *((volatile unsigned long *)(PWMTIMER_BASE+0x44)) )

#define 		WAIT		0
#define		SET_PWD	1
#define		PWD_RIGHT 	2
#define		PWD_ERROR	3

typedef void (isr) (void);
extern void asm_timer_irq();


int state = 1;//按键
int key_cnt_1;//个位
int key_cnt_2;//十位
int cnt_k3_wait=0;//等待输入密码状态时cnt
int cnt_k4_error=0;//输入密码错误时cnt
int stream_led = 0;


void irq_init(void)
{
	/* 在中断控制器里使能timer0中断 */
	VIC0INTENABLE |= (1<<23);

	VIC0INTSELECT =0;

	isr** isr_array = (isr**)(0x7120015C);

	isr_array[0] = (isr*)asm_timer_irq;

	/*将GPK4-GPK7配置为输出口*/
	GPKCON0 = 0x11110000;
	
	/*熄灭四个LED灯*/
	GPKDATA = 0xff;

	//将GPF14蜂鸣器配置为输出口(s3c6410手册中将GPFCON的29-28位设置成01)
	GPFCON |= 1<<28;
	GPFCON &= ~(1<<29);

}

// timer0中断的中断处理函数
void do_irq()
{

	
	//等待输入状态
	if(state == WAIT)
	{
		
		//LED1-4 4-1流水灯点亮
		if(stream_led % 6 == 0)
			GPKDATA = 0xef;	//1110 1111
		else if(stream_led % 6 == 1)
			GPKDATA = 0xdf;	//1101 1111
		else if(stream_led % 6 == 2)
			GPKDATA = 0xbf;	//1011 1111
		else if(stream_led % 6 == 3)
			GPKDATA = 0x7f;	//0111 1111
		else if(stream_led % 6 == 4)
			GPKDATA = 0xbf;	//1011 1111
		else if(stream_led % 6 == 5)
			GPKDATA = 0xdf;	//1101 1111

		stream_led++;
	}
	else if(state == PWD_RIGHT){
		GPKDATA = 0x00;
	}
	//输入错误
	else if(state == PWD_ERROR)
	{

		//LED1-4 4-1流水灯点亮
		if(stream_led%6==0)
			GPKDATA=0xef;	//1110 1111
		else if(stream_led%6==1)
			GPKDATA=0xdf;	//1101 1111
		else if(stream_led%6==2)
			GPKDATA=0xbf;	//1011 1111
		else if(stream_led%6==3)
			GPKDATA=0x7f;	//0111 1111
		else if(stream_led%6==4)
			GPKDATA=0xbf;	//1011 1111
		else if(stream_led%6==5)
			GPKDATA=0xdf;	//1101 1111

		stream_led ++;
		buzzer_on();
		Sleep(300);
		buzzer_off();	//蜂鸣器鸣叫
	}
	unsigned long uTmp;
	//清timer0的中断状态寄存器
	uTmp = TINT_CSTAT;
	TINT_CSTAT = uTmp;
	VIC0ADDRESS=0x0;	
}

// 初始化timer
void timer_init(unsigned long utimer,unsigned long uprescaler,unsigned long udivider,unsigned long utcntb,unsigned long utcmpb,int state_temp)
{
	unsigned long temp0;

	//传入main函数中的状态参数
	state = state_temp;
	
	// 定时器的输入时钟 = PCLK / ( {prescaler value + 1} ) / {divider value} = PCLK/(65+1)/16=62500hz

	//设置预分频系数为66
	temp0 = TCFG0;
	temp0 = (temp0 & (~(0xff00ff))) | (uprescaler<<0);
	TCFG0 = temp0;

	// 16分频
	temp0 = TCFG1;
	temp0 = (temp0 & (~(0xf<<4*utimer))& (~(1<<20))) |(udivider<<4*utimer);
	TCFG1 = temp0;

	// 1s = 62500hz
	//0.5s=31250hz
	TCNTB0 = utcntb;
	TCMPB0 = utcmpb;

	// 手动更新
	TCON |= 1<<1;

	// 清手动更新位
	TCON &= ~(1<<1);

	// 自动加载和启动timer0
	TCON |= (1<<0)|(1<<3);

	// 使能timer0中断
	temp0 = TINT_CSTAT;
	temp0 = (temp0 & (~(1<<utimer)))|(1<<(utimer));
	TINT_CSTAT = temp0;
}

