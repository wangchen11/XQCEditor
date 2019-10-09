#include <stdio.h>
#include <stdlib.h>
#include <malloc.h>
/* run this program using the console pauser or add your own getch, system("pause") or input loop */

int main(int argc, char *argv[]) {
	int i,n,*arry,max=0x80000000,min=0x7fffffff,sum=0;
	scanf("%d",&n);
	arry=(int *)malloc(sizeof(int)*n); 
	for(i=0;i<n;i++)
	{
		scanf("%d",arry+i);
		sum+=arry[i];
		if(arry[i]>max)
		    max=arry[i];
		if(arry[i]<min)
		    min=arry[i];
	}
	printf("%d\n%d\n%d",max,min,sum);
	free(arry);
	return 0;
}