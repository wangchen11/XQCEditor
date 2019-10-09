#include <stdio.h>
void Print5(int n)
{
	int i,j,k;
	for(i=1;i<10;i++)
	for(j=0;j<10;j++)
	for(k=0;k<10;k++)
	{
		if(i+i+j+j+k==n)
		{
			printf("%d%d%d%d%d\n",i,j,k,j,i);
		}
	}
}
void Print6(int n)
{
	int i,j,k;
	for(i=1;i<10;i++)
	for(j=0;j<10;j++)
	for(k=0;k<10;k++)
	{
		if(i+i+j+j+k+k==n)
		{
			printf("%d%d%d%d%d%d\n",i,j,k,k,j,i);
		}
	}
}
int main()
{
	int n;
	scanf("%d",&n);
	Print5(n);
	Print6(n);
	return 0;
}