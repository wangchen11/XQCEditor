#include <stdio.h>
void DtoH(int d);
int main(){
	int d;
	scanf("%d",&d);
	DtoH(d);
	return 0;
}
void DtoH(int d)
{
	int t=0;
	t=d%16;
	if(d/16>0)
	   DtoH(d/16);
	if(t<10)
		printf("%d",t);
	else
		printf("%c",t-10+'A');
	    
}