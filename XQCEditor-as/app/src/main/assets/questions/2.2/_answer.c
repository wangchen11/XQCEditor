#include <stdio.h>
#include <string.h>
#include <stdbool.h>

bool endsWith(char *str, char *ends);

int main(){
	char str[500] = {0};
	char ends[50] = {0};
	scanf("%s",str);
	scanf("%s",ends);
	if(endsWith(str, ends))
		printf("true\n");
	else
		printf("false\n");
	return 0;
}

bool endsWith(char *str, char *ends)
{
	
	int len_1 = strlen(str);
	int len_2 = strlen(ends);
	

		
	while(len_2 >= 0)
	{
		if(str[len_1--] != ends[len_2--])
		{
			return false;
		}
	}
	return true;
}