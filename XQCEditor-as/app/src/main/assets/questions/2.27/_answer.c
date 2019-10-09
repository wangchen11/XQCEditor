#include <stdio.h> 
#include <malloc.h>
#include <string.h>
int main()  
{
	int n,w,h,l,i,j,x,y,m;
	char *arry;
	scanf("%d",&n);
	w=h=5+n*4;
	arry=(char *)malloc(w*h);
	memset(arry,'.',w*h);
	
	for(m=0;m<=n;m++)//画没一层
	{
		for(i=(m+1)*2;i<w-(m+1)*2;i++)//四边 
		{
			x=m*2;
			y=i;
			arry[x+y*w]='$';
			x=w-m*2-1;
			y=i;
			arry[x+y*w]='$';
			x=i;
			y=m*2;
			arry[x+y*w]='$';
			x=i;
			y=w-m*2-1;
			arry[x+y*w]='$';
		}
		for(i=m*2;i<=(m+1)*2;i++)//角
		{
			x=i;
			y=(m+1)*2;
			arry[x+y*w]='$';
			x=(m+1)*2;
			y=i;
			arry[x+y*w]='$';
			
			x=w-i-1;
			y=(m+1)*2;
			arry[x+y*w]='$';
			x=w-(m+1)*2-1;
			y=i;
			arry[x+y*w]='$';
			
			x=i;
			y=h-(m+1)*2-1;
			arry[x+y*w]='$';
			x=(m+1)*2;
			y=h-i-1;
			arry[x+y*w]='$';
			
			x=w-i-1;
			y=h-(m+1)*2-1;
			arry[x+y*w]='$';
			x=w-(m+1)*2-1;
			y=h-i-1;
			arry[x+y*w]='$';
		} 
	} 
	for(i=0;i<h;i++)
	{
		for(j=0;j<w;j++)
		{
			printf("%c",arry[i*w+j]);
		}
		printf("\n");
	}
    return 0;  
}  
