#include <stdio.h>

int main()
{
	int i=0,n=0;
	scanf("%d",&n);
	int a[6]={0};
	
	for( i=0;i<6;i++)
	{
		a[i]=n%10;
		n/=10;
		if(n==0)
			break;
	}
	printf("%d\n",i+1);
	for(int j=0;j<=i;j++)
		printf("%d ",a[j]);
	return 0;
}