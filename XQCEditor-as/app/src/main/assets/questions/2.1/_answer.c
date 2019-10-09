#include <stdio.h>

int main(int argc, char *argv[]) {
	int year;
	scanf("%d",&year);
	if(year%4==0)
	{
		if(year%100==0)
		{
			if(year%400==0)
			{
				printf("yes");
			}
			else
				printf("no");
		}
		else
			printf("yes");
	}
	else
	{
		printf("no");
	}
	return 0;
}	