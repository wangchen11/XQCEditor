#include <stdio.h> 

float mySum(float num[], int len);
float myMean(float num[], int len);

int main()
{
	float num[256] = {0};
	int len = 0;
		
	scanf("%d", &len);
	
	for(int i=0; i<len; i++)
	{
		scanf("%f", &num[i]);
	}
	
	float sum = mySum(num, len);
	float mean = myMean(num, len);
	
	printf("%8.2f\n", sum);
	printf("%8.2f\n", mean);
	
	return 0;
}
float mySum(float num[], int len)
{
	float n = 0;
	for(int i=0; i<len; i++)
	{
		n += num[i];
	}
	return n;
}
float myMean(float num[], int len)
{
	return mySum(num, len)/(len*1.0);
}