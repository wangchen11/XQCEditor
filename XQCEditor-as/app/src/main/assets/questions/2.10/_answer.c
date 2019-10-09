#include <stdio.h>
char text[9]; 
int hctoi(char h)
{
	if(h>='A'&&h<='Z')
	     return h-'A'+10;
	if(h>='a'&&h<='z')
	     return h-'a'+10;
	return h-'0';
}
int main() {
	unsigned int t;
	int i;
	scanf("%s",text);
	t=0;
	for(i=0;text[i];i++)
	{
		t<<=4;
		t+=hctoi(text[i]);
	}
	printf("%u",t);
	return 0;
}