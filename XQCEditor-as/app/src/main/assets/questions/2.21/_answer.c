#include <stdio.h>


int main() {
	int i,n,t,max,num;
	max=0;
	scanf("%d",&n);
	for(i=0;i<n;i++)
	{
		scanf("%d",&t);
		if(t>max)
		{
			max=t;
			num=i;
		}
	}
	printf("%d %d",max,num);
	return 0;
}