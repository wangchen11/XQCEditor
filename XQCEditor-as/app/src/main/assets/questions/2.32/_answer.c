#include <stdio.h>

int main()
{
	int a[11]={1,4,6,9,13,16,19,28,40,100};
	int temp1,temp2,number,end,i,j;

	scanf("%d",&number);
	end=a[9];
	
	if(number>end)
		a[10]=number;
	else{
		for(i=0;i<10;i++){
			if(a[i]>number){
				temp1=a[i];
				a[i]=number;
				for(j=i+1;j<11;j++){
					temp2=a[j];
					a[j]=temp1;
					temp1=temp2;
				}
				break;
			}
		}
	}
	
	for(i=0;i<11;i++)
		printf("%d ",a[i]);
	return 0;
}
