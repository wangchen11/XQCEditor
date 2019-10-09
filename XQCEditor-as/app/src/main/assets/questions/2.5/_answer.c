#include <stdio.h>
#include <string.h>

int indexChOfStr(char *str, char ch, int start);
int countChOfStr(char *str, char ch, int start);

int main(){
	char str[1024] = { 0 };
	char ch = '0';
	int start = 0, index = 0, count = 0;
	
	scanf("%s", str);
//getchar();//把回车去掉
	scanf("%c", &ch);
	scanf("%d", &start);
	
	
	index = indexChOfStr(str, ch, start);
	count = countChOfStr(str, ch, start);

	if (-1 == index)
		printf("false\n");
	else
		printf("%d %d\n", index, count);
	return 0;
}

int indexChOfStr(char *str, char ch, int start){
	char temp_ch = '0';
	while ((temp_ch = str[start]) != '\0'){
  
		if (temp_ch == ch)
			return start;
		start++;
	}
	return -1;
}

int countChOfStr(char *str, char ch, int start){
	int count = 0;
	while ((start = indexChOfStr(str, ch, start)) != -1){
		start++;
		count++;
	}
	return count;
}