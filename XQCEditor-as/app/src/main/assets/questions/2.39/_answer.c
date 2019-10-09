#include <stdio.h>

int jieMi(int n){
	int a[4]={0};
	for(int i=0;i<4;i++){
		a[i]=(n%10-5+10)%10;
		n/=10;
	}
	int nn=0;
	for(int i=0;i<4;i++){
		nn=nn*10+a[i];
	}
	return nn;
}
int main()
{
	int n=0;
	scanf("%d",&n);
	printf("%d\n",jieMi(n));
	return 0;
}