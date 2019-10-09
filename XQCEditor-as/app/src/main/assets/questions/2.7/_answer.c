/**********************************
** 杨辉三角 **
***********************************/

#include <stdio.h>
#define  NUM_MAX   37

int main()
{
	int aaa[NUM_MAX][NUM_MAX] = { { 0 } };
	int num = 0;
	scanf("%d", &num);
	
	for (int i = 0; i<num; i++)
	{
		for (int j = 0; j<num; j++)
		{
			if (0 == j)//每行第一列
			{
				aaa[i][j] = 1;
			}
			else if (i == j)//第i行第i列
			{
				aaa[i][j] = 1;
				continue;
			}
			else
			{
				//杨辉三角特性算法
				aaa[i][j] = aaa[i - 1][j - 1] + aaa[i - 1][j];
			}
		}
	}

	//打印数列
	for (int i = 0; i<num; i++)
	{
		for (int k = 0; k <= num - i; k++)
		{
			printf("   ");//因为后面是%6d 所以这里是3个空格
		}
		for (int j = 0; j<num; j++)
		{
			if (j > i) continue;//第i行打印i个数

			printf("%6d", aaa[i][j]);
		}
		printf("\n");
	}

	return 0;
}

