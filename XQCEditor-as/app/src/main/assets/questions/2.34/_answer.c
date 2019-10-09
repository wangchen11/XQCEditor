
#include <stdio.h>
#include <malloc.h>
#include <string.h>

char *lianjie(char *a, char *b);//连接函数

int main()
{
	char a[1024] = {0};
	char b[1024] = {0};
	
	
	scanf("%s",a);
	scanf("%s",b);
	
	char *c=lianjie(a,b);
	
	printf("%s",c);
	
	return 0;
}

char *lianjie(char *a, char *b)//连接函数
{
	int k=0;
	char *c=(char *)malloc(sizeof(char)*(strlen(a)+strlen(b)));
	for(int i=0; a[i] != '\0'; i++)
	{
		c[k++] = a[i];
	}
	
	for(int j=0; b[j] != '\0'; j++)
	{
		c[k++] = b[j];
	}
	c[k]='\0';//
	return c;
}
/*
知识点:
*/
