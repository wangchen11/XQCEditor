#include <stdio.h>
#include <malloc.h>

int *arry;
int main()
{
	int i,n,a;
	scanf("%d",&n);
	arry=(int *)malloc(n*sizeof(int));
	for(i=0;i<n;i++)
	{
		scanf("%d",&arry[i]);
	}
	scanf("%d",&a);
	for(i=0;i<n;i++)
	{
		if(arry[i]==a)
		{
		    printf("%d",i+1);
		    break;
		}
	}
	if(i>=n)
		printf("%d",-1);
	return 0;
}