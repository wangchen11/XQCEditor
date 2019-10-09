#include <stdio.h>

int main()
{
	int a=0,n=0,s=0;
	scanf("%d%d",&a,&n);
	for(int i=1;i<=n;i++){
		
		int temp=0;
		for(int j=0;j<i;j++){
			temp=temp*10+a;
		}
		s+=temp;
	}
	printf("%d", s);
	
	return 0;
}
