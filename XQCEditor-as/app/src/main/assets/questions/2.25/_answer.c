#include <stdio.h>
int main()
{
	int a=3,b=5;
	scanf("%d %d",&a,&b);
	if(a==1&&b==1||(a%2==0&&b%2==0))
	    printf("0");
	else
	    printf("%d",a*b-a-b);
	return 0;
}