#include <stdio.h>

int myStrcmp(char *astr, char *bstr){
	int i=0,j=0;
	while(1){
		j=astr[i]-bstr[i];
		if(0 != j)
			return j;
		
		if(astr[i] == '\0')
			return 0;
		i++;
	}
}
int main(){
	char astr[100]={0};
	char bstr[100]={0};
	
	scanf("%s",astr);
	scanf("%s",bstr);
	
	int n=myStrcmp(astr,bstr);
	
	if(n>0)
		printf("%s>%s\n", astr,bstr);
	else if(n == 0)
		printf("%s=%s\n", astr,bstr);
	else
		printf("%s<%s\n",astr,bstr);
	return 0;
}