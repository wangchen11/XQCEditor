#include <stdio.h>
void Print()
{
	int i,j,k;
	for(i=1;i<10;i++)
	for(j=0;j<10;j++)
	{
			printf("%d%d%d%d\n",i,j,j,i);
	}
}
int main()
{
	Print();
	return 0;
}