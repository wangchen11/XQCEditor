#include <stdio.h>

int jiaMi(int n){
	int a[4]={0};
	for(int i=0;i<4;i++){
		a[i]=(n%10+5)%10;
		n/=10;
	}
	/*
	int temp=a[0];
	a[0]=a[3];
	a[3]=temp;
	
	temp=a[1];
	a[1]=a[2];
	a[2]=temp;
	*/
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
	printf("%d",jiaMi(n));
	return 0;
}