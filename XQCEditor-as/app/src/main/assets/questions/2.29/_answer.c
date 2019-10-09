#include <stdio.h>
long long aaa(int n){
	long long s=0;
	if(n==1){
		s=1;
	}else{
		s=n*aaa(n-1);
	}
	return s;
}
int main(){
	int n=0;
	scanf("%d",&n);
	printf("%lld",aaa(n));
	return 0;
}