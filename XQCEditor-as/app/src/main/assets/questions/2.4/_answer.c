/*
简单模仿Java的切割函数split
*/


#include <stdio.h>
#include <string.h>
#include <malloc.h>

#define  _strLEN     1024
#define  _regexLEN   20
#define  _printLEN    100

void strSplit(char *str, char *regex);

void clearArray(char *str, int len);

int main(){
	char str[_strLEN] = {0};
	char regex[_regexLEN] = {0};
	
	scanf("%s", str);
	scanf("%s", regex);
	
	strSplit(str, regex);

	return 0;
}

void strSplit(char *str, char *regex)
{
	int len_1 = strlen(str);
	int len_2 = strlen(regex);

	char printStr[_printLEN] = { 0 };
	char *tempStr = (char *)malloc(sizeof(char)* len_2);//给临时字符串申请切割字符串一样大的内存

	//重要部分
	int i=0, j = 0, k = 0, p = 0, count = 1;
	for(i = 0; i < len_1; i++)
	{
		printStr[k++] = str[i];//保存准备打印
		for (j = i+1; j < len_2 +1+ i; j++)
		{
			tempStr[p++] = str[j];//从i向后读len_2个字符
			if (0 == (strcmp(tempStr, regex)))//跟切割字符串比较一下
			{
				count++;
				printf("%s\n", printStr);//        
				clearArray(printStr, k+1);//清空数组
				clearArray(tempStr, p + 1);//len_2也可以   当找到切割字符串时 清空临时字符串
				p = k = 0;
				i += len_2;
			}
		}
		clearArray(tempStr, p + 1);//由于tempStr已经满了 所以没有找到也要清空临时字符串 len_2也可以
		p = 0;

	}
	if (0 != strlen(printStr))//输出最后一个切割字符串后面的子字符串
	{
		count++;
		printf("%s\n", printStr);//	
	}
	if (0 == count)
	{
		printf("false\n");
	}
}

void clearArray(char *str, int len)
{
	if(len <= 0)
		return;
		
	for(int i=0;i<len;i++)
	{
		str[i] = 0;
	}
}