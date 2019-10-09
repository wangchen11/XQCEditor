#include <stdio.h>

#define PI (3.14159265358979323)

int main(){
	int r;
	double s;
	
	scanf("%d",&r);
	s = PI*r*r;
	
	printf("%0.7lf",s);
	return 0;
}