#include <stdio.h>
#include <stdlib.h>
#include <string.h>
void sort(int arry[9],int n)
{
	int i,j,temp;
	for(i=0;i<n;i++)
	{
		for(j=0;j<n-i-1;j++)
		{
			if(arry[j]<arry[j+1])
			{
				temp=arry[j];
				arry[j]=arry[j+1];
				arry[j+1]=temp;
			}
		}
	}
}
int main() {
	int m,n,*arry,*tary,i,l,k,r;
	scanf("%d",&n);
	arry=(int *)malloc(sizeof(int)*(n+1));
	tary=(int *)malloc(sizeof(int)*(n+1));
	for(i=1;i<=n;i++)//Å×Æú0 
		scanf("%d",arry+i);
	scanf("%d",&m);
	for(i=1;i<=m;i++)
	{
		scanf("%d%d%d",&l,&r,&k);
		memcpy(tary+1,arry+l,(r-l+1)*sizeof(int));
		sort(tary+1,r-l+1);
		printf("%d\n",tary[k]);
	}
	free(arry);
	free(tary);
	return 0;
}