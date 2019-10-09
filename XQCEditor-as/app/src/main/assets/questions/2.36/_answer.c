#include <stdio.h>

int main()
{
	int n=0;
	float s=0;
	scanf("%d",&n);
	if(n%2==0){
		for(int i=2;i<=n;i++){
			if(i%2==0)
				s+=1.0/i;
		}
	}else{
		for(int i=1;i<=n;i++){
			if(i%2!=0)
				s+=1.0/i;
		}
	}
	printf("%.2f",s);
	return 0;
}