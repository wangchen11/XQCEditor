#include <stdio.h>
#include <malloc.h>
int main()
{
	int n,m,allnum,i,j,k,temp,num;	
        unsigned int * arry;
	scanf("%d%d",&n,&m);
	allnum=m;
	arry = (unsigned int *)malloc(sizeof(unsigned int)*(m+1));
	for(i=1;i<=m;i++)//抛弃 第0个 便于运算
		arry[i]=i;
	temp=2;
	for(i=1;i<allnum;i++)
	{
		for(j=allnum-1;j>=i;j--)
		{
			if(j%temp==0)
			{
				for(k=j;k<allnum;k++)
				{
					arry[k]=arry[k+1];
				}
				allnum--;
			}
		}
		temp=arry[i+1];
	}
	num=0;
	for(i=1;i<=allnum;i++)
	{
		if(arry[i]>n&&arry[i]<m)
			num++;
	}
	printf("%d",num);
	free(arry);
	return 0;
}