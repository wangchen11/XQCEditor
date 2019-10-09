#include <stdio.h>
#include <string.h>
#include <stdbool.h>

bool startsWith(char *str, char *starts);

int main(){
	char str[500] = {0};
	char starts[50] = {0};
	scanf("%s",str);
	scanf("%s",starts);
	if(startsWith(str, starts))
		printf("true\n");
	else
		printf("false\n");
	return 0;
}

bool startsWith(char *str, char *starts)
{
	
	int len_1 = strlen(str);
	int len_2 = strlen(starts);
	
	if(len_1 < len_2)
		return false;
		
	len_2 = 0;
	while(starts[len_2] != '\0')
	{
		if(str[len_2] != starts[len_2])
		{
			return false;
		}
		len_2++;//
	}
	return true;
}