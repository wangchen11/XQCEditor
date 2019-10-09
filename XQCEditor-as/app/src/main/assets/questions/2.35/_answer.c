#include <stdio.h>
#define  MAX   1024

int myStrlen(char *chs);

int main(){
	char chs[MAX]={0};
	scanf("%s",chs);
	printf("%d",myStrlen(chs));
	return 0;
}

int myStrlen(char *chs){
	int i=0;
	while(chs[i]!='\0')
		i++;
	return i;
}