#include <stdio.h>
#include <stdlib.h>

/* run this program using the console pauser or add your own getch, system("pause") or input loop */

int main(int argc, char *argv[]) {
	int i,j,n,m,t;
	
	char a[27]="";
	scanf("%d%d",&n,&m);
	a[m]=0;
	for(i=0;i<n;i++)
	{
		for(j=0;j<m;j++)
		{
			t=0-i+j;
			if(t<0)
			   t=0-t;
			a[j]='A'+t;
		}
		printf("%s\n",a);
	}
	return 0;
}