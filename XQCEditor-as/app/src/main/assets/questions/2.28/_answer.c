#include <stdio.h>

long long aaa(int n){
	long long s=1;
	for(int i=1;i<n;i++){
		s*=i;
	}
	return s;
}
int main()
{
	int n=0;
	long long s=0;
	for(n=1;n<=20;n++)
    {
		s+=aaa(n);
	}
	printf("%lld",s);
	return 0;
}
