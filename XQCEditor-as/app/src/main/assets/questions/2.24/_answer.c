#include <stdio.h>

int main(int argc, char *argv[]) {
    int a,b,c,i;
    int all;
    scanf("%d %d %d",&a,&b,&c);
    all=a*b*c;
    for(i=1;i<=all;i++)
	{
		if(i%a==0&&i%b==0&&i%c==0)
		{
			printf("%d",i);
			break;
		}
	}
    return 0;
}
