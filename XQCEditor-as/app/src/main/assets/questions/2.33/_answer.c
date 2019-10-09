#include <stdio.h>
#include <malloc.h>

void move(int arr[],int n,int m);

int main()
{
	int m=0,n=0;
	scanf("%d",&n);
	int *arr=(int *)malloc(sizeof(int)*n);
	for(int i=0;i<n;i++)
		scanf("%d",&arr[i]);
	scanf("%d",&m);
	
	move(arr,n,m);
	
	for(int i=0;i<n;i++)
		printf("%d",arr[i]);
	
	return 0;
}

void move(int arr[],int n,int m)
{
	int *temp=(int *)malloc(sizeof(int)*m);
	for(int i=n-m,j=0;i<n;i++,j++)//把最后m个元素保存起来
		temp[j]=arr[i];
	
	for(int i=n-m-1,j=n-1;i>=0;i--,j--)//移动剩下的元素
		arr[j]=arr[i];
	for(int i=0;i<m;i++)//把保存的元素放到数组头部
		arr[i]=temp[i];
		
}