#include <stdio.h>
#include <stdbool.h>

bool isZhishu(int n){
	for(int i=2;i<=n/2;i++){
		if(n%i==0)
			return false;
	}
	return true;
}
int main()
{
	int n=0;
	scanf("%d",&n);
	
	for(int i=2;i<=n/2;i++)
	{
		if(isZhishu(i) && isZhishu(n-i)){
			printf("%d %d",i,n-i);
			break;
		}
	}
	
	return 0;
}