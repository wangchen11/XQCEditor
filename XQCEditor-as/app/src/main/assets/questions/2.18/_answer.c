#include <stdio.h>
#include <stdlib.h>

/* run this program using the console pauser or add your own getch, system("pause") or input loop */

int main(int argc, char *argv[]) {
	char arry[5]={'0','0','0','0','0'};
	int i,j;
	for(j=0;j<32;j++)
	{
		for(i=0;i<5;i++)
		{
			printf("%c",arry[5-i-1]);
		}
		printf("\n");
		for(i=0;i<5;i++)
		{
			if(arry[i]=='0')
			{
				arry[i]='1';
				break;
			}
			else
			    arry[i]='0';
		}
	}
	//printf(); 
	return 0;
}