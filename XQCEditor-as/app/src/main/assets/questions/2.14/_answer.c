#include <stdio.h>
void Print()
{
	int i,j,k,t;
	for(i=1;i<10;i++)
	for(j=0;j<10;j++)
	for(k=0;k<10;k++)
	{
		t=i*100+j*10+k;
		if(i*i*i+j*j*j+k*k*k==t)
			printf("%d\n",t);
	}
}
int main()
{
	Print();
	return 0;
}