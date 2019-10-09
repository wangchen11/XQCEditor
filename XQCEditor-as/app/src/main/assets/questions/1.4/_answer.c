#include <stdio.h>


long long fibonacci(long long n){
	if(n<=2)
		return 1;
	
	long long fn_1;
	long long fn_2;
	long long fn_index;
	
	int index;
	for( index=3,fn_1 = fibonacci(index-1),fn_2 = fibonacci(index-2) 
				; index<=n 
				; index++,fn_2 = fn_1, fn_1 = fn_index ){
		fn_index = (fn_1 + fn_2)%10007;
		//printf("---%lld---\n",fn_index);
	}
	return fn_index;
}

int main(){
	int n;
	scanf("%d",&n);
	printf("%lld",fibonacci(n));
	return 0;
}
